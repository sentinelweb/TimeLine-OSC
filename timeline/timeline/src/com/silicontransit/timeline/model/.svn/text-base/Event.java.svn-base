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
import java.util.*;



import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oscbase.OscMessage;
import oscbase.OscP5;
import proxml.*;
public class Event implements PropertySettable {
	public static final int FIELD_EVENTTIME=0;
	public static final int FIELD_OSCMSGNAME=1;
	public static final int FIELD_ACTIVE=2;
	public static final int FIELD_VALUE=3;
	public static final int FIELD_TARGET=4;
	public static final int FIELD_TARGETPLAYMODE=5;
	public static final int FIELD_TARGETDATA=6;
	public static final int FIELD_OSCINDEX=7;
	public static final int FIELD_OSCP5=8;
	public static final int FIELD_VALUEINDEX=9;
	public static final int FIELD_ID=10;
	
	public int eventTime=0;
	public String oscMsgName="";
	public String id="";
	public String targetStr="";
	public TimeLineObject target=null;
	public String targetPlayMode="";
	public String targetData="";
	public OscMessage oscMessage=null;
	public Vector value=new Vector();
	public boolean active=true;
	public long lastPlayed=0;
	public int oscIndex=0;
	public OscP5 oscP5;
	// these are for con
	private static  int fwidth=10;
	private static int fheight=10;
	public Vector extraOscMessages= new Vector();
	public Vector extraValues= new Vector();
	
	
	public Event() {}
	
	public String getTargetId() {
		if (target!=null) {return target.id;}
		else return targetStr;
	}
	
	public String getDesc() {
		String s="";
		if (!"".equals(this.oscMsgName)) {
			s=this.oscMsgName;
		} else if (target!=null) {
			s=this.target.id;
		}
		if (s.length()>10) {s=".."+s.substring(s.length()-10,s.length());}
		return s;
	}
	
	public String getValueStr() {
		return getValueStr(this.value);
	}
	
	public static String getValueStr(Vector val) {
		String vals="";
		for (int i =0; i<val.size();i++) {
			vals+=(("".equals(vals))?"":" ")+val.get(i);
		}
		return vals;
	}
	
	public String getValueStrAbbrev() {
		String vals="";
		for (int i =0; i<this.value.size();i++) {
			String s=this.value.get(i).toString();
			if (s.length()>20 && ! (s.charAt(0)=='$')) {
				int slashIndex=s.lastIndexOf("/")+1;
				try {
					if (slashIndex>-1) {
						int slashIndexEnd=slashIndex+17;
						if (slashIndexEnd>s.length()) {slashIndexEnd=s.length(); }
						s=s.substring(slashIndex,slashIndexEnd);
						if (slashIndexEnd==s.length()) {s+="..."+s.substring(s.length()-3,s.length());}
					}
				} catch (StringIndexOutOfBoundsException se) {}
			}
			vals+=(("".equals(vals))?"":" ")+s;
		}
		return vals;
	}
	
	public Vector getValue() {
		if (this.value.size()>0) {return this.value;}
		else {
			Vector v=new Vector();v.add(new Integer(0));
			return v;
		}
	}
	
	public void setValue(String valueStr) {
		this.value = getValueVector(valueStr);
	}

	private Vector getValueVector(String valueStr) {
		Vector val = new Vector();
		this.oscMessage=null;
		if (!"".equals(valueStr)) {
			String newVals[]=valueStr.split(" ");
			for (int i=0;i<newVals.length;i++) {
				val.add(getValueFromStr(newVals[i]));
			}
		}
		return val;
	}
	
	public static synchronized Object getValueFromStr(String s) {
		try {return (Integer.valueOf(s));}
		catch(NumberFormatException n) {// not an int
			try {return (Float.valueOf(s));}
			catch(NumberFormatException n1) {//not a Float either
				return (s);// add as a String.
			}
		}
	}
	
	public XMLElement asXMLElement() {
		XMLElement xe = new XMLElement("event");
		xe.addAttribute("eventTime",this.eventTime);
		xe.addAttribute("id",this.id);
		xe.addAttribute("oscMsgName",this.oscMsgName.trim());
		xe.addAttribute("value",this.getValueStr());
		xe.addAttribute("target",getTargetId());
		xe.addAttribute("targetPlayMode",this.targetPlayMode);
		xe.addAttribute("targetData",this.targetData);
		xe.addAttribute("active",""+this.active);
		xe.addAttribute("oscIndex",""+this.oscIndex);
		return xe;
	}
	
	public Element asDOMXMLElement(Document doc) {
		Element xe = doc.createElement("event");
		xe.setAttribute("eventTime", ""+this.eventTime);
		xe.setAttribute("id", this.id);
		xe.setAttribute("oscMsgName", this.oscMsgName.trim());
		xe.setAttribute("oscMsgNameLen", ""+this.extraOscMessages.size());
		for (int i=1;i<extraOscMessages.size();i++) {
			xe.setAttribute( "oscMsgName"+i,""+ extraOscMessages.get(i));
		}
		xe.setAttribute("value", this.getValueStr());
		xe.setAttribute("valueLen", ""+this.extraValues.size());
		for (int i=1;i<extraValues.size();i++) {
			xe.setAttribute( "value"+i, getValueStr((Vector)extraValues.get(i)));
		}
		xe.setAttribute("target", getTargetId());
		xe.setAttribute("targetPlayMode", this.targetPlayMode);
		xe.setAttribute("targetData", this.targetData);
		xe.setAttribute("active", ""+this.active);
		xe.setAttribute("oscIndex", ""+this.oscIndex);
		return xe;
	}
	
	public void importData(XMLElement eventXML,OscP5[] oscServers){
		try { this.eventTime = Math.round(eventXML.getFloatAttribute("eventTime")); } catch(InvalidAttributeException e) {}
		try { 
			this.oscIndex = eventXML.getIntAttribute("oscIndex"); 
			this.oscP5=oscServers[this.oscIndex];
		} catch(InvalidAttributeException e) {}
		try { this.oscMsgName = eventXML.getAttribute("oscMsgName").trim();} catch(InvalidAttributeException e) {}
		try { this.id = eventXML.getAttribute("id");} catch(InvalidAttributeException e) {}
		//try { this.setValue( eventXML.getAttribute("strValue")); } catch(InvalidAttributeException e) {}
		try { this.setValue(eventXML.getAttribute("value")); } catch(InvalidAttributeException e) {}
		try { this.targetStr = eventXML.getAttribute("target"); } catch(InvalidAttributeException e) {}
		try { this.targetPlayMode = eventXML.getAttribute("targetPlayMode"); } catch(InvalidAttributeException e) {}
		try { this.targetData = eventXML.getAttribute("targetData"); } catch(InvalidAttributeException e) {}
		try { this.active = Boolean.valueOf(eventXML.getAttribute("active")).booleanValue(); } catch(Exception e) {}
	}
	
	public void importDataDOM(Element eventXML,OscP5[] oscServers){
		try { this.id = eventXML.getAttribute("id");} catch(RuntimeException e) {}
		try { this.eventTime = Math.round(Float.parseFloat(eventXML.getAttribute("eventTime"))); } catch(RuntimeException e) {}
		try { 
			this.oscIndex = Integer.parseInt(eventXML.getAttribute("oscIndex")); 
			this.oscP5=oscServers[this.oscIndex];
		} catch(RuntimeException e) {}
		try { this.oscMsgName = eventXML.getAttribute("oscMsgName").trim();} catch(RuntimeException e) {}
		extraOscMessages = new Vector();
		try { 
			int oscMsgLen = Integer.parseInt(eventXML.getAttribute("oscMsgNameLen")); 
			for (int i=0;i<oscMsgLen;i++) {
				try { 
					String v =  eventXML.getAttribute("oscMsgName"+((i>0)?""+i:"")) ;
					if (v!=null  ) {extraOscMessages.add( v.trim());} 
					else {extraOscMessages.add("");}
				} catch(RuntimeException e) {extraOscMessages.add("");}
				//xe.getAttribute( "oscMsgName"+i,""+ extraOscMessages.get(i));
			}
		} catch(RuntimeException e) {}
		try { this.setValue(eventXML.getAttribute("value")); } catch(RuntimeException e) {}
		extraValues = new Vector();
		try { 
			int vLen = Integer.parseInt(eventXML.getAttribute("valueLen")); 
			for (int i=0;i<vLen;i++) {
				try { 
					String v =  eventXML.getAttribute("value"+((i>0)?""+i:"")) ;
					if (v!=null ) {extraValues.add(getValueVector( v.trim()));} 
					else {extraValues.add("");}
				} catch(RuntimeException e) {extraValues.add("");}
				//xe.getAttribute( "oscMsgName"+i,""+ extraOscMessages.get(i));
			}
		} catch(RuntimeException e) {}
		try { this.targetStr = eventXML.getAttribute("target"); } catch(RuntimeException e) {}
		try { this.targetPlayMode = eventXML.getAttribute("targetPlayMode"); } catch(RuntimeException e) {}
		try { this.targetData = eventXML.getAttribute("targetData"); } catch(RuntimeException e) {}
		try { this.active = Boolean.valueOf(eventXML.getAttribute("active")).booleanValue(); } catch(RuntimeException e) {}
	}
	
	public Event getCopy(TimeLineObject tlo){
		Event e=new Event();
		if (!"".equals(this.id) && tlo!=null) {
			// generate a new index.
			int index = this.id.length()-1;
			for (; index > 0 ;index--) {	if (!Character.isDigit(this.id.charAt(index))) {break;	}}
			int thisindex = 0;
			try {
				thisindex = Integer.parseInt(this.id.substring(index + 1));
			} catch (NumberFormatException ex) {	}
			String base = this.id .substring(0,index+1);
			while (tlo.getEvent(base+thisindex)!=null) {thisindex++;}
			e.id=base+thisindex;
		} else {
			e.id = this.id;
		}
		
		e.eventTime=this.eventTime;
		e.oscMsgName=this.oscMsgName;
		e.extraOscMessages=new Vector(this.extraOscMessages);
		e.value=new Vector(this.value);
		e.extraValues=new Vector(this.extraValues);
		e.target=this.target;
		e.active=this.active;
		e.targetPlayMode=this.targetPlayMode;
		e.oscIndex=this.oscIndex;
		e.oscP5=this.oscP5;
		return e;
	}
	
	public void  setField(int field,Object data) {
		switch (field) {
		case FIELD_ACTIVE : this.active=((Boolean)data).booleanValue(); break;
		case FIELD_VALUE: this.setValue((String)data); break;
		case FIELD_OSCMSGNAME: this.oscMsgName=(String)data; this.oscMessage=null; break;
		case FIELD_TARGET: this.target=(TimeLineObject)data;this.targetStr="" ;break;
		case FIELD_TARGETPLAYMODE: this.targetPlayMode=(String)data; break;
		case FIELD_TARGETDATA: this.targetData=(String)data; break;
		case FIELD_OSCINDEX: this.oscIndex=((Integer)data).intValue(); break;
		case FIELD_OSCP5: this.oscP5=((OscP5)data); break;
		case FIELD_VALUEINDEX: try {
				String split[]=((String)data).split(" ");
				this.value.set(Integer.parseInt(split[0]),getValueFromStr(split[1]));
			} catch(Exception e) {}
			break;
		case FIELD_ID: this.id=(String) data; break;
		}
	}

	public boolean checkBounds(String id, Object value) {
		return true;
	}

	public Object getProperty(String id) {
		if ("id".equals(id)) {return this.id;	}
		else if ("eventTime".equals(id)) {return this.eventTime;	}
		else if ("oscMsgName".equals(id)) {
			String messages = this.oscMsgName;// this.oscMsgName;
			for (int i=1;i<extraOscMessages.size();i++) {
				messages+="\n"+extraOscMessages.get(i);
			}
			return messages;	
		}
		else if ("value".equals(id)) {
			String values =  getValueStr();
			for (int i=1;i<extraValues.size();i++) {
				values+="\n"+getValueStr((Vector)extraValues.get(i));
			}
			return values;	
		}
		else if ("targetPlayMode".equals(id)) {return this.targetPlayMode;	}
		else if ("target".equals(id)) {return this.target;	}
		//else if ("parameters".equals(id)) {return this.parameters.toString();	}
		else if ("active".equals(id)) {return this.active;	}
		else if ("oscIndex".equals(id)) {return this.oscIndex;	}
		return null;
	}

	public void setProperty(String id, Object value) {
		if ("id".equals(id)) { this.id = (String)value;	}
		else if ("eventTime".equals(id)) { this.eventTime =(Integer) value;	}
		else if ("oscMsgName".equals(id)) { 
			String[] messages = ((String)value).split("\\n");
			this.oscMsgName = messages[0];
			if (messages.length>0) {this.extraOscMessages.clear();}
			if (messages.length>1) {
				for (int i=0;i<messages.length;i++) {
					this.extraOscMessages.add(messages[i]);
				}
			}
			//this.oscMsgName= (String) value;	
		}
		else if ("value".equals(id)) { 
			String[] values = ((String)value).split("\\n");
			if (values.length>0) {
				this.setValue( values[0]);
			} else {
				this.value=new Vector();
			}
			this.extraValues.clear();
			if (values.length>1) {
				for (int i=0;i<values.length;i++) {
					this.extraValues.add(getValueVector(values[i]));
				}
			}
			//this.setValue((String)value);	
		}
		else if ("targetPlayMode".equals(id)) { this.targetPlayMode= (String) value;	}
		//else if ("parameters".equals(id)) { this.setp	}
		else if ("target".equals(id)) { this.target=(TimeLineObject) value;	}
		else if ("active".equals(id)) { this.active=((Boolean) value).booleanValue();	}
		else if ("oscIndex".equals(id)) { 
			this.oscIndex= (Integer) value;	
			
		}
	}
	
}
