/*
 * Created on 05-Oct-2007
 */
package com.silicontransit.timeline.window.helpers;
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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.silicontransit.timeline.TimeLine;
import com.silicontransit.timeline.model.ControlSettings;
import com.silicontransit.timeline.util.SwingStyler;

/**
 * @author munror
 *
 */
public class ControlHelper extends InputHelper{
	
	JTextField scale;
	JTextField offset;
	JTextField oscMsg;
	private JComboBox partSelector;
	private JComboBox controlSelector;
	private JComboBox devSelector;
	private JComboBox typeSel;
	private JComboBox oscSelector;
	private ExprHelper paramHelper;
	/**
	 * @param type
	 */
	public ControlHelper(TimeLine t) {
		super(TYPE_CONTROL,t,false);
	}


	public void buildComponents() {
		partSelector = getMidiPartSelector();
		addField("Part",partSelector);
		//devSelector = getMidiDeviceSelector();
		//addField("Device",partSelector);
		controlSelector = getMidiControlSelector(timeLine.currentMIDIInputDevice);
		addField("Control",controlSelector);
		oscSelector = getOSCPortSelector();
		addField("Osc Index", oscSelector);
		oscMsg = getTextField(TEXT_TYPE_NORMAL);
		oscMsg.setPreferredSize(new Dimension(130,15));
		JPanel oscMsgPanel = addField("Osc Message", oscMsg);
		 SwingStyler.ImgButton exprButton = new  SwingStyler.ImgButton("...");
		exprButton.setName("exprBut");
		exprButton.setToolTipText("Expression help");
		SwingStyler.setStyleButton(exprButton);
		exprButton.setPreferredSize(new Dimension(20,15));
		exprButton.addActionListener(new ExprButListner(oscMsg));
		oscMsgPanel.add(exprButton);
		scale = getTextField(TEXT_TYPE_DOUBLE);
		addField("Scale", scale);
		offset = getTextField(TEXT_TYPE_DOUBLE);
		addField("Offset", offset);
		typeSel = getSelector(midiTypeOpts);
		addField("Type", typeSel);
		thisFrame.setTitle("Control Helper");
	}

	public String getInput() {
		return input.getText();
	}


	public void setInput(String input) {
		setMidiControlSelector( timeLine.currentMIDIInputDevice, controlSelector );
		ControlSettings cs = new ControlSettings();
		cs.parseControlStr(input);
		setByValue(partSelector, ""+cs.part);
		setByValue(controlSelector, ""+cs.control);
		setByValue(typeSel, ""+cs.type);
		setByValue(oscSelector, ""+cs.oscIndex);
		oscMsg.setText(cs.oscMsg);
		scale.setText(""+cs.scale);
		offset.setText(""+cs.offset);
	}


	public void updateSelection(EventObject ae) {
//		if (ae.getSource()==controlSelector) {
//			try {
//				String dev=getSelectedValue(controlSelector);
//				t.midiWin.setMidiDevice(dev);
//			} catch (NumberFormatException e) {	}
//		}
		String text=getSelectedValue(partSelector)+" "+
							getSelectedValue(controlSelector)+" "+
							getSelectedValue(oscSelector)+" "+
							oscMsg.getText()+" "+
							scale.getText()+" "+
							offset.getText()+" "+
							getSelectedValue(typeSel);
		input.setText(text);
	}
	public class ExprOkButListner implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource().equals("OK")) {
					oscMsg.setText(paramHelper.getInput());
				} else {
					String text=oscMsg.getText();
					oscMsg.setText(
						text.substring(0,oscMsg.getSelectionStart())+
						paramHelper.getInput()+
						text.substring(oscMsg.getSelectionEnd())
					);
					e.setSource(oscMsg);
					updateSelection(e);
				}
			}
		}
	
		public class ExprButListner implements ActionListener {
				private JTextField field;
		
				public ExprButListner(JTextField txt) {	field = txt;}
		
				public void actionPerformed(ActionEvent e) {
					if (paramHelper==null) {paramHelper = new ExprHelper(timeLine,false,true);}
					if (oscMsg.getText().startsWith("$")) {
						paramHelper.setInput(oscMsg.getText());
					} else {	paramHelper.setInput("");	}
					paramHelper.setVisible(true);
					paramHelper.setOkAction(new ExprOkButListner());
				}
			}
}
