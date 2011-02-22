/*
 * Created on 09-Oct-2007
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author munror
 *
 * Doesnt really need to be a seperate class but i think it make it a bit easier to understand by splitting this stuff out .
 */
public class WindowSectionObject extends DisplayObject {
	//	for height change listener.
	private WindowSectionObject parentObject = null;
	public ActionListener changeListner;
	public WindowSectionObject(int w, int h, int l, int t) {
		super(w, h, l, t);

	}
	
	// this implements a listner to check if a height change in a componet should fire a listner event
	public ActionListener getChangeListner() {		return changeListner;	}
	public void setChangeListner(ActionListener listener) {		changeListner = listener;	}
	
	public void setHeightAndNotifyChangeIfNessecary(int height) {
		int oldHeight = this.height;
		this.height = height;
		if (oldHeight !=height  && parentObject!=null){
			parentObject.updatePositions();
			if (parentObject.getChangeListner()!=null) {
				ActionEvent e =  new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "heightChange") ;
				parentObject.changeListner.actionPerformed(e);
			}
		}
	}
	
	public void addBoundedObject(WindowSectionObject dspObj) {
		dspObj.parentObject=this;
		addBoundedObject( dspObj, dspObj.left, dspObj.top, dspObj.width, dspObj.height ); 
	}
	
	public void updatePositions() {
		int height = 0;
		for (int i=0; i<boundsArray.size(); i++) {
			 //DisplayObject dispObj = (DisplayObject)boundsArray.get(i);
			 DisplayObject.BoundedObject dispObj = (DisplayObject.BoundedObject)boundsArray.get(i);
			 ((DisplayObject)dispObj.boundedObject).top=height;
			 height += ((DisplayObject)dispObj.boundedObject).height;
		}
	}
	
}
