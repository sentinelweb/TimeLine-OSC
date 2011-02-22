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
import java.io.File;

public class ControlSettings implements Comparable, PropertySettable{
	public int part=-1;
	public int control=-1;
	public String controlTxt="";
	public int oscIndex=0;
	public String oscMsg="/";
	public float scale=1;
	public float offset=0;
	public String type="n";
	public float value=0;
	public float previousVal=0;// for geting directional value from joyctick ctl 
	public int midiValue=0;
	Object targetObject=null;
	public int getControl() {		return control;	}
	public void setControl(int control) {		this.control = control;	}
	public float getOffset() {		return offset;	}
	public void setOffset(float offset) {		this.offset = offset;	}
	public int getOscIndex() {		return oscIndex;	}
	public void setOscIndex(int oscIndex) {		this.oscIndex = oscIndex;	}
	public String getOscMsg() {		return oscMsg;	}
	public void setOscMsg(String oscMsg) {		this.oscMsg = oscMsg;	}
	public int getPart() {		return part;	}
	public void setPart(int part) {	this.part = part;	}
	public float getScale() {		return scale;	}
	public void setScale(float scale) {		this.scale = scale;	}
	public String toString() {	return part+" "+control+" "+oscIndex+" "+oscMsg+" "+scale+" "+offset+" "+type;	}
	public String toDisplayString() {	return part+" "+(("".equals(controlTxt))?""+control:controlTxt)+" "+oscIndex+" "+oscMsg+" "+scale+" "+offset+" "+type;	}
	public void parseControlStr(String inputStr) {
		String[] midiRangeAndMsg=inputStr.split(" ");
		try { this.setPart(Integer.parseInt(midiRangeAndMsg[0]));} catch (Exception e) {}
		try { this.setControl(Integer.parseInt(midiRangeAndMsg[1]));} catch (Exception e) {}
		try { this.setOscIndex(Integer.parseInt(midiRangeAndMsg[2]));} catch (Exception e) {}
		try { this.setOscMsg(midiRangeAndMsg[3]);} catch (Exception e) {}
		try { this.setScale(Float.parseFloat(midiRangeAndMsg[4]));} catch (Exception e) {}
		try {this.setOffset(Float.parseFloat(midiRangeAndMsg[5]));} catch (Exception e) {}
		try {this.setType(midiRangeAndMsg[6]);} catch (Exception e) {}
		if ("a".equals(type)) {value=this.getOffset();}
	}
	public String getType() {	return type;	}
	public void setType(String type) {	this.type = type;	}
	public float getValue(int val) {
		if ("a".equals(this.type)) { // accumultaor
			int directionalVal=val;
			if ("/".equals(File.separator)) {//unix
				// if value is the same as before then break out - dont want them
				if (previousVal==val) {return this.value;}
				if (val>0 && val<127) {
					if (previousVal>val) {directionalVal=127;}
					else {directionalVal=1;}
				}else if (val==0) {
					if (previousVal==127) {directionalVal=1;} else {directionalVal=127;} 
				}else if (val==127) {
					if (previousVal==0) {directionalVal=127;} else {directionalVal=1;} 
				}
				previousVal=val;
			}
			if (directionalVal>64) {this.value+=this.scale;}
			else {this.value-=this.scale;}
		}
		else if ("t".equals(this.type)) { // toggle
			value=(value==0)?this.scale:0;
		}
		else if ("b".equals(this.type)) { //button
			value=(value==0)?this.scale:0;
		}
		else if ("l".equals(this.type)) {// logarithmic scale
			value=(float)(Math.pow(this.scale,val)+this.offset);
		}
		else if  ("n".equals(this.type)) {// normal linear scale
			value= ((val*this.scale)+this.offset);
		}
		return value;
	}
	public void setValue(float val) {value=val;}
	public boolean equals(ControlSettings cs) {
		return (this.control==cs.control) && (this.part==cs.part)&& (this.oscIndex==cs.oscIndex)&& (this.oscMsg.equals(cs.oscMsg));
	}

	public int compareTo(Object o) {
		ControlSettings cs=(ControlSettings)o;
		return (part+" "+control).compareTo(cs.part+" "+cs.control);
	}
	
	public boolean checkBounds(String id, Object value) {
		return true;
	}
	
	public Object getProperty(String id) {
		if ("part".equals(id)) {return this.part;	}
		else if ("control".equals(id)) {return this.control;	}
		else if ("oscIndex".equals(id)) {return this.oscIndex;	}
		else if ("oscMsg".equals(id)) {return this.oscMsg;	}
		else if ("type".equals(id)) {return this.type;	}
		else if ("scale".equals(id)) {return this.scale;	}
		else if ("offset".equals(id)) {return this.offset;	}
		return null;
	}
	
	public void setProperty(String id, Object value) {
		if ("part".equals(id)) { this.part = (Integer) value;	}
		else if ("control".equals(id)) { this.control =(Integer) value;	}
		else if ("oscIndex".equals(id)) { this.oscIndex =(Integer) value;	}
		else if ("oscMsg".equals(id)) { this.oscMsg =(String) value;	}
		else if ("type".equals(id)) { this.type =(String) value;	}
		else if ("scale".equals(id)) { this.scale =Float.parseFloat((String) value);	}
		else if ("offset".equals(id)) { this.offset =Float.parseFloat((String) value);	}
		
	}
	
}
