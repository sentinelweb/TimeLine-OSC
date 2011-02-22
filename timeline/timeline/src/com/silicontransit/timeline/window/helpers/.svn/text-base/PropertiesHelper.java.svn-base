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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.BoxLayout; 
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import com.silicontransit.timeline.PropertiesEditorMaps;
import com.silicontransit.timeline.TimeLine;
import com.silicontransit.timeline.model.PropertySettable;
import com.silicontransit.timeline.util.NumberScrollingUtil;
import com.silicontransit.timeline.util.SwingStyler;
//import com.sun.java.swing.SwingUtilities2;
//import com.sun.java.swing.SwingUtilities3;;

public class PropertiesHelper extends InputHelper {
	
	private Object currentObject = null;
	private Object currentObjectType = null;
	public ExprHelper exprGetHelper = null;
	//public ExprHelper exprSetHelper = null;
	private JTextComponent targetComponentForHelper = null;
	private Vector components = new Vector();
	
	public NumberScrollingUtil numberScrollingUtil = null;
	private MouseWheelListener textMouseWheelListener = null;
	private JTextComponent numberScrollTarget = null;
	public PropertiesHelper(TimeLine t) {
		super(TYPE_PROPS, t, false);
		exprGetHelper = new ExprHelper(t,true);	
		exprGetHelper.setOkAction(new ExprHelperActionListener());
		//exprSetHelper = new ExprHelper(t,false,false);	
		//exprSetHelper.setOkAction(new ExprHelperActionListener());
		numberScrollingUtil = new NumberScrollingUtil(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (numberScrollTarget!=null) {
					numberScrollTarget.setSelectionStart(e.getModifiers());
				}
			}
		});
		textMouseWheelListener = new TextMouseWheelListener();
	}
	
	public PropertiesHelper(int type, TimeLine t, boolean insert) {
		super(TYPE_PROPS, t, insert);
		exprGetHelper = new ExprHelper(t,true);	
		exprGetHelper.setOkAction(new ExprHelperActionListener());
		//exprSetHelper = new ExprHelper(t,false,false);	
		//exprSetHelper.setOkAction(new ExprHelperActionListener());
	}


	@Override
	public void buildComponents() {
		input.setEnabled(false);
		fieldPanel.setLayout(new BoxLayout(fieldPanel,BoxLayout.PAGE_AXIS));
		fieldPanel.setBackground(SwingStyler.backgroundColor);
		thisFrame.setTitle("Properties Helper");
		thisFrame.setPreferredSize(new Dimension(400, 330));
		ComponentListener cl = new WindowResizeHandler(); 
		thisFrame.addComponentListener(cl);
	}

	@Override
	public String getInput() {
		
		return null;
	}

	@Override
	public void setInput(String input) {
		
	}

	public void setObject(Object o,String type) {
		this.currentObject = o;
		this.currentObjectType = type;
		this.input.setText(type);
		removeFields();
		buildFields();
		thisFrame.repaint();
	}
	
	private void removeFields() {
		fieldPanel.removeAll();
	}
	private class SpinChangeListener implements ChangeListener{

		public void stateChanged(ChangeEvent e) {
			if (!suppressSelectionEvents) updateSelection( e );
		}
	}
	
	private void buildFields() {
		Vector props = (Vector)PropertiesEditorMaps.properties.get(this.currentObjectType);
		components.clear();
		for (int i=0;props!=null && i<props.size();i++) {
			PropertySettable objectSet = ((PropertySettable)this.currentObject  );
			
			Object[] propCfg= (Object[]) props.get(i);
			try {
				JComponent objectField = null;
				if (propCfg[1]!=JComboBox.class) {
					objectField =(JComponent) ((Class) propCfg[1]).newInstance();
					objectField.setName((String) propCfg[0]);
				} else {
					if ("OSCPort".equals(propCfg[2])) {
						objectField = getOSCPortSelector();
					} else if ("timeline".equals(propCfg[2])){
						objectField = getTimelineSelector();
						((JComboBox)objectField).addItem(new SelBean("",null));
					}else if ("playmode".equals(propCfg[2])){
						objectField = getSelector(playModeOpts);
					}else if ("midipart".equals(propCfg[2])){
						objectField = getMidiPartSelector();
					}
					else if ("midicontrol".equals(propCfg[2])){
						objectField = getMidiControlSelector(timeLine.currentMIDIInputDevice);
					}
					else if ("midtControlType".equals(propCfg[2])){
						objectField = getSelector(midiTypeOpts);
					}
					objectField.setName((String) propCfg[0]);
				}
				if (objectField instanceof JTextField) {
					((JTextField)objectField).addKeyListener(new InputKeyHandler());
					objectField.addMouseWheelListener(textMouseWheelListener);
				}else if (objectField instanceof JTextArea) {
					((JTextArea)objectField).addKeyListener(new InputKeyHandler());
					objectField.addMouseWheelListener(textMouseWheelListener);
				}else if (objectField instanceof JSpinner) {
					((JSpinner)objectField).addKeyListener(new InputKeyHandler());
					((JSpinner)objectField).addChangeListener(new SpinChangeListener());
					objectField.addMouseWheelListener(textMouseWheelListener);
				}else if (objectField instanceof JCheckBox) {
					((JCheckBox)objectField).addChangeListener(new SpinChangeListener());
				}
				SwingStyler.setStyleInput( objectField);
				suppressSelectionEvents=true;  // dont trigger update.
				setComponentValue(objectField, objectSet.getProperty((String) propCfg[0]));
				suppressSelectionEvents=false;
				JPanel objPanel =null;
				if (objectField instanceof JTextArea) {
					objectField.setPreferredSize(null);
					JScrollPane scrollPane = new JScrollPane(objectField);
					JPanel panel = new JPanel();
					panel.setLayout(new GridLayout(1,1));
					//panel.add(scrollPane);
					panel.add(objectField);
					panel.setPreferredSize(new Dimension(150,15));
					
					//scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(10,10));
					//scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10,10));
					//scrollPane.setAutoscrolls(true);
					//scrollPane.setPreferredSize(new Dimension(150,50));
					components.add(panel);
					objPanel = addField( (String) propCfg[0] , panel );
					setTextBoxHeight((JTextArea)objectField);
				} else {
				
					components.add(objectField);
					objPanel = addField( (String) propCfg[0] , objectField );
				}
				
				
				if (propCfg[3]!=null ){
					if (objectField instanceof SwingStyler.ColorField ) {
						JButton but = new SwingStyler.ImgButton("...");
						SwingStyler.setStyleButton(but);
						but.setPreferredSize(new Dimension(40,15));
						objPanel.add(but);
						but.addActionListener(new ColorHelperLauncher((SwingStyler.ColorField)objectField));
					} else 	if (objectField instanceof JTextComponent) {
						JButton but = new SwingStyler.ImgButton("...");
						SwingStyler.setStyleButton(but);
						but.setPreferredSize(new Dimension(40,15));
						objPanel.add(but);
						but.addActionListener(new ExprHelperLauncher((JTextComponent)objectField));
					}  
				} 
				fieldPanel.add(objPanel);
			} catch (InstantiationException e) {

			} catch (IllegalAccessException e) {

			}
		}
		setCmpSizes();
		fieldPanel.validate();
	}
	
	@Override
	public void updateSelection(EventObject ae) {
		if (this.currentObject instanceof PropertySettable) {
			PropertySettable objectSet = ((PropertySettable)this.currentObject  );
			Object setValue = getComponentValue((JComponent)ae.getSource());
			
			String prop = ((JComponent )ae.getSource()).getName();
			if (objectSet.checkBounds(prop, setValue)) {
				objectSet.setProperty(prop, setValue);
				 timeLine.markDirty(true);
			} else {
				setComponentValue((JComponent)ae.getSource(), objectSet.getProperty(prop));
			}
			if (ae.getSource() instanceof JTextArea) {
				setTextBoxHeight((JTextArea)ae.getSource());
			}
		}
	}

	private void setTextBoxHeight(JTextArea txtArea) {
		int lineCount = txtArea.getText().split("\n").length+1;
		JComponent sizeComp = (JComponent) txtArea.getParent();//.getParent();
		sizeComp.setPreferredSize(new Dimension((int)sizeComp.getPreferredSize().getWidth() , lineCount*15));
	}
	
	private Object getComponentValue(JComponent comp) {
		Object setValue = "";
		 if (comp instanceof SwingStyler.ColorField) {
			setValue =((SwingStyler.ColorField)comp).getColor();
		}else if (comp instanceof JTextField) {
			setValue = ((JTextField)comp).getText();
		}else if (comp instanceof JTextArea) {
			setValue = ((JTextArea)comp).getText();
		}else if (comp instanceof JSpinner) {
			setValue = ((JSpinner)comp).getValue();
		}else if (comp instanceof JCheckBox) {
			setValue = ((JCheckBox)comp).isSelected();
		}else if (comp instanceof JComboBox) {
			setValue = ((SelBean)((JComboBox)comp).getSelectedItem()).getObject();
		}
		return setValue;
	}
	
	private void setComponentValue(JComponent comp,Object value) {
		if (value==null) return; 
		 if (comp instanceof SwingStyler.ColorField) {
			((SwingStyler.ColorField)comp).setColor((Color)value);
		}else if (comp instanceof JTextField) {
			 ((JTextField)comp).setText(value.toString());
		}else if (comp instanceof JTextArea) {
			 ((JTextArea)comp).setText(value.toString());
		}else if (comp instanceof JSpinner) {
			if (value instanceof Integer ) {
				((JSpinner)comp).setValue(value);
			} else if (value instanceof String) {
				try {
					((JSpinner)comp).setValue(Integer.parseInt((String)value));
				} catch (NumberFormatException e) {
					((JSpinner)comp).setValue(0);
				}
			} else {
				((JSpinner)comp).setValue(0);
			}
		}else if (comp instanceof JCheckBox) {
			 ((JCheckBox)comp).setSelected((Boolean)value);
		}else if (comp instanceof JComboBox) {
			setByObject((JComboBox)comp,value);
		}
	}

	public class ExprHelperActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			boolean insert=e.getSource().equals("Insert");
			if (insert) {
				String curTxt = targetComponentForHelper.getText();
				String text = curTxt.substring(0, targetComponentForHelper.getSelectionStart())+
									(exprGetHelper.getInput())+
									 curTxt.substring(targetComponentForHelper.getSelectionEnd(),curTxt.length());
				targetComponentForHelper.setText(text);
			} else {
				targetComponentForHelper.setText(exprGetHelper.getInput());
			}
			updateSelection(new ActionEvent(targetComponentForHelper,0,""));
		}
	}
	
	public class ExprHelperLauncher implements ActionListener {
		JTextComponent target = null;
		public ExprHelperLauncher(JTextComponent c) {
			target =c;
		}
		public void actionPerformed(ActionEvent e) {
			targetComponentForHelper = target;
			String selectedText = target.getSelectedText();
			if (selectedText==null) {selectedText="";}
			exprGetHelper.setInput(selectedText.replaceAll("\\n", ""));
			exprGetHelper.setVisible(true);
		}
	}

	public class ColorHelperLauncher implements ActionListener {
		SwingStyler.ColorField target = null;
		
		public ColorHelperLauncher(SwingStyler.ColorField c) {
			target =c;
		}
		
		public void actionPerformed(ActionEvent e) {
			ColorChooserThread cct=new ColorChooserThread();
			cct.start();
		}
		
		private class ColorChooserThread extends Thread{
			public  Color startColor = Color.white;
			public void run() {
				this.setName("ColorChooserThread");
				Color c=JColorChooser.showDialog(thisFrame,"Choose color", startColor);
				target.setColor(c);
				updateSelection(new ActionEvent(target,0,""));
			}
		}
	}
	private void setCmpSizes() {
		int width = (int) Math.round(thisFrame.getWidth());
		int winHeight=(int) Math.round(thisFrame.getHeight());
		if (components==null) {System.out .println("null"); return; }
		for (int i=0;i<components.size();i++) {
			JComponent c = (JComponent)components.get(i);
			int cmpWidth=width - 120;
			if (c.getParent().getComponentCount()>2) {cmpWidth-=50;}
			c.setPreferredSize(new Dimension(cmpWidth , (int) c.getPreferredSize().getHeight()));
		}
	}
	private class ResizeRunable implements Runnable {		public void run() {		setCmpSizes();		}			}
	
	private class WindowResizeHandler implements ComponentListener {
		public void componentHidden(ComponentEvent e) {		}
		public void componentMoved(ComponentEvent e) {	}
		public void componentResized(ComponentEvent e) {
			SwingUtilities.invokeLater(new ResizeRunable());
		}
		public void componentShown(ComponentEvent e) { }
	 }
	
	private class TextMouseWheelListener implements MouseWheelListener {
		public void mouseWheelMoved(MouseWheelEvent e) {
			//add code to handle JSpinner
			if (!(e.getSource() instanceof JTextComponent)) {return;}
			JTextComponent textComp  = (JTextComponent) e.getSource();
			NumberScrollingUtil.NumberCursorData ncd  = numberScrollingUtil.getNumberNearCursor( textComp.getText(), textComp.getSelectionStart() ); 
			if ( ncd!=null ) {						
				//numberScrollingUtil.
				numberScrollTarget = textComp;
				String inputStr = numberScrollingUtil.getNextNumber( e, ncd,textComp.getText(), textComp.getSelectionStart() );
				int cursorPos = textComp.getSelectionStart();
				textComp.setText(inputStr);
				textComp.setSelectionStart( cursorPos );
				textComp.setSelectionEnd( cursorPos );
				updateSelection(new ActionEvent(textComp,0,""));
			} 
		}
	}
}
