
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.silicontransit.timeline.TimeLine;
import com.silicontransit.timeline.bean.FilterBean;
import com.silicontransit.timeline.bean.MIDIControlCfgBean;
import com.silicontransit.timeline.bean.MIDIDeviceCfgBean;
import com.silicontransit.timeline.bean.MIDINoteCfgBean;
import com.silicontransit.timeline.model.ControlSettings;
import com.silicontransit.timeline.model.Event;
import com.silicontransit.timeline.model.TimeLineObject;
import com.silicontransit.timeline.obj.Standard;
import com.silicontransit.timeline.window.LogWindow;

import proxml.InvalidAttributeException;
import proxml.InvalidDocumentException;
import proxml.XMLElement;
import proxml.XMLInOut;

public class FileUtil {
	TimeLine t=null;
	
	
	public FileUtil(TimeLine t) {
		this.t=t;
		//fud = new FileUtilDOM(t);
	}
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	XML File load:
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void loadAdditionalTimelines(String inputStr) {
		open(inputStr,false);
	}
	public void open() {
		open(t.fileName,true);
	}
	public void open(String fileName, boolean clearCurrent) {
		open( fileName, clearCurrent,true) ;
	}
	public void open(String fileName, boolean clearCurrent,boolean runInit) {
		XMLInOut xmlInOut = new XMLInOut(t);
		try {
			
			String fileNameTest = t.dataFilePath+fileName;
			File f=new File(fileNameTest);
			if (clearCurrent) {	
				clearTimeline();
				if (fileNameTest.indexOf("undo")==-1) {t.clearUndo();} 
			}
			if (!f.exists()) {
				t.showMessage("file doesnt exist :"+fileName,LogWindow.TYPE_WARN);
				return;
			} 
			
			XMLElement timelinesEl = xmlInOut.loadElementFrom(fileNameTest);//timeLineObject.
			
			for(int j = 0; j < timelinesEl.countChildren();j++){
				XMLElement timelineEl=timelinesEl.getChild(j);
				if ("notes".equals( timelineEl.getElement())){
					for (int i=0; i<timelineEl.countChildren();i++) {
						XMLElement  noteEl=timelineEl.getChild(i);
						if (noteEl.hasChildren()) { t.notes.add(noteEl.getChild(0).getElement());}
						try {
							if ( noteEl.getAttribute("n")!=null) { t.notes.add( noteEl.getAttribute("n"));}
						}catch(Exception ide){}
					}
					continue;
				}
				if ("filters".equals( timelineEl.getElement())){
					for (int i=0; i<timelineEl.countChildren();i++) {
						XMLElement  filterEl=timelineEl.getChild(i);
						String filterTgt=filterEl.getAttribute("target");
						Vector v=(Vector)t.filters.get(filterTgt);
						if (v==null ) {
						   v=new Vector();
						   t.filters.put(filterTgt,v);
					    }
						for (int k=0;k<filterEl.countChildren();k++) {
							XMLElement exprEl=filterEl.getChild(k);
							FilterBean fb=new FilterBean() ;
							fb.setExpr(exprEl.getAttribute("text"));
							fb.setActive(Boolean.valueOf(exprEl.getAttribute("active")).booleanValue());
							v.add(fb);
						}
					}
					continue;
				}
				if ("midiKeys".equals( timelineEl.getElement())){
					for (int i=0;i<9;i++) {
						String attName=""+i;
						try {
							t.midiKeyBindings.put(attName,timelineEl.getAttribute(attName));
						} catch(InvalidAttributeException iaex){}
					}
					continue;
				}
				if ("midinotes".equals( timelineEl.getElement())){
					String[] devices=new String[1];
					try {
						devices[0]=  timelineEl.getAttribute("device") ; }
					catch (InvalidAttributeException iaex) {
						devices=t.midiDeviceNames;
						
					}
					for (int l=0;l<devices.length;l++) {
						String device= devices[l];
					
						HashMap midiNoteMapsForDevice=(HashMap)t.allMidiNoteMaps.get(device);
						if (midiNoteMapsForDevice==null) {
							midiNoteMapsForDevice =new HashMap();
							t.allMidiNoteMaps.put(device,midiNoteMapsForDevice);
						}
						boolean defaultMap=true;
						String id="";
						try {id=timelineEl.getAttribute("id");} catch(InvalidAttributeException iaex){id="def";}
						try {defaultMap=timelineEl.hasAttribute("default");} catch(InvalidAttributeException iaex){}
						String[][] noteMap=(String[][])midiNoteMapsForDevice.get(id);
						if(noteMap==null) {
							noteMap = new String[16][128];
						}
						if (defaultMap) {
							t.currentMidiNoteMaps.put(device,noteMap);
							t.currentMidiNoteMapIds.put(device,id);
						}
						((HashMap)t.allMidiNoteMaps.get(device)).put(id,noteMap);
					
						for (int i=0; i<timelineEl.countChildren();i++) {
							XMLElement xMidiPart=timelineEl.getChild(i);
							int part =-1;
							try {
								 part=Integer.parseInt(xMidiPart.getAttribute("part"));//getIntAttribute("part");//
							} catch (InvalidAttributeException e) {
								 part=Integer.parseInt(xMidiPart.getElement().substring(xMidiPart.getElement().indexOf("-")+1));
							}
							for (int k=0;k<128;k++) {
								try { noteMap[part][k]=xMidiPart.getAttribute(""+k);} catch(InvalidAttributeException e) {}
							}
						}
					}
					continue;
				}
				
				
				if ("midicontrols".equals( timelineEl.getElement())){
					String[] devices=new String[1];
					try {
						devices[0]=  timelineEl.getAttribute("device") ; }
					catch (InvalidAttributeException iaex) {
						devices=t.midiDeviceNames;
						
					}
					for (int l=0;l<devices.length;l++) {
						String device= devices[l];
					
						HashMap midiControlMapsForDevice=(HashMap)t.allMidiControlMaps.get(device);
						if (midiControlMapsForDevice==null) {
							midiControlMapsForDevice =new HashMap();
							t.allMidiControlMaps.put(device,midiControlMapsForDevice);
						}
						boolean defaultMap=true;
						String id="";
						try {id=timelineEl.getAttribute("id");} catch(InvalidAttributeException iaex){id="def";}
						try {defaultMap=timelineEl.hasAttribute("default");} catch(InvalidAttributeException iaex){}
						HashMap midiControlMap=(HashMap) midiControlMapsForDevice.get(id);
						if(midiControlMap==null) {
							midiControlMap = new HashMap();
						}	
						if (defaultMap) {
							t.currentMidiControlMaps.put(device,midiControlMap);
							t.currentMidiControlMapIds.put(device,id);
						}
						((HashMap)t.allMidiControlMaps.get(device)).put(id,midiControlMap);
						MIDIDeviceCfgBean mdcb=(MIDIDeviceCfgBean)  t.midiDeviceConfigMaps.get(device);
						for (int i=0; i<timelineEl.countChildren();i++) {
							XMLElement xMidiCtl=timelineEl.getChild(i);
							int size=xMidiCtl.getIntAttribute("size");
							Vector mappings=new Vector();
							String key="";
							for (int k=0;k<size;k++) {
								String ctlStr=xMidiCtl.getAttribute("ctlstring"+k); 
								ControlSettings c=new ControlSettings();
								c.parseControlStr(ctlStr);
								if (mdcb!=null) {
									MIDIControlCfgBean mccb=mdcb.getControl(c.control);
									if (mccb!=null) {c.controlTxt=mccb.getControlText();}
								}
								mappings.add(c);
								key=c.getPart()+"_"+c.getControl();
							}
							midiControlMap.put(key,mappings);
						}
					}
					continue;
				}
				TimeLineObject timeLineObject=new TimeLineObject(t);//oscServers[0]
				timeLineObject.parseTimeLine(timelineEl, t.oscServers);
				t.timeLines.add(timeLineObject);
				// add to playing array if playing.
				if ("".equals(timeLineObject.playMode) ) {
					t.playing.remove(timeLineObject);
				}  else {
					if (! t.playing.contains(timeLineObject)){ t.playing.add(timeLineObject);}
				}
			}
			try {
				String codeFile=timelinesEl.getAttribute("codeFile");
				if (codeFile!=null) { t.dynCompiler.loadXML( t.dataFilePath+codeFile);}
			} catch (RuntimeException e) {			}
			
		}catch(Exception ide){
			TimeLine.println(" bad File ..:"+ide.getMessage());
			t.showMessage(" bad File :"+fileName+" - "+ide.getMessage());
			ide.printStackTrace();
			return;
		}
		if ( t.timeLines.size()>0) {
			t.timeLineObject = (TimeLineObject) t.timeLines.get(0);
			t.timeLineSelection.add(t.timeLineObject);
			t.updateTimeLineDisplays();
		}
		// link up timelines on load.
		for (Iterator timeLineIter =  t.timeLines.iterator(); timeLineIter.hasNext();) {
			TimeLineObject timeLineElement = (TimeLineObject) timeLineIter.next();
			for (Iterator eventIter = timeLineElement.timeLine.iterator(); eventIter.hasNext();) {
				Event event = (Event) eventIter.next();
				if (!"".equals(event.targetStr)) {
					event.target= t.getTimeline(event.targetStr);
				}
			} 
		}
		t.timeLineObject.rebuildTimeLine();
		t.showMessage("load: "+fileName, LogWindow.TYPE_MSG);
		t.timeLineIndex=0;
		t.markDirty(false);
		// play 'init' timeline.
		if (runInit) {
			TimeLineObject init= t.getTimeline("init");
			if (init !=null) {	  t.togglePlayMode("p",init);	 }
		}
	}
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	XML File save():
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public  void clearTimeline() {
		t.dynamicObjects.clear();
		t.dynamicObjects.put("std",new Standard());
		t.timeLines.clear();
		t.playing.clear();
		t.notes.clear();
		t.filters.clear();
		//t.midiNotes=new String[16][128];
		t.currentMidiControlMaps=new HashMap();
		t.currentMidiControlMapIds=new HashMap();
		t.allMidiControlMaps=new HashMap();
		t.currentMidiNoteMaps=new HashMap();
		t.currentMidiNoteMapIds=new HashMap();
		t.allMidiNoteMaps=new HashMap();
		t.midiKeyBindings.clear();
		t.debugBean.clear();
		
		t.timeLineSelection.clear();
		t.timeLineDisplays.clear();
	}
	
	
	
	public boolean save(String fileName) {
		TimeLine.println("saving...");
		
		 //fud.save(fileName);
		 //if (true) return;// stop unreachable code error
		 
		XMLInOut xmlInOut = new XMLInOut(t);
		XMLElement xtimelines =new XMLElement("timelines");
		if ( t.dynCompiler.isXMLDirty()) {
			String codeFileName=t.dynCompiler.fileName;
			if ("".equals(codeFileName)) {
				codeFileName=fileName;
			} 
			t.dynCompiler.saveXML(fileName);
			xtimelines.addAttribute(
				"codeFile",
				t.dynCompiler.fileName//.substring(t.dataFilePath.length())
			);
		}
		//save notes.
		XMLElement xnotes =new XMLElement("notes");
		for (int i=0;i<t.notes.size();i++) {
			XMLElement xnote =new XMLElement("note");
			xnote.addAttribute("n",(String)t.notes.get(i));
			xnotes.addChild(xnote);
		}
		xtimelines.addChild(xnotes);
		//save filters
		XMLElement xfilters =new XMLElement("filters");
		Iterator filterIter=t.filters.keySet().iterator();
		while(filterIter.hasNext()) {
			String filterTgt=(String)filterIter.next();
			XMLElement xfilter =new XMLElement("filter");
			xfilter.addAttribute("target",filterTgt);
			Vector filters=(Vector)t.filters.get(filterTgt);
			for (int x=0;x<filters.size();x++) {
				FilterBean fb=(FilterBean)filters.get(x);
				XMLElement xexpr =new XMLElement("expression");
				xexpr.addAttribute("active",""+fb.isActive());
				xexpr.addAttribute("text",fb.getExpr());
				xfilter.addChild(xexpr);
			}
			xfilters.addChild(xfilter);
		}
		xtimelines.addChild(xfilters);
		//save midi keys
		XMLElement xkeys =new XMLElement("midiKeys");
		Iterator keyIter=t.midiKeyBindings.keySet().iterator();
		while (keyIter.hasNext()) {
			String key=(String)keyIter.next();
			xkeys.addAttribute(key,(String)t.midiKeyBindings.get(key));
		}
		xtimelines.addChild(xkeys);
	    Iterator noteDevIter=t.allMidiNoteMaps.keySet().iterator();
	    while (noteDevIter.hasNext()) {
	    	String device=(String )noteDevIter.next();
	    	HashMap noteMapsForDevice=(HashMap)t.allMidiNoteMaps.get(device);
	    	Iterator noteMapsForDeviceIter=noteMapsForDevice.keySet().iterator();
	    	while (noteMapsForDeviceIter.hasNext()) {
	    		String id=(String)noteMapsForDeviceIter.next();
	    		String[][] noteMap=(String[][])noteMapsForDevice.get(id);
	    		
				XMLElement xMidiNotes = new XMLElement("midinotes");
				xMidiNotes.addAttribute("device",device);
				xMidiNotes.addAttribute("id",id);
				// change this when defaults are configured properly.
				if (t.currentMidiNoteMaps.containsValue(noteMap)) {xMidiNotes.addAttribute("default","true");}
				for (int i=0;i<noteMap.length;i++) {
					XMLElement xMidiPart=null;
					for (int j=0;j<noteMap[i].length;j++) {
						if (noteMap[i][j]!=null) {
							if (xMidiPart==null) {
								xMidiPart = new XMLElement("midipart-"+i);
							}
							xMidiPart.addAttribute(""+j,noteMap[i][j]);
						}
					}
					if (xMidiPart!=null) {xMidiNotes.addChild(xMidiPart);}
				}
				xtimelines.addChild(xMidiNotes);
	    	}
	    }
		Iterator ctlDevIter=t.allMidiControlMaps.keySet().iterator();
	    while (ctlDevIter.hasNext()) {
	    	String device=(String )ctlDevIter.next();
	    	HashMap ctlMapsForDevice=(HashMap)t.allMidiControlMaps.get(device);
	    	Iterator ctlMapsForDeviceIter=ctlMapsForDevice.keySet().iterator();
	    	while (ctlMapsForDeviceIter.hasNext()) {
	    		String id=(String)ctlMapsForDeviceIter.next();
	    		HashMap midiControlMap=(HashMap)ctlMapsForDevice.get(id);
				XMLElement xMidiControls = new XMLElement("midicontrols");
				xMidiControls.addAttribute("device",device);
				xMidiControls.addAttribute("id",id);
				if (t.currentMidiControlMaps.containsValue(midiControlMap)) {xMidiControls.addAttribute("default","true");}
				Iterator ctlIter=midiControlMap.keySet().iterator();
				while (ctlIter.hasNext()) {
					String key=(String)ctlIter.next();
					Vector v=(Vector)midiControlMap.get(key);
					XMLElement xMidiControl = new XMLElement("midicontrol");
					xMidiControl.addAttribute("size",v.size());
					for (int i=0;i<v.size();i++) {
						ControlSettings c=(ControlSettings)v.get(i);
						xMidiControl.addAttribute("ctlstring"+i,c.toString());
					}
					xMidiControls.addChild(xMidiControl);
				}
				xtimelines.addChild(xMidiControls);
		    }
		}
		for (int i=0;i<t.timeLines.size();i++) {
			if (t.timeLineObject.timeLine.size()>=0) {
				TimeLineObject timeLineObject=(TimeLineObject)t.timeLines.get(i);
				XMLElement xtimeline = timeLineObject.asXmlElement();
				xtimelines.addChild(xtimeline);
			}
		}
		xmlInOut.saveTo(xtimelines, t.dataFilePath+fileName);   //timeLineObject.
		TimeLine.println("saved!!");
		t.showMessage("saved: "+fileName,LogWindow.TYPE_MSG);
		if (fileName.indexOf("undo")==-1) {t.markDirty(false);}
		return true;
	}
	
	public void parseMidiControlMaps() {
		XMLInOut xmlInOut = new XMLInOut(t);
		String fileNameTest = t.mapFilePath;//
		//String fileNameTest= t.configDirectoryPath+"maps"+File.separator+"ControlMaps.xml";
		System.out.println(fileNameTest);
		File f=new File(fileNameTest);
		if (!f.exists()) {t.showMessage("file doesnt exist :"+t.fileName);return;} 
		try {
			XMLElement xControlConfig= xmlInOut.loadElementFrom(fileNameTest);
			for (int  i=0;i<xControlConfig.countChildren();i++) {
				XMLElement xDevice =xControlConfig.getChild(i);
				//HashMap devHash=new HashMap();
				try {
					MIDIDeviceCfgBean mdcb=new MIDIDeviceCfgBean(xDevice.getAttribute("timeLineId"));
					try {mdcb.setMidiId(xDevice.getAttribute("midiId"));} catch(InvalidAttributeException e) {}
					try {mdcb.setImagePath(xDevice.getAttribute("image"));} catch(InvalidAttributeException e) {}
					for (int  j=0;j<xDevice.countChildren();j++) {
						XMLElement xControl = xDevice.getChild(j); 
						try {
							if (xControl.getElement().equals("control")) {
								//devHash.put(xControl.getAttribute("num"),xControl.getAttribute("desc"));
								MIDIControlCfgBean mccb=new MIDIControlCfgBean();
								try {mccb.setControlNum(xControl.getIntAttribute("num"));} catch(InvalidAttributeException e) {}
								try {mccb.setControlText(xControl.getAttribute("desc"));} catch(InvalidAttributeException e) {}
								try {mccb.setTop(xControl.getIntAttribute("top"));} catch(InvalidAttributeException e) {}
								try { mccb.setLeft(xControl.getIntAttribute("left"));} catch(InvalidAttributeException e) {}
								try {mccb.setWidth(xControl.getIntAttribute("width"));} catch(InvalidAttributeException e) {}
								try {mccb.setHeight(xControl.getIntAttribute("height"));} catch(InvalidAttributeException e) {}
								mdcb.addControl(mccb);
							} else if (xControl.getElement().equals("notes")) {
								MIDINoteCfgBean mncb=new MIDINoteCfgBean();
								try {mncb.setTop(xControl.getIntAttribute("top"));} catch(InvalidAttributeException e) {}
								try { mncb.setLeft(xControl.getIntAttribute("left"));} catch(InvalidAttributeException e) {}
								try {mncb.setWidth(xControl.getIntAttribute("width"));} catch(InvalidAttributeException e) {}
								try {mncb.setHeight(xControl.getIntAttribute("height"));} catch(InvalidAttributeException e) {}
								try {mncb.setOctaves(xControl.getIntAttribute("octaves"));} catch(InvalidAttributeException e) {}
								try {mncb.setBlackKeyWidth(xControl.getIntAttribute("blackKeyWidth"));} catch(InvalidAttributeException e) {}
								try {mncb.setBlackKeyHeight(xControl.getIntAttribute("blackKeyHeight"));} catch(InvalidAttributeException e) {}
								mdcb.setMidiNoteCfgBean(mncb);
							}
						} catch(InvalidAttributeException e) {}
					}
					t.midiDeviceConfigMaps.put(xDevice.getAttribute("timeLineId"),mdcb);
				} catch(InvalidAttributeException e) {}
			}
		} catch (InvalidDocumentException e) {
			System.out.println("Couldnt load midi maps.");
		}
	}
	
	public void parseConfig() {
		XMLInOut xmlInOut = new XMLInOut(t);
		String fileNameTest= t.configFilePath;//t.configDirectoryPath+"config.xml";
		File f=new File(fileNameTest);
		if (!f.exists()) {t.showMessage("file doesnt exist :"+t.fileName);return;} 
		try {
			XMLElement xConfig= xmlInOut.loadElementFrom(fileNameTest);
			for (int  i=0;i<xConfig.countChildren();i++) {
				XMLElement item =xConfig.getChild(i);
				if (item.getElement().equals("hosts")) {
					int newOcServerPorts[]= new int[item.countChildren()] ;
					String newOscServerHost[]= new String[item.countChildren()];
					int newOscServerRcPorts[]= new int[item.countChildren()] ;
					
					for (int  j=0;j<item.countChildren();j++) {
						XMLElement host =item.getChild(j);
						newOcServerPorts[j]= Integer.parseInt(host.getAttribute("sndPort")) ;
						newOscServerHost[j]= host.getAttribute("addr");
						newOscServerRcPorts[j]= Integer.parseInt(host.getAttribute("rcvPort")) ;
						System.out.println("host:"+newOscServerHost[j]+" -snd:"+newOcServerPorts[j]+" -rcv:"+newOscServerRcPorts[j]);
					}
					t.oscServerPorts= newOcServerPorts;
					t.oscServerHost= newOscServerHost;
					t.oscServerRcPorts= newOscServerRcPorts;
					
				}
				if (item.getElement().equals("midi")) {
					String newmidiDevices[]=new String[item.countChildren()];
					
					for (int  j=0;j<item.countChildren();j++) {
						XMLElement midi = item.getChild(j);
						newmidiDevices[j]=midi.getAttribute("id");
						System.out.println("midi device:"+newmidiDevices[j]);
					}
					t.midiDeviceNames=newmidiDevices;
				}
				if (item.getElement().equals("classpath")) {
					t.additionalClassPath=item.getAttribute("path");
				}
				if (item.getElement().equals("datapath")) {
					t.dataFilePath=item.getAttribute("path");
				}
			}
		} catch (InvalidDocumentException e) {
			t.showMessage("Couldnt load config.");
		}
	}
}
