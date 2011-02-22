/*

 */
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
import com.silicontransit.timeline.TimeLine;
import com.silicontransit.timeline.model.TimeLineObject;

/**
 * @author munror
 *
 */
public class CalcUtil {
	private TimeLine t=null;
	public CalcUtil(TimeLine t) {
		this.t=t;
	}
	public int calculateBPM(TimeLineObject t) {
		int bpm=0;
		bpm = Math.round(1000f*60f/(t.quantize*t.beatLength));
		return bpm;
	}
	
	public void setBPM(TimeLineObject t, int bpm ) {
		int  timelineBeatLength=t.quantize*t.beatLength; // ms
		int bpmBeatLength=Math.round(1000f*60f/bpm);  // ms
		if (timelineBeatLength==bpmBeatLength) {return;}
		else if (bpmBeatLength%t.quantize==0) {
			t.beatLength = bpmBeatLength/t.quantize;
		} else { // adjust quantization
			t.quantize=Math.round((float)bpmBeatLength / t.beatLength);
		}
	}
}
