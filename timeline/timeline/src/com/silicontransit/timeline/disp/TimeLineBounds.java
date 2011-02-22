
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
public class TimeLineBounds extends WindowSectionObject {
	
	public TimeLineBounds(int w, int h, int l, int t) {
		super(w, h, l, t);
	}
	
	public void clearBoundedObjects() {
		super.clearBoundedObjects();
	}

	public void addBoundedObject(WindowSectionObject dspObj) {
		dspObj.setTop(getTotalHeight());
		super.addBoundedObject(dspObj);
	}
	
	public void setWidth(int width) {;
		for (int i=0; i<boundsArray.size(); i++) {
			DisplayObject.BoundedObject dispObj = (DisplayObject.BoundedObject)boundsArray.get(i);
			((DisplayObject)  dispObj.boundedObject).width = width;
			// need to update bounds too.
			dispObj.right=width;
			dispObj.w=width;
		}
	}
	
	public int  getTotalHeight() {
		int height = 0;
		for (int i=0; i<boundsArray.size(); i++) {
			DisplayObject.BoundedObject dispObj = (DisplayObject.BoundedObject)boundsArray.get(i);
			 height += ((DisplayObject)  dispObj.boundedObject).height;
		}
		return height;
	}
	
}
