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
import java.awt.Image;
import java.util.Iterator;
import java.util.Vector;

import com.silicontransit.timeline.bean.DebugBean;
import com.silicontransit.timeline.util.ColorMap;

public class DebugDraw extends DisplayObject {
	FontMetrics metrics;
	private ColorMap oscMap = null;
	private ColorMap exprMap = null;
	public DebugDraw(int w, int h, int l, int t, ColorMap oscMap, ColorMap exprMap) {
		super(w, h, l, t);
		metrics = g2.getFontMetrics();
		this.oscMap=oscMap;
		this.exprMap=exprMap;
	}
	
	public Image getImage(DebugBean debugBean) {
		clearImage();
		clearBoundedObjects();
		
		drawButton(0,0,49,10, DebugBean.SHOW_OSC, debugBean.getMode().equals(DebugBean.SHOW_OSC), "", DebugBean.SHOW_OSC,Color.WHITE,Color.gray);
		drawButton(50,0,49,10, DebugBean.SHOW_EXPR, debugBean.getMode().equals(DebugBean.SHOW_EXPR), "", DebugBean.SHOW_EXPR,Color.WHITE,Color.gray);
	//	drawButton(100,0,49,10, DebugBean.SHOW_LOG, debugBean.getMode().equals(DebugBean.SHOW_LOG), "", DebugBean.SHOW_LOG,Color.WHITE,Color.gray);
		
		g2.drawLine(0,10,300,10);
		debugBean.setDrawing(true);
		if (debugBean.getMode().equals(DebugBean.SHOW_OSC)) {
			debugBean.setDrawing(true);
			int ctr=1;
			Iterator i = debugBean.getOscMessages().keySet().iterator();
			while (i.hasNext()) {
				String oscMsg=(String)i.next();
				String val=(String)debugBean.getOscMessages().get(oscMsg);
				String oscMessageString = (oscMsg.indexOf(" ")>-1) ? oscMsg.substring( 0, oscMsg.indexOf(" ") ) : oscMsg ;
				text( oscMsg+" : "+val, 0, ++ctr*10, oscMap.getColorFor( oscMessageString , Color.lightGray ) );
			}
		} else if (debugBean.getMode().equals(DebugBean.SHOW_EXPR)) {
			debugBean.setDrawing(true);
			int ctr=1;
			Iterator i = debugBean.getExpressions().keySet().iterator();
			while (i.hasNext()) {
				String expr=(String)i.next();
				Vector val=(Vector)debugBean.getExpressions().get(expr);
				text(expr+" "+val.get(0)+" "+val.get(1),0, ++ctr*10, exprMap.getColorFor(expr, Color.lightGray));
			}
		} 
//		else if (debugBean.getMode().equals(DebugBean.SHOW_LOG)) {
//			int ctr=1;
//			for (int i = debugBean.getLog().size()-1;i > (debugBean.getLog().size()-debugBean.getLogDispLines()) && i>=0; i--) {
//				text((String)debugBean.getLog().get(i),0, ++ctr*10);
//			}
//		}
		debugBean.setDrawing(false);
		debugBean.setDirty(false);
		return this.img;
	}
}
