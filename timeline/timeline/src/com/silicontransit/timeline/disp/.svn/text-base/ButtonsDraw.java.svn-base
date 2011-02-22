
package com.silicontransit.timeline.disp;
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
import java.awt.Image;
import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.silicontransit.timeline.TimeLine;
import com.silicontransit.timeline.ToolTipTexts;
import com.silicontransit.timeline.model.Cueable;
import com.silicontransit.timeline.model.TimeLineObject;
import com.silicontransit.timeline.model.TimeLineSet;


public class ButtonsDraw extends WindowSectionObject {
	
	public ButtonsDraw(int w, int h,int l,int t) {
		super(w, h, l, t);
	}
	
	public Image getImage(TimeLine timeLine) {
		clearImage();
		clearBoundedObjects();
		g2.setColor(Color.lightGray);
		g2.drawLine(0,this.height-1, this.width,this.height-1);
		HashMap objectIndexes=new HashMap();
//		draw timeline buttons
		int posy=0;
		int numberPerLine=this.width/50;
		int counter = 0;
		for ( counter=0;counter<timeLine.timeLines.size();counter++) {
			Point p = getButtonPoint(counter, numberPerLine);
			TimeLineObject t = (TimeLineObject)timeLine.timeLines.get(counter);
			int type=TimeLineObject.BUTTON_NORMAL;
			if (t==timeLine.timeLineObject) {	type = TimeLineObject.BUTTON_CURRENT;}
			else if (timeLine.timeLineSelection.contains(t)) {	type = TimeLineObject.BUTTON_SELECTED;}
			boolean inCurrentSet = (timeLine.timeLineSet!=null) && timeLine.timeLineSet.getSet().contains(t);
			g2.drawImage(t.getButtonImage(type,inCurrentSet), p.x, p.y,null);
			addBoundedObject(t, p.x, p.y, 50, 12,ToolTipTexts.TIMELINE_BUTTON_TOOLTIP);
			objectIndexes.put(t,new Integer(counter));
			posy = p.y;
		}

		for (int i=0;i<timeLine.timeLineSets.size();i++,counter++) {
			Point p= getButtonPoint(counter, numberPerLine);
			TimeLineSet t = (TimeLineSet)timeLine.timeLineSets.get(i);
			g2.drawImage(t.getButtonImage(t==timeLine.timeLineSet), p.x, p.y,null);
			addBoundedObject(t, p.x, p.y,50,12,ToolTipTexts.TIMELINESET_BUTTON_TOOLTIP);
			objectIndexes.put(t,new Integer(counter));
			posy = p.y;
		} 
		
		// draw cue lines
		for (Iterator cueIter = timeLine.cue.getCue().keySet().iterator(); cueIter.hasNext();) {
			TimeLineObject trig = (TimeLineObject) cueIter.next();
			counter= ((Integer) objectIndexes.get(trig)).intValue();
			Point src=getButtonPoint(counter, numberPerLine);
			Vector waiting = (Vector)  timeLine.cue.getCue().get(trig);
			for (Iterator iterator = waiting.iterator(); iterator.hasNext();) {
				Cueable cuedObj = (Cueable) iterator.next();
				counter= ((Integer) objectIndexes.get(cuedObj)).intValue();
				Point tgt = getButtonPoint(counter, numberPerLine);
				if ("p".equals(cuedObj.getCueMode())) {		g2.setColor(Color.green); 	}
				else if ("l".equals(cuedObj.getCueMode())) {		g2.setColor(Color.orange); 	}
				else {	g2.setColor(Color.gray); } 
				g2.fillOval(src.x+43, src.y+3,4,4);
				g2.fillOval(tgt.x+3, tgt.y+3,4,4);
				g2.drawLine(src.x+45, src.y+5, tgt.x+5, tgt.y+5);
			}
		}
		if (timeLine.cueTimeLineObject!=null ) {
			if (timeLine.mouseButton == TimeLine.LEFT) {g2.setColor(Color.green); }
			else if (timeLine.mouseButton == TimeLine.RIGHT){g2.setColor(Color.orange); }	
			else {	g2.setColor(Color.gray); } 
			counter = ((Integer) objectIndexes.get(timeLine.cueTimeLineObject)).intValue();
			Point src = getButtonPoint(counter, numberPerLine);
			g2.fillOval(src.x+43, src.y+3, 4, 4);
			g2.drawLine(src.x+45, src.y+5, timeLine.mouseX-this.getLeft(), timeLine.mouseY-this.getTop());
		}
		setHeightAndNotifyChangeIfNessecary(posy+12);
		return this.img;
	}
	
	private Point getButtonPoint(int index, int numberPerLine) {
		int num=(numberPerLine!=0?numberPerLine:1);
		return new Point(
			((index%num)*50)%this.width,
			(index/num)*12
		);
	}
}
