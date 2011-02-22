
package com.silicontransit.timeline.util;
/*
This file is part of Timeline OSC.

   Timeline OSC is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Timeline OSC is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar.  If not, see <http://www.gnu.org/licenses/>
 */
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import javax.sound.midi.spi.MidiFileReader;

import com.silicontransit.timeline.TimeLine;
import com.silicontransit.timeline.bean.MIDIControlCfgBean;
import com.silicontransit.timeline.bean.MIDIDeviceCfgBean;
import com.silicontransit.timeline.model.ControlSettings;
import com.silicontransit.timeline.model.Event;
import com.silicontransit.timeline.model.TimeLineObject;
import com.silicontransit.timeline.obj.Poly;

import promidi.Controller;
import promidi.MidiIO;
import promidi.MidiOut;
import promidi.Note;
import promidi.UnavailablePortException;


public class MIDIUtil {
	TimeLine t=null;
	public MIDIUtil(TimeLine t) {
		this.t=t;
	}
	
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	MIDI Stuff: initMidi() - attempts to open all devices specified in midiDeviceNames
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void  initMidi() {
		t.midiIO = MidiIO.getInstance( t ); 
		TimeLine.println("printPorts of midiIO");
		t.midiIO.printPorts();
		
		// this may not work when more than 2 midi devices are plugged in.
		for (int i=0;i<t.midiDeviceNames.length;i++) {
			try {
				for (int j=0; j<t.midiIO.getNumberOfInputs();j++) {
					if (t.midiIO.getInputName(j).indexOf(t.midiDeviceNames[i])==0) {
						t.midiIO.openInput(j);
						
					}
				}
			} catch (UnavailablePortException upEx) {
				System.out.println("couldn't open MIDI device:"+t.midiDeviceNames[i]+":"+upEx.getMessage());
			}
		}
		t.midiOutDevices = new MidiOut[t.midiOutDeviceNames.length];
		for (int i=0;i<t.midiOutDeviceNames.length;i++) {
			try {
				for (int j=0; j<t.midiIO.getNumberOfOutputs();j++) {
					if (t.midiIO.getOutputName(j).indexOf(t.midiOutDeviceNames[i])==0) {
						t.midiOutDevices[i] = t.midiIO.openOutput(j);
						System.out.println("Opened Output:"+t.midiOutDeviceNames[i]);
					}
				}
			} catch (UnavailablePortException upEx) {
				System.out.println("couldn't open MIDI out device:"+t.midiOutDeviceNames[i]+":"+upEx.getMessage());
			}
		}
	}
	
	public  void closeMidi() {
		t.midiIO.dispose();
	}
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	MIDI Stuff: noteOn - handle noteOn event 
//	if recording then record events on current timeline.
//	if in note input mode("+") do input help (sets a note range to an osc event)
//	otherwise if a mapping is specified for the note then send an OSC message 
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public void noteOn(Note note){
		int mp = note.getMidiPort();
		String device=t.midiIO.getInputName(mp);
		// look for a truncated device id. (i.e. w/o hw[x,0])
		for (int i=0;i<t.midiDeviceNames.length;i++) {
			if (device.indexOf(t.midiDeviceNames[i])==0) {device =t.midiDeviceNames[i]; }
		}
		noteOn( note, device);
	}
	
	public void noteOn(Note note,String device){
		int vel = note.getVelocity();
		int pit = note.getPitch();
		int mch = note.getMidiChannel();
		if ("".equals(device)) {return;}
		t.currentMIDIInputDevice=device;
		String[][] midiNoteMap=(String[][] )t.currentMidiNoteMaps.get(device);
		if  (midiNoteMap==null) {
			String id = newNoteMap();
			//midiNoteMap=(String[][])t.currentMidiControlMaps.get(t.currentMIDIInputDevice);
			midiNoteMap=(String[][])t.currentMidiControlMaps.get(id);
		}
		
		Event e=new Event();
		e.setValue( pit +" " +vel );
		if (t.inputMode.equals("+")){//input help
			if ((t.shift) && (midiNoteMap[mch][pit]!=null)) {
//				shift overwrites current setting
				String[] s=midiNoteMap[mch][pit].split(",");
				t.inputStr=mch+" "+pit+" "+pit+" "+s[0]+" "+s[1];
				t.cursorPos=t.inputStr.length();
				return;
			}
			updateMidiNoteStr(pit, mch);
			t.cursorPos=t.inputStr.length();
		} else {
			if (t.timeLineObject.playMode.equals("r")) {
				Event last=t.getLastEvent();
				e.oscMsgName="/note";
				e.oscIndex=t.timeLineObject.oscIndex;
				if (last!=null) {
					e.oscMsgName=last.oscMsgName;
					e.oscIndex = last.oscIndex;
				}
				e.oscP5=t.oscServers[e.oscIndex];
				e.eventTime=t.timeLineObject.pos;
				t.timeLineObject.timeLine.add(e);
				t.timeLineObject.rebuildTimeLine();
			} 
			if (midiNoteMap!=null && midiNoteMap[mch][pit]!=null) {
				String[] msgData=midiNoteMap[mch][pit].split(",");
				String messageStr = msgData[1];
				String oscIndex = msgData[0];
				// fill in poly b4 exec expr
				Poly poly =(Poly)  t.dynamicObjects.get("poly");
				if (vel != 0) {	poly.noteOn(pit);	} else { poly.noteOff(pit);}
				// check noteoff status oscMsg prefixed with * (signifies dont play note off)
				if (messageStr.length()==0) { return;  }
				boolean playNoteoff=true;
				if (messageStr.substring(0,1).equals("*")) {
					playNoteoff=false;
					messageStr = messageStr.substring(1, messageStr.length());
				}
				if (vel==0 && !playNoteoff) {return;}
			
				if('$'==messageStr.charAt(0)) {
					Vector v= new Vector();
					v.add(new Integer(pit));v.add(new Integer(vel));v.add(poly.index());
					t.exprUtil.setValueExpr(messageStr,v);
					return;
				}
				else {// treat as OSC message.
					e.oscMsgName=t.exprUtil.resolveExpr(messageStr);
					String oscMsgStr=e.oscMsgName;
					e.oscIndex=Integer.parseInt(oscIndex);
					e.oscP5 = t.oscServers[e.oscIndex];
					t.oscUtil.simpleOscMessage(e);// play through
				}
			}
		}
		TimeLine.println("noteon: v:"+vel+":p:"+pit+":mc:"+mch+":dev :"+device);
	}
	
	public void updateMidiNoteStr(int pit, int mch) {
		int part=-1;
		int startNote=-1;
		int endNote=-1;
		int oscIndex=t.timeLineObject.oscIndex;
		String oscMsg=null;
		String[] midiRangeAndMsg=t.inputStr.split(" ");
		try {part=Integer.parseInt(midiRangeAndMsg[0]); } catch (Exception ex) {}
		try {startNote=Integer.parseInt(midiRangeAndMsg[1]);} catch (Exception ex) {}
		try {endNote=Integer.parseInt(midiRangeAndMsg[2]);} catch (Exception ex) {}
		try {oscIndex=Integer.parseInt(midiRangeAndMsg[3]);} catch (Exception ex) {};
		try {oscMsg=midiRangeAndMsg[4];} catch (Exception ex) {};
		if (part==-1) {part=mch;}
		if (startNote==-1) {startNote=pit;}
		else if (endNote==-1) {endNote=pit;}
		if (pit<startNote)  {startNote=pit;}
		else if (pit>endNote) {endNote=pit;}
		else if (t.shift) {startNote=pit;}
		else if (t.ctrl) {endNote=pit;}
		if (oscMsg==null) {oscMsg="/";}
		t.inputStr=part+" "+startNote+" "+endNote+" "+oscIndex+" "+oscMsg;
	}
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	MIDI Stuff: noteOff - does nothing
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void noteOff(Note note){
		int vel = note.getVelocity();
		int pit = note.getPitch();
		int mch = note.getMidiChannel();
		int mp = note.getMidiPort();
		
		String device=t.midiIO.getInputName(mp);
		if (t.isUnix) {device=device.substring(0,device.indexOf(" ")).trim();}
//		if ((device.indexOf(t.midiDeviceNames[0])==0 || device.indexOf(t.midiDeviceNames[2])==0) {
//			// herc get noteoff msg for 0.
//			Controller ctlMsg=new Controller(mp,mch,pit,vel);
//			controllerIn(ctlMsg);
//		}
		MIDIDeviceCfgBean mdcb = (MIDIDeviceCfgBean) t.midiDeviceConfigMaps.get(device);
		if (mdcb!=null && mdcb.getMidiNoteCfgBean()!=null) {
			// Note n = new Note(mch,pit,vel);
			noteOn(note);
		} else {// assument this noteOff as created by a control
			Controller ctlMsg=new Controller(mp,mch,pit,vel);
			controllerIn(ctlMsg);
		}
		TimeLine.println("noteoff: v:"+vel+":p:"+pit+":mc:"+mch+":mp:"+mp);
	}
	
	
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	MIDI Stuff: setMidiNoteRange(String inputStr) process input string and pits into midi control map
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public void setMidiNoteRange(String inputStr) {
		try {
			String[][] midiNoteMap=(String[][] )t.currentMidiNoteMaps.get(t.currentMIDIInputDevice);
			if  (midiNoteMap==null) {
				String id = newNoteMap();
				midiNoteMap=(String[][])t.currentMidiNoteMaps.get(t.currentMIDIInputDevice);
			}
			String[] midiRangeAndMsg=inputStr.split(" ");
			int part=Integer.parseInt(midiRangeAndMsg[0]);
			int startNote=Integer.parseInt(midiRangeAndMsg[1]);
			int endNote=Integer.parseInt(midiRangeAndMsg[2]);
			int oscIndex=Integer.parseInt(midiRangeAndMsg[3]);
			if (endNote<startNote) {int tmp=startNote;startNote=endNote;endNote=tmp;}
			String oscMsg=midiRangeAndMsg[4];
			//if (oscMsg.indexOf("/")!=0 && oscMsg.indexOf("$")!=0) {oscMsg="/"+oscMsg;}
			for (int i=startNote; i<=endNote; i++) {
				midiNoteMap[part][i]=oscIndex+","+oscMsg;
			}
		} catch (Exception e) {
			t.showMessage("format: part start end oscInex oscMsg");
		}
	}
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	MIDI Stuff: dumpMidiNoteMap() - output note map to console
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public Vector dumpMidiNoteMap() {
		//String output="";
		Vector texts=new Vector();
		Iterator noteDevIter=t.allMidiNoteMaps.keySet().iterator();
	    while (noteDevIter.hasNext()) {
	    	String device=(String )noteDevIter.next();
	    	HashMap noteMapsForDevice=(HashMap)t.allMidiNoteMaps.get(device);
	    	Iterator noteMapsForDeviceIter=noteMapsForDevice.keySet().iterator();
	    	while (noteMapsForDeviceIter.hasNext()) {
	    		String id=(String)noteMapsForDeviceIter.next();
	    		String[][] noteMap=(String[][])noteMapsForDevice.get(id);
	    		if (noteMap!=null) {
	    			texts.add("----device:"+device+" id:"+id+"-------");
					String currOscMsg=null;
					for (int i=0;i<noteMap.length;i++) {
						currOscMsg=null;
						String str="";
						for (int j=0;j<noteMap[i].length;j++) {
							String oscMsg=noteMap[i][j];
							if (currOscMsg==null && oscMsg!=null) {
								str=i+" "+j+" ";
								currOscMsg=oscMsg;
							}
							else if ((currOscMsg!=null) && ((oscMsg==null) || (!currOscMsg.equals(oscMsg)))) {
								str+=(j-1)+" "+currOscMsg.replaceAll(","," ");
								texts.add(str);
								str=i+" "+j+" ";
								currOscMsg=oscMsg;
							}
						}
					}
	    		}
	    	}
	    }
		return texts;
	}
	
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// sendMIDIMapOut: dump midi note map to the device.
//	
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public void sendMIDIMapOut(String device) {
		int index =-1;
		for (int i=0;i<t.midiOutDeviceNames.length;i++) {
			if (device.equals(t.midiOutDeviceNames[i])) {
				index =i; break;
			}
		}
		MIDIDeviceCfgBean mdcb=(MIDIDeviceCfgBean)t.midiDeviceConfigMaps.get(device);
		
		if (index >-1) {
			MidiOut mo = t.midiOutDevices[index];
			System.out.println("mo:"+mo);
			if (mo!=null) {
				HashMap midiControlMap=(HashMap) t.currentMidiControlMaps.get(device);
				//System.out.println("midiControlMap:"+midiControlMap.size());
				Iterator i = midiControlMap.keySet().iterator();
				while (i.hasNext()) {
					String next =(String) i.next();
					Vector v = (Vector)midiControlMap.get(next);
					//System.out.println("v."+v +" - id:"+next);
					if (v!=null) {
						for (int j = 0;j<v.size();j++){
							ControlSettings cs = (ControlSettings)v.get(j);
							//System.out.println("cs:"+cs.midiValue);
							Controller c = new Controller(cs.part,cs.control,cs.midiValue);
							mo.sendController(c);
							if (mdcb!=null) {
								MIDIControlCfgBean mccb=mdcb.getControl(cs.control);
								System.out.println("mccb:"+mccb.getControlText()+":"+cs.midiValue);
								if (mccb!=null) {mccb.setWinValue(cs.midiValue);}
							}
						}
					}
				}
			}
		}
	}
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// sendMIDIOut: dump midi controller value to the device.
//	
//		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
		public void sendMIDIOut(String device,Controller c) {
			int index =-1;
			for (int i=0;i<t.midiOutDeviceNames.length;i++) {
				if (device.equals(t.midiOutDeviceNames[i])) {
					index =i; break;
				}
			}
			if (index >-1) {
				MidiOut mo = t.midiOutDevices[index];
				//System.out.println("mo:"+mo);
				if (mo!=null) {
					mo.sendController(c);
				}
			}
		}
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	MIDI Stuff: controllerIn(Controller controller) - process MIDI control input
//	if in input mode do input help 
//	otherwise send all OSC messages for the mapping
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
	public void controllerIn(Controller controller){
		controllerIn( controller, false);
	}
	long lastRecSample=0;
	public void controllerIn(Controller controller,boolean fromOSC){
		int mp = controller.getMidiPort();
		String device="";
		if (fromOSC) {
			device="OSCMIDI:"+mp;
		} else {
			device=t.midiIO.getInputName(mp);
			if (t.isUnix) {device=device.substring(0,device.indexOf(" ")).trim();}
		}
		controllerIn( controller, device);
	}
	public void controllerIn(Controller controller,String device){
		int val = controller.getValue();
		int num = controller.getNumber();
		int mch = controller.getMidiChannel();

		HashMap midiControlMap=(HashMap) t.currentMidiControlMaps.get(device);
		if (midiControlMap==null) {
			String id = newControllerMap();
			midiControlMap=(HashMap)t.currentMidiControlMaps.get(t.currentMIDIInputDevice);
		}
		Vector existingVector=(Vector) midiControlMap.get(mch+"_"+num);
		if (t.inputMode.equals("=")) {//input help.
			t.currentMIDIInputDevice=device;
			t.showMessage(t.currentMIDIInputDevice);
			if ("".equals(t.inputStr)) {
				if ((existingVector!=null) && (existingVector.size()>0))  {	
					ControlSettings existing=(ControlSettings)existingVector.get(0);
					t.inputStr=existing.toString();
				}
				else  {
					t.inputStr=mch+" "+num+" "+t.timeLineObject.oscIndex+" / 1 0 n";
				}
			}
			else if (t.shift){
				ControlSettings input = new ControlSettings();
				input.parseControlStr(t.inputStr);
				input.setPart(mch);
				input.setControl(num);
				t.inputStr=input.toString();
			}
			t.cursorPos=t.inputStr.length();
		}
		else if (t.timeLineObject.playMode.equals("r")) {
			
			if ((System.currentTimeMillis()-lastRecSample>t.recordGranularity) 
					&& (t.recordInput.equals( "") || device.indexOf(t.recordInput)==0 || t.recordInput.indexOf("/")==0)) { // not sure if this workd for all cases
				lastRecSample=System.currentTimeMillis();
				if (existingVector!=null) {// play events
					for (int i=0;i<existingVector.size();i++) {
						
						ControlSettings existing=(ControlSettings)existingVector.get(i);
						if (t.recordInput.equals( "") ||device.indexOf(t.recordInput)==0 || t.recordInput.indexOf( existing.getOscMsg()) == 0) {
							Event e = new Event();
							e.oscMsgName=existing.getOscMsg();
							e.oscIndex=existing.oscIndex;
							e.oscP5=t.oscServers[e.oscIndex];
							e.setValue(""+existing.getValue(val));
							e.eventTime=t.timeLineObject.pos;
							t.timeLineObject.timeLine.add(e);
							t.timeLineObject.rebuildTimeLine();
						}
					}
				}
			}
		} 
		if (existingVector!=null) {// play events
			for (int i=0;i<existingVector.size();i++) {
				ControlSettings existing=(ControlSettings)existingVector.get(i);
				if ((val==0) && (existing.type.equals("t"))) {continue;}
				existing.midiValue = val;
				if (existing.oscMsg.indexOf("$")==0) {
					Object o=t.exprUtil.setValueExpr(existing.oscMsg,""+existing.getValue(val));
					if (o!=null && o instanceof Vector) {
						Vector subPlay=(Vector)o;
						for (int j=0;j<subPlay.size();j++) {
							ControlSettings subCtlSettings=(ControlSettings)subPlay.get(j);
							if (subCtlSettings.oscMsg.indexOf("/")==0) {
								Event e=new Event();
								e.oscMsgName=t.exprUtil.resolveExpr(subCtlSettings.oscMsg);
								//	 cos getValue inc accumulator value the value is never set to zero.
								e.setValue(""+subCtlSettings.value);
								e.oscIndex=subCtlSettings.getOscIndex();
								e.oscP5=t.oscServers[e.oscIndex];
								t.oscUtil.simpleOscMessage(e);
							}
						}
					}
				} else {
					Event e=new Event();
					
					e.oscMsgName=t.exprUtil.resolveExpr(existing.oscMsg);
					//	 cos getValue inc accumulator value the value is never set to zero.
					e.setValue(""+existing.getValue(val));
					e.oscIndex=existing.getOscIndex();
					e.oscP5 = t.oscServers[e.oscIndex];
					t.oscUtil.simpleOscMessage(e);
				}
			}
		}
		// rm: experimental set winvalue on ctl in - test
		MIDIDeviceCfgBean mdcb=(MIDIDeviceCfgBean)t.midiDeviceConfigMaps.get(device);
		if (mdcb!=null) {
			MIDIControlCfgBean mccb=mdcb.getControl(num);
			if (mccb!=null) {mccb.setWinValue(val);}
		}
		
		TimeLine.println("ctlin: val:"+val+":num:"+num+":mc:"+mch+":d:"+device);
	}
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	MIDI Stuff: setMidiControl() - 
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void setMidiControl(String inputStr) {
		
		// problem here when no controller is pressed to input data the currentMIDIInputDevice .
		HashMap midiControlMap=(HashMap) t.currentMidiControlMaps.get(t.currentMIDIInputDevice);
		if (midiControlMap==null) {
			String id = newControllerMap();
			midiControlMap=(HashMap)t.currentMidiControlMaps.get(t.currentMIDIInputDevice);
		}
		
		ControlSettings controlSettings =  new ControlSettings();
		controlSettings.parseControlStr(inputStr);
		
		if (midiControlMap==null) {
			// search for control in all maps;
			Iterator i=t.currentMidiControlMaps.keySet().iterator();
			while (i.hasNext()) {
				String midiCtlMapKey=(String)i.next();
				HashMap testMap=(HashMap)t.currentMidiControlMaps.get(midiCtlMapKey);
				if (testMap.get(controlSettings.getPart()+"_"+ controlSettings.getControl())!=null) {
					midiControlMap=testMap;
					t.currentMIDIInputDevice=midiCtlMapKey;
				}
			}
		}
		if (midiControlMap!=null) {
			//HashMap devTexts=(HashMap)t.midiDeviceCtlMaps.get(t.currentMIDIInputDevice);
			//controlSettings.controlTxt=(String)devTexts.get(""+controlSettings.control);
			MIDIDeviceCfgBean mdcb=(MIDIDeviceCfgBean)t.midiDeviceConfigMaps.get(t.currentMIDIInputDevice);
			if (mdcb==null || controlSettings == null) {return ;}// avoid np - why?
			MIDIControlCfgBean mccb=mdcb.getControl(controlSettings.control);
			if (mccb!=null) {controlSettings.controlTxt=mccb.getControlText();}
			
		}
		Vector existingVector=(Vector)midiControlMap.get(controlSettings.getPart()+"_"+ controlSettings.getControl());
		if (existingVector==null) {
			existingVector=new Vector();
			existingVector.add(controlSettings);
			midiControlMap.put(controlSettings.getPart()+"_"+ controlSettings.getControl(), existingVector);
		}
		
		int thisIndex=-1;
		for (int i=0;i<existingVector.size();i++) {
			ControlSettings test = (ControlSettings)existingVector.get(i);
			if (controlSettings.equals(test)) {thisIndex=i;break;}
		}
		// if inputstr ends with - then delete entry.
		if (inputStr.lastIndexOf("-")==inputStr.length()-1) {
			if (thisIndex>-1) {existingVector.remove(thisIndex);}
			return;
		}
		//otherwise add/replace entry in vector..
		if (thisIndex>-1) {existingVector.set(thisIndex,controlSettings);}
		else {existingVector.add(controlSettings);}
	}
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	MIDI Stuff: changeControlMap() - input help to get next/last control map.
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public  String changeControlMap(String inputStr, int decrement) {
		if ("".equals(inputStr)) {return inputStr;}
		HashMap midiControlMap=(HashMap)t.currentMidiControlMaps.get(t.currentMIDIInputDevice);
		ControlSettings controlSettings =  new ControlSettings();
		controlSettings.parseControlStr(inputStr);
		Vector existingVector=(Vector)midiControlMap.get(controlSettings.getPart()+"_"+ controlSettings.getControl());
		if (existingVector==null) {return inputStr;}
		int thisIndex=-1;
		for (int i=0;i<existingVector.size();i++) {
			ControlSettings test = (ControlSettings)existingVector.get(i);
			if (controlSettings.equals(test)) {thisIndex=i;break;}
		}
		if (thisIndex>-1) {
			if (decrement==1) {if (thisIndex>0) {thisIndex--;}else {thisIndex=existingVector.size()-1;}}
			else {if (thisIndex<existingVector.size()-1) {thisIndex++;}else {thisIndex=0;}}
			return( (ControlSettings)existingVector.get(thisIndex)).toString();
		} else {return inputStr;}
	}
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	MIDI Stuff: dumpMidiCtlMap() - output controller map to console
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public Vector dumpMidiCtlMap() {
		Vector texts=new Vector();
		Iterator devIter=t.currentMidiControlMaps.keySet().iterator();
		while (devIter.hasNext()) {
			String dev=(String)devIter.next();
			HashMap midiControlMap=(HashMap)t.currentMidiControlMaps.get(dev);
			
			texts.add("DEVICE:"+dev);
			if (midiControlMap!=null) {
				TimeLine.println("DEVICE:"+dev);
				Vector devTexts=new Vector();
				Iterator ctlIter=midiControlMap.keySet().iterator();
				while (ctlIter.hasNext()) {
					String key=(String)ctlIter.next();
					Vector v=(Vector)midiControlMap.get(key);
					for (int i=0;i<v.size();i++) {
						ControlSettings c=(ControlSettings)v.get(i);
						devTexts.add(c);//.toString()
						TimeLine.println(c.toDisplayString());
					}
				}
			
			Collections.sort(devTexts);
			texts.addAll(devTexts);
			}
		}
		return texts;
	}
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	MIDI Stuff: setMap) - set current baps based on numeric key mapping. 
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public void setMaps(char key) {
		String id=(String )t.midiKeyBindings.get(""+key);
		System.out.println("setMap:"+key+":"+id+":");
		Iterator ctlDevIter=t.allMidiControlMaps.keySet().iterator();
	    while (ctlDevIter.hasNext()) {
	    	String device=(String )ctlDevIter.next();
	    	HashMap ctlMapsForDevice=(HashMap)t.allMidiControlMaps.get(device);
	    	HashMap mapToSet=(HashMap)ctlMapsForDevice.get(id);
	    	if (mapToSet != null) {
	    		 t.currentMidiControlMaps.put(device,mapToSet);
	    		 t.currentMidiControlMapIds.put(device,id);
	    		 System.out.print("send map.");
	    		 sendMIDIMapOut(device);
	    	}
	    }
	    Iterator noteDevIter=t.allMidiNoteMaps.keySet().iterator();
	    while (noteDevIter.hasNext()) {
	    	String device=(String )noteDevIter.next();
	    	HashMap noteMapsForDevice=(HashMap)t.allMidiNoteMaps.get(device);
	    	String[][] mapToSet=(String[][])noteMapsForDevice.get(id);
	    	if (mapToSet != null) {
	    		 t.currentMidiNoteMaps.put(device,mapToSet);
	    		 t.currentMidiNoteMapIds.put(device,id);
	    	}
	    }
	}
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	MIDI Stuff: new controller map
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public String newControllerMap() {
		HashMap newCtlMap=new HashMap();
		HashMap devCtlMap=(HashMap)t.allMidiControlMaps.get(t.currentMIDIInputDevice);
		if (devCtlMap==null) {
			devCtlMap=new HashMap();
			t.allMidiControlMaps.put(t.currentMIDIInputDevice,devCtlMap);
		}
		String key="new";
		String testKey=key;int ctr=0;
		while (devCtlMap.get(testKey)!=null) {
			testKey=key+(ctr++);
		}
		devCtlMap.put(testKey,newCtlMap);
		if (t.currentMidiControlMaps.get(t.currentMIDIInputDevice)==null) {
			t.currentMidiControlMaps.put(t.currentMIDIInputDevice,newCtlMap);
			t.currentMidiControlMapIds.put(t.currentMIDIInputDevice,testKey);
		}
		return testKey;
	}
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	MIDI Stuff: new note map
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public String newNoteMap() {
		String[][] newNoteMap=new String[16][128];
		HashMap devNoteMap=(HashMap)t.allMidiNoteMaps.get(t.currentMIDIInputDevice);
		if (devNoteMap==null) {
			devNoteMap=new HashMap();
			t.allMidiNoteMaps.put(t.currentMIDIInputDevice,devNoteMap);
		}
		String key="new";
		String testKey=key;int ctr=0;
		while (devNoteMap.get(testKey)!=null) {
			testKey=key+(ctr++);
		}
		devNoteMap.put(testKey,newNoteMap);
		if (t.currentMidiNoteMaps.get(t.currentMIDIInputDevice)==null) {
			t.currentMidiNoteMaps.put(t.currentMIDIInputDevice,newNoteMap);
			t.currentMidiNoteMapIds.put(t.currentMIDIInputDevice,testKey);
		}
		return testKey;
	}
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	MIDI Stuff: Import MIDI file - use the standard javax.midi.spi classes to import from a MIDI file 
// each track goes on a seperate timeline.
// only read noteon and note off at the mo - ignores the rest.
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public void importMIDI(String fileName) {

		File midiFile = new File(fileName);
		try {
			Sequence seq = MidiSystem.getSequence(midiFile);
			MidiFileFormat mff = MidiSystem.getMidiFileFormat(midiFile);
			Track[] tracks = seq.getTracks();
			System.out.println("format:"+mff.getDivisionType() +" - PPQ:"+Sequence.PPQ+" - SMPTE_24:"+Sequence.SMPTE_24+" - SMPTE_25:"+Sequence.SMPTE_25+" - SMPTE_30:"+Sequence.SMPTE_30+" - SMPTE_30DROP:"+Sequence.SMPTE_30DROP);
			System.out.println("res:"+mff.getResolution());
			System.out.println("length(ms):"+mff.getMicrosecondLength());
			System.out.println("size(bytes):"+mff.getByteLength());
			if (mff.getDivisionType()!=Sequence.PPQ) {throw new RuntimeException("Only works for PPQ format at the mo!!! Sorry.");}
			for (int i=0; i<tracks.length; i++) {
				Track track = tracks[i];
				TimeLineObject to = new TimeLineObject(t);
				to.setId("track_"+i);
				to.setBeatLength( t.timeLineObject.getBeatLength() );
				to.setBeatPerBar( t.timeLineObject.getBeatPerBar() );
				to.setQuantize( t.timeLineObject.getQuantize() );
				to.setOscIndex(t.timeLineObject.getOscIndex());
				
				long trackLenTicks = tracks[i].ticks();
				int tickResolution = mff.getResolution(); //the number of ticks in a beat
				int tlLength = (int) trackLenTicks / tickResolution * to.getQuantize() * to.getBeatLength();
				System.out.println( trackLenTicks + ":" + trackLenTicks/tickResolution + "=" + tlLength );
				
				to.setTimeLineLength( tlLength );
				
				t.timeLines.add(to);
				for (int j=0;j<track.size();j++) {
					MidiEvent me = track.get(j);
					Event e = new Event();
					e.oscMsgName="/midi"+i+"/note";
					e.oscIndex=to.getOscIndex();
					e.oscP5=t.oscServers[to.getOscIndex()];
					String val = "";
					
					//for (int k=0;k<me.getMessage().getLength();k++){
					//	val+=me.getMessage().getMessage()[k]+" ";
					//}
					
					byte[] msg = me.getMessage().getMessage();
					int ctl = (0x000000FF & ((int)msg[0])); //'unpack' signed byte to int.
					int part = ctl  % 16;
					int eventType = ctl / 16 ;
					System.out.println(ctl+":"+part+":"+eventType);
 					if (eventType==9) { // note on
						val = msg[1]+" "+msg[2]; // note +vel
					} else if (eventType == 8) { // note off
						val = msg[1]+" "+0; // note +vel
					} else {
						continue;
					}
					e.setValue ( val );
					e.eventTime = (int) me.getTick() * to.getBeatLength() * to.getQuantize() / tickResolution;
					to.timeLine.add(e);
				}
				to.rebuildTimeLine();
				to.currentEvent=to.lastSelEvent=0;
			}
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
