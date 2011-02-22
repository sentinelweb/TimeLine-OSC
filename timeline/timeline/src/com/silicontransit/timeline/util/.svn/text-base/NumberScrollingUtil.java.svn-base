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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;

public class NumberScrollingUtil {
	private ActionListener cursorPosListener = null;
	
	public NumberScrollingUtil(ActionListener cursorPosListener) {
		super();
		this.cursorPosListener = cursorPosListener;
	}

	public String getNextNumber(MouseWheelEvent e, NumberCursorData ncd,String inputStr2,int cursorPos) {
		int dirFactor=1;
		if (e.getWheelRotation() > 0) 	{ dirFactor=-1;}
		String newValue="";
		int oldLength = ncd.stringVal.length();
		if (ncd.value instanceof Float) {
			int indexOfDot = ncd.stringVal.indexOf(".");
			float digitsMultiplier = indexOfDot<ncd.posInNumber?
													(indexOfDot - ncd.posInNumber+1) :
														 indexOfDot -( ncd.posInNumber );//oldLength-
			float newValueFloat = ncd.value.floatValue() + (dirFactor * (float) Math.pow(10f, digitsMultiplier-1));
			//newValueFloat = (new BigDecimal(newValueFloat)).setScale(oldLength-indexOfDot).floatValue();
			newValue=""+newValueFloat;
			int oldLenComp = ncd.value.floatValue()>0?oldLength:oldLength-1;
			while (newValue.length()< oldLenComp) {
				newValue+="0";
			}
			if ( newValue.length() > oldLength ) {		cursorPosListener.actionPerformed(new ActionEvent(e, cursorPos++ , ""));	}
		} else {
			int digitsMultiplier = oldLength - ncd.posInNumber;
			int newValueInt = ncd.value.intValue() + (dirFactor * (int) Math.pow(10f, digitsMultiplier-1));
			newValue=""+newValueInt;
			if ( newValue.length() > oldLength ) {		cursorPosListener.actionPerformed(new ActionEvent(e, cursorPos++ , "")); }
		}	
		return inputStr2.substring( 0, ncd.start ) + newValue +((ncd.end<inputStr2.length())? inputStr2.substring( ncd.end, inputStr2.length() ):"");
	}

	///////////////////////////////// 
	//get the number next to the cursor for use with mouse wheel
	////////////////////////////////
	public class NumberCursorData {
		public int posInNumber = 0;
		public Number value = null ;	
		public String stringVal = null ;	
		public int start = 0;
		public int end = 0;
	}
	
	public NumberCursorData getNumberNearCursor(String inputStr, int cursorPos) {
		char beforeChar = cursorPos>0?inputStr.charAt(cursorPos-1):(char)-1;
		char afterChar = cursorPos<inputStr.length()?inputStr.charAt(cursorPos):(char)-1;
		if (Character.isDigit(beforeChar)||Character.isDigit(afterChar)) {
			NumberCursorData ncd = new NumberCursorData();
			// move left from cursor to get stat of number 
			int start = cursorPos;
			if (start>=inputStr.length() ) {return null;}
			for (; start>-1 ;start--) {
				if ("1234567890.-".indexOf(inputStr.charAt(start))==-1) {start ++;	break;	}
			}
			if (start==-1) {start=0;}
			//	move right of cursor to get the end of the number.
			int end = cursorPos;
			for (; end<inputStr.length() ;end++) {
				if ("1234567890.-".indexOf(inputStr.charAt(end))==-1) { break; }
			}
			if (start>=end) {return null;} 
			String numStr = inputStr.substring(start, end);
			
			try  {
				if (numStr.indexOf(".")>-1) {
					ncd.value = new Float(numStr);
				} else {
					ncd.value = new Integer( numStr);
				}
			} catch (NumberFormatException n) {
				return null;
			}
			ncd.posInNumber = cursorPos-start;
			ncd.stringVal  = numStr;
			ncd.start = start;
			ncd.end = end;
			return ncd;
		}
		return null;
	}
}
