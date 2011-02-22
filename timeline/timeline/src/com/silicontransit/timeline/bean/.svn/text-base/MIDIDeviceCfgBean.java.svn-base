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
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class MIDIDeviceCfgBean {
	private String imagePath="";
	private String timeLineId="";
	private String midiId="";
	private HashMap controlCfg=new HashMap();
	private Image image;
	private MIDINoteCfgBean midiNoteCfgBean=null;
	
	public MIDIDeviceCfgBean(String timeLineId) {
		this.timeLineId=timeLineId;
	}
	public void addControl(MIDIControlCfgBean mccb) {
		controlCfg.put(""+mccb.getControlNum(),mccb);
	}
	
	public MIDIControlCfgBean getControl(int ctlNum) {
		return (MIDIControlCfgBean) controlCfg.get(""+ctlNum);
	}
	
	public HashMap getControlCfg() {
		return controlCfg;
	}
	
	public void setControlCfg(HashMap controlCfg) {
		this.controlCfg = controlCfg;
	}
	
	public String getImagePath() {
		return imagePath;
	}
	
	public Image getImage() {
		return image;
	}
	
	public void setImagePath(String image) {
		this.imagePath = image;
		try {
			this.image=ImageIO.read(new File(this.imagePath));
		} catch (IOException e) {
			image=null;
			System.out.println("couldnt read image: "+e.getMessage()+":"+this.imagePath);
		}
	}
	
	public String getMidiId() {
		return midiId;
	}
	
	public void setMidiId(String midiId) {
		this.midiId = midiId;
	}
	
	public String getTimeLineId() {
		return timeLineId;
	}
	
	public void setTimeLineId(String timeLineId) {
		this.timeLineId = timeLineId;
	}
	
	public MIDINoteCfgBean getMidiNoteCfgBean() {
		return midiNoteCfgBean;
	}
	
	public void setMidiNoteCfgBean(MIDINoteCfgBean midiNoteCfgBean) {
		this.midiNoteCfgBean = midiNoteCfgBean;
	}
	
}
