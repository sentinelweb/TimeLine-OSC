/*
 * Created on 05-Oct-2007
 *
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
import com.silicontransit.timeline.util.SwingStyler;

/**
 * @author munror
 */
public class NoteHelper extends InputHelper {
	

	private JComboBox partSelector;
	private JComboBox startSelector;
	private JComboBox endSelector;
	private JComboBox oscSelector;
	public ExprHelper paramHelper;
	
	JTextField oscMsg;
	public NoteHelper(TimeLine t) {
		super(TYPE_NOTE,t,false);
	}
	
	public void buildComponents() {
		partSelector = getMidiPartSelector();
		addField("Part",partSelector);
		//devSelector = getMidiDeviceSelector();
		//addField("Device",partSelector);
		startSelector = getMidiNoteSelector();
		addField("Start Note",startSelector);
		endSelector = getMidiNoteSelector();
		addField("End Note",endSelector);
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
		thisFrame.setTitle("Note Helper");
	}

	public String getInput() {
		return input.getText();
	}

	public void setInput(String input) {
		String[] midiRangeAndMsg=input.split(" ");
		if (midiRangeAndMsg.length>3) {
			setByValue(partSelector, midiRangeAndMsg[0]);
			setByValue(startSelector, midiRangeAndMsg[1]);
			setByValue(endSelector, midiRangeAndMsg[2]);
			setByValue(oscSelector, midiRangeAndMsg[3]);
		}
		if (midiRangeAndMsg.length>4) oscMsg.setText(midiRangeAndMsg[4]);
	}

	public void updateSelection(EventObject ae) {
		String text=getSelectedValue(partSelector)+" "+
								getSelectedValue(startSelector)+" "+
								getSelectedValue(endSelector)+" "+
								getSelectedValue(oscSelector)+" "+
								oscMsg.getText();
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
