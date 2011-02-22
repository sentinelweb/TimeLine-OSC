package com.silicontransit.timeline.util;
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
import java.util.HashMap;


public class ColorMap extends HashMap {
	public ColorMap() {
		super();
		this.put("",assignDefaultColor()); // empty is white
	}
	
	public Color getColorFor(String oscMessage) {
		Color c = (Color) this.get(oscMessage);
		if (c==null) {
			c=assignDefaultColor();
			this.put(oscMessage,c);
		}
		return c;
	}
	
	public Color getColorFor(String oscMessage, Color defaultColor) {
		Color c = (Color) this.get(oscMessage);
		if (c==null) {			c=defaultColor;		}
		return c;
	}
	
	private static final int[][] colorMap = {		{1,1,1},		{1,0,0},		{1,0,1},		{1,0,0},		{0,0,1},		{0,1,1},		{0,1,0}	};
	
	private Color assignDefaultColor() {
		int seed=this.size();
		boolean notFound=true;
		boolean overlap=false;// this is a hack, need to go trough and remove unused colours from map.
		Color c=null;
		while (notFound) {
			int baseColor = seed%colorMap.length;
			int multiplier = 255 - (seed/colorMap.length)*15;
			if (multiplier<0) {multiplier=255;overlap=true;}
			int r = colorMap[baseColor][0]*multiplier;
			int g = colorMap[baseColor][1]*multiplier;
			int b = colorMap[baseColor][2]*multiplier;
			
			c = new Color(r, g, b);
			if (!this.values().contains(c)|| overlap) {notFound=false;}
			else {seed++;;}
		}
		return c;
	}
}
