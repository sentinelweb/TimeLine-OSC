package com.silicontransit.timeline.model;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public class Cue {
	private HashMap cue = new HashMap();
	private HashSet cuedObjects = new HashSet();
	public Cue() {	super();	}
	//TODO need to add playMode for the cuedObjects so can show on button.
	public void addToCue(TimeLineObject trig, Cueable toCue, String playMode, boolean replace,boolean stop) {
		toCue.setCueMode(playMode);
		toCue.setStop(stop);
		//CueObject cueObject = new CueObject( toCue, playMode);
		Vector waiting=(Vector)cue.get(trig);
		if (waiting == null) {
			waiting = new Vector();
		}
		if (replace) {waiting.clear();}
		if (!waiting.contains(toCue)) {
			waiting.add(toCue);
		}
		cuedObjects.add(toCue);
		cue.put(trig, waiting);
	}
	
	public Vector checkQue(TimeLineObject trig) {
		Vector waiting = (Vector) cue.get( trig );
		if (waiting == null) {waiting=new Vector();}
		return waiting;
	}
	
	public boolean checkQued(Cueable toCue) {
		return cuedObjects.contains(toCue);
	}
	
	public boolean checkQued(TimeLineObject trig, Cueable toCue) {
		Vector waiting = (Vector) cue.get( trig );
		if (waiting != null) {return waiting.contains(toCue);}
		else return false;
	}
	
	public void deCue(TimeLineObject trig, Cueable toCue){
		Vector waiting = (Vector) cue.get(trig);
		if (waiting!=null) {
			waiting.remove(toCue);	
			checkAndRemoveIfNotQuedElsewhere(toCue);
		}
	}
	
	public void deCueTriger(TimeLineObject trig){
		Vector waiting = (Vector) cue.get(trig);
		cue.remove(trig);
		if (waiting!=null) {
			for (Iterator waitIter = waiting.iterator(); waitIter.hasNext();) {
				Cueable cueObj = (Cueable) waitIter.next();
				checkAndRemoveIfNotQuedElsewhere(cueObj);
			}
		}
	}
	
	public void deCueEveryWhere(Cueable toCue){
		for (Iterator iterator = cue.keySet().iterator(); iterator.hasNext();) {
			TimeLineObject trig = (TimeLineObject) iterator.next();
			Vector waiting= (Vector)cue.get(trig);
			Vector toRemove=new Vector();
			for (Iterator waitIter = waiting.iterator(); waitIter.hasNext();) {
				Cueable cueObj = (Cueable) waitIter.next();
				if (cueObj == toCue) {	toRemove.add(toCue);		}
			}
			waiting.removeAll(toRemove);
		}
		cuedObjects.remove(toCue);
	}


	
	private void checkAndRemoveIfNotQuedElsewhere(Cueable toCue) {
		boolean removedEveryWhere=true;
		Vector waiting=null;
		for (Iterator iterator = cue.keySet().iterator();  iterator.hasNext(); ) {
			waiting = (Vector)cue.get(iterator.next());
			for (Iterator waitIter = waiting.iterator(); waitIter.hasNext();) {
				Cueable cueObj = (Cueable) waitIter.next();
				if (cueObj==toCue) {	removedEveryWhere=false;		}
			}
		}
		if (removedEveryWhere) {cuedObjects.remove(toCue);}
	}

	public HashMap getCue() {	return cue;}

}
