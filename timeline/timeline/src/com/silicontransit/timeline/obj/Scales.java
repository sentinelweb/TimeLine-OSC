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
public class Scales {

	// with ref:-
	// http://www.phy.mtu.edu/~suits/scales.html (quite cool)
	//
	
//////////////////////////////////////////////////////////////////////////
	// scales

	
	int majorScale[] = {2,2,1,2,2,2,1};
	int minorScale[] = {2,1,2,2,1,2,2};
	int harmonicMinorScale[] = {2,1,2,2,1,3,1};
	int majorPentatonicScale[] = {2,2,3,2,3};
	int minorPentatonicScale[] = {3,2,2,3,2};
	int wholeNoteHextonicScale[] = {2,2,2,2,2,2};
	int bluesHextonicScale[] = {3,2,1,1,3,2};
	// the current scale.
	int currentScale[]=minorScale;
	
	
	public static double equal=Math.pow(2,1/12.0);
	private float justFactors[]= {1, (25.0f/24.0f) , (9.0f/8.0f) , (6.0f/5.0f) , (5.0f/4.0f) , (4.0f/3.0f) , (45f/32.0f) ,(3.0f/2.0f) , (8.0f/5.0f) , (5.0f/3.0f) , (9f/5.0f) , (15f/8.0f), 2f};

	int ctr=-1;
	int scaleCtr=0;
	int scaleAccum=0;
	float freqBase=60.0f;
	int key = 0; // note modifier
	
	public void setFreqBase(Float f) {freqBase = f.floatValue();}
	public Float getFreqBase() {return new Float(freqBase);}

	
	// get equal temperament factors.
	public float getEqFact(int i) {    return (float)Math.pow(2,i/12.0);}
	// get Just temprament factor
	public double getJustFact(int i) {return justFactors[i];}

	public float getNote(Integer  numIntervals) {
	     float fact = (float)getEqFact(numIntervals%12);
	     return fact;
	}
	
	public float getNote() {
	    ctr=++ctr%12;
	    return getNote(ctr); 
	}
	

	
	public float getOctaveFactor(int  numIntervals) {
	   int fact=(numIntervals)/12;
	   return (float) Math.pow(2,(float) fact);
	}
	
	public Float playScale() {
	     scaleCtr = (++scaleCtr) % currentScale.length;
	     scaleAccum+=currentScale[scaleCtr];
	     scaleAccum%=50;
	     return (float) getOctaveFactor(scaleAccum)*getNote(scaleAccum%12);
	}
	
	public float freqScale() {
	    playScale();
	    float freq = this.freqBase;
	     freq=freq*getOctaveFactor(scaleAccum)*getNote(scaleAccum%12);
	    return freq;
	}

	public void resetScale() {
		scaleCtr = 0; 
		scaleAccum = 0;
	}

	public Float getNoteFreq(java.lang.Integer index) {
		int theNote = index+key;
		//long start = System.nanoTime();
		int accum=0;
		for (int i=0;i<theNote;i++) {	accum+=currentScale[i%currentScale.length];	}
		float note = getNote(accum%12);
		double scaleMutliplier = Math.pow(2.0f,(float)(accum/12)); 
		float op=(float)freqBase*((float)scaleMutliplier)*note;
		Float f = new Float(op);
		return  f;
	}
	
	public void setScale(String scaleName) {
		if (scaleName.equals("major")) {
			currentScale = majorScale;
		} else if (scaleName.equals("minor")) {
			currentScale = minorScale;
		}else if (scaleName.equals("harmonicMinor")) {
			currentScale = harmonicMinorScale;
		}else if (scaleName.equals("majorPentatonic")) {
			currentScale = majorPentatonicScale;
		}else if (scaleName.equals("minorPentatonic")) {
			currentScale = minorPentatonicScale;
		}else if (scaleName.equals("wholeNoteHextonic")) {
			currentScale = wholeNoteHextonicScale;
		}else if (scaleName.equals("bluesHextonic")) {
			currentScale = bluesHextonicScale;
		}else {
			System.out.println("setScale: not scale named "+scaleName);
		}
	}
	public Integer getKey() {
		return key;
	}
	public void setKey(Integer key) {
		this.key = key.intValue();
	}
	
}
