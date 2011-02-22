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

import com.silicontransit.timeline.TimeLine;
import com.silicontransit.timeline.window.LogWindow;

public class InputBarDraw extends WindowSectionObject {
	public int inputTextLeft=65;
	public InputBarDraw(int w, int h,int l,int t) {
			super(w, h, l,t);
	}
	
	public Image getImage(TimeLine timeLine) {
		clearImage();
		drawButton(0,0,9,9,"D",true,"Debug window","debugButton",Color.white,Color.GRAY);
		drawButton(0,10,9,9,"C",true,"Compile window","compilerButton",Color.black,Color.yellow);
		drawButton(10,10,9,9,"M",true,"MIDI window","midiButton",Color.white,new Color(128,0,0));
		drawButton(10,0,9,9,"F",true,"Filter edit","filterButton",Color.white,new Color(0,128,0));
		drawButton(20,0,9,9,"N",true,"Notes window","notesButton",Color.white,new Color(0,128,128));
		drawButton(20,10,9,9,"K",true,"Color edit","colorButton",Color.white,new Color(128,128,255));
		drawButton(30,0,9,9,"L",true,"Log Window","logButton",Color.black,new Color(255,128,255));
		drawButton(30,10,9,9,"O",true,"Object Edit","objectButton",Color.black,new Color(255,128,128));
		drawButton(40,0,9,9,"X",true,"MIDI Key Edit","keyButton",Color.black,new Color(255,128,255));
		drawButton(40,10,9,9,"P",true,"Properties","propButton",Color.black,new Color(255,0,255));
		drawButton(50,0,9,9,"I",true,"Interface","ifaceButton",Color.black,new Color(255,0,255));
		drawButton(width-50,0,19,19,">", false, "Play button", "playButton", Color.green, Color.GRAY);
		drawButton(width-20,0,19,19,"||", false, "Pause button", "pauseButton",Color.orange, Color.GRAY);
		
		int inputLeft=60;
	   	if (!"".equals(timeLine.inputMode)) {
			// input at bottom.
			g2.setColor(timeLine.timeLineObject.getAwtColor());
			g2.fillRect(inputLeft,0,15,20);
			text(timeLine.inputMode, inputLeft,height-5);
			if ("fFvk=+hvyTiI".indexOf(timeLine.inputMode)>-1) {
				addBoundedObject("inputButton",inputLeft,0,15,20);
				g2.setColor(Color.WHITE);
				g2.drawRect(inputLeft,0,15,20);
				g2.setColor(timeLine.timeLineObject.getAwtColor());
				drawButton(inputLeft,0,15,20,timeLine.inputMode, true, "Input help", "inputButton",Color.white, timeLine.timeLineObject.getAwtColor());
			}
			if (timeLine.editValueIndex>-1) { text(""+timeLine.editValueIndex, 25,height-5);}
			String dispInputStr="";
			if (timeLine.inputStr.length()>0) {
				//write string with cursor in it.
				if (timeLine.cursorPos>timeLine.inputStr.length()) {timeLine.cursorPos=timeLine.inputStr.length();}
				dispInputStr=timeLine.inputStr.substring(0,timeLine.cursorPos)+"|"+timeLine.inputStr.substring(timeLine.cursorPos,timeLine.inputStr.length());
			}
			text(dispInputStr, inputLeft+15,this.height-5);
			g2.drawLine(inputLeft+15, this.height-2,515,this.height-2);
			inputTextLeft = inputLeft+15;
			addBoundedObject("inputField", inputLeft+15, 0, 520, 20);
		}
		
		if (timeLine.msgCtr>-1) {
			Color c=Color.red;
			if (timeLine.msgType==LogWindow.TYPE_WARN) {
				c=Color.orange;
			} else if (timeLine.msgType==LogWindow.TYPE_MSG){
				c=Color.green;
			}
			text(timeLine.msg, 515,height-6,c);
			timeLine.msgCtr++;
			if (timeLine.msgCtr>20) {timeLine.msgCtr=-1;}
		}
		
		return this.img;
	}
}