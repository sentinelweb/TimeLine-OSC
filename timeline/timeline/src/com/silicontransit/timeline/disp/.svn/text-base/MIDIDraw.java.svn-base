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
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import com.silicontransit.timeline.TimeLine;
import com.silicontransit.timeline.bean.MIDIControlCfgBean;
import com.silicontransit.timeline.bean.MIDICtlNoteCfgBean;
import com.silicontransit.timeline.bean.MIDIDeviceCfgBean;
import com.silicontransit.timeline.bean.MIDINoteCfgBean;
import com.silicontransit.timeline.model.ControlSettings;

public class MIDIDraw extends DisplayObject {
	public static final String PARTUP = "PART+";
	public static final String PARTDN = "PART-";
	public static final String OCTAVEUP = "OCTAVE+";
	public static final String OCTAVEDN = "OCTAVE-";
	public static final String COPY = "COPY";
	public static final String VIEW_NAME = "N";
	public static final String VIEW_NUM = "X";
	public static final String VIEW_DATA = "D";
	public static final String VIEW_OSC = "O";
	public static final String VIEW_SETTINGS = "S";
	public static final String VIEW_CONTROL = "C";
	public static final int TOP_BAR_OFFSET=11;
	private TimeLine timeLine;
     private String currMIDIDev;
     private String currMapEdit="";
     private MIDICtlNoteCfgBean currHoverArea=null;
     private MIDIControlCfgBean currHoverEl=null;
     public int selTop = - 1 ;
     public int selLeft = - 1 ;
     public int selWidth = - 1 ;
     public int selHeight = - 1 ;
     public int curX = - 1 ;
     public int curY = - 1 ;
     public int part=0;
     public int octave=0;
     FontMetrics fontMetrics;
     public Point dragPoint=null;
     public HashSet viewFields=new HashSet();
	public MIDIDraw(int w, int h, int l, int t,TimeLine timeLine) {
		super(w, h, l, t);
		this.timeLine=timeLine;
		setCurrMIDIDev( timeLine.midiDeviceNames[0]) ;
		fontMetrics=g2.getFontMetrics();
		viewFields.add(VIEW_NAME);
		viewFields.add(VIEW_OSC);
		viewFields.add(VIEW_CONTROL);
	}
	
	public Image getImage() {
		clearImage();
		
		clearBoundedObjects();
		
		int buttLeft=0;
		for ( int i=0;i<timeLine.midiDeviceNames.length;i++) {
			Color butColor=timeLine.midiDeviceNames[i].equals(currMIDIDev)?Color.WHITE:Color.LIGHT_GRAY;
			g2.setColor(butColor);
			int butWidth=75;//fontMetrics.stringWidth(currMIDIDev);
			g2.drawRect(buttLeft,0,butWidth,10);
			text(timeLine.midiDeviceNames[i],buttLeft+2, 9,butColor);
			addBoundedObject("¬"+timeLine.midiDeviceNames[i],buttLeft,0,butWidth,10);
			buttLeft+=butWidth;
			if (timeLine.midiDeviceNames[i].equals(currMIDIDev)) {
				//control maps
				HashMap midiControlMapsForDevice=(HashMap)timeLine.allMidiControlMaps.get(currMIDIDev);
				if (midiControlMapsForDevice!=null) {
					Iterator ctlMapsIter=midiControlMapsForDevice.keySet().iterator();
					String currDevId=(String)timeLine.currentMidiControlMapIds.get(currMIDIDev);
					while (ctlMapsIter.hasNext()) {
						String id=(String)ctlMapsIter.next();
						
						butColor=(currDevId!=null && currDevId.equals(id))?Color.YELLOW:Color.ORANGE;
						butWidth=fontMetrics.stringWidth(id);
						g2.drawRect(buttLeft,0,butWidth,10);
						text(id,buttLeft+2, 9,butColor);
						addBoundedObject("="+id,buttLeft,0,butWidth,10);
						buttLeft+=butWidth;
					}
				}
				butColor=Color.ORANGE;
				butWidth=fontMetrics.stringWidth("+");
				g2.drawRect(buttLeft,0,butWidth,10);
				text("+",buttLeft+2, 9,butColor);
				addBoundedObject("=+",buttLeft,0,butWidth,10);
				buttLeft+=butWidth;
				// note maps
				HashMap midiNoteMapsForDevice=(HashMap)timeLine.allMidiNoteMaps.get(currMIDIDev);
				if (midiNoteMapsForDevice!=null) {
					Iterator noteMapsIter=midiNoteMapsForDevice.keySet().iterator();
					String currDevId=(String)timeLine.currentMidiNoteMapIds.get(currMIDIDev);
					while (noteMapsIter.hasNext()) {
						String id=(String)noteMapsIter.next();
						butColor=(currDevId!=null && currDevId.equals(id))?Color.YELLOW:Color.GREEN;
						butWidth=fontMetrics.stringWidth(id);
						g2.drawRect(buttLeft,0,butWidth,10);
						text(id,buttLeft+2, 9,butColor);
						addBoundedObject("+"+id,buttLeft,0,butWidth,10);
						buttLeft+=butWidth;
					}
				}
				butColor=Color.GREEN;
				butWidth=fontMetrics.stringWidth("+");
				g2.drawRect(buttLeft,0,butWidth,10);
				text("+",buttLeft+2, 9,butColor);
				addBoundedObject("++",buttLeft,0,butWidth,10);
				buttLeft+=butWidth;
			}
		}
		g2.setColor(Color.WHITE);
		//  part inc / dec contorls
		g2.drawRect(buttLeft,0,40,10);
		text("-",buttLeft+2, 9);
		text("P:"+part,buttLeft+11, 9);
		text("+",buttLeft+31, 9);
		addBoundedObject(PARTDN,buttLeft,0,10,10);
		addBoundedObject(PARTUP,buttLeft+30,0,10,10);
		
		// view select buttons
		Color color=Color.WHITE;
		if (viewFields.contains(VIEW_NAME)) {color=Color.RED;} else { color=Color.WHITE;} g2.setColor(color);
		g2.drawRect(buttLeft+42,0,8,10);
		text(VIEW_NAME,buttLeft+43, 9,color);
		addBoundedObject(VIEW_NAME,buttLeft+42,0,8,10);
		if (viewFields.contains(VIEW_NUM)) {color=Color.RED;} else { color=Color.WHITE;} g2.setColor(color);
		g2.drawRect(buttLeft+52,0,8,10);
		text(VIEW_NUM,buttLeft+53, 9,color);
		addBoundedObject(VIEW_NUM,buttLeft+52,0,8,10);
		if (viewFields.contains(VIEW_OSC)) {color=Color.RED;} else { color=Color.WHITE;} g2.setColor(color);
		g2.drawRect(buttLeft+62,0,8,10);
		text(VIEW_OSC,buttLeft+63, 9,color);
		addBoundedObject(VIEW_OSC,buttLeft+62,0,8,10);
		if (viewFields.contains(VIEW_DATA)) {color=Color.RED;} else { color=Color.WHITE;} g2.setColor(color);
		g2.drawRect(buttLeft+72,0,8,10);
		text(VIEW_DATA,buttLeft+73, 9,color);
		addBoundedObject(VIEW_DATA,buttLeft+72,0,8,10);
		if (viewFields.contains(VIEW_SETTINGS)) {color=Color.RED;} else { color=Color.WHITE;} g2.setColor(color);
		g2.drawRect(buttLeft+82,0,8,10);
		text(VIEW_SETTINGS,buttLeft+83, 9,color);
		addBoundedObject(VIEW_SETTINGS,buttLeft+82,0,8,10);
		if (viewFields.contains(VIEW_CONTROL)) {color=Color.RED;} else { color=Color.WHITE;} g2.setColor(color);
		g2.drawRect(buttLeft+92,0,8,10);
		text(VIEW_CONTROL,buttLeft+93, 9,color);
		addBoundedObject(VIEW_CONTROL,buttLeft+92,0,8,10);
		g2.setColor(Color.WHITE);
		//	 octave inc / dec contorls
		g2.drawRect(buttLeft+100,0,45,10);
		text("-",buttLeft+102, 9);
		text("O:"+octave,buttLeft+111, 9);
		text("+",buttLeft+136, 9);
		addBoundedObject(OCTAVEDN,buttLeft+100,0,10,10);
		addBoundedObject(OCTAVEUP,buttLeft+135,0,10,10);
		MIDIDeviceCfgBean mdcb=(MIDIDeviceCfgBean)timeLine.midiDeviceConfigMaps.get(currMIDIDev);
		if (mdcb!=null&&mdcb.getImage()!=null) {
			g2.drawImage(mdcb.getImage(),0,TOP_BAR_OFFSET,null);
			if (selTop>-1){
				text(selLeft+":"+selTop+":"+selWidth+":"+selHeight,this.img.getWidth()-80,10);
				g2.drawRect(this.img.getWidth()-90,0,10,10);
				addBoundedObject(COPY,this.img.getWidth()-90,0,10,10);
				g2.drawRect(selLeft,selTop+TOP_BAR_OFFSET,selWidth,selHeight);
			}
			text(curX+":"+curY,this.img.getWidth()-140,10,Color.RED);
			// clear hover area if outside.
			if (currHoverArea!=null &&!checkInside(currHoverArea)) {
				currHoverArea=null;
				currHoverEl=null;
			}
			Iterator ctlIter=mdcb.getControlCfg().keySet().iterator();
			Vector drawLast=new Vector();
			while (ctlIter.hasNext() || drawLast.size()>0) {
				if (ctlIter.hasNext()) {
					MIDIControlCfgBean mccb=(MIDIControlCfgBean)mdcb.getControlCfg().get(ctlIter.next());
					boolean drawLastBool=drawControl(mccb,false);
					if (drawLastBool) {
						drawLast.add(mccb);
					}
				} else {
					drawControl((MIDIControlCfgBean)drawLast.get(0),true);
					drawLast.remove(0);
				}
			}
			if (currHoverEl!=null) {
				drawControl(currHoverEl,true);
			}
			if (mdcb.getMidiNoteCfgBean()!=null) {
				g2.setColor(Color.GRAY);
				MIDINoteCfgBean mncb=mdcb.getMidiNoteCfgBean();
				g2.drawRect(mncb.getLeft(),mncb.getTop()+TOP_BAR_OFFSET,mncb.getWidth(),mncb.getHeight());
				int numKeys=mncb.getOctaves()*7+1;
				double keyWidth=(double)mncb.getWidth()/(double)numKeys;
				int left=mncb.getLeft();
				//boolean roundUp=false;
				int noteNum=60+this.octave*12-12;
				
				String[][] midiNoteMap=(String[][])timeLine.currentMidiNoteMaps.get(currMIDIDev);
				for (int k=0;k<numKeys;k++) {
					// draw black keys.
					if (k%7==1 || k%7==2 ||k%7==4 ||k%7==5 ||k%7==6) {
						int keyLeft=(int)Math.round((left+k*keyWidth)-mncb.getBlackKeyWidth()/2);
						g2.drawRect(keyLeft, mncb.getTop()+TOP_BAR_OFFSET, mncb.getBlackKeyWidth(), mncb.getBlackKeyHeight());
						MIDICtlNoteCfgBean mcncb=new MIDICtlNoteCfgBean(keyLeft, mncb.getTop(), mncb.getBlackKeyWidth(), mncb.getBlackKeyHeight());
						if (midiNoteMap!=null) {
							String text=((midiNoteMap[part][noteNum]!=null)?midiNoteMap[part][noteNum]:"[+]")+" ["+noteNum+"]";
							drawKeyText(text+(checkInside(mcncb)?"*":""),mcncb,checkInside(mcncb),true,noteNum);
						}
						noteNum++;
					}
					int keyLeft=(int)Math.round(left+k*keyWidth);
					//draw white keys.
					g2.drawRect(keyLeft, mncb.getTop()+TOP_BAR_OFFSET, (int)Math.round(keyWidth), mncb.getHeight());
					MIDICtlNoteCfgBean mcncb=new MIDICtlNoteCfgBean(keyLeft, mncb.getTop(), (int)Math.round(keyWidth), mncb.getHeight());
					if (midiNoteMap!=null) {
						String text=((midiNoteMap[part][noteNum]!=null)?midiNoteMap[part][noteNum]:"[+]")+" ["+noteNum+"]";
						drawKeyText(text+(checkInside(mcncb)?"*":""),mcncb,checkInside(mcncb),false,noteNum);
					}
					noteNum++;
				}
				
				//drawTexts(timeLine.midiUtil.dumpMidiNoteMap(),mncb,  checkInside(mncb));
			}
			if (dragPoint!=null) {
				g2.setColor(Color.YELLOW);
				g2.drawLine( (int)dragPoint.getX(), (int)dragPoint.getY(), curX, curY );
			}
		}
		return this.img;
	}
	
	private boolean drawControl(MIDIControlCfgBean mccb,boolean highlight) {
		boolean retval=false;
		g2.setColor(Color.DARK_GRAY);
		Vector texts= new Vector();
		Vector bindings= new Vector();
		g2.drawRect(mccb.getLeft(),mccb.getTop()+TOP_BAR_OFFSET,mccb.getWidth(),mccb.getHeight());
		addBoundedObject(mccb,mccb.getLeft(),mccb.getTop()+TOP_BAR_OFFSET,mccb.getWidth(),mccb.getHeight());
		HashMap midiDev=(HashMap)timeLine.currentMidiControlMaps.get(currMIDIDev);
		if (	!highlight && checkInside(mccb)) {
			if (currHoverEl==null) {
				currHoverEl=mccb;
			}
			return true;
		}
		Color textColor=Color.WHITE;
		if (highlight) {		textColor=Color.RED;	}
		if (this.viewFields.contains(VIEW_NUM)){
			texts.add("["+mccb.getControlNum()+"] ");
			bindings.add(null);
		}
		if (this.viewFields.contains(VIEW_NAME)) {			
			texts.add(mccb.getControlText());
			bindings.add(null);
		} 
		if (midiDev!=null) {
			Vector v=(Vector) (midiDev).get(part+"_"+mccb.getControlNum());
			if (v!=null) {
				for (int i=0;i<v.size();i++) {
					ControlSettings cs=(ControlSettings) v.get(i);
					if (this.viewFields.contains(VIEW_OSC)) {
						texts.add(cs.getOscMsg());
						bindings.add(cs);
					}
					if (this.viewFields.contains(VIEW_DATA)) {
						texts.add(""+cs.value);
						bindings.add(cs);
					}
					if (this.viewFields.contains(VIEW_SETTINGS)) {
						texts.add("s:"+cs.getScale()+",o:"+cs.getOffset()+",oi:"+cs.getOscIndex()+",t:"+cs.getType());
						bindings.add(cs);
					}
				}
			}
		}
		if (texts.size()>0) {drawTexts(texts,bindings,mccb,highlight);}
		// draw ctl
		if (this.viewFields.contains(VIEW_CONTROL)){
			g2.setColor(Color.yellow);
			int bottom=mccb.getTop()+mccb.getHeight();
			
			int centreX = mccb.getLeft()+mccb.getWidth()/2;
			int centreY = mccb.getTop()+mccb.getHeight()/2;
			switch (mccb.getType()){
				case MIDIControlCfgBean.TYPE_VSLIDER:
					g2.drawRect(mccb.getLeft(),
								(int)(bottom+
										TOP_BAR_OFFSET- // starts at bottom
										((mccb.getWinValue()/127.0)*mccb.getHeight())),
								mccb.getWidth(),
								3);
					break;
					
				case MIDIControlCfgBean.TYPE_HSLIDER:
					g2.drawRect(
							(int)(mccb.getLeft()+
									((mccb.getWinValue()/127.0)*mccb.getWidth())),
									mccb.getTop(),
									3,
									mccb.getHeight());
					break;
					
				case MIDIControlCfgBean.TYPE_KNOB:
					g2.drawOval(mccb.getLeft(), 
							mccb.getTop()+
							TOP_BAR_OFFSET, 
							mccb.getWidth(), 
							mccb.getHeight());
					double angle = mccb.getWinValue()/127.0*320.0+10;
					g2.drawLine(centreX, 
							centreY+
							TOP_BAR_OFFSET, 
							(int)(centreX - (mccb.getWidth()/2)*Math.sin(angle/180*Math.PI)), 
							(int)(centreY +
									TOP_BAR_OFFSET +(mccb.getHeight()/2)*Math.cos(angle/180*Math.PI)));
					
					break;
					
				case MIDIControlCfgBean.TYPE_BUTTON:
					if (mccb.getWinValue()>0) {
						g2.setColor(Color.red);
					}
					g2.drawOval(mccb.getLeft(), 
							mccb.getTop()+
							TOP_BAR_OFFSET, 
							mccb.getWidth(), 
							mccb.getHeight());
					
					break;
			}
			// end draw ctl
		}
		return retval;
	}

	private void drawTexts(Vector texts,Vector bindings,MIDIControlCfgBean mccb,boolean highlight) {
		Color oldColor=g2.getColor();
		int width=1;
		for (int i=0;i<texts.size();i++) {
			width=Math.max(width,fontMetrics.stringWidth((String)texts.get(i)));
		}
		if (highlight) {
			int width0=Math.max(width,fontMetrics.stringWidth((String)texts.get(0)));
			if (width<width0+10){width+=10;}
		}
		
		int left=mccb.getLeft()+1;
		if (highlight && (left+width>this.img.getWidth())) {
			left+=(this.img.getWidth()-(left+width));
		}
		Color txtColour=Color.WHITE;
		if (highlight) {
			txtColour=Color.RED;
			g2.setColor(Color.BLACK);
			g2.fillRect(left,mccb.getTop()+TOP_BAR_OFFSET,width,texts.size()*9+2);
			g2.setColor(Color.WHITE);
			g2.drawRect(left,mccb.getTop()+TOP_BAR_OFFSET,width,texts.size()*9+2);
			if (mccb==currHoverEl) {
				text("[+]",left+width-13,mccb.getTop()+TOP_BAR_OFFSET+9,txtColour);
				addBoundedObject("~"+mccb.getControlNum(),left+width-13,mccb.getTop()+TOP_BAR_OFFSET,13,10);
			}
			if (currHoverArea==null) {
				currHoverArea=new MIDICtlNoteCfgBean();
				currHoverArea.setLeft(left);
				currHoverArea.setTop(mccb.getTop());
				currHoverArea.setWidth(width);
				currHoverArea.setHeight(texts.size()*9+2);
			}
		}
		for (int i=0;i<texts.size();i++) {
				text((String) texts.get(i),left,mccb.getTop()+TOP_BAR_OFFSET+9+(i*9),txtColour);
				if (bindings.get(i)!=null) {
					addBoundedObject(bindings.get(i),left,mccb.getTop()+TOP_BAR_OFFSET+(i*9),width,9);
				} 
		}
		g2.setColor(oldColor);
	}
	
	private void drawKeyText(String text,MIDICtlNoteCfgBean mccb,boolean highlight,boolean blackKey,int ctlNum) {
		Color oldColor=g2.getColor();
		//int left=mccb.getLeft()+1;
		Color txtColour=Color.WHITE;
		if (!blackKey) {txtColour=Color.BLACK;}
		if (highlight) {
			txtColour=Color.RED;
			g2.setColor(Color.BLACK);
			g2.fillRect(mccb.getLeft(),mccb.getTop()+TOP_BAR_OFFSET,mccb.getWidth(),mccb.getHeight());
			g2.setColor(Color.WHITE);
			g2.drawRect(mccb.getLeft(),mccb.getTop()+TOP_BAR_OFFSET,mccb.getWidth(),mccb.getHeight());
		}
		g2.setTransform(AffineTransform.getRotateInstance(-Math.PI/2,(double)(mccb.getLeft()+13) ,(double)mccb.getTop()+TOP_BAR_OFFSET+mccb.getHeight()));
		text(text,mccb.getLeft()+13,mccb.getTop()+TOP_BAR_OFFSET+mccb.getHeight(),txtColour);
		int keyBottom=mccb.getTop()+TOP_BAR_OFFSET+mccb.getHeight();
		addBoundedObject("#"+ctlNum,mccb.getLeft(),keyBottom-50,13,keyBottom);
		g2.setTransform(new AffineTransform());
		g2.setColor(oldColor);
	}
	private boolean checkInside (MIDICtlNoteCfgBean mcncb) {
		return checkInside ( mcncb,false);
	
	}
	private boolean checkInside (MIDICtlNoteCfgBean mcncb,boolean debug) {
		
		boolean result=false;
		if ((curX>mcncb.getLeft())&& 
				(curX<mcncb.getLeft()+mcncb.getWidth()) && 
				(curY-TOP_BAR_OFFSET>mcncb.getTop()) && 
				(curY-TOP_BAR_OFFSET<mcncb.getTop()+mcncb.getHeight())) {
			result= true;
		}
		//System.out.println(curX+":"+curY+":"+mcncb.getLeft()+":"+(mcncb.getLeft()+mcncb.getWidth())+":"+mcncb.getTop()+":"+(mcncb.getTop()+mcncb.getHeight())+":"+result);
		return result;
	}
	public void setCurrMIDIDev(String currMIDIDev) {
		this.currMIDIDev = currMIDIDev;
		MIDIDeviceCfgBean mdcb=(MIDIDeviceCfgBean)timeLine.midiDeviceConfigMaps.get(currMIDIDev);
		selTop=-1;selLeft=-1;selWidth=-1;selHeight=-1;
		if (mdcb!=null && mdcb.getImage()!=null) {
			this.setWidth(mdcb.getImage().getWidth(null));
			this.setHeight(mdcb.getImage().getHeight(null));
			this.img = new BufferedImage(this.getWidth(),this.getHeight()+10,BufferedImage.TYPE_INT_BGR);
			this.g2 = (Graphics2D)this.img.getGraphics();
			this.fontMetrics=this.g2.getFontMetrics();
		}
	}
	public String getCurrMIDIDev() {
		return currMIDIDev;
	}
	
	
	public String getCurrMapEdit() {
		return currMapEdit;
	}

	public void setCurrMapEdit(String currMapEdit) {
		this.currMapEdit = currMapEdit;
	}
	
}
