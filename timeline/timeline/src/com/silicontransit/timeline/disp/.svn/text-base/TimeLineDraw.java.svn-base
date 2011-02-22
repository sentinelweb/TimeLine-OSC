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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.util.Iterator;
import java.util.Vector;

import com.silicontransit.timeline.TimeLine;
import com.silicontransit.timeline.model.Event;
import com.silicontransit.timeline.model.TimeLineObject;


public class TimeLineDraw extends WindowSectionObject {
	private static final Color INACTIVE_COLOUR = new Color(200,200,200);
	private static  int MAX_SUBEVENT_PLOT=20;
	private static  int GROUP_BUTTON_HEIGHT=10;
	private static  int SCROLL_HEIGHT=5;
	private TimeLineObject timeLineObj; 
	public TimeLineDraw(int w, int h,int l,int t,TimeLineObject timeLineObj) {
		super(w, h, l,t);
		this.timeLineObj=timeLineObj;
	}

	public Image getImage(TimeLine t ) {  // 
		int linePosY=35;//this.height/3;
		
		if (timeLineObj.typeDisplay) { 
			timeLineObj.dispHeight = (linePosY+(timeLineObj.typeDisplayIndexs.size())*15 +GROUP_BUTTON_HEIGHT+SCROLL_HEIGHT+10);
		} else {
			timeLineObj.dispHeight=timeLineObj.setHeight;
		}
		this.setHeightAndNotifyChangeIfNessecary( timeLineObj.dispHeight );
		Font thisFont = new Font("Dialog", Font.PLAIN, 8);
		g2.setFont(thisFont);
		// draw marks
		clearImage();
		clearBoundedObjects();
		//	show record Input 
		
		if ("r".equals(timeLineObj.playMode)){
			g2.setColor(Color.red); 
			text("rec: "+t.recordInput +"(" +t.recordGranularity+")",5,10);
		}
		
		// timeline toolbar.
		g2.setColor(timeLineObj == t.timeLineObject ? Color.white: Color.gray);
		// set label.
		text( timeLineObj.id, this.width-g2.getFontMetrics(thisFont).stringWidth(timeLineObj.id)-20, this.height-10);
		if (!"".equals(timeLineObj.followOnExpr)) {
			text(timeLineObj.followOnExpr, this.width-g2.getFontMetrics(thisFont).stringWidth(timeLineObj.followOnExpr)-20, this.height-20);
		}
		// zoom, scroll, stick buttons

		drawButton(this.width-20, 0, 19, 12, "Zm", false, "Use wheel to zoom", "Zoom",Color.orange, Color.yellow);
		drawButton(this.width-40, 0, 19, 12, "Sc", false, "Use wheel to scroll", "Scroll",Color.orange, Color.yellow);
		drawButton(this.width-60, 0, 20, 12, "Hgt", false , "Set height", "Height",Color.orange, Color.yellow);
		drawButton(this.width-70, 0, 9, 12, "", timeLineObj.scrollPosToView, "Follow position bar", "ScrollPosToView", Color.orange, Color.red);
		drawButton(this.width-80, 0, 9, 12, "", timeLineObj.typeDisplay, "Display each type on it own line", "TypeDisplay", Color.orange, Color.orange);
		drawButton(this.width-90, 0, 9, 12,"x", false, "Close", "Close", Color.orange, Color.orange);
		
		if (timeLineObj.scrollPosToView) {
			// modify viewport to show pos in middle.
			int length = timeLineObj.displayEnd - timeLineObj.displayStart;
			if (timeLineObj.displayStart>timeLineObj.pos ) {
				timeLineObj.displayStart = timeLineObj.pos-length/2;
				if(timeLineObj.displayStart<0){timeLineObj.displayStart=0;}
				timeLineObj.displayEnd = timeLineObj.displayStart+length;
			} else if (timeLineObj.displayEnd<timeLineObj.pos) {
				timeLineObj.displayEnd = timeLineObj.pos+length/2;
				if(timeLineObj.displayEnd>timeLineObj.timeLineLength){timeLineObj.displayStart=timeLineObj.timeLineLength;}
				timeLineObj.displayStart = timeLineObj.displayEnd-length;
			}
		}
		// end timeline toolbar.
		
		// plot selection rectangle
		if (timeLineObj.timeSelStart>-1) {
			g2.setColor(new Color(50, 200,50)); 
			int tSelStartx=timeLineObj.getTimeInFrame(timeLineObj.timeSelStart,width);
			int tSelEndx=timeLineObj.getTimeInFrame(timeLineObj.timeSelEnd,width);
			g2.fillRect(tSelStartx,0,tSelEndx-tSelStartx,this.height);
		}
		
		// plot rows if int typeDislay mode
		if (timeLineObj.typeDisplay) {
			Iterator i = timeLineObj.typeDisplayIndexs.keySet().iterator();
			while (i.hasNext()) {
				String name=(String) i.next();
				Integer val =  (Integer) timeLineObj.typeDisplayIndexs.get(name);
				Color lblColorColor=Color.WHITE;
				if (name.indexOf("$")!=0) {
					lblColorColor = t.oscMessageColorMap.getColorFor(name) ;
				} else {
					lblColorColor = t.expressionColorMap.getColorFor(name);
				}
				g2.setColor(lblColorColor);
				int baseLine = linePosY + (val.intValue()+1)*15;
				text(name, 0, baseLine,lblColorColor);
				g2.drawLine(0, baseLine, this.width, baseLine);
			}
		}
		
		// plot scroll bar
		g2.setColor(new Color(100,100,100)); 
		g2.fillRect(0,this.height-5,width,this.height);
		int stView=Math.round((float)timeLineObj.displayStart/(float)timeLineObj.timeLineLength*(float)width);
		int endView=Math.round((float)timeLineObj.displayEnd/(float)timeLineObj.timeLineLength*(float)width);
		g2.setColor(INACTIVE_COLOUR); 
		g2.fillRect(stView,this.height-5,endView-stView,this.height-2);
		addBoundedObject("scroll",stView,this.height-5,endView-stView,this.height-2);
		
		// plot quantization marks
		g2.setColor(Color.orange);
		g2.drawLine(0, linePosY, this.width, linePosY);
		
		int qs=(timeLineObj.displayStart/timeLineObj.quantize)*timeLineObj.quantize;
		int beatLen=timeLineObj.quantize*timeLineObj.beatLength;
		int barLen=timeLineObj.quantize*timeLineObj.beatLength*timeLineObj.beatPerBar;
		int markStart=(this.width/3);
		//if (timeLineObj.showLongMarks) {markStart=12;} // determine whether to show  high beats/bars marks
		boolean showQMarks = Math.abs(getTimeInFrame(timeLineObj,timeLineObj.displayStart+timeLineObj.quantize))>1;
		boolean showBeatMarks = Math.abs(getTimeInFrame(timeLineObj,timeLineObj.displayStart+beatLen))>1;
		for (int qt=qs;qt<timeLineObj.displayEnd;qt+=timeLineObj.quantize) {
			int qpos=getTimeInFrame(timeLineObj,qt);
			if (showQMarks) {g2.drawLine(qpos, linePosY, qpos,linePosY+2);}
			 if (showBeatMarks && qt%beatLen==0 ) {//  mark beat.
				g2.drawLine(qpos, linePosY, qpos,linePosY+4);
			 }
			 if (qt%barLen==0) {//  mark bar.
				g2.drawLine(qpos, linePosY, qpos,linePosY+6);
			 }
		}
		
		// draw group buttons
		Iterator i=timeLineObj.groups.keySet().iterator();
		int ctr=0;
		while (i.hasNext()) {
			String groupName=(String)i.next();
			Vector group=(Vector)timeLineObj.groups.get(groupName);
			Event e0=(Event)group.get(0);
			addGroupButton(timeLineObj, ctr, groupName, e0);
			ctr++;
		}
		if (timeLineObj.selection.size()>0) {
			addGroupButton(timeLineObj, ctr, "selection", (Event)timeLineObj.selection.get(0));
		}
		  		  
		  g2.setColor(new Color(100, 102,50));
		  int offset=0;
		  Vector timeLineEnds=new Vector();
		  Event currentEvent=timeLineObj.getLastEvent();
		  FontMetrics fm = g2.getFontMetrics();
		  for (int thisTimeLineIndex=0;thisTimeLineIndex<timeLineObj.timeLine.size();thisTimeLineIndex++) {
			Event thisEvent=(Event)timeLineObj.timeLine.get( thisTimeLineIndex );
			Color eventColor=Color.WHITE;
			if (thisEvent.oscMsgName.indexOf("$")!=0) {
				eventColor = t.oscMessageColorMap.getColorFor(thisEvent.oscMsgName) ;
			} else {
				eventColor = t.expressionColorMap.getColorFor(thisEvent.oscMsgName);
			}
			int posx=timeLineObj.getTimeInFrame( thisEvent.eventTime, width );
			int posy=linePosY;//this.height/3;
			int posxEnd=-1;
			// calc end if target to see if we need to plot it.
			if (thisEvent.target!=null) { 
				posxEnd=timeLineObj.getTimeInFrame(thisEvent.eventTime+thisEvent.target.timeLineLength,width);
		   }
			if (!timeLineObj.typeDisplay) {
				//calc y-offset if this event is on top of previous ones.
				offset=1;
				while ( 	(thisTimeLineIndex>0) &&
								((posx-timeLineObj.getTimeInFrame(((Event)timeLineObj.timeLine.get(thisTimeLineIndex-offset)).eventTime,width))<6) &&  
								((thisTimeLineIndex-offset)>0)  ) {
				   offset++;
				}
				// inc offset to show blow tab at same pos.
				posy+=(offset-1)*15;
		  	} else {
				Integer index = (Integer) timeLineObj.typeDisplayIndexs.get( thisEvent.oscMsgName );
				if (index != null) {	posy+=(index.intValue()+1)*15;	}
		  	}
		   	if (( (posx>=0) || ((posxEnd)>0)) && (posx<=width)) { 
				// plot event.
				g2.setColor(eventColor);
				// uncomment later
				if (!thisEvent.active) {g2.setColor(INACTIVE_COLOUR);}
				if (thisEvent.oscMsgName.indexOf("/")==0) {
					g2.drawOval(posx-5, posy-5, 10, 10);
				} else {
					g2.drawRect(posx-5, posy-5, 10, 10);
				}
				if (thisEvent.lastPlayed+200>System.currentTimeMillis()) {
					g2.setColor( Color.YELLOW); 
					g2.drawOval(posx-6, posy-6, 12, 12);
					g2.drawLine(posx-6, posy-6, posx+6, posy+6);
					g2.drawLine(posx-6, posy+6, posx+6, posy-6);
				}
				addBoundedObject(thisEvent, posx-5, posy-5, 10, 10);
				// plot line thru middle of event. 
				g2.setColor( eventColor); 
				g2.drawLine(posx, posy-7, posx,posy+7);
				// plot line thru middle of event. on timeline.
				g2.drawLine(posx, (linePosY)-7, posx,(linePosY)+7);
				//plot sub timeline
				if (thisEvent.target!=null && (!"".equals(thisEvent.targetPlayMode)) && (!"b".equals(thisEvent.targetPlayMode))) {
					// this plots subtimelines more horizontally.
					// check timelines ended.
					for (int k=0;k<timeLineEnds.size();k++) {
						Integer end =(Integer)timeLineEnds.get(k);
						if (end.intValue()<posx) {timeLineEnds.remove(k);k--;}
					}
					int tl_y_pos = 10+posy+7+timeLineEnds.size()*6-offset*5;
	    	
					g2.setColor(thisEvent.target.getAwtColor()); 
					g2.drawLine(posx, posy+4, posx, tl_y_pos);// draw vert line down to timeline.
					g2.drawLine(posx, tl_y_pos, posxEnd , tl_y_pos);// draw horiz timeline. posx 
					if ("p".equals(thisEvent.targetPlayMode)) {
						g2.drawLine(posxEnd,tl_y_pos-2, posxEnd , tl_y_pos+2);  // stright line at end for playonce
					} else if ("l".equals(thisEvent.targetPlayMode)) {
						g2.drawLine(posxEnd-2, tl_y_pos-2, posxEnd +2, tl_y_pos+2); // diagonal line at end for loop
					}
					timeLineEnds.add(new Integer(posxEnd));
	    	
					//	draw events.
					if (thisEvent.target.timeLine.size()<MAX_SUBEVENT_PLOT) {
						for (int subEventIndex=0;subEventIndex<thisEvent.target.timeLine.size();subEventIndex++) {
							Event subEvent=(Event)thisEvent.target.timeLine.get(subEventIndex);
							int eventPosInFrame= timeLineObj.getTimeInFrame(thisEvent.eventTime+subEvent.eventTime,width);
							g2.drawLine(eventPosInFrame, tl_y_pos+1,eventPosInFrame, tl_y_pos+2);
						}
					}
					//show pos.
					long tlPos=timeLineObj.getTimeInFrame(thisEvent.eventTime + thisEvent.target.pos,width);
					g2.drawLine((int)tlPos, tl_y_pos,(int) tlPos, tl_y_pos+2);// draw pos marker.
					// show timeline name next to it.
					text(thisEvent.target.id,posx+1, tl_y_pos);
				}
				//plot current event data.
				if (thisTimeLineIndex==timeLineObj.currentEvent) {
				  g2.drawRect(posx-6, posy-6, 12, 12);
				  int posyText=(linePosY);
				  Color eventTxtColor=Color.WHITE;
				  if (!thisEvent.active) {eventTxtColor=INACTIVE_COLOUR;}
				  int textx=posx-3;// default text x-pos.
				  int txtWidth=0;
				  String text1 = thisEvent.getTargetId()+":"+thisEvent.targetPlayMode+":"+thisEvent.targetData;
				  txtWidth=fm.stringWidth(text1);
				  String text2 =thisEvent.oscMsgName+"="+thisEvent.getValueStrAbbrev();
				  txtWidth=Math.max(txtWidth,fm.stringWidth(text2));
				  String text3 =thisEvent.eventTime+":"+thisEvent.oscIndex+":"+t.oscServerPorts[thisEvent.oscIndex]+":"+thisEvent.id;
				  txtWidth=Math.max(txtWidth,fm.stringWidth(text3));
				  if (posx+txtWidth>this.width) {
					  text( text1, posx -fm.stringWidth(text1)+6 ,posyText-26,eventTxtColor );
					  text( text2, posx -fm.stringWidth(text2)+6 ,posyText-16,eventTxtColor );
					  text( text3, posx -fm.stringWidth(text3)+6 ,posyText-6,eventTxtColor );
				  } else {
					  text( text1, posx , posyText-26,eventTxtColor );
					  text( text2, posx  ,posyText-16,eventTxtColor );
					  text( text3, posx  ,posyText-6,eventTxtColor );
				  }
				}
				if (thisTimeLineIndex==timeLineObj.lastSelEvent) {
					g2.setColor(Color.lightGray); //new Color(100, 102,200)
					g2.drawRect(posx-6, posy-6, 12, 12);
				}
				// mark selected events.
				if (timeLineObj.selection.contains(thisEvent)) {
					g2.setColor(new Color(100, 200,100)); 
					g2.drawRect(posx-7, posy-7, 14, 14);
				}
				//mark number on event
				text(""+thisTimeLineIndex, posx-3, posy+3, thisEvent.active?Color.WHITE:eventColor);
				// show values for the smae messages across the time line.
				if ((currentEvent!=null) && (thisEvent!=currentEvent)) {
					Color eventTxtColor=Color.gray;
					if (!thisEvent.active) {eventTxtColor=INACTIVE_COLOUR;}
					  
					if (!"".equals(thisEvent.oscMsgName) && (thisEvent.oscMsgName.equals(currentEvent.oscMsgName))) {
						text(thisEvent.getValueStrAbbrev(),posx,7,eventTxtColor);
					} else if ((currentEvent.target!=null) && (thisEvent.target!=null) && (currentEvent.target==thisEvent.target)){
						text(thisEvent.target.id+":"+thisEvent.targetPlayMode,posx,7,eventTxtColor);
						
					}
				}
			}
		  }
		// position marker
		g2.setColor(new Color(200,200,255));
		int positionMkrX=timeLineObj.getTimeInFrame(timeLineObj.pos,width);
		g2.drawLine(positionMkrX,0,positionMkrX,this.height-5);
		addBoundedObject("posBar", positionMkrX-2,0,positionMkrX+2,this.height-5);
		// scrollbar pos marker
		g2.setColor(Color.BLACK);
		int posMkrScrollX=Math.round((float)timeLineObj.pos/(float)timeLineObj.timeLineLength*this.width);
		g2.drawLine(posMkrScrollX,this.height-5,posMkrScrollX,this.height);
		// display logic for popupWindow.
//		if (t.timeLineDisplays.get(0)==this) {
//			t.popupWindowDraw.top=this.top;
//			t.popupWindowDraw.height=this.height;
//			setPopupDisplay(t, timeLineObj); 
//		}
		
//		if ((t.slidersDraw.isVisible()) && (timeLineObj == t.timeLineObject) && (!"".equals(t.inputMode) && "vw".indexOf(t.inputMode)>-1)) {
//			t.slidersDraw.top=this.top;
//			t.slidersDraw.height=this.height;
//			displaySliders(t);
//		} else {
//			t.slidersDraw.reset();
//		}
		if (t.showCrosshair) {
			g2.setColor(Color.gray);
			g2.drawLine(t.mouseX,0,t.mouseX,this.height);
			if (t.mouseY>this.top && t.mouseY<this.top+this.height) {g2.drawLine(0,t.mouseY-this.top,width,t.mouseY-this.top);}
		}
		if (timeLineObj == t.timeLineObject) {
			g2.setColor(Color.white);
			g2.drawRect(0, 0, this.width-1, this.height-1);
		}
		return this.img;
	}

//	public void displaySliders(TimeLine t) {
//		 if (t.slidersDraw.isVisible()  ){
//					g2.drawImage(
//						t.slidersDraw.getImage(t),
//						0,0, Color.black, null
//					);
//			}
//	}

	private void addGroupButton(TimeLineObject timeLineObj, int ctr, String groupName, Event e0) {
		int xpos=ctr*50;
		int ypos=this.height-12-5;
		g2.setColor(Color.gray);
		g2.drawRect(xpos,ypos,50,12);
		addBoundedObject("g_"+groupName,xpos,ypos,50,12);
		g2.setColor(Color.WHITE);
		if (!e0.active) {g2.setColor(INACTIVE_COLOUR);}
		g2.fillRect(xpos+1,ypos+1,10,10);
		text(groupName,xpos+12,ypos+9,timeLineObj.getAwtColor());
	}
	
	public int getTimeInFrame(TimeLineObject t,long time1) {
		int displayPos=(int)(time1-t.displayStart);
		int len=t.displayEnd-t.displayStart;
		int posx=displayPos*this.width/len;
		return posx;
	 }

	public TimeLineObject getTimeLineObj() {	return timeLineObj;}
	public void setTimeLineObj(TimeLineObject object) {	timeLineObj = object;}

}
