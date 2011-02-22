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
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.silicontransit.timeline.model.Event;
import com.silicontransit.timeline.model.TimeLineObject;
import com.silicontransit.timeline.model.TimeLineSet;
import com.silicontransit.timeline.util.SwingStyler;

public class PropertiesEditorMaps {
	public static HashMap properties = new HashMap();
	public static Vector timelineProps = new Vector();
	public static Vector eventProps = new Vector();
	public static Vector groupProps = new Vector();
	public static Vector setProps = new Vector();
	public static Vector filterProps = new Vector();
	public static Vector midiCtlProps = new Vector();
	public static Vector midiNoteProps = new Vector();
	static {
		timelineProps.add( new Object[] {"id",JTextField.class, null, null});
		timelineProps.add( new Object[] {"timeLineLength",JSpinner.class, null, null});
		timelineProps.add( new Object[] {"quantize",JSpinner.class, null, null});
		timelineProps.add( new Object[] {"beatLength",JSpinner.class, null, null});
		timelineProps.add( new Object[] {"beatPerBar",JSpinner.class ,null, null});
		//timelineProps.add( new Object[] {"parameters",JTextField.class, null});
		timelineProps.add( new Object[] {"oscIndex",JComboBox.class, "OSCPort", null});
		timelineProps.add( new Object[] {"pitch",JTextField.class, null, null});
		timelineProps.add( new Object[] {"colour",SwingStyler.ColorField.class, null, "colour"});
		timelineProps.add( new Object[] {"followOnExpr",JTextField.class, null, "exprHelper"});
		
		eventProps.add( new Object[] {"id",JTextField.class,null, null});
		eventProps.add(new Object[] {"oscMsgName", JTextArea.class, null,  "exprHelper"});
		eventProps.add(new Object[] {"value", JTextArea.class, null,  "exprHelper"});
		eventProps.add(new Object[] {"target", JComboBox.class, "timeline", null});
		eventProps.add(new Object[] {"targetPlayMode",JComboBox.class, "playmode", null});
		eventProps.add(new Object[] {"active", JCheckBox.class, null, null});
		eventProps.add( new Object[] {"eventTime",JSpinner.class, null, null});
		eventProps.add( new Object[] {"oscIndex",JComboBox.class, "OSCPort", null});
		
		groupProps.add( new Object[] {"id",JTextField.class});
		groupProps.add(new Object[] {"indexes", JTextField.class});
		
		setProps.add( new Object[] {"id",JTextField.class, null, null});
		
		filterProps.add( new Object[] {"trigger",JTextField.class});
		filterProps.add( new Object[] {"active",JCheckBox.class});
		filterProps.add( new Object[] {"expresstion",JTextField.class});
		
		//midiCtlProps.add( new Object[] {"part",JComboBox.class, "midipart", null});
		//midiCtlProps.add( new Object[] {"control",JComboBox.class, "midicontrol", null});
		midiCtlProps.add( new Object[] {"oscIndex",JComboBox.class, "OSCPort", null});
		midiCtlProps.add( new Object[] {"oscMsg",JTextField.class, null, "exprHelper"});
		midiCtlProps.add( new Object[] {"type", JComboBox.class, "midtControlType", null});
		midiCtlProps.add( new Object[] {"scale", JTextField.class, null, null});
		midiCtlProps.add( new Object[] {"offset",JTextField.class, null, null});
		
		midiNoteProps.add(new Object[] {"part", JTextField.class});
		midiNoteProps.add(new Object[] {"start", JTextField.class});
		midiNoteProps.add( new Object[] {"end",JTextField.class});
		midiNoteProps.add( new Object[] {"OSC Index",JTextField.class});
		midiNoteProps.add( new Object[] {"OSC Msg/Expr",JTextField.class});
		
		properties.put("timeline", timelineProps);
		properties.put("event", eventProps);
		properties.put("timelineSet", setProps);
		properties.put("group", groupProps);
		properties.put("filter", filterProps);
		properties.put("midiCtl", midiCtlProps);
		properties.put("midiNote", midiNoteProps);
		
	}
	
}
