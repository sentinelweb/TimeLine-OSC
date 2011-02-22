/*
 * Created on Jul 17, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
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
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import com.silicontransit.timeline.MenuListing;
import com.silicontransit.timeline.TimeLine;
import com.silicontransit.timeline.bean.FilterBean;
import com.silicontransit.timeline.model.ControlSettings;

public class PopupWindowDraw extends DisplayObject{
	private static int SCROLLBAR_WIDTH=10;
	private Vector texts=new Vector();  
	private Vector displayTexts=texts;  
	private Vector displayColors=null;
	private String title;
	private boolean visible;
	private int scrollPos=0;
	private boolean scrolling=false;
	
	public PopupWindowDraw(int w, int h, int l, int t) {
		super(w, h, l, t);
	}

	public Image getImage(TimeLine t) {
		clearImage();
		clearBoundedObjects();
		g2.setColor(Color.white);
		g2.drawRect(0,0,this.width-1,this.height-1);
		boolean scrollBarVisible=false;
		// draw scrollbar if nessecary.
		if (texts.size()*10 >= this.height) {
			g2.setColor(new Color(100,100,100)); 
			g2.fillRect(this.width-1-SCROLLBAR_WIDTH,1,SCROLLBAR_WIDTH,this.height-2);
			float pcScrolledDown=(float)scrollPos/(float)texts.size();
			int scrollPosY=1+Math.round(pcScrolledDown*(this.height-2));
			g2.setColor(new Color(200,200,200)); 
			g2.fillRect(this.width-SCROLLBAR_WIDTH,scrollPosY,SCROLLBAR_WIDTH-1,10);
		}
		g2.setColor(Color.white);
		text( this.title==null? "Popup" : this.title, 2, 8 );
		g2.drawLine(1, 9, this.width-1-(scrollBarVisible?1:0)*SCROLLBAR_WIDTH, 9);
		Vector theTexts=displayTexts;
		if (displayTexts==null || displayTexts.size() == 0) { theTexts=texts; }
		for (int i=scrollPos;i<theTexts.size();i++) {
			int realIndex=i-scrollPos;
			text(theTexts.get(i).toString(), 2, 18+10*(realIndex),  (displayColors==null) ? Color.lightGray:((Color) displayColors.get(i)));
			addBoundedObject(new Integer(i), 2, 18+10*(realIndex-1), this.width-1-SCROLLBAR_WIDTH, 10);
		}
		return this.img;	
	}
	
	public void reset() {
		this.setVisible(false);
		this.setTitle(null);
		this.scrollPos=0;
		if (texts.size()>0) {
			texts.clear();
			displayTexts.clear();
			displayColors=null;
		}
	}
	
	public void setScroll(boolean down) {
		this.scrollPos+=(down)?1:-1;
		if (this.scrollPos<0) {this.scrollPos=0;}
		if (this.scrollPos>(texts.size()-4)) {this.scrollPos=texts.size()-4;}
	}

	public Vector getTexts() {		return texts;	}
	public void setTexts(Vector vector) {		texts = vector;	}
	public String getTitle() {		return title;	}
	public void setTitle(String string) {		title = string;	}
	public boolean isVisible() {		return visible;	}
	public void setVisible(boolean b) {		visible = b;	}
	public boolean isScrolling() {		return scrolling;	}
	public void setScrolling(boolean b) {		scrolling = b;	}
	public Vector getDisplayTexts() {		return displayTexts;	}
	public void setDisplayTexts(Vector vector) {		displayTexts = vector;	}
	
	public void setPopupDisplay(TimeLine t) {//, TimeLineObject timeLineObj
		// TODO move this into its own object 
		if ("N".equals(t.inputMode)) {
			displayTexts=new Vector();
			displayColors = null;
			setTexts(t.notes);
			setVisible(true);
			setTitle("Notes");
		} else if ("=".equals(t.inputMode)) {
			displayTexts=new Vector();
			displayColors = null;
			if (getTitle()==null) {
				Vector v=t.midiUtil.dumpMidiCtlMap();
				Vector texts=new Vector();
				Vector disp=new Vector();
				for ( int n=0;n < v.size(); n++ ) {
					Object o=v.get(n);
					if (o instanceof ControlSettings) {
						texts.add(((ControlSettings)o).toString());
						disp.add(((ControlSettings)o).toDisplayString());
					} else {
						texts.add(o);
						disp.add(o);
					}
				}
				setTexts(texts);
				setDisplayTexts(disp);
			}
			setTitle("MIDI control map");
			setVisible(true);
		}
		else if ("+".equals(t.inputMode)) {
			displayTexts=new Vector();
			displayColors = null;
			if (getTitle()==null) {
				setTexts(t.midiUtil.dumpMidiNoteMap());
			}
			setTitle("MIDI note map");
			setVisible(true);
		} else if ("K".equals(t.inputMode)) {
			setTitle("Color map");
			texts=new Vector();
			displayTexts=new Vector();
			displayColors = new Vector();
			// osc msg colors.
			texts.add("OSC Messages:"); displayColors.add(Color.WHITE);
			Vector oscVec = new Vector(t.oscMessageColorMap.keySet());
			Collections.sort(oscVec);
			texts.addAll(oscVec);
			for (int i=0; i < oscVec.size(); i++) {
				displayColors.add(t.oscMessageColorMap.get(oscVec.get(i)));
			}
			//	expr colors.
			 texts.add("Expressions:"); displayColors.add(Color.WHITE);
			 Vector exprVec = new Vector(t.expressionColorMap.keySet());
			 Collections.sort(exprVec);
			 texts.addAll(exprVec);
			 for (int i=0; i < exprVec.size(); i++) {
				 displayColors.add(t.expressionColorMap.get(exprVec.get(i)));
			 }
			 setTitle("Colour map");
			 setVisible(true);
		} else if ("y".equals(t.inputMode)) {
			displayTexts = new Vector();
			displayColors = null;
			if (getTitle()==null) {
				setFilterTexts(t);
			}
			setTitle("Filter map");
			setVisible(true);
		} else if ("O".equals(t.inputMode)) {
			texts=new Vector();
			displayTexts = new Vector();
			displayColors = null;
			for (Iterator objIter = t.dynamicObjects.keySet().iterator(); objIter.hasNext();) {
				String key = (String) objIter.next();
				Object obj = t.dynamicObjects.get(key);
				texts.add(key);
				if (obj!=null) {
					displayTexts.add(key+" = "+obj.getClass().getName());
				}
			}
			 setTitle("Object map");
			 setVisible(true);
		} else if ("T".equals(t.inputMode) && t.timeLineObject!=null) {
			displayTexts = new Vector();
			displayColors = null;
			if (getTitle()==null) {
				setTexts(t.timeLineObject.parameters);
			}
			setTitle("TimeLine "+t.timeLineObject.id+" Parameters");
			setVisible(true);
		}
		 else if (!"".equals(t.inputMode)&&"123456789X".indexOf(t.inputMode)>-1) {
			 texts=new Vector();
			 displayColors = null;
			displayTexts = new Vector();
			// if (getTitle()==null) {
				 //Iterator itr=t.midiKeyBindings.keySet().iterator();
				 Vector v=new Vector();
				for (int i=1;i<10;i++) {
					//String ky=(String)itr.next();
					String binding= (String)t.midiKeyBindings.get(""+i);
					v.add(i +" - "+(binding!=null?binding:""));
				}
				setTexts(v);
			//}
			setTitle("MIDI Key Bindings");
			setVisible(true);
		}else if (t.menu !=null){
			Vector texts = new Vector();
			String title = "Menu";
			if (t.menu != MenuListing.menu) {
				for (int k=0;k<MenuListing.menu.size();k++) {
					Object[] menuItem = (Object[]) MenuListing.menu.get(k);
					if (menuItem[1]==t.menu) {title=(String)menuItem[0] +" menu";}
				}
			}
			for (int j=0;j<t.menu.size();j++) {
				if (t.menu == MenuListing.menu) {
					Object[] menuItem = (Object[]) t.menu.get(j);
					texts.add(menuItem[0]);
					
				} else {
					String[] menuItem = (String[]) t.menu.get(j);
					texts.add(menuItem[0] +"(" +menuItem[1]+")");
					
				}
			}
			setTexts(texts);
			setTitle(title);
			setVisible(true);
		}
		else {	
			reset();
		}
		// // display logic for sliderWindow.
//		if (!"".equals(t.inputMode) && "vw".indexOf(t.inputMode)>-1) {
//		} else {
//			t.slidersDraw.reset();
//		}
		
//		if (isVisible()){
//			g2.drawImage(
//				getImage(t),
//				0,0, Color.black, null
//			);
//		}
	}
	public void setFilterTexts(TimeLine t) {
			Vector filterTexts= new Vector();
			Iterator filterIter=t.filters.keySet().iterator();
			while(filterIter.hasNext()) {
				String filterTgt=(String)filterIter.next();
				Vector filters=(Vector)t.filters.get(filterTgt);
				for (int x=0;x<filters.size();x++) {
					FilterBean fb=(FilterBean)filters.get(x);
					filterTexts.add((fb.isActive()?"*":"!")+" "+filterTgt+" "+fb.getExpr());
				}
			}
			setTexts(filterTexts);
		}
}
