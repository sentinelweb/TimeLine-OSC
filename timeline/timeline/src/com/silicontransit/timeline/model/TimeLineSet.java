/*
 * Created on 10-Oct-2007
 */
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
import java.util.Iterator;
import java.util.Vector;

import com.silicontransit.timeline.TimeLine;
import com.silicontransit.timeline.disp.DisplayObject;

/**
 * @author munror
 */
public class TimeLineSet extends DisplayObject implements Cueable, PropertySettable{
	
	private Vector set = new Vector();
	private TimeLine timeLineApplet = null;
	
//	store var from cueable interface.
	private String cueMode = "";
	private boolean cueStop = true;
	private int cueFlash = 0;
	
	public TimeLineSet(TimeLine timeLineApplet) {
		super(50, 12, 0, 0);
		this.timeLineApplet = timeLineApplet;
		
	}
	public TimeLineSet(int w, int h, int l, int t) {
		super(w, h, l, t);
	}
	private String id = ""+hashCode();

	public String getId() {		return id;	}
	public void setId(String string) {		id = string;	}

	public void clear() {		set.clear();	}
	public void addAll(Vector timeLineSelection) {		set.addAll(timeLineSelection);	}

	public Image getButtonImage( boolean selected ) {
		clearImage();
		g2.setColor(Color.orange);
		g2.drawRoundRect( 0, 0, this.width-1, this.height-1, 6, 6);
		g2.drawLine(12,0,12,this.height-1);
		g2.drawLine(35,0,35,this.height-1);
		g2.setColor(Color.yellow);
		g2.drawOval( 1, 1, 10, 10 );
		
		String text = this.id;
		FontMetrics metrics = g2.getFontMetrics();
		int width = metrics.stringWidth( text );
		int height = metrics.getHeight();
		g2.drawString( text, 13, height-3 );
		if (selected) {g2.setColor(Color.WHITE); g2.drawLine(3,0,47,0);}
		//		need to modify checkQued to get qued playmode and change square colour
		if (timeLineApplet.cue.checkQued(this)) {
			this.cueFlash++;
			if (this.cueFlash%4>2) {//this flashes the button 
				if ("p".equals(this.cueMode)) {		g2.setColor(Color.green); 	}
				else if ("l".equals(this.cueMode)) {		g2.setColor(Color.orange); 	}
				else {	g2.setColor(Color.gray); } 
				g2.fillOval(1,1,10,this.height-2);
			}
		}
		return this.img;
	}
	
	public Vector getSet() {		return set;	}
	public void setSet(Vector set) {		this.set = set;	}
	public String getList() {
		Iterator i = set.iterator();	String op = "";
		while (i.hasNext()){
			op+=((TimeLineObject)i.next()).id+(i.hasNext()?",":"");
		}
		return op;
	}
	// implementation of Cueable interface
	public String getCueMode() {	return cueMode;}
	public void setCueMode(String cueMode) {		this.cueMode=cueMode;}
	public boolean getStop() {
		return cueStop;
	}
	public void setStop(boolean cueStop) {
		this.cueStop=cueStop;
		
	}
	public boolean checkBounds(String id, Object value) {
		return true;
	}
	public Object getProperty(String id) {
		if ("id".equals(id)) {return this.id;	}
		return null;
	}
	public void setProperty(String id, Object value) {
		if ("id".equals(id)) { this.id = (String)value;	}
	}
}
