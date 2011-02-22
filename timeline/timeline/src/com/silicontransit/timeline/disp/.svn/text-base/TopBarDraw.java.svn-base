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
import java.util.Date;

import com.silicontransit.timeline.TimeLine;
import com.silicontransit.timeline.model.TimeLineObject;

public class TopBarDraw extends WindowSectionObject {
	
	public TopBarDraw(int w, int h,int l,int t) {
		super(w, h, l,t);
	}
	
	public Image getImage(TimeLine timeLine) {
		clearImage();
			//	draw timeline color box.
		TimeLineObject timeLineObject=timeLine.timeLineObject;

		//if bright color print playmode balck
		
		drawButton(0,0, 30,10, "Menu" , false , "Click to show menu" ,"menu"  , Color.white ,  Color.white);
		text("Time Line: id:"+timeLineObject.id+ " ("+timeLine.timeLines.size()+":"+timeLine.timeLineIndex+")"+
				 " q:"+timeLineObject.quantize+" b:"+timeLineObject.beatLength+" - "+timeLineObject.beatPerBar+
				" osc:"+timeLineObject.oscIndex+" - "+timeLine.oscServerPorts[timeLineObject.oscIndex]+
				":"+(timeLine.undoOn?"U":"")
				, 35, 9);
					  
		//times
		text( " len:"+timeLine.timeFormat.format(new Date(timeLineObject.timeLineLength))+"["+timeLine.timeFormat.format(new Date(Math.round(timeLineObject.timeLineLength/timeLineObject.pitch)))+"]"+
				" pos:"+timeLine.timeFormat.format(new Date(timeLineObject.pos))+
				" st:"+timeLine.timeFormat.format(new Date(timeLineObject.displayStart))+
				" ed:"+timeLine.timeFormat.format(new Date(timeLineObject.displayEnd))+
				" pit:"+timeLineObject.pitch+
				" bpm:"+timeLine.calcUtil.calculateBPM(timeLineObject),
				330, 9  );
		drawButton(this.width-24, 0, 11, 9, "+" , timeLine.showCrosshair , "Show crosshair" ,"showCrosshair"  , timeLine.showCrosshair?Color.BLACK:Color.YELLOW , Color.YELLOW);
		drawButton(this.width-12, 0, 11, 9, "T" , timeLine.showTooltips , "Show Tooltips" ,"showToolTips"  , Color.WHITE , Color.blue);
		drawButton(this.width-36, 0, 11, 9, "A" , timeLine.alt , "Show Tooltips" ,"showToolTips"  , Color.orange , Color.blue);
		drawButton(this.width-48, 0, 11, 9, "C" , timeLine.ctrl , "Show Tooltips" ,"showToolTips"  , Color.orange , Color.blue);
		drawButton(this.width-60, 0, 11, 9, "S" , timeLine.shift , "Show Tooltips" ,"showToolTips"  , Color.orange , Color.blue);
		return this.img;
	}
}
