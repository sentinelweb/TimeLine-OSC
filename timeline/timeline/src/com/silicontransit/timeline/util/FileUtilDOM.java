
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
import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.silicontransit.timeline.TimeLine;
import com.silicontransit.timeline.bean.FilterBean;
import com.silicontransit.timeline.bean.MIDIControlCfgBean;
import com.silicontransit.timeline.bean.MIDIDeviceCfgBean;
import com.silicontransit.timeline.bean.MIDINoteCfgBean;
import com.silicontransit.timeline.model.ControlSettings;
import com.silicontransit.timeline.model.Event;
import com.silicontransit.timeline.model.TimeLineObject;
import com.silicontransit.timeline.model.TimeLineSet;
import com.silicontransit.timeline.window.LogWindow;

public class FileUtilDOM {
	TimeLine t=null;
	XMLUtil xu = new XMLUtil();
	FileUtil fu=null;
	
	public FileUtilDOM(TimeLine t) {
		this.t=t;
		this.fu=new FileUtil(t);
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
		try {
			String fileNameTest = t.dataFilePath+fileName;
			System.out.println(fileNameTest);
			File f=new File(fileNameTest);
			if (clearCurrent) {	
				t.clearEveryThing();
				if (fileNameTest.indexOf("undo")==-1) {t.clearUndo();} 
			}
			if (!f.exists()) {
				t.showMessage("file doesnt exist :"+fileName,LogWindow.TYPE_WARN);
				t.timeLines.add(new TimeLineObject(t));
				t.timeLineObject = (TimeLineObject) t.timeLines.get(0);
				t.timeLineSelection.add(t.timeLineObject);
				t.updateTimeLineDisplays();
				return;
			} 
			Document doc = xu.parseXmlFile( fileNameTest ,false );
			Element timelinesEl = (doc==null)?null:doc.getDocumentElement();
			if (timelinesEl == null || timelinesEl.getAttribute("version")==null) {
				fu.open( fileName,  clearCurrent, runInit);
				return;
			}
			NodeList firstLevelNodes = timelinesEl.getChildNodes();
			for(int j = 0; j <firstLevelNodes.getLength(); j++){
				if (firstLevelNodes.item(j).getNodeType()!=Node.ELEMENT_NODE) {continue;} 
				
				Element timelineEl = (Element)firstLevelNodes.item(j);
				
				if ("source".equals( timelineEl.getNodeName())){
					Element codeElement = (Element)  timelineEl;
					Element objectsElement = (Element) codeElement.getElementsByTagName("objects").item(0);
					t.dynCompiler.loadElement(objectsElement);
				} 
				else if ("interface".equals(timelineEl.getNodeName()))  {t.loadInterface(timelineEl.getAttribute("path"));}
				else if ("description".equals( timelineEl.getNodeName())){
					if (((Element)timelineEl).getChildNodes().getLength()>0) {
						t.noteWindow.setText(
							((CDATASection)((Element)timelineEl).getChildNodes().item(0)).getData()
						);
					}
				}else if ("notes".equals( timelineEl.getNodeName())){
					NodeList noteElements = timelineEl.getElementsByTagName("note");
					String s = "";
					for (int i=0; i<noteElements.getLength();i++) {
						Element noteEl = ( Element)noteElements.item(i);
						//if (noteEl.hasChildNodes()) { t.notes.add(xu.getText(noteEl));}
						if (noteEl.hasChildNodes()) {s += i+". "+xu.getText(noteEl)+"\n";}
					}
				}
				else if ("colors".equals( timelineEl.getNodeName())){
					ColorMap cm= null;
					if ("osc".equals(timelineEl.getAttribute("type"))) {
						//t.oscMessageColorMap = new ColorMap();
						cm=t.oscMessageColorMap;
					} else if ("expr".equals(timelineEl.getAttribute("type"))){
						//t.expressionColorMap = new ColorMap();
						cm=t.expressionColorMap;
					}
					if (cm!=null) {
						NodeList colorElements= timelineEl.getElementsByTagName("color");
						for (int i=0; i<colorElements.getLength();i++) {
							Element colorEl = ( Element)colorElements.item(i);
							String[] rgbStr=colorEl.getAttribute("value").split(":");
							cm.put(
								colorEl.getAttribute("for"), 
								new Color(Integer.parseInt(rgbStr[0]), Integer.parseInt(rgbStr[1]), Integer.parseInt(rgbStr[2]) )
							);
						}
					}
				}
				else if ("filters".equals( timelineEl.getNodeName())){
					NodeList filterElements = timelineEl.getElementsByTagName("filter");
					for (int i=0; i<filterElements.getLength();i++) {
						Element filterEl = ( Element)filterElements.item(i);
						String filterTgt=filterEl.getAttribute("target");
						Vector v=(Vector)t.filters.get(filterTgt);
						if (v==null ) {
						   v=new Vector();
						   t.filters.put(filterTgt,v);
					    }
						NodeList exprElements = filterEl.getElementsByTagName("expression");
						for (int k=0;k<exprElements.getLength();k++) {
							Element exprEl=( Element)exprElements.item(k);
							FilterBean fb=new FilterBean() ;
							fb.setExpr(exprEl.getAttribute("text"));
							fb.setActive(Boolean.valueOf(exprEl.getAttribute("active")).booleanValue());
							v.add(fb);
						}
					}
				}
				else if ("midiKeys".equals( timelineEl.getNodeName())){
					for (int i=0;i<9;i++) {
						String attName="key_"+i;
							String keyBinding=timelineEl.getAttribute(attName);
							if (keyBinding!=null && !"".equals(keyBinding)) {
								t.midiKeyBindings.put(attName.substring(4),keyBinding);
							}
					}
				}
				else if ("midinotes".equals( timelineEl.getNodeName())){
					String[] devices=new String[1];
						// RM note : what does this do - looks dodgy.
					devices[0]=  timelineEl.getAttribute("device") ; 
					if (devices[0]==null) {devices=t.midiDeviceNames;}
					for (int l=0;l<devices.length;l++) {
						String device= devices[l];
						HashMap midiNoteMapsForDevice=(HashMap)t.allMidiNoteMaps.get(device);
						if (midiNoteMapsForDevice==null) {
							midiNoteMapsForDevice =new HashMap();
							t.allMidiNoteMaps.put(device,midiNoteMapsForDevice);
						}
						boolean defaultMap=true;
						String id=timelineEl.getAttribute("id");
						if (id==null) {id="def";}
						defaultMap=timelineEl.hasAttribute("default");
						
						String[][] noteMap=(String[][])midiNoteMapsForDevice.get(id);
						if(noteMap==null) {
							noteMap = new String[16][128];
						}
						if (defaultMap) {
							t.currentMidiNoteMaps.put(device,noteMap);
							t.currentMidiNoteMapIds.put(device,id);
						}
						((HashMap)t.allMidiNoteMaps.get(device)).put(id,noteMap);
						NodeList partElements = timelineEl.getElementsByTagName("midipart");
						for (int i=0; i<partElements.getLength();i++) {
							
							Element xMidiPart=(Element) partElements.item(i);
							int part=Integer.parseInt(xMidiPart.getAttribute("part"));//xMidiPart.getElement().substring(xMidiPart.getElement().indexOf("-")+1)
							for (int k=0;k<128;k++) {
								noteMap[part][k]=xMidiPart.getAttribute("note_"+k);
							}
						}
					}
				}
				else if ("midicontrols".equals( timelineEl.getNodeName())){
					String[] devices=new String[1];
						// RM note : what does this do - looks dodgy.
					devices[0] =  timelineEl.getAttribute("device") ; 
					if (devices[0]==null) {devices=t.midiDeviceNames;}
					for (int l=0;l<devices.length;l++) {
						String device= devices[l];
					
						HashMap midiControlMapsForDevice=(HashMap)t.allMidiControlMaps.get(device);
						if (midiControlMapsForDevice==null) {
							midiControlMapsForDevice =new HashMap();
							t.allMidiControlMaps.put(device,midiControlMapsForDevice);
						}
						boolean defaultMap=true;
						String id=timelineEl.getAttribute("id");
						if (id==null) {id="def";}
						defaultMap=timelineEl.hasAttribute("default");
						
						HashMap midiControlMap=(HashMap) midiControlMapsForDevice.get(id);
						if(midiControlMap==null) {
							midiControlMap = new HashMap();
						}	
						if (defaultMap) {
							t.currentMidiControlMaps.put(device, midiControlMap);
							t.currentMidiControlMapIds.put(device,id);
						}
						((HashMap) t.allMidiControlMaps.get( device ) ).put( id, midiControlMap );
						MIDIDeviceCfgBean mdcb=(MIDIDeviceCfgBean)  t.midiDeviceConfigMaps.get(device);
						NodeList controlElements = timelineEl.getElementsByTagName("midicontrol");
						for (int i=0; i<controlElements.getLength();i++) {
							Element xMidiCtl=(Element)controlElements.item(i);
							try {
								int size=Integer.parseInt(xMidiCtl.getAttribute("size"));
								Vector mappings=new Vector();
								String key="";
								for (int k=0;k<size;k++) {
									String ctlStr=xMidiCtl.getAttribute("ctlstring_"+k); 
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
							} catch (NumberFormatException e1) {
								t.showMessage("midiControls ("+id+") ("+device+") invalid.", LogWindow.TYPE_WARN);
							}
						}
					}
				}  else if ("timeline".equals( timelineEl.getNodeName())) {
					TimeLineObject timeLineObject=new TimeLineObject(t);//oscServers[0]
					timeLineObject.parseTimeLineDOM(timelineEl, t.oscServers);
					t.timeLines.add(timeLineObject);
					// add to playing array if playing.
					if ("".equals(timeLineObject.playMode) ) {
						t.playing.remove(timeLineObject);
					}  else {
						if (! t.playing.contains(timeLineObject)){ t.playing.add(timeLineObject);}
					}
				}else if ("set".equals( timelineEl.getNodeName())) {
					String timeLines= timelineEl.getAttribute("list");
					String[] tlNames= timeLines.split(",");
					TimeLineSet set = new TimeLineSet(t);
					set.setId(timelineEl.getAttribute("id"));
					for (int i=0;i<tlNames.length;i++) {
						TimeLineObject tlo = t.getTimeline(tlNames[i]);
						set.getSet().add(tlo);
					}
					t.timeLineSets.add(set);
				} else if ("view".equals( timelineEl.getNodeName())) {
					String[] list = timelineEl.getAttribute("visible").split(",");
					for (int z=0;z<list.length;z++) {
						t.timeLineObject = (TimeLineObject) t.getTimeline(list[z]) ;
						t.timeLineSelection.add(t.timeLineObject);
					}
					t.updateTimeLineDisplays();
				}
			}
		}catch(Exception ide){
			TimeLine.println("bad File ..:"+ide.getMessage());
			t.showMessage("bad File :"+fileName+" - "+ide.getMessage());
			ide.printStackTrace();
			return;
		}
		if ( t.timeLineSelection.size()==0) { // show first if view setting not saved
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
	
	
	public void save(String fileName) {
		TimeLine.println("saving...");
		
		//XMLInOut xmlInOut = new XMLInOut(t);
		Document doc = xu.createDomDocument();
		Element xtimelines = doc.createElement("timelines");
		xtimelines.setAttribute("version","1.0");
		doc.appendChild(xtimelines);
		
		Element xsource = doc.createElement("source");
		xsource.appendChild(t.dynCompiler.saveElement(doc));
		xtimelines.appendChild(xsource);
		//save notes.
//		Element xnotes = doc.createElement("notes");
//		for (int i=0;i<t.notes.size();i++) {
//			Element xnote = doc.createElement("note");
//			xnote.appendChild(doc.createTextNode((String)t.notes.get(i)));
//			xnotes.appendChild(xnote);
//		}
//		xtimelines.appendChild(xnotes);
		
		Element xdesc = doc.createElement("description");
		CDATASection noteCData=doc.createCDATASection(t.noteWindow.getText());	
		xdesc.appendChild(noteCData);
		xtimelines.appendChild(xdesc);
		
		// save colors
		for (int i=0;i<2;i++) {
			Element xcolors = doc.createElement("colors");
			ColorMap cm=null;
			if ( i==0 ) {		xcolors.setAttribute("type","osc");		cm = t.oscMessageColorMap;} 
			else {		xcolors.setAttribute("type","expr");	cm = t.expressionColorMap;			}
			for (Iterator oscColIter = cm.keySet().iterator(); oscColIter.hasNext();) {
				String oscMsg = (String) oscColIter.next();
				Color c  =  cm.getColorFor(oscMsg);	
				Element xcolor = doc.createElement("color");
				xcolor.setAttribute("for",oscMsg);
				xcolor.setAttribute("value",c.getRed()+":"+c.getGreen()+":"+c.getBlue());
				xcolors.appendChild(xcolor);
			}
			xtimelines.appendChild(xcolors);
		}
		
		//save filters
		Element xfilters = doc.createElement("filters");
		Iterator filterIter=t.filters.keySet().iterator();
		while(filterIter.hasNext()) {
			String filterTgt=(String)filterIter.next();
			Element xfilter = doc.createElement("filter");
			xfilter.setAttribute("target",filterTgt);
			Vector filters=(Vector)t.filters.get(filterTgt);
			for (int x=0;x<filters.size();x++) {
				FilterBean fb=(FilterBean)filters.get(x);
				Element xexpr = doc.createElement("expression");
				xexpr.setAttribute("active",""+fb.isActive());
				xexpr.setAttribute("text",fb.getExpr());
				xfilter.appendChild(xexpr);
			}
			xfilters.appendChild(xfilter);
		}
		xtimelines.appendChild(xfilters);
		//save midi keys
		Element xkeys = doc.createElement("midiKeys");
		Iterator keyIter=t.midiKeyBindings.keySet().iterator();
		while (keyIter.hasNext()) {
			String key=(String)keyIter.next();
			String keyBinding = (String)t.midiKeyBindings.get(key);
			if (keyBinding!=null && !"".equals(keyBinding)) {
				xkeys.setAttribute("key_"+key,keyBinding);
			}
		}
		xtimelines.appendChild(xkeys);
	    Iterator noteDevIter=t.allMidiNoteMaps.keySet().iterator();
	    while (noteDevIter.hasNext()) {
	    	String device=(String )noteDevIter.next();
	    	if ("".equals(device)) {continue;}
	    	HashMap noteMapsForDevice=(HashMap)t.allMidiNoteMaps.get(device);
	    	Iterator noteMapsForDeviceIter=noteMapsForDevice.keySet().iterator();
	    	while (noteMapsForDeviceIter.hasNext()) {
	    		String id=(String)noteMapsForDeviceIter.next();
	    		String[][] noteMap=(String[][])noteMapsForDevice.get(id);
				Element xMidiNotes = doc.createElement("midinotes");
				xMidiNotes.setAttribute("device",device);
				xMidiNotes.setAttribute("id",id);
				// change this when defaults are configured properly.
				if (t.currentMidiNoteMaps.containsValue(noteMap)) {xMidiNotes.setAttribute("default","true");}
				for (int i=0;i<noteMap.length;i++) {
					Element xMidiPart = null;
					for (int j=0;j<noteMap[i].length;j++) {
						if (noteMap[i][j]!=null && !"".equals(noteMap[i][j])) {
							if (xMidiPart==null) {
								xMidiPart = doc.createElement("midipart");//new XMLElement("midipart-"+i);
								xMidiPart.setAttribute("part",""+i);
							}
							xMidiPart.setAttribute("note_"+j, noteMap[i][j]);  /// NOTE might be a problem herre with just using a numeric attribute
						}
					}
					if (xMidiPart!=null) {xMidiNotes.appendChild(xMidiPart);}
				}
				xtimelines.appendChild(xMidiNotes);
	    	}
	    }
		Iterator ctlDevIter=t.allMidiControlMaps.keySet().iterator();
	    while (ctlDevIter.hasNext()) {
	    	String device=(String )ctlDevIter.next();
	    	HashMap ctlMapsForDevice=(HashMap) t.allMidiControlMaps.get(device);
	    	Iterator ctlMapsForDeviceIter=ctlMapsForDevice.keySet().iterator();
	    	while (ctlMapsForDeviceIter.hasNext()) {
	    		String id=(String)ctlMapsForDeviceIter.next();
	    		HashMap midiControlMap=(HashMap)ctlMapsForDevice.get(id);
				Element xMidiControls = doc.createElement("midicontrols");
				xMidiControls.setAttribute("device",device);
				xMidiControls.setAttribute("id",id);
				if (t.currentMidiControlMaps.containsValue(midiControlMap)) {xMidiControls.setAttribute("default","true");}
				if (midiControlMap!=null) {
					Iterator ctlIter=midiControlMap.keySet().iterator();
					while (ctlIter.hasNext()) {
						String key=(String)ctlIter.next();
						Vector v=(Vector)midiControlMap.get(key);
						Element xMidiControl =  doc.createElement("midicontrol");
						xMidiControl.setAttribute("size",""+v.size());
						for (int i=0;i<v.size();i++) {
							ControlSettings c=(ControlSettings)v.get(i);
							xMidiControl.setAttribute("ctlstring_"+i,c.toString());
						}
						xMidiControls.appendChild(xMidiControl);
					}
				}
				xtimelines.appendChild(xMidiControls);
		    }
		}
		for (int i=0;i<t.timeLines.size();i++) {
				TimeLineObject timeLineObject=(TimeLineObject)t.timeLines.get(i);
				Element xtimeline = timeLineObject.asDOMXmlElement(doc);
				xtimelines.appendChild(xtimeline);
		}
		for (int i=0;i<t.timeLineSets.size();i++) {
			TimeLineSet timeLineSet = (TimeLineSet)t.timeLineSets.get(i);
			Element xSet = doc.createElement("set");
			xSet.setAttribute( "list", timeLineSet.getList());
			xSet.setAttribute( "id", timeLineSet.getId());
			xtimelines.appendChild(xSet);
		}
		// write view setings.
		Element xView = doc.createElement("view");
		TimeLineSet undoSet = new TimeLineSet(t);
		undoSet.addAll(t.timeLineSelection);
		String visList = undoSet.getList();
		xView.setAttribute("visible", visList);
		xtimelines.appendChild(xView);
		// save interface file ref.
		if ( t.interfaceFile!=null) {
			Element xInterface = doc.createElement("interface");
			xInterface.setAttribute("path", t.interfaceFile);
			xtimelines.appendChild(xInterface);
		}
		xu.writeXmlFile(doc, t.dataFilePath+fileName);
		TimeLine.println("saved!!");
		t.showMessage("saved: "+fileName,LogWindow.TYPE_MSG);
		if (fileName.indexOf("undo")==-1) {t.markDirty(false);}
	}
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	parseMidiControlMaps()
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void parseMidiControlMaps() {
		String fileNameTest = t.mapFilePath;
		System.out.println(fileNameTest);
		File f=new File(fileNameTest);
		if (!f.exists()) {t.showMessage("file doesnt exist :"+fileNameTest);return;} 
		try {
			Document doc=xu.parseXmlFile(fileNameTest,false);
			NodeList xControlConfig=doc.getDocumentElement().getElementsByTagName("device");
			for (int  i=0;i<xControlConfig.getLength();i++) {
				Element xDevice = (Element) xControlConfig.item(i);
				try {
					MIDIDeviceCfgBean mdcb=new MIDIDeviceCfgBean(xDevice.getAttribute("timeLineId"));
					try {mdcb.setMidiId(xDevice.getAttribute("midiId"));} catch(RuntimeException e) {}
					try {mdcb.setImagePath(xDevice.getAttribute("image"));} catch(RuntimeException e) {}
					NodeList controlElements=xDevice.getElementsByTagName("control");
					for (int  j=0;j<controlElements.getLength();j++) {
						Element xControl = (Element)controlElements.item(j); 
						MIDIControlCfgBean mccb=new MIDIControlCfgBean();
						try {mccb.setControlNum(Integer.parseInt(xControl.getAttribute("num")));} catch(RuntimeException e) {}
						try {mccb.setControlText(xControl.getAttribute("desc"));} catch(RuntimeException e) {}
						try {mccb.setTop(Integer.parseInt(xControl.getAttribute("top")));} catch(RuntimeException e) {}
						try {mccb.setLeft(Integer.parseInt(xControl.getAttribute("left")));} catch(RuntimeException e) {}
						try {mccb.setWidth(Integer.parseInt(xControl.getAttribute("width")));} catch(RuntimeException e) {}
						try {mccb.setHeight(Integer.parseInt(xControl.getAttribute("height")));} catch(RuntimeException e) {}
						try {mccb.setType(xControl.getAttribute("type"));} catch(RuntimeException e) {}
						mdcb.addControl(mccb);
					}
					NodeList noteElements=xDevice.getElementsByTagName("notes");
					for (int  j=0;j<noteElements.getLength();j++) {
						Element xNote = (Element)noteElements.item(j); 
						MIDINoteCfgBean mncb=new MIDINoteCfgBean();
						try {mncb.setTop(Integer.parseInt(xNote.getAttribute("top")));} catch(RuntimeException e) {}
						try { mncb.setLeft(Integer.parseInt(xNote.getAttribute("left")));} catch(RuntimeException e) {}
						try {mncb.setWidth(Integer.parseInt(xNote.getAttribute("width")));} catch(RuntimeException e) {}
						try {mncb.setHeight(Integer.parseInt(xNote.getAttribute("height")));} catch(RuntimeException e) {}
						try {mncb.setOctaves(Integer.parseInt(xNote.getAttribute("octaves")));} catch(RuntimeException e) {}
						try {mncb.setBlackKeyWidth(Integer.parseInt(xNote.getAttribute("blackKeyWidth")));} catch(RuntimeException e) {}
						try {mncb.setBlackKeyHeight(Integer.parseInt(xNote.getAttribute("blackKeyHeight")));} catch(RuntimeException e) {}
						mdcb.setMidiNoteCfgBean(mncb);
					}
					t.midiDeviceConfigMaps.put(xDevice.getAttribute("timeLineId"),mdcb);
				} catch(RuntimeException e) {}
			}
		} catch (Exception e) {
			System.out.println("Couldnt load midi maps.");
		}
	}
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	parseConfig()
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void parseConfig() {
		String fileNameTest= t.configFilePath;
		File f=new File(fileNameTest);
		System.out.println(fileNameTest);
		
		if (!f.exists()) {t.showMessage("file doesnt exist :"+t.fileName);return;} 
		try {
			Document doc=xu.parseXmlFile(fileNameTest,false);
			Element xConfig=doc.getDocumentElement();
			NodeList firstLevelNodes = xConfig.getChildNodes();
			for (int  i=0;i<xConfig.getChildNodes().getLength();i++) {
				if (firstLevelNodes.item(i).getNodeType()!=Node.ELEMENT_NODE) {continue;} 
				Element item = (Element) firstLevelNodes.item(i);
				if (item.getNodeName().equals("hosts")) {
					NodeList hostsElements=item.getElementsByTagName("host");
					int newOcServerPorts[]= new int[hostsElements.getLength()] ;
					String newOscServerHost[]= new String[hostsElements.getLength()];
					int newOscServerRcPorts[]= new int[hostsElements.getLength()] ;
					for (int  j=0;j<hostsElements.getLength();j++) {
						Element host = (Element)hostsElements.item(j);
						newOcServerPorts[j] = Integer.parseInt(host.getAttribute("sndPort")) ;
						newOscServerHost[j] = host.getAttribute("addr");
						newOscServerRcPorts[j]= Integer.parseInt(host.getAttribute("rcvPort")) ;
						System.out.println("host:"+newOscServerHost[j]+" -snd:"+newOcServerPorts[j]+" -rcv:"+newOscServerRcPorts[j]);
					}
					t.oscServerPorts= newOcServerPorts;
					t.oscServerHost= newOscServerHost;
					t.oscServerRcPorts= newOscServerRcPorts;
				}
				if (item.getNodeName().equals("midi")) {
					NodeList midiDeviceElements=item.getElementsByTagName("device");
					String newmidiDevices[]=new String[midiDeviceElements.getLength()];
					
					for (int  j=0;j<midiDeviceElements.getLength();j++) {
						Element midi = (Element)midiDeviceElements.item(j);
						newmidiDevices[j]=midi.getAttribute("id");
						System.out.println("midi device:"+newmidiDevices[j]);
					}
					t.midiDeviceNames=newmidiDevices;
				}
				if (item.getNodeName().equals("midiout")) {
					NodeList midiDeviceElements=item.getElementsByTagName("device");
					String newmidiDevices[]=new String[midiDeviceElements.getLength()];
					
					for (int  j=0;j<midiDeviceElements.getLength();j++) {
						Element midi = (Element)midiDeviceElements.item(j);
						newmidiDevices[j]=midi.getAttribute("id");
						System.out.println("midi device:"+newmidiDevices[j]);
					}
					t.midiOutDeviceNames=newmidiDevices;
				}
				if (item.getNodeName().equals("classpath")) {
					t.additionalClassPath=item.getAttribute("path");
				}
				if (item.getNodeName().equals("datapath")) {
					t.dataFilePath=item.getAttribute("path");
				}
				if (item.getNodeName().equals("mappath")) {
					t.mapFilePath=item.getAttribute("path");
				}
			}
		} catch (RuntimeException e) {
			t.showMessage("Couldn't load config.");
		}
	}
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	saveData()
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void saveData() {
		File home=new File(System.getProperty("user.home"));
		File rc=new File(home,".timelinerc");
		if (!rc.exists()) {	rc.mkdir();	}	
		File dataFile=new File(rc,"data.xml");

		Document doc = xu.createDomDocument();
		Element xdata = doc.createElement("data");
		xdata.setAttribute("showTooltips",""+t.showTooltips);
		doc.appendChild(xdata);
		
		Element xdir = doc.createElement("last_directory");
		xdir.setAttribute("path",t.dataFilePath);
		xdata.appendChild(xdir);
		
		Element xfile = doc.createElement("last_file");
		xfile.setAttribute("name",t.fileName);
		xdata.appendChild(xfile);
		
		Element xwin = doc.createElement("main_window");
		xwin.setAttribute("x",""+TimeLine.thisFrame.getX());
		xwin.setAttribute("y",""+TimeLine.thisFrame.getY());
		xdata.appendChild(xwin);
		xwin = doc.createElement("compile_window");
		xwin.setAttribute("x",""+t.dynCompiler.thisFrame.getX());
		xwin.setAttribute("y",""+t.dynCompiler.thisFrame.getY());
		xdata.appendChild(xwin);
		xwin = doc.createElement("midi_window");
		xwin.setAttribute("x",""+t.midiWin.thisFrame.getX());
		xwin.setAttribute("y",""+t.midiWin.thisFrame.getY());
		xdata.appendChild(xwin);
		xwin = doc.createElement("debug_window");
		xwin.setAttribute("x",""+t.debugWindow.thisFrame.getX());
		xwin.setAttribute("y",""+t.debugWindow.thisFrame.getY());
		xdata.appendChild(xwin);
		xu.writeXmlFile(doc, dataFile.getAbsolutePath());
		TimeLine.println("saved data");
	}
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	loadData()
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void loadData() {
		File home=new File(System.getProperty("user.home"));
		File rc=new File(home,".timelinerc");
		if (!rc.exists()) {	rc.mkdir();	}	
		File dataFile=new File(rc,"data.xml");
		if (!dataFile.exists()) {return;}
		
		try {
			Document doc = xu.parseXmlFile(dataFile.getAbsolutePath(),false);
			Element xdata = doc.getDocumentElement();
			t.showTooltips=!"false".equals(xdata.getAttribute("showTooltips"));
			NodeList nodeList = xdata.getChildNodes();
			for (int i=0;i<nodeList.getLength();i++) {
				if (nodeList.item(i).getNodeType()==Node.ELEMENT_NODE) {
					Element e= (Element)nodeList.item(i);
					if (e.getNodeName().equals("last_directory"))  {t.setDataFilePath(e.getAttribute("path"));}
					if (e.getNodeName().equals("last_file"))  {t.setFileName(e.getAttribute("name"));}
					if (e.getNodeName().equals("show"))  {t.setFileName(e.getAttribute("name"));}
					if (e.getNodeName().equals("main_window"))  {
						TimeLine.thisFrame.setLocation(
								Integer.parseInt(e.getAttribute("x")), 
								Integer.parseInt(e.getAttribute("y"))
						);
					}
					if (e.getNodeName().equals("compile_window"))  {
						t.dynCompiler.setLocation(
								new Point(
								Integer.parseInt(e.getAttribute("x")), 
								Integer.parseInt(e.getAttribute("y"))
								)
						);
					}
					if (e.getNodeName().equals("midi_window"))  {
						t.midiWin.setLocation(new Point(
								Integer.parseInt(e.getAttribute("x")), 
								Integer.parseInt(e.getAttribute("y"))
						));
					}
					if (e.getNodeName().equals("debug_window"))  {
						t.debugWindow.setLocation(new Point(
								Integer.parseInt(e.getAttribute("x")), 
								Integer.parseInt(e.getAttribute("y"))
						));
					}
				}
			}
		} catch (Exception e) {
			// skip data load.
		}

		TimeLine.println("loaded data");
	}
}
