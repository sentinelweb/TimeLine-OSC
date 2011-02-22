
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Vector;

import com.silicontransit.timeline.TimeLine;
import com.silicontransit.timeline.bean.SliderBean;


public class SlidersDraw extends DisplayObject {
	private static final Color activateColor = new Color(0,150,0);
	private Vector sliders=new Vector();
	private boolean visible;
	private String  title;
	private SliderBean sliding=null;
	private boolean send=false;
	int yPosShiftPressed=-1;
	// wil be statics.
	int sliderSpacing=30;
	int butWidth=10;
	int butHeight=4;
	int sliderTop=10;
	int sliderHeight=60;
	NumberFormat n=new DecimalFormat("####.00");
	private boolean changed=false;

	public SlidersDraw(int w, int h, int l, int t) {
		super(w, h, l, t);
	}

	public Image getImage(TimeLine t) {
		clearImage();
		clearBoundedObjects();
		
		g2.setColor(Color.white);
		g2.drawRect(0,0,this.width-1,this.height-1);
		
		text(this.title==null?"Sliders":this.title,2,8);
		g2.drawLine(1,9,this.width-1,9);
		
		this.width=sliders.size()*sliderSpacing+20;
		
		for (int i=0;i<sliders.size();i++) {
			SliderBean s = (SliderBean)sliders.get(i);
			g2.setColor(new Color(150,150,150));
			if (s==sliding) {g2.setColor(Color.WHITE);}
			g2.drawRect(15+sliderSpacing*(i)-1, sliderTop, 2, sliderHeight);// back bar.
			int sliderButTop=Math.round(sliderHeight-(s.getValue()/(s.getRange())*sliderHeight)-(butHeight/2));//sliderTop+
			g2.drawRect(15+sliderSpacing*(i)-butWidth/2+1, sliderButTop, butWidth, butHeight);// slider button.
			text(s.getLabel(), 15+sliderSpacing*(i)-5, 95);
			
			g2.setColor(activateColor);
			g2.fillRect(15+sliderSpacing*(i)-butWidth/2+1+5, sliderButTop, butWidth/2, butHeight);// slider arm.
			
			if (this.send && (s==sliding)) {setTextColor(activateColor);}
			text(""+n.format(s.getValue()), 15+sliderSpacing*(i)-5, 85);
			setTextColor(Color.WHITE);
			//addBoundedObject(s, 15+sliderSpacing*(i)-butWidth/2+1, sliderButTop, butWidth, butHeight);
			addBoundedObject(""+i, 15+sliderSpacing*(i), sliderTop, sliderSpacing-5, sliderHeight);
		}
		
		return this.img;
	}
	
	public boolean isVisible() {		return visible;	}
	public void setVisible(boolean b) {		visible = b;	}
	
	public void reset() {
		if (sliders.size()>0)sliders=new Vector();
		this.visible=false;
		this.title=null;
	}
	
	public int makeSliders(Vector v) {
		sliders=new Vector();
		for (int i=0;i<v.size();i++) {
			Object o = v.get(i);
			if (o instanceof Number) {
				SliderBean s= new SliderBean();
				if (o instanceof Float) {s.setValue(((Float)o).floatValue());s.setNumFloat(true);}
				else {s.setValue((float)((Integer)o).intValue());s.setNumFloat(false);}
				s.setLabel("v:"+i);
				s.setMax(s.getValue()*2+1);
				s.setMin(0);
				s.setArrayIndex(i);
				sliders.add(s);
			}
		}
		return sliders.size();
	}
	
	public void setBackValues(Vector v) {
		for (int i=0;i<sliders.size();i++) {
			SliderBean s= (SliderBean)sliders.get(i);
			if (s.isChanged()){
				Object value=Float.valueOf(n.format(s.getValue()));
				//if (Math.round(s.getValue())==(int)s.getValue()) {value=new Integer(Math.round(s.getValue()));}
				String valStr=((Float)value).toString();
				int testInt=Integer.parseInt(valStr.substring(valStr.indexOf(".")+1));
				if (testInt==0) {value=new Integer(Math.round(s.getValue()));}
				v.set(s.getArrayIndex(),value);
			}
		}
	}
	
	public float getSliderValue(int i){// rounds to 2 d.p
		float f= ((SliderBean)sliders.get(i)).getValue();
		return Float.valueOf(n.format(f)).floatValue();
	}
	
	public void checkSliding(int x,int y) {
		Object o=this.checkForObjectOnScreen(x,y);
		if (o!=null && o instanceof SliderBean) {
			this.sliding=(SliderBean)o;
			int[] pt=getBoundObjectInsideCoord(o,x,y);
			if (pt[0]>5) {
				this.send=true;
			}
		}
	}
	
	public boolean isSliding() {
		return (sliding!=null);
	}
	
	public void stopSliding(){
		// reset slider bounds here.
		if (sliding!=null) {
			float halfRange=Math.abs(sliding.getValue()*2);
			if (halfRange<0.5) {halfRange=0.5f;}
			sliding.setMax(sliding.getValue()+halfRange);
			sliding.setMin(sliding.getValue()-halfRange);
			sliding=null;
			this.send=false;
		}
	}
	public void updateSlider(int x , int y,boolean round, boolean fine){
		if (sliding!=null) {
			sliding.setChanged(true);
			this.changed=true;
			float pcSlidfromBottom=((float)(sliderHeight-(y-sliderTop))) / (float)sliderHeight;
			if (!fine) {
				float newValue=pcSlidfromBottom * sliding.getRange();
				sliding.setValue(newValue);
			} else {
				if (yPosShiftPressed>-1) {// yPosShiftPressed is set in key perssed and key released.
					float multiplier=1f;
					if (y>yPosShiftPressed) {multiplier=-1f;} 
					float newValue=(multiplier*sliding.getRange()*0.003f);
					sliding.setValue(sliding.getValue()+newValue);
				}
			}
	
			if (round) {sliding.setValue(Math.round(sliding.getValue()));}
		}
	}
	
	public void updateSliderWheel(int x , int y,boolean round, boolean fine,  int change){
		Object o=this.checkForObjectOnScreen(x,y);
		if (o!=null && o instanceof String) {
			try {
				int index= Integer.parseInt((String) o);
				SliderBean sb = (SliderBean)sliders.get(index);
				sb.setChanged(true);
				float oldValue=sb.getValue();
				float ratio = Math.abs(sb.getValue()/10);
				if (ratio<0.01 || oldValue==0) {ratio = 0.01f;}
				if (fine) {ratio/=10f;}
				float newValue=sb.getValue()-change*ratio;
				if (round) {
					newValue=Math.round(newValue);
					if (newValue == oldValue) {newValue -= change; }
				}
				sb.setValue(newValue);
				float halfRange=Math.abs(sb.getValue()*2);
				if (halfRange<0.5) {halfRange=0.5f;}
				if (sb.getValue()<0) { halfRange*=-1; }
				sb.setMax(sb.getValue()+halfRange);
				sb.setMin(sb.getValue()-halfRange);
			} catch (Exception e) {}
		}
	}
	
	public Vector getSliders() {		return sliders;	}
	public String getTitle() {		return title;	}
	public void setSliders(Vector vector) {		sliders = vector;	}
	public void setTitle(String string) {		title = string;	}
	public boolean isChanged() {		return changed;	}
	public void setChanged(boolean b) {		changed = b;	}


	public int getYPosShiftPressed() {
		return yPosShiftPressed;
	}

	public void setYPosShiftPressed(int i) {
		yPosShiftPressed = i;
	}

	public boolean isSend() {		return send;	}
	public void setSend(boolean b) {		send = b;	}

}
