/*
 * Created on 26-Oct-2007
 */
package com.silicontransit.timeline.obj;
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
import java.util.HashMap;
import java.util.Iterator;

/**
 * Maintains a hashMap of assigned indexes to pitch keys.
 * @author munror
 */
public class Poly {
	int poly = -1;
	HashMap bank = new HashMap();
	
	/**
	 * Have to search through the hashMap for the lowest non-assinged index when found assign the ptich value 
	 * set the poly filed to the assigned index
	 * @param num
	 */
	public void noteOn(int pitch) {
		int allocatedIndex=-1;
		for (int i=0; allocatedIndex==-1; i++) {
			boolean found = false;
			for (Iterator bankIter = bank.keySet().iterator(); bankIter.hasNext();) {
				Integer pitchInt = (Integer) bankIter.next();
				Integer assigned = (Integer) bank.get(pitchInt);
				if (assigned.intValue()==i) {found=true;}
			}
			if (!found) {allocatedIndex = i;}
		}
		 bank.put(new Integer(pitch), new Integer(allocatedIndex));
		 poly = allocatedIndex;
	}
	public void noteOn(Integer pitch) {noteOn(pitch.intValue());}
	
	/**
	 * De Allocate index for this pitch value.
	 * set the poly field to the de-assigned index
	 * @param pitch
	 */
	public void noteOff(int pitch) {
		Integer remove=null;
		for (Iterator bankIter = bank.keySet().iterator(); bankIter.hasNext();) {
			Integer pitchInt = (Integer) bankIter.next();
			Integer assigned = (Integer) bank.get(pitchInt);
			if (pitchInt.intValue()==pitch) {
				poly=assigned.intValue();
				remove=pitchInt;
				break;
			}
		}
		if (remove!=null) {bank.remove(remove);}
	}
	public void noteOff(Integer pitch) {noteOff(pitch.intValue());}
	
	public Integer index() {
		return new Integer(poly);
	}
	public void reset() {
		poly=-1;
	}
}
