package com.silicontransit.timeline.bean;
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
public class MIDIControlCfgBean extends MIDICtlNoteCfgBean {
	public static final int TYPE_HSLIDER=1;
	public static final int TYPE_VSLIDER=0;
	public static final int TYPE_KNOB=2;
	public static final int TYPE_BUTTON=3;
	private int controlNum=-1;
	private String controlText="noText";
	private int winValue=0;//simulated value when using window.
	private int type=TYPE_VSLIDER;
	public int getWinValue() {
		return winValue;
	}
	public void setWinValue(int winValue) {
		this.winValue = winValue;
		if (this.winValue>127) {this.winValue=127;}
		else if (this.winValue<0) {this.winValue=0;}
	}
	public MIDIControlCfgBean () {
	}
	public int getControlNum() {
		return controlNum;
	}
	public void setControlNum(int controlNum) {
		this.controlNum = controlNum;
	}
	public String getControlText() {
		return controlText;
	}
	public void setControlText(String controlText) {
		this.controlText = controlText;
	}
	public void setType(String type) {
		if ("vslider".equals(type)) {
			this.type = MIDIControlCfgBean.TYPE_VSLIDER;
		}else if ("hslider".equals(type)) {
			this.type = MIDIControlCfgBean.TYPE_HSLIDER;
		}else if ("knob".equals(type)) {
			this.type = MIDIControlCfgBean.TYPE_KNOB;
		}else if ("button".equals(type)) {
			this.type = MIDIControlCfgBean.TYPE_BUTTON;
		}
		
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}
