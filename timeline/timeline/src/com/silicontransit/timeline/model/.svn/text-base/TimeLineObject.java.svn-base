package com.silicontransit.timeline.model;
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
import java.awt.FontMetrics;
import java.awt.Image;
import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.silicontransit.timeline.TimeLine;
import com.silicontransit.timeline.TimeLineApp;
import com.silicontransit.timeline.disp.DisplayObject;
import com.silicontransit.timeline.util.*;
import com.silicontransit.timeline.window.LogWindow;

import oscbase.OscP5;
import proxml.InvalidAttributeException;
import proxml.XMLElement;

// the timeline model object and its view settings.
public class TimeLineObject extends DisplayObject implements Cueable , PropertySettable{
	
	public static final int BUTTON_NORMAL = 0;
	public static final int BUTTON_CURRENT = 1;
	public static final int BUTTON_SELECTED = 2;
	
	  public String id=""+this.hashCode();
	  public int currentEvent=0;
	  public int timeLineLength=8000;

	  public int displayStart=0; // start time in ms
	  public int displayEnd=8000; // end time in ms
	  public int setHeight=100; // hieght in pixels
	  public int dispHeight=100; // hieght in pixels
	  
	  public int timeSelStart=-1;
	  public int timeSelEnd=-1;
	  public String followOnExpr="";
	  public int lastSelEvent=0;
	  public int nextEvent=0;// last event played.
	  public long lastTime=0; // the last actual world time
	  public long currentTime=0; // the actual world time
	  public int pos=-1; // this position in timeline
	  public int lpos=-1; // last position in timeline
	 
	  // variable for fractional pitch correction.
	  public long startTime =0;
	  public long startPos =0;
	  
	  public float pitch=1.0f;
	  
	  public String playMode="";
	  
	  public Vector timeLine=new Vector();  
	  public Vector selection=new Vector();  
	  public long[] timeArray=new long[100];
	  
	  public int oscIndex=0;
	  
	  public int quantize=50;
	  public int beatLength=10;
	  public int beatPerBar=4;
	  //public boolean showLongMarks = false;
	  public boolean scrollPosToView = false;
	  
	  public boolean typeDisplay = false;// display each event of a different type on its own line
	  public HashMap typeDisplayIndexs = new HashMap(); // a unique set of stiring to use with typeDisplayMode
	  
	  public Vector parameters=new Vector();
	  
	  public int color[]= {255,0,0};
	  TimeLine timLineApplet=null;
	  
	  public HashMap groups= new HashMap(); 
	  
//	store var from cueable interface.
	 private String cueMode = "";
	 private boolean cueStop =true;
	 private int cueFlash = 0;
	  
	  public TimeLineObject (TimeLine t){
	  	 super(50,12,0,0);
		 timLineApplet=t;
	  }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	  playEvents() : 
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	  boolean playLock = false; // prevent further timer pulses executing the thread.
	  public Vector playEvents() {
		  	if (playLock) {return new Vector();}
		  	playLock=true;
		    Vector returnEvents=new Vector();
		    if (!this.playMode.equals("") ) {
		    	//checkExecFiollowOn();
		    	if (this.pos>=this.timeLineLength) {
			        if (this.playMode.equals("p") || this.playMode.equals("r")) {
			        	String oldPlaymode=this.playMode;
			        	this.setPlayMode("");
			        	this.pos=-1;
			        	this.nextEvent=0;
			        	this.lpos=-1;  
			        	if (oldPlaymode.equals("p") && (!"".equals(this.followOnExpr)) && timLineApplet.cue.checkQue(this).size()==0) {
			        		//long st=System.nanoTime();
			        		this.timLineApplet.exprUtil.setThis(this,null);
			        		this.timLineApplet.exprUtil.setValueExpr(this.followOnExpr,"");
			        		this.timLineApplet.exprUtil.clearThis();
			        		///LogWindow.log("execTime:"+(System.nanoTime()-st), LogWindow.TYPE_MSG);
			        		playLock=false;
			        		return returnEvents;
			        	}
			        }
			        else if (this.playMode.equals("l")) {
			        	//this.pos=-1;
			        	this.pos=0;
			        	this.nextEvent=0;
			        	 // test timline bug.
			        	this.currentTime=(long)System.currentTimeMillis();
						this.lpos=-1;  
						this.pos+=(this.currentTime-this.lastTime)*pitch;
						this.startPos=0;
						this.startTime=this.currentTime;
			        }
		      }
		      else {
			    	 this.currentTime=(long)System.currentTimeMillis();
					 this.lpos=this.pos;  
					 if (pos==-1) {pos=0;}// reset pos if only initialised
					 this.pos+=(this.currentTime-this.lastTime)*pitch;
					 int realPos = (int) ((this.currentTime-this.startTime)*pitch - this.startPos);
					 //System.out.println("(("+this.currentTime+"-"+this.startTime+")*"+pitch+" - "+this.startPos+") = "+realPos +" --- "+this.pos);
					 if (this.pos<realPos) {this.pos=realPos;}
					 
		      }
		      if ( this.timeLine.size()>0 && !this.playMode.equals("r") ) {
		    	  int eventIndex=this.nextEvent;
		    	  //System.out.println("---: pos:"+this.pos+": lpos:"+this.lpos+": evtIdx:"+eventIndex+": timeArr[evi]:"+((eventIndex<this.timeArray.length) ?(""+this.timeArray[eventIndex]):"n/a"));
			      while ( (eventIndex<this.timeArray.length) && this.timeArray[eventIndex]<=this.pos && this.timeArray[eventIndex]>this.lpos   ) {
			      //for (;  ((eventIndex<this.timeArray.length)&&(this.timeArray[eventIndex]<=this.pos));  eventIndex++) {
			    	  Event event=(Event)this.timeLine.get(eventIndex);
	            	  //System.out.println(event.oscMsgName+": evt:"+event.eventTime+": pos:"+this.pos+": lpos:"+this.lpos+": evtIdx:"+eventIndex+": timeArr[evi]:"+this.timeArray[eventIndex]);
			         // System.out.println("cond:"+(this.timeArray[eventIndex]>this.lpos || this.lpos<=0));
		    	  
		              //if (this.timeArray[eventIndex]>this.lpos || this.lpos<=0) {
		            	  //Event event=(Event)this.timeLine.get(eventIndex);
		            	  if (event.active) {
			                	playEvent(event);
				                if (event.target!=null) {
				                	System.out.println("queing target:" + event.target.id);
				                	returnEvents.add( event ); 
				                }
			                }
		            	  eventIndex++;
		            	  //System.out.println("evi: "+eventIndex);
		            	  //this.nextEvent=eventIndex;
		              //}
			      }
			      //System.out.println(eventIndex);
			      this.nextEvent=eventIndex;
		      }
		      this.lastTime=this.currentTime;
		      this.lpos=this.pos;
		    }
		    playLock=false;
		    return returnEvents;
	  }
	  
	  public boolean checkExecFollowOn() {
		  if ((this.pos>=this.timeLineLength-1) &&this.playMode.equals("p") && (!"".equals(this.followOnExpr)) && timLineApplet.cue.checkQue(this).size()==0) {
	    	  this.playMode="";
	    	  this.timLineApplet.exprUtil.setThis(this,null);
			  this.timLineApplet.exprUtil.setValueExpr(this.followOnExpr,"");
			  this.timLineApplet.exprUtil.clearThis();
	    	  return true;
		  }
		  return false;
	  }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	  setPlayMode(String mode) 
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	//	  
	  public void setPlayMode(String mode) {
	    this.playMode=mode;
	    if (this.playMode.equals("")) {
	    	this.lastTime=0;
	    } else {
	    	this.lastTime = (long)System.currentTimeMillis();
	    	this.startTime = this.lastTime;
	    	this.startPos = (this.pos<0)?0:this.pos;
	    }
	  }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	  playEvent(Event e)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	//
	public void setBeats(String bts) {
		String inputSplit[]=bts.split("-");
		if (inputSplit.length>0) {try{beatLength=Integer.parseInt(inputSplit[0]);}catch (NumberFormatException n) {}}
		if (inputSplit.length>1) {try{beatPerBar=Integer.parseInt(inputSplit[1]);}catch (NumberFormatException n) {}}
	}
	  
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	  playEvent(Event e)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	//
	public   void playEvent(Event e){
		  //println("fire event:"+this.id +" : "+e.oscMsgName+" : "+e.intVal+":"+e.strValue);
		//this.simpleOscMessage(e);  
		//this.timlineApplet.oscUtil.simpleOscMessage(e,e.oscIndex);
		this.timLineApplet.exprUtil.setThis(this, e);
		this.timLineApplet.oscUtil.simpleOscMessage(e);
		this.timLineApplet.exprUtil.clearThis();
	  }
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	  getCopy: copies timeline.
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	//
	public TimeLineObject getCopy() {
		TimeLineObject t=new TimeLineObject(this.timLineApplet);//this.oscP5
		for (int i=0;i<this.timeLine.size();i++) {
			t.timeLine.add(((Event)this.timeLine.get(i)).getCopy(null));
		}
		t.timeLineLength=this.timeLineLength;
		t.displayStart=this.displayStart;
		t.displayEnd=this.displayEnd;
		t.setHeight=this.setHeight;
		t.quantize=this.quantize;
		t.beatLength=this.beatLength;
		t.beatPerBar=this.beatPerBar;
		t.id=this.id;
		t.oscIndex=this.oscIndex;
		t.color[0]=this.color[0];
		t.color[1]=this.color[1];
		t.color[2]=this.color[2];
		t.rebuildTimeLine();
		return t;
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  rebuildTimeLine(): sort timeLine and and make long array of times.
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  public  void rebuildTimeLine() {
	  Collections.sort(this.timeLine,new TimeSorter());
	  if (this.timeLine.size()>0) {
	    int i=0;
	    this.timeArray=new long[this.timeLine.size()];
		typeDisplayIndexs = new HashMap();
	    //for (i=0;i<Math.min(this.timeLine.size(),100);i++) {
		for (i=0; i<this.timeLine.size(); i++) {
	    	Event event = (Event)this.timeLine.get(i);
			this.timeArray[i]=(event).eventTime;
			if ( typeDisplayIndexs.get(event.oscMsgName) == null ) {typeDisplayIndexs.put(event.oscMsgName, new Integer( typeDisplayIndexs.size()));}
	    }
	  }
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  sortSelection(): sorts the selection by tiime.
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	 public void sortSelection() {
		 Collections.sort(this.selection,new TimeSorter());
	 }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	  sortSelection(): sorts the selection by tiime.
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 
	public int[] getColor() {	return color;}
	public String getColorStr() {	return color[0]+" "+color[1]+" "+color[2];}
	public void setColor(int[] color) {	this.color = color;}
	
	public void setColor(int r,int g,int b) {
		int col[]= {r,g,b} ;
		this.color = col;
	}
	
	public void setColor(String colStr) {
		String[] s= colStr.split(" ");
		try {
			int col[]= {Integer.parseInt(s[0]),Integer.parseInt(s[1]),Integer.parseInt(s[2])} ;
			setColor(col);
		} catch (NumberFormatException e) {
			System.out.println("bad color: "+colStr+" : "+e.getMessage());
		}
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	  parseTimeLine(): parse timeline from XML Element.
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public  void parseTimeLine(XMLElement timelineEl, OscP5[] oscServers) {//
		int oscIndex=0;
		try {oscIndex  = timelineEl.getIntAttribute("oscIndex");} catch(InvalidAttributeException e) {this.oscIndex=0;}
		this.oscIndex=oscIndex;
		this.timeLine.clear();
		try {this.timeLineLength = timelineEl.getIntAttribute("timeLineLength");} catch(InvalidAttributeException e) {}
		try {this.currentEvent = timelineEl.getIntAttribute("currentEvent");} catch(InvalidAttributeException e) {}
		try {this.displayStart = timelineEl.getIntAttribute("displayStart");} catch(InvalidAttributeException e) {}
		try {this.displayEnd= timelineEl.getIntAttribute("displayEnd");} catch(InvalidAttributeException e) {}
		try {this.setHeight= timelineEl.getIntAttribute("height");} catch(InvalidAttributeException e) {}
		try {this.lastSelEvent  = timelineEl.getIntAttribute("lastSelEvent");} catch(InvalidAttributeException e) {}
		try {this.quantize  = timelineEl.getIntAttribute("quantize");} catch(InvalidAttributeException e) {}
		try {this.beatLength  = timelineEl.getIntAttribute("beatLength");} catch(InvalidAttributeException e) {}
		try {this.beatPerBar  = timelineEl.getIntAttribute("beatPerBar");} catch(InvalidAttributeException e) {}
		try {this.id  = timelineEl.getAttribute("id");} catch(InvalidAttributeException e) {}
		try {this.playMode = timelineEl.getAttribute("playMode");} catch(InvalidAttributeException e) {}
		try {this.setColor( timelineEl.getAttribute("color"));} catch(InvalidAttributeException e) {}
		try {this.pitch= timelineEl.getFloatAttribute("pitch");} catch(InvalidAttributeException e) {}
		try {this.followOnExpr= timelineEl.getAttribute("followOnExpr");} catch(InvalidAttributeException e) {}
		 // import event data.
		 for(int i = 0; i < timelineEl.countChildren();i++){
		    XMLElement childXML = timelineEl.getChild(i);
		    if ("event".equals(childXML.getElement())) {
			    Event e= new Event();
			    e.importData(childXML,oscServers);
			    if (e.oscP5==null) {e.oscIndex=this.oscIndex;e.oscP5=oscServers[e.oscIndex];}
			    // add event.
			    this.timeLine.add(e);
		    }
		 }		    
		for(int i = 0; i < timelineEl.countChildren();i++){
			XMLElement childXML = timelineEl.getChild(i);
			if ("group".equals(childXML.getElement())) {
				String[] s=childXML.getAttribute("eventIndexes").split(",");
				Vector group=new Vector();
				for (int j=0;j<s.length;j++) {
					Event e=(Event)timeLine.get(Integer.parseInt(s[j]));
					group.add(e);
				}
				String groupName=childXML.getAttribute("id");
				groups.put(groupName,group);
			}
		 }		    
		 this.rebuildTimeLine();
	}
	
	public  void parseTimeLineDOM(Element timelineEl, OscP5[] oscServers) {//
			//int oscIndex=0;
			 try {	this.oscIndex  = Integer.parseInt( timelineEl.getAttribute("oscIndex") );	} catch (RuntimeException e) {	this.oscIndex=0;	}
			 ///this.oscIndex=oscIndex;
			 this.timeLine.clear();
			 try {this.timeLineLength =Integer.parseInt( timelineEl.getAttribute("timeLineLength"));} catch(RuntimeException e) {}
			 try {this.currentEvent =Integer.parseInt( timelineEl.getAttribute("currentEvent"));} catch(RuntimeException e) {}
			 try {this.displayStart =Integer.parseInt( timelineEl.getAttribute("displayStart"));} catch(RuntimeException e) {}
			 try {this.displayEnd= Integer.parseInt(timelineEl.getAttribute("displayEnd"));} catch(RuntimeException e) {}
			 try {this.setHeight = Integer.parseInt(timelineEl.getAttribute("height"));} catch(RuntimeException e) {}
			 try {this.typeDisplay = Boolean.parseBoolean(timelineEl.getAttribute("typeDisplay"));} catch(RuntimeException e) {}
			 try {this.lastSelEvent  = Integer.parseInt(timelineEl.getAttribute("lastSelEvent"));} catch(RuntimeException e) {}
			 try {this.beatLength  = Integer.parseInt(timelineEl.getAttribute("beatLength"));} catch(RuntimeException e) {}
			 try {this.quantize  = Integer.parseInt(timelineEl.getAttribute("quantize"));} catch(RuntimeException e) {}
			 try {this.beatPerBar  =Integer.parseInt( timelineEl.getAttribute("beatPerBar"));} catch(RuntimeException e) {}
			 try {this.id  = timelineEl.getAttribute("id");} catch(RuntimeException e) {}
			 try {this.playMode = timelineEl.getAttribute("playMode");} catch(RuntimeException e) {}
			 try {this.setColor( timelineEl.getAttribute("color"));} catch(RuntimeException e) {}
			 try {this.pitch= Float.parseFloat(timelineEl.getAttribute("pitch"));} catch(RuntimeException e) {}
			 try {this.followOnExpr= timelineEl.getAttribute("followOnExpr");} catch(RuntimeException e) {}
			 // import event data.
			NodeList eventElements = timelineEl.getElementsByTagName("event");
						
			 for(int i = 0; i < eventElements.getLength();i++){
				Element childXML = (Element) eventElements.item(i);
				//if ("event".equals(childXML.getElement())) {
				Event e= new Event();
				e.importDataDOM(childXML,oscServers);
				if (e.oscP5==null) {e.oscIndex=this.oscIndex;e.oscP5=oscServers[e.oscIndex];}
				// add event.
				this.timeLine.add(e);
				//}
			 }		    
			 
			NodeList groupElements = timelineEl.getElementsByTagName("group");
			for(int i = 0; i < groupElements.getLength();i++){
				Element childXML =(Element) groupElements.item(i);
				//if ("group".equals(childXML.getElement())) {
					String[] s=childXML.getAttribute("eventIndexes").split(",");
					Vector group=new Vector();
					for (int j=0;j<s.length;j++) {
						Event e=(Event)timeLine.get(Integer.parseInt(s[j]));
						group.add(e);
					}
					String groupName=childXML.getAttribute("id");
					groups.put(groupName,group);
				//}
			 }		    
			 this.rebuildTimeLine();
		}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	  asXmlElement(): make XML element from timeline 
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
	public XMLElement asXmlElement() {
		XMLElement xtimeline =new XMLElement("timeline");
		xtimeline.addAttribute("timeLineLength",this.timeLineLength);
		xtimeline.addAttribute("currentEvent",this.currentEvent);
		xtimeline.addAttribute("displayStart",this.displayStart);
		xtimeline.addAttribute("displayEnd",this.displayEnd);
		xtimeline.addAttribute("height",this.setHeight);
		xtimeline.addAttribute("lastSelEvent",this.lastSelEvent);
		xtimeline.addAttribute("quantize",this.quantize);
		xtimeline.addAttribute("oscIndex",this.oscIndex);
		xtimeline.addAttribute("beatLength",this.beatLength);
		xtimeline.addAttribute("beatPerBar",this.beatPerBar);
		xtimeline.addAttribute("oscIndex",this.oscIndex);
		xtimeline.addAttribute("id",this.id);
		xtimeline.addAttribute("playMode",this.playMode);
		xtimeline.addAttribute("color",this.getColorStr()); 
		xtimeline.addAttribute("pitch",this.pitch); 
		xtimeline.addAttribute("followOnExpr",this.followOnExpr); 
		for (int evtIdx=0;evtIdx<this.timeLine.size();evtIdx++) {
		      Event e=(Event)this.timeLine.get(evtIdx);
		      xtimeline.addChild(e.asXMLElement());
		}
		// save groups.
		Iterator i=groups.keySet().iterator();
		while (i.hasNext()) {
			String groupName=(String)i.next();
			Vector group=(Vector) groups.get(groupName);
			XMLElement xgroup = new XMLElement("group");
			xgroup.addAttribute("id",groupName);
			String groupIndexes = "";
			for (int j=0;j<group.size();j++) {
				groupIndexes+=timeLine.indexOf(group.get(j));
				if (j<group.size()-1) groupIndexes+=",";
			}
			xgroup.addAttribute("eventIndexes",groupIndexes);
			xtimeline.addChild(xgroup);
		}
		return xtimeline;
	}
	public Element asDOMXmlElement(Document doc) {
		
			//XMLElement xtimeline =new XMLElement("timeline");
			Element xtimeline = doc.createElement("timeline");
			xtimeline.setAttribute("timeLineLength",""+this.timeLineLength);
			xtimeline.setAttribute("currentEvent",""+this.currentEvent);
			xtimeline.setAttribute("displayStart",""+this.displayStart);
			xtimeline.setAttribute("displayEnd",""+this.displayEnd);
			xtimeline.setAttribute("height",""+this.setHeight);
			xtimeline.setAttribute("typeDisplay",""+this.typeDisplay);
			xtimeline.setAttribute("lastSelEvent",""+this.lastSelEvent);
			xtimeline.setAttribute("oscIndex",""+this.oscIndex);
			xtimeline.setAttribute("beatLength",""+this.beatLength);
			xtimeline.setAttribute("quantize",""+this.quantize);
			xtimeline.setAttribute("beatPerBar",""+this.beatPerBar);
			xtimeline.setAttribute("oscIndex",""+this.oscIndex);
			xtimeline.setAttribute("id",this.id);
			xtimeline.setAttribute("playMode",this.playMode);
			xtimeline.setAttribute("color",this.getColorStr()); 
			xtimeline.setAttribute("pitch",""+this.pitch); 
			xtimeline.setAttribute("followOnExpr",this.followOnExpr); 
			for (int evtIdx=0;evtIdx<this.timeLine.size();evtIdx++) {
				  Event e=(Event)this.timeLine.get(evtIdx);
				  //xtimeline.addChild(e.asXMLElement());
					xtimeline.appendChild(e.asDOMXMLElement( doc));
			}
			// save groups.
			Iterator i=groups.keySet().iterator();
			while (i.hasNext()) {
				String groupName=(String)i.next();
				Vector group=(Vector) groups.get(groupName);
				//XMLElement xgroup = new XMLElement("group");
				Element xgroup = doc.createElement("group");
				xgroup.setAttribute("id",groupName);
				String groupIndexes = "";
				for (int j=0;j<group.size();j++) {
					groupIndexes+=timeLine.indexOf(group.get(j));
					if (j<group.size()-1) groupIndexes+=",";
				}
				xgroup.setAttribute("eventIndexes",groupIndexes);
				xtimeline.appendChild(xgroup);
			}
			return xtimeline;
		}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	  asXmlElement(): make XML element from timeline 
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		

//	public void playNextEvent() {
//		Event e=(Event)this.timeLine.get(this.nextEvent);
//		while(!e.active) {		
//			this.nextEvent=(++nextEvent)%this.timeLine.size();
//			e=(Event)this.timeLine.get(this.nextEvent);
//		}
//		long thisTime=e.eventTime;
//		while (thisTime==e.eventTime) {
//			//this.simpleOscMessage(e);
//			this.timLineApplet.oscUtil.simpleOscMessage(e);
//			this.nextEvent=(++nextEvent)%this.timeLine.size();
//			e=(Event)this.timeLine.get(this.nextEvent);
//		}
//	}
	
	public void setParameters(Object value, int i) {
		while (parameters.size()<=i) {	parameters.add("");	}
		parameters.set(i,value);
	}
	
	public void setPosAndNextEvent(int pos) {
		this.pos=pos;
		this.lpos=pos-2;
		for (int i=0;i<this.timeLine.size();i++) {
			Event e=(Event)this.timeLine.get(i);
			if (e.eventTime>=this.pos) {this.nextEvent=i;break;}
		}
		//LogWindow.log("nextEvent:"+this.nextEvent,LogWindow.TYPE_WARN);
	}
	
	public Image getButtonImage( int type, boolean inCurrentSet) {
			clearImage();
			g2.setColor(new Color(this.color[0], this.color[1], this.color[2]));
			g2.drawRect(0,0,this.width-1,this.height-1);
			g2.drawLine(12,0,12,this.height-1);
			g2.drawLine(35,0,35,this.height-1);
			switch (type) {
				case BUTTON_NORMAL: g2.setColor(new Color(this.color[0], this.color[1], this.color[2]));break;
				case BUTTON_CURRENT: g2.setColor(Color.white);break;
				case BUTTON_SELECTED: g2.setColor(Color.lightGray);break;
			}
			g2.drawLine(0,0,this.width,0);
			
			if (!playMode.equals("")) {
				if (playMode.equals("p")) {g2.setColor(Color.green);}
				else if (playMode.equals("l")) {g2.setColor(Color.orange);}
				else if (playMode.equals("r")) { g2.setColor(Color.red); }
				g2.fillRect(1,1,10,this.height-2);
			} 
			// need to modify checkQued to get qued playmode and change square colour
			 if (timLineApplet.cue.checkQued(this)) {
			 	this.cueFlash++;
			 	if (this.cueFlash%4>2) {//this flashes the button 
				 	if ("p".equals(this.cueMode)) {		g2.setColor(Color.green); 	}
					else if ("l".equals(this.cueMode)) {		g2.setColor(Color.orange); 	}
					else {	g2.setColor(Color.gray); } 
					g2.fillRect(1,1,10,this.height-2);
					g2.setColor(Color.black);
					g2.fillRect(4,4,2,2);
			 	}
			}
			
			 if (inCurrentSet) {
				g2.setColor(Color.gray);
				g2.fillRect(36, 1, 13, 10);
			}
			// draw indicator to show time lhas groups.
			 if (groups.size()>0) {
				g2.setColor(Color.gray);
				g2.fillRect(13, 1, 21, 10);
			}
			g2.setColor(Color.white);
			String text = this.id;
			FontMetrics metrics = g2.getFontMetrics(thisFont);
			int width = metrics.stringWidth( text );
			int height = metrics.getHeight();
			g2.drawString( text, 3, height-3 );
			
			if (!"".equals(playMode)) {// tl pos progress bar
				 g2.setColor(new Color(255,255,0)); 
				 g2.drawLine(0, 1, Math.round(( (float)pos / (float)timeLineLength)*50),1);
		  	} 
			return this.img;
		
	}
	public int getTimeInFrame(long time1,int scrWidth) {
		int displayPos=(int)(time1-this.displayStart);
		int len=this.displayEnd-this.displayStart;
		int posx=displayPos*scrWidth/len;
		return posx;
	 }
	public int getBeatLength() {		return beatLength;	}
	public int getBeatPerBar() {		return beatPerBar;	}
	public int getCurrentEvent() {		return currentEvent;	}
	public int getDisplayEnd() {		return displayEnd;	}
	public int getDisplayStart() {		return displayStart;	}
	public String getId() {		return id;	}
	public int getLastSelEvent() {		return lastSelEvent;	}
	public int getOscIndex() {		return oscIndex;	}
	public String getPlayMode() {		return playMode;	}
	public long getPos() {		return pos;	}
	public int getQuantize() {		return quantize;	}
	public Vector getSelection() {		return selection;	}
	//public boolean isShowLongMarks() {		return showLongMarks;	}
	public int getTimeLineLength() {		return timeLineLength;	}
	public int getTimeSelEnd() {		return timeSelEnd;	}
	public int getTimeSelStart() {		return timeSelStart;	}
	public void setBeatLength(int i) {		beatLength = i;	}
	public void setBeatPerBar(int i) {		beatPerBar = i;	}
	public void setCurrentEvent(int i) {		currentEvent = i;	}
	public void setDisplayEnd(int i) {		displayEnd = i;	}
	public void setDisplayStart(int i) {		displayStart = i;	}
	public void setId(String string) {		id = string;	clearCache();}
	public void setLastSelEvent(int i) {		lastSelEvent = i;	}
	public void setOscIndex(int i) {		oscIndex = i;	}
	public void setPos(int l) {		pos = l;	}
	public void setQuantize(int i) {		quantize = i;	}
	public void setSelection(Vector vector) {		selection = vector;	}
	//public void setShowLongMarks(boolean b) {		showLongMarks = b;	}
	public void setTimeLineLength(int i) {		timeLineLength = i;	}
	public void setTimeSelEnd(int i) {		timeSelEnd = i;	}
	public void setTimeSelStart(int i) {		timeSelStart = i;	}
	public int getDisplayHeight() {	return setHeight;}	
	public String getFollowOnExpr() {	return followOnExpr;}
	public HashMap getGroups() {		return groups;	}
	public Vector getParameters() {		return parameters;	}
	public float getPitch() {		return pitch;	}
	public boolean isScrollPosToView() {		return scrollPosToView;	}
	public Vector getTimeLine() {		return timeLine;	}
	public void setDisplayHeight(int i) {		setHeight = i;	}
	public void setFollowOnExpr(String string) {		followOnExpr = string;	}
	public void setGroups(HashMap map) {		groups = map;	}
	public void setParameters(Vector vector) {		parameters = vector;	}
	public void setPitch(float f) {		pitch = f;	}
	public void setScrollPosToView(boolean b) {		scrollPosToView = b;	}
	public void setTimeLine(Vector vector) {		timeLine = vector;	}
	
	public Color getAwtColor() {
		return new Color(color[0],color[1],color[2]);
	}
	
	public void addGroup(String inputStr) {
		if (selection.size()>0) {
			Vector v = new Vector(selection);
			groups.put(inputStr,v);
		}
	}
	
	public void removeFromGroups(Event event) {
		Iterator iter=groups.keySet().iterator();
		Vector removeVector=new Vector();
		while(iter.hasNext()) {
			String groupName=(String)iter.next();
			Vector v= (Vector)groups.get(groupName);
			v.remove(event);
			if (v.size()==0) {removeVector.add(groupName);}
		}
		for (int i=0;i<removeVector.size();i++) {
			groups.remove(removeVector.get(i));
		}
	}
	
	public void removeGroup(String string) {
		groups.remove(string);
	}
	
	public void normaliseOnPitch() {
		this.timeLineLength=Math.round(this.timeLineLength/this.pitch);
		this.pos=Math.round(this.pos/this.pitch);
		this.lpos=Math.round(this.lpos/this.pitch);
		this.timeSelStart=Math.round(this.timeSelStart/this.pitch);
		this.timeSelEnd=Math.round(this.timeSelEnd/this.pitch);
		this.displayEnd=Math.round(this.displayEnd/this.pitch);
		this.displayStart=Math.round(this.displayStart/this.pitch);
		this.quantize=Math.round(this.quantize/this.pitch);
		
		for (int i=0;i<timeLine.size();i++) {
			Event e=(Event)timeLine.get(i);
			e.eventTime=Math.round(e.eventTime/this.pitch);
		}
		this.pitch=1.0f;
		rebuildTimeLine();
	}

	public Event getEvent(String s) {
		for (int i=0;i<timeLine.size();i++) {
			Event e= (Event)timeLine.get(i);
			if (e.id.equals(s)) {return e;}
		}
		return null;
	}
	
	public String toString() {
		return this.id;
	}
	
	public Event getLastEvent() {
		if (timeLine.size()==0){return null;}
		Event e=null;
		try {
			e=((Event)timeLine.get(lastSelEvent));
		} catch(ArrayIndexOutOfBoundsException arr) {return null;}
		return e;
	}
	
	public String getCueMode() {	return cueMode;}
	
	public void setCueMode(String cueMode) {		this.cueMode=cueMode;}
	
	public boolean getStop() {
		return cueStop;
	}
	
	public void setStop(boolean cueStop) {
		this.cueStop = cueStop;
	}
	
	public Object getProperty(String id) {
		if ("id".equals(id)) {return this.id;	}
		else if ("timeLineLength".equals(id)) {return this.timeLineLength;	}
		else if ("quantize".equals(id)) {return this.quantize;	}
		else if ("beatLength".equals(id)) {return this.beatLength;	}
		else if ("beatPerBar".equals(id)) {return this.beatPerBar;	}
		//else if ("parameters".equals(id)) {return this.parameters.toString();	}
		else if ("oscIndex".equals(id)) {return this.oscIndex;	}
		else if ("pitch".equals(id)) {return this.pitch;	}
		else if ("colour".equals(id)) {return new Color(this.color[0], this.color[1], this.color[2]);	}
		else if ("followOnExpr".equals(id)) {return this.followOnExpr;	}
		return null;
	}
	
	public void  setProperty(String id, Object value) {
		if ("id".equals(id)) { this.id = (String)value;	}
		else if ("timeLineLength".equals(id)) { 
			if (this.displayStart==0 && this.displayEnd==this.timeLineLength ||  this.displayEnd>(Integer) value ) {
				this.displayStart=0;
				this.displayEnd = (Integer) value;
			}
			this.timeLineLength =(Integer) value;	
			
		}
		else if ("quantize".equals(id)) { this.quantize= (Integer) value;	}
		else if ("beatLength".equals(id)) { this.beatLength= (Integer) value;	}
		else if ("beatPerBar".equals(id)) { this.beatPerBar= (Integer) value;	}
		//else if ("parameters".equals(id)) { this.setp	}
		else if ("oscIndex".equals(id)) { this.oscIndex= (Integer) value;	}
		else if ("pitch".equals(id)) { 
			try {
				this.pitch= Float.parseFloat((String) value);
			} catch (NumberFormatException n) {}
		}
		else if ("colour".equals(id)) { 
			Color c = (Color)value;
			this.color[0] = c.getRed();
			this.color[1]= c.getGreen();
			this.color[2]= c.getBlue();	
		}
		else if ("followOnExpr".equals(id)) { this.followOnExpr=(String) value;	}
	}
	
	public boolean checkBounds(String id,Object value) {
		if ("oscIndex".equals(id)) {
			int testVal = (Integer) value;	// Integer.parseInt((String) value);
			return (testVal>-1) && (testVal<this.timLineApplet.oscServers.length);
		}
		return true;
	}


}
