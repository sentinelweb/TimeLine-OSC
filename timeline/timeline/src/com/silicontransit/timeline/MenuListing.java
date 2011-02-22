/*
 * Created on 26-Sep-2007
 *
 */
package com.silicontransit.timeline;
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
import java.util.Vector;

/**
 * @author munror
 * 
 */
public class MenuListing {
	public static Vector menu = new Vector();
	public static Vector fileMenu = new Vector();
	public static Vector editMenu = new Vector();
	public static Vector selectMenu = new Vector();
	public static Vector globalMenu = new Vector();
	public static Vector timelineMenu = new Vector();
	public static Vector eventMenu = new Vector();
	public static Vector playMenu = new Vector();
	public static Vector midiMenu = new Vector();
	public static Vector setMenu = new Vector();
	
	static {
		menu.add(new Object[] {"File",fileMenu});
		menu.add(new Object[] {"Edit",editMenu});
		menu.add(new Object[] {"Select / Group",selectMenu});
		menu.add(new Object[] {"Global",globalMenu});
		menu.add(new Object[] {"TimeLine",timelineMenu});
		menu.add(new Object[] {"Event",eventMenu});
		menu.add(new Object[] {"Play / Record",playMenu});
		menu.add(new Object[] {"MIDI",midiMenu});
		menu.add(new Object[] {"Set", setMenu});
		
		fileMenu.add( new String[] {"Open",			"o",		"Open selected file"} );
		fileMenu.add( new String[] {"FileName",		"f",		"Set filename"} );
		fileMenu.add( new String[] {"Save",			"s",		"Save file"} );
		fileMenu.add( new String[] {"Append file",	"F",		"Appends all timeline in a file"} );
		fileMenu.add( new String[] {"Erase everything",			"E",		"Clear everytihng and start afresh"} );
		
		editMenu.add( new String[] {"Undo",				"u",		"Undo last"} );
		editMenu.add( new String[] {"Redo",				"alt+u",		"Redo undone"} );
		editMenu.add( new String[] {"Clear undo",		"alt+U",		"Clear undo list"} );
		editMenu.add( new String[] {"Toggle undo",		"U",		"Toggles undo file creation"} );
		editMenu.add( new String[] {"Delete events",	"d",		"Delete event(s)"} );
		editMenu.add( new String[] {"Delete Timeline",	"D",		"Delete timeline"} );		
		editMenu.add( new String[] {"Copy events",		"c",		"Copy event"} );
		editMenu.add( new String[] {"Copy Timeline",	"C",		"Delete timeline"} );		
		editMenu.add( new String[] {"Copy",				"ctrl+c",		"Copy selected event(s)"} );		
		editMenu.add( new String[] {"Paste",			"ctrl+v",		"Paste event(s) copied"} );		
		editMenu.add( new String[] {"Set Value Index",	"V",		"Set the index of the value to edit"} );		
		//editMenu.add( new String[] {"",			"",		""} );
		
		selectMenu.add( new String[] {"(De)Select",			"g",		"(De)Select event"} );	
		selectMenu.add( new String[] {"(De)Select same",	"alt+g",	"(De)Select same event(s)"} );	
		selectMenu.add( new String[] {"Group",				"ctrl+g",	"Group "} );
		//selectMenu.add( new String[] {"",			"",		""} );
		
		globalMenu.add( new String[] {"Set filter",		"y",		"Set a filter (append - to delete)"} );
		globalMenu.add( new String[] {"Objects",			"O",		"Show objects (click and append - to del)"} );
		globalMenu.add( new String[] {"Note",			"N",		"Add note (append - to delete)"} );
		
		
		timelineMenu.add( new String[] {"Set length",			"t",		"Set timeline length"} );	
		timelineMenu.add( new String[] {"Set quantization",	"q",		"Set quantization"} );	
		timelineMenu.add( new String[] {"Set OSC Index",		"M",		"Set the default OSC index for this tiimeline"} );	
		timelineMenu.add( new String[] {"Set name",			"n",		"Set Timeline name"} );	
		timelineMenu.add( new String[] {"Set parameters",		"T",		"Set timeline parameters"} );	
		timelineMenu.add( new String[] {"Set Beats/Bar",		"b",		"Set beat and bar length"} );
		timelineMenu.add( new String[] {"Set BPM",				"B",		"Set beats per minute"} );
		timelineMenu.add( new String[] {"Set pitch",			"w",		"Set the speed multiplier"} );
		timelineMenu.add( new String[] {"Normailize to pitch",	"W",		"Normalize the timeline length to this pitch ( [Yy]/n )"} );
		timelineMenu.add( new String[] {"Set color",			"k",		"set timeline color"} );
		timelineMenu.add( new String[] {"Rebuild",				"*",		"Rebuild optimisation vector"} );
		timelineMenu.add( new String[] {"Goto Root",			")",		"Goto Root TImeline"} );
		timelineMenu.add( new String[] {"Position at start",	"0",		"Set the position to the start"} );
		timelineMenu.add( new String[] {"Goto child",			"`",		"Goes the the event target timeline"} );
		timelineMenu.add( new String[] {"Goto parent",			"`",		"Goes back to the parent timeline"} );
		timelineMenu.add( new String[] {"Next timeline",		"x",		"Goes to the next timeline"} );
		timelineMenu.add( new String[] {"Goto parent",			"z",		"Goes to the previous timeline"} );
		timelineMenu.add( new String[] {"Follow on expr",			"I",		"Expression executed at the end of play (not loop)"} );
		
		
		eventMenu.add( new String[] {"OSC Msg",			"/",		"Set OSC Message name"} );
		eventMenu.add( new String[] {"Set target",		"e",		"Set target timeline"} );
		eventMenu.add( new String[] {"Set value",		"v",		"Set the value"} );
		eventMenu.add( new String[] {"(De)Activate",	"a",		"(De)Activate event"} );
		eventMenu.add( new String[] {"(De)Activate same",	"ctrl+a",		"(De)Activate event"} );
		eventMenu.add( new String[] {"Trigger",			" ",		"Trigger event"} );
		eventMenu.add( new String[] {"Set name",		"alt+n",	"Sets the event name"} );
		//eventMenu.add( new String[] {"",			"",		""} );
		
		
		playMenu.add( new String[] {"Play",			"p",		"Play"} );
		playMenu.add( new String[] {"Loop",			"l",		"Loop"} );
		playMenu.add( new String[] {"Stop play",	"P",		"Stop play all"} );
		playMenu.add( new String[] {"Record",		"r",		"Record midi"} );
		playMenu.add( new String[] {"Record granularity",			"R",		"Set record granuarity (int)"} );
		playMenu.add( new String[] {"Record input",			"alt+r",		"Set record input (str)"} );
		//playMenu.add( new String[] {"",			"",		""} );
		
		midiMenu.add( new String[] {"MIDI control",	"=",		"Set a MIDI controller mapping (str) (append - to delete)"} );
		midiMenu.add( new String[] {"MIDI Note",		"+",			"Set a MIDI note mapping (str) "} );
		midiMenu.add( new String[] {"Edit map id",			"h",		"Set the map id for the currently selected MIDI map "} );
		midiMenu.add( new String[] {"Delete map",			"H",		"Delete the currently selected MIDI map"} );
		midiMenu.add( new String[] {"Map 1",		"1",		"Switch to map set 1"} );
		midiMenu.add( new String[] {"Map 2",		"2",		"Switch to map set 2"} );
		midiMenu.add( new String[] {"Map 3",		"3",		"Switch to map set 3"} );
		midiMenu.add( new String[] {"Map 4",		"4",		"Switch to map set 4"} );
		midiMenu.add( new String[] {"Map 5",		"5",		"Switch to map set 5"} );
		midiMenu.add( new String[] {"Map 6",		"6",		"Switch to map set 6"} );
		midiMenu.add( new String[] {"Map 7",		"7",		"Switch to map set 7"} );
		midiMenu.add( new String[] {"Map 8",		"8",		"Switch to map set 8"} );
		midiMenu.add( new String[] {"Map 9",		"9",		"Switch to map set 9"} );
		midiMenu.add( new String[] {"Add Map 1",		"alt+1",		"Map id to bind to key 1 ( - to delete)"} );
		midiMenu.add( new String[] {"Add Map 2",		"alt+2",		"Map id to bind to key 2 ( - to delete)"} );
		midiMenu.add( new String[] {"Add Map 3",		"alt+3",		"Map id to bind to key 3 ( - to delete)"} );
		midiMenu.add( new String[] {"Add Map 4",		"alt+4",		"Map id to bind to key 4 ( - to delete)"} );
		midiMenu.add( new String[] {"Add Map 5",		"alt+5",		"Map id to bind to key 5 ( - to delete)"} );
		midiMenu.add( new String[] {"Add Map 6",		"alt+6",		"Map id to bind to key 6 ( - to delete)"} );
		midiMenu.add( new String[] {"Add Map 7",		"alt+7",		"Map id to bind to key 7 ( - to delete)"} );
		midiMenu.add( new String[] {"Add Map 8",		"alt+8",		"Map id to bind to key 8 ( - to delete)"} );
		midiMenu.add( new String[] {"Add Map 9",		"alt+9",		"Map id to bind to key 9 ( - to delete)"} );
		//midiMenu.add( new String[] {"",			"",		""} );
		
		setMenu.add( new String[] {"Create set",				"S",		"Enter set name (- to delete)"} );
		
		
		
		
	}
}
