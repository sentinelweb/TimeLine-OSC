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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.MouseInputListener;

import com.silicontransit.timeline.TimeLine;
import com.silicontransit.timeline.util.SwingStyler;

public class ExprHelper extends InputHelper {
	private JComboBox typeSelector;
	private JPanel typeSelectorPanel;
	private JComboBox timeLineSelector;
	private JPanel timeLineSelectorPanel;
	private JComboBox timeLineSetSelector;
	private JPanel timeLineSetTypeSelectorPanel;
	private JComboBox timeLineSetTypeSelector;
	private JPanel timeLineSetSelectorPanel;
	private JComboBox timeLineOptsSelector;
	private JPanel timeLineOptsSelectorPanel;
	private JComboBox eventSelector;
	private JPanel eventSelectorPanel;
	private JComboBox eventOptsSelector;
	private JPanel eventOptsSelectorPanel;
	private JComboBox groupSelector;
	private JPanel groupSelectorPanel;
	private JComboBox groupOptsSelector;
	private JPanel groupOptsSelectorPanel;
	private JComboBox midiDeviceSelector;
	private JPanel midiDeviceSelectorPanel;
	private JComboBox midiMapSelector;
	private JPanel midiMapSelectorPanel;
	private JComboBox midiPartSelector;
	private JPanel midiPartSelectorPanel;
	private JComboBox midiControlSelector;
	private JPanel midiControlSelectorPanel;
	private JComboBox midiOptsSelector;
	private JPanel midiOptsSelectorPanel;
	private JComboBox midiSettingsSelector; // the control settings for a part_ctrl combo
	private JPanel midiSettingsSelectorPanel;
	private JComboBox objectSelector;
	private JPanel objectSelectorPanel;
	private JComboBox variableSelector;
	private JPanel variableSelectorPanel;
	private JComboBox methodSelector;
	private JPanel methodSelectorPanel;
	private JCheckBox newObjectChk;
	private JPanel newObjectPanel;
	private JTextField timeLineValTxt;
	private JPanel timeLineValTxtPanel;
	private JTextField eventValTxt;
	private JPanel eventValTxtPanel;
	private Vector parameters = new Vector();
	public ExprHelper paramHelper;
	boolean set = false;
	ObjectHelper objectHelper;
	public ExprHelper( TimeLine t, boolean insert) {
		super(TYPE_EXPR, t,insert);
		 objectHelper = new ObjectHelper(TYPE_OBJECT, t,false);
		 objectHelper.setOkAction(new ObjectOKListener());
	}
	public ExprHelper( TimeLine t,boolean set,boolean insert) {
		this(t,insert);
		this.set=set;
		//super(TYPE_EXPR, t);
	}

	public void buildComponents() {
		typeSelector = getSelector(exprTypes);
		typeSelectorPanel = addField("Experession type",typeSelector);
		timeLineSelector = getTimelineSelector();
		timeLineSelectorPanel = addField("Timeline",timeLineSelector);
		timeLineSetSelector = getTimelineSelector();
		timeLineSetSelectorPanel = addField("Set",timeLineSetSelector);
		timeLineSetTypeSelector = getSelector( setOpts );
		timeLineSetTypeSelectorPanel = addField("Set field",timeLineSetTypeSelector);
		timeLineOptsSelector = getSelector(timelineOpts);
		timeLineOptsSelectorPanel = addField("Timeline Field",timeLineOptsSelector);
		timeLineValTxt =  getTextField(TEXT_TYPE_INT);
		timeLineValTxtPanel = addField("Value index",timeLineValTxt);
		eventSelector = getEventSelector(timeLine.timeLineObject);
		eventSelectorPanel = addField("Event",eventSelector);
		eventOptsSelector = getSelector(eventOpts);
		eventOptsSelectorPanel = addField("Event Field",eventOptsSelector);
		eventValTxt =  getTextField(TEXT_TYPE_INT);
		eventValTxtPanel = addField("Value index",eventValTxt);
		groupSelector = getGroupSelector(timeLine.timeLineObject);
		groupSelectorPanel = addField("Group",groupSelector);
		groupOptsSelector = getSelector(groupOpts);
		groupOptsSelectorPanel = addField("Group Field",groupOptsSelector);
		midiDeviceSelector = getMidiDeviceSelector();
		midiDeviceSelectorPanel = addField("MIDI Device",midiDeviceSelector);
		midiMapSelector = getMidiDeviceSelector();
		midiMapSelectorPanel = addField("MIDI Map",midiMapSelector);
		midiPartSelector = getMidiPartSelector();
		midiPartSelectorPanel = addField("MIDI Part",midiPartSelector);
		midiControlSelector = getMidiControlSelector(timeLine.currentMIDIInputDevice);
		midiControlSelectorPanel = addField("MIDI Control",midiControlSelector);
		midiOptsSelector = getSelector(midiOpts);
		midiOptsSelectorPanel = addField("MIDI Options",midiOptsSelector);
		midiSettingsSelector = getMidiSettingsSelector();
		midiSettingsSelectorPanel = addField("MIDI Settings",midiSettingsSelector);
		objectSelector = getObjectSelector();
		objectSelector.setToolTipText("select or type object class (blank+enter launches object helper)");
		objectSelectorPanel= addField("Object",objectSelector);
		variableSelector = getVariableSelector();
		variableSelector.setToolTipText("Select a variable name or type a new one and hit enter");
		variableSelectorPanel= addField("Variable",variableSelector);
		methodSelector = getMethodSelector();
		methodSelectorPanel= addField("Method",methodSelector);
		newObjectChk =new JCheckBox();
		SwingStyler.setStyleCommon(newObjectChk);
		newObjectChk.addActionListener(selectedObjectHandler);
		newObjectPanel = addField("New object",newObjectChk);
		SwingStyler.ImgButton wrapBracketButton = new  SwingStyler.ImgButton("");
		SwingStyler.setStyleButton(wrapBracketButton);
		wrapBracketButton.setText("{ . . . }");
		wrapBracketButton.setPreferredSize(new Dimension(40,15));
		wrapBracketButton.addActionListener(new WrapBracketButListner());
		wrapBracketButton.setToolTipText("(Un)Wrap { .. }");
		buttonPanel.add(wrapBracketButton);
		thisFrame.setTitle("Expression Helper");
		thisFrame.setPreferredSize(new Dimension(300, 300));
	}

	public String getInput() {
		return input.getText();
	}

	public void setInput(String inputTxt) {
		removeFields();
		parameters.clear();
		fieldPanel.add(typeSelectorPanel); 
		input.setText(inputTxt);
		if ("".equals(inputTxt)) {return;}
		try {
			int endPos=inputTxt.indexOf("[");
			if (endPos==-1) {endPos=inputTxt.length();}
			String[] exprSplit = inputTxt.substring(1,endPos).split(":");
			setByValue(typeSelector, exprSplit[0]);
			if ("s".indexOf(exprSplit[0])>-1) {
				if (exprSplit.length>1) {
					clearObjects(timeLineSetSelector);
					setTimelineSetSelector(timeLineSetSelector);
					setByValue(timeLineSetSelector,exprSplit[1]);
					fieldPanel.add(timeLineSetSelectorPanel);
					if (exprSplit.length>2) {
						setByValue(timeLineSetTypeSelector,exprSplit[2]);
					}
					fieldPanel.add(timeLineSetTypeSelectorPanel);
				}
			} else if ("teg".indexOf(exprSplit[0])>-1) {
				clearObjects(timeLineSelector);
				setTimelineSelector(timeLineSelector);
				fieldPanel.add(timeLineSelectorPanel);
				if (exprSplit.length>1) {
					setByValue(timeLineSelector, exprSplit[1]);
					if ("t".equals(exprSplit[0])) {
						if (exprSplit.length>2) {
							setByValue( timeLineOptsSelector,exprSplit[2]);
						}
						fieldPanel.add(timeLineOptsSelectorPanel);
						if (exprSplit.length>3) {
							timeLineValTxt.setText(exprSplit[3]);
							fieldPanel.add(timeLineValTxtPanel);
						} else {timeLineValTxt.setText("");	}
					} else if ("e".equals(exprSplit[0])){
						if (exprSplit.length>2) {
							clearObjects(eventSelector);
							setEventSelector(timeLine.getTimeline(exprSplit[1]),eventSelector);
							setByValue( eventSelector,exprSplit[2]);
						}
						fieldPanel.add(eventSelectorPanel);
						if (exprSplit.length>3) {
							setByValue( eventOptsSelector, exprSplit[3]);
						}
						fieldPanel.add(eventOptsSelectorPanel);
						if (exprSplit.length>4) {
							eventValTxt.setText(exprSplit[4]);
							fieldPanel.add(eventValTxtPanel);
						} else {eventValTxt.setText("");} 
					} else if ("g".equals(exprSplit[0])){
						if (exprSplit.length>2) {
							clearObjects(groupSelector);
							setGroupSelector(timeLine.getTimeline(exprSplit[1]),groupSelector);
							setByValue( groupSelector,exprSplit[2]);
						}
						fieldPanel.add(groupSelectorPanel);
						if (exprSplit.length>3) {
							setByValue( groupOptsSelector,exprSplit[3]);
						}
						fieldPanel.add(groupOptsSelectorPanel);
					} 
				}
			} else if ("m".equals(exprSplit[0])) {
				if (exprSplit.length>1) {
					setByValue(midiDeviceSelector, exprSplit[1]);
					fieldPanel.add(midiDeviceSelectorPanel);
					if (exprSplit.length>2) {
						String[] ptCtlSplit=exprSplit[2].split("_");
						setByValue(midiPartSelector, ptCtlSplit[0]);
						fieldPanel.add(midiPartSelectorPanel);
						clearObjects(midiControlSelector);
						try {
							setMidiControlSelector(timeLine.midiDeviceNames[Integer.parseInt(exprSplit[1])], midiControlSelector);
						} catch (NumberFormatException e) {}
						if (ptCtlSplit.length>1) {
							setByValue(midiControlSelector, ptCtlSplit[1]);
						}
						fieldPanel.add(midiControlSelectorPanel);
						if (exprSplit.length>3 && ptCtlSplit.length>1) {
							setByValue(midiOptsSelector, exprSplit[3]);
							fieldPanel.add(midiOptsSelectorPanel);
							if (exprSplit.length>4) {
								clearObjects(midiSettingsSelector);
								setMidiSettingsSelector(midiSettingsSelector, exprSplit[1],ptCtlSplit[0],ptCtlSplit[1]);
								setByValue(midiSettingsSelector, exprSplit[4]);
								fieldPanel.add(midiSettingsSelectorPanel);
							}
						}
					}
				}
			} else if ("M".equals(exprSplit[0])) {
				if (exprSplit.length>1) {
					setByValue(midiDeviceSelector, exprSplit[1]);
					fieldPanel.add(midiDeviceSelectorPanel);
					if (exprSplit.length>2) {
						clearObjects(midiMapSelector);
						setMidiMapSelector(  midiMapSelector, exprSplit[1] );
						setByValue(midiMapSelector, exprSplit[2]);
						fieldPanel.add(midiMapSelectorPanel);
						if (exprSplit.length>3) {
							String[] ptCtlSplit=exprSplit[3].split("_");
							setByValue(midiPartSelector, ptCtlSplit[0]);
							fieldPanel.add(midiPartSelectorPanel);
							clearObjects(midiControlSelector);
							try {
								setMidiControlSelector( timeLine.midiDeviceNames[Integer.parseInt(exprSplit[1])], midiControlSelector );
							} catch (NumberFormatException e) {}
							if (ptCtlSplit.length>1) {
								setByValue(midiControlSelector, ptCtlSplit[1]);
							}
							fieldPanel.add(midiControlSelectorPanel);
							if (exprSplit.length>4 && ptCtlSplit.length>1) {
								setByValue(midiOptsSelector, exprSplit[4]);
								fieldPanel.add(midiOptsSelectorPanel);
								if (exprSplit.length>5) {
									clearObjects(midiSettingsSelector);
									setMidiSettingsSelector(midiSettingsSelector, exprSplit[1], exprSplit[2], ptCtlSplit[0], ptCtlSplit[1]);
									setByValue(midiSettingsSelector, exprSplit[5]);
									fieldPanel.add(midiSettingsSelectorPanel);
								}
							}
						}
					}
				}
			} else if ("f".equals(exprSplit[0])) {
				if (exprSplit.length>1) {
					String[] eqSplit=exprSplit[1].split("=");
					String var = eqSplit[0];
					String obj = "";
					if (eqSplit.length>1) {
						obj = eqSplit[1];
					}
					 clearObjects(objectSelector);
					 setObjectSelector(objectSelector);
					 setByValue(objectSelector, obj);
					 fieldPanel.add(objectSelectorPanel);
					 clearObjects(variableSelector);
					 setVariableSelector(variableSelector);
					 setByValue(variableSelector, var);
					 fieldPanel.add(variableSelectorPanel);
					 Class theClass=null;
					 Object o = timeLine.dynamicObjects.get(var);
					 if (o==null) {
						theClass = getSelectedClass(obj);
					 } else {
						theClass=o.getClass();
					 }
					 if (theClass!=null) {
						clearObjects(methodSelector);
						setMethodSelector(methodSelector,theClass.getName());
						fieldPanel.add(methodSelectorPanel);
						fieldPanel.add(newObjectPanel);
					 }
					 if (exprSplit.length>2) {
						 setByValue(methodSelector, exprSplit[2]);// this wont work as method sig not included
						 if (!set) {
							
							processMethods( getSelectedName(methodSelector));
						 }
						 if (exprSplit.length>3) {
							 newObjectChk.setSelected("n".equals(exprSplit[3]));
						 }
					 }
					 for (int i=0;i<parameters.size();i++) {
						fieldPanel.add(((JTextField)parameters.get(i)).getParent());
					 }
					 if (inputTxt.indexOf("[")>-1) {
						 String paramString=inputTxt.substring(inputTxt.indexOf("[")+1,inputTxt.indexOf("]"));
						 String[] params=paramString.split(",");
						 for (int i=0;i<params.length;i++) {
							 JTextField txtFld=(JTextField)parameters.get(i);
							 txtFld.setText(params[i]);
						 }
					 }
				}
			} else if ("gc".equals(exprSplit[0])) {
				
			}
		}catch (Exception e) {}
	}
	private void removeFields() {
		fieldPanel.removeAll();
		//fieldPanel.validate();
	}
	public void updateSelection(EventObject ae) {
		removeFields();
		String text="";
		fieldPanel.add(typeSelectorPanel); //always add this
		if (ae.getSource() == typeSelector) {
			 String typeVal=getSelectedValue(typeSelector);
			 if ("teg".indexOf(typeVal)>-1) {
				 clearObjects(timeLineSelector);
				 setTimelineSelector(timeLineSelector);
				 fieldPanel.add(timeLineSelectorPanel);
				 text="$"+typeVal+":";
			 } else if ("mM".indexOf(typeVal)>-1){
				 fieldPanel.add(midiDeviceSelectorPanel);
				 text="$"+typeVal+":";
			 } else if ("f".indexOf(typeVal)>-1){
				 clearObjects(objectSelector);
				 setObjectSelector(objectSelector);
				 fieldPanel.add(objectSelectorPanel);
				 clearObjects(variableSelector);
				 setVariableSelector(variableSelector);
				 fieldPanel.add(variableSelectorPanel);
				 text="$"+typeVal+":";
			 } else if ("s".indexOf(typeVal)>-1){
				clearObjects(timeLineSetSelector);
				setTimelineSetSelector(timeLineSetSelector);
				fieldPanel.add(timeLineSetSelectorPanel);
				text="$"+typeVal+":";
			} else {
				text="$"+typeVal;
			}
		} else if (ae.getSource() == timeLineSelector) {
			String typeVal=getSelectedValue(typeSelector);
			String timeVal=getSelectedValue(timeLineSelector);
			fieldPanel.add(timeLineSelectorPanel);
			if ("t".indexOf(typeVal)>-1) {
				 fieldPanel.add(timeLineOptsSelectorPanel);
			} else if ("e".indexOf(typeVal)>-1 && (!"current".equals(timeVal)) && (!"this".equals(timeVal)) ){
				 clearObjects(eventSelector);
				 setEventSelector(timeLine.getTimeline(timeVal), eventSelector);
				 fieldPanel.add(eventSelectorPanel);
			 } else if ("g".indexOf(typeVal)>-1 &&  (!"current".equals(timeVal)) && (!"this".equals(timeVal))){
				 clearObjects(groupSelector);
				 setGroupSelector(timeLine.getTimeline(timeVal), groupSelector);
				 fieldPanel.add(groupSelectorPanel);
			 }
			 text="$"+typeVal+":"+timeVal+":";
		} else if (ae.getSource() == timeLineOptsSelector) {
			String typeVal=getSelectedValue(typeSelector);
			String timeVal=getSelectedValue(timeLineSelector);
			String timeOptVal=getSelectedValue(timeLineOptsSelector);
			fieldPanel.add(timeLineSelectorPanel);
			fieldPanel.add(timeLineOptsSelectorPanel);
			if ("v".indexOf(timeOptVal)>-1) {
				fieldPanel.add(timeLineValTxtPanel);
			}
			text="$"+typeVal+":"+timeVal+":"+timeOptVal;
		} else if ( ae.getSource() == timeLineSetSelector ) {
			String typeVal=getSelectedValue(typeSelector);
			String setVal=getSelectedValue(timeLineSetSelector);
			fieldPanel.add(timeLineSetSelectorPanel);
			fieldPanel.add(timeLineSetTypeSelectorPanel);
			text="$"+typeVal+":"+setVal+":";
		} else if ( ae.getSource() == timeLineSetTypeSelector ) {
			String typeVal=getSelectedValue(typeSelector);
			String setVal=getSelectedValue(timeLineSetSelector);
			String setTypeVal=getSelectedValue(timeLineSetTypeSelector);
			fieldPanel.add(timeLineSetSelectorPanel);
			fieldPanel.add(timeLineSetTypeSelectorPanel);
			text="$"+typeVal+":"+setVal+":"+setTypeVal;
		} else if ( ae.getSource() == timeLineValTxt ) {
			String typeVal=getSelectedValue(typeSelector);
			String timeVal=getSelectedValue(timeLineSelector);
			String timeOptVal=getSelectedValue(timeLineOptsSelector);
			String timeValIndex=timeLineValTxt.getText();
			fieldPanel.add(timeLineSelectorPanel);
			fieldPanel.add(timeLineOptsSelectorPanel);
			fieldPanel.add(timeLineValTxtPanel);
			text="$"+typeVal+":"+timeVal+":"+timeOptVal+":"+timeValIndex;
			timeLineValTxt.requestFocus();// set focus back 
		} else if ( ae.getSource() == eventSelector ) {
			String typeVal=getSelectedValue(typeSelector);
			String timeVal=getSelectedValue(timeLineSelector);
			String eventVal=getSelectedValue(eventSelector);
			String eventOptVal=getSelectedValue(eventOptsSelector);
			fieldPanel.add(timeLineSelectorPanel);
			fieldPanel.add(eventSelectorPanel);
			fieldPanel.add(eventOptsSelectorPanel);
			text="$"+typeVal+":"+timeVal+":"+eventVal+(!"".equals(eventOptVal)?(":"+eventOptVal):"");
		} else if (ae.getSource() == eventOptsSelector) {
			String typeVal=getSelectedValue(typeSelector);
			String timeVal=getSelectedValue(timeLineSelector);
			String eventVal=getSelectedValue(eventSelector);
			String eventOptVal=getSelectedValue(eventOptsSelector);
			fieldPanel.add(timeLineSelectorPanel);
			fieldPanel.add(eventSelectorPanel);
			fieldPanel.add(eventOptsSelectorPanel);
			if ("v".indexOf(eventOptVal)>-1) {
				fieldPanel.add(eventValTxtPanel);
			}
			text="$"+typeVal+":"+timeVal+":"+eventVal+(!"".equals(eventOptVal)?(":"+eventOptVal):"");
		} else if (ae.getSource() == eventValTxt) {
			String typeVal=getSelectedValue(typeSelector);
			String timeVal=getSelectedValue(timeLineSelector);
			String eventVal=getSelectedValue(eventSelector);
			String eventOptVal=getSelectedValue(eventOptsSelector);
			String eventValIndex=eventValTxt.getText();
			fieldPanel.add(timeLineSelectorPanel);
			fieldPanel.add(eventSelectorPanel);
			fieldPanel.add(eventOptsSelectorPanel);
			fieldPanel.add(eventValTxtPanel);
			text="$"+typeVal+":"+timeVal+":"+eventVal+":"+eventOptVal+":"+eventValIndex;
			eventValTxt.requestFocus();// set focus back 
		} else if (ae.getSource() == groupSelector) {
			String typeVal=getSelectedValue(typeSelector);
			String timeVal=getSelectedValue(timeLineSelector);
			String groupVal=getSelectedValue(groupSelector);
			String groupOptVal=getSelectedValue(groupOptsSelector);
			fieldPanel.add(timeLineSelectorPanel);
			fieldPanel.add(groupSelectorPanel);
			fieldPanel.add(groupOptsSelectorPanel);
			text="$"+typeVal+":"+timeVal+":"+groupVal+(!"".equals(groupOptVal)?(":"+groupOptVal):"");
		} else if (ae.getSource() == groupOptsSelector) {
			String typeVal=getSelectedValue(typeSelector);
			String timeVal=getSelectedValue(timeLineSelector);
			String groupVal=getSelectedValue(groupSelector);
			String groupOptVal=getSelectedValue(groupOptsSelector);
			fieldPanel.add(timeLineSelectorPanel);
			fieldPanel.add(groupSelectorPanel);
			fieldPanel.add(groupOptsSelectorPanel);
			text="$"+typeVal+":"+timeVal+":"+groupVal+(!"".equals(groupOptVal)?(":"+groupOptVal):"");
		} else if (ae.getSource() == midiDeviceSelector) {
			String typeVal=getSelectedValue(typeSelector);
			String midiDevVal=getSelectedValue(midiDeviceSelector);
			fieldPanel.add(midiDeviceSelectorPanel);
			try {
				int devIndex=Integer.parseInt(midiDevVal);
				if ("M".equals(typeVal)) {
					clearObjects(midiMapSelector);
					setMidiMapSelector(  midiMapSelector, ""+devIndex );
					fieldPanel.add(midiMapSelectorPanel);
				}
				fieldPanel.add(midiPartSelectorPanel);
				clearObjects(midiControlSelector);
				setMidiControlSelector(timeLine.midiDeviceNames[devIndex], midiControlSelector);
				fieldPanel.add(midiControlSelectorPanel);
			} catch (NumberFormatException ne) {}
			text="$"+typeVal+":"+midiDevVal+":";
		} else if (ae.getSource() == midiMapSelector) {
			String typeVal=getSelectedValue(typeSelector);
			String midiDevVal=getSelectedValue(midiDeviceSelector);
			fieldPanel.add(midiDeviceSelectorPanel);
			String midiMapVal=null;
			if ("M".equals(typeVal)) {
				midiMapVal=getSelectedValue(midiMapSelector);
				fieldPanel.add(midiMapSelectorPanel);
			}
			try {
				int devIndex=Integer.parseInt(midiDevVal);
				fieldPanel.add(midiPartSelectorPanel);
				clearObjects(midiControlSelector);
				setMidiControlSelector(timeLine.midiDeviceNames[devIndex], midiControlSelector);
				fieldPanel.add(midiControlSelectorPanel);
			} catch (NumberFormatException ne) {}
			text="$"+typeVal+":"+midiDevVal+":"+
					("M".equals(typeVal)?midiMapVal+":":"");
		} else if (ae.getSource() == midiPartSelector) {
			String typeVal=getSelectedValue(typeSelector);
			String midiDevVal=getSelectedValue(midiDeviceSelector);
			String midiPartVal=getSelectedValue(midiPartSelector);
			String midiControlVal=getSelectedValue(midiControlSelector);
			fieldPanel.add(midiDeviceSelectorPanel);
			String midiMapVal=null;
			if ("M".equals(typeVal)) {
				midiMapVal=getSelectedValue(midiMapSelector);
				fieldPanel.add(midiMapSelectorPanel);
			}
			fieldPanel.add(midiPartSelectorPanel);
			fieldPanel.add(midiControlSelectorPanel);
			if (!"".equals(midiPartVal) && !"".equals(midiControlVal)) {
				fieldPanel.add(midiOptsSelectorPanel);
			}
			text="$"+typeVal+":"+midiDevVal+":"+
					("M".equals(typeVal)?midiMapVal+":":"")+
					midiPartVal+"_"+midiControlVal+":";
					
		} else if (ae.getSource() == midiControlSelector) {
			String typeVal=getSelectedValue(typeSelector);
			String midiDevVal=getSelectedValue(midiDeviceSelector);
			String midiPartVal=getSelectedValue(midiPartSelector);
			String midiControlVal=getSelectedValue(midiControlSelector);
			fieldPanel.add(midiDeviceSelectorPanel);
			String midiMapVal=null;
			if ("M".equals(typeVal)) {
				midiMapVal=getSelectedValue(midiMapSelector);
				fieldPanel.add(midiMapSelectorPanel);
			}
			fieldPanel.add(midiPartSelectorPanel);
			fieldPanel.add(midiControlSelectorPanel);
			if (!"".equals(midiPartVal) && !"".equals(midiControlVal)) {
				fieldPanel.add(midiOptsSelectorPanel);
			}
			text="$"+typeVal+":"+midiDevVal+":"+
					("M".equals(typeVal)?midiMapVal+":":"")+
					midiPartVal+"_"+midiControlVal+":";
		} else if (ae.getSource() == midiOptsSelector) {
			String typeVal=getSelectedValue(typeSelector);
			String midiDevVal=getSelectedValue(midiDeviceSelector);
			String midiPartVal=getSelectedValue(midiPartSelector);
			String midiControlVal=getSelectedValue(midiControlSelector);
			String midiOptsVal=getSelectedValue(midiOptsSelector);
			fieldPanel.add(midiDeviceSelectorPanel);
			String midiMapVal=null;
			if ("M".equals(typeVal)) {
				midiMapVal=getSelectedValue(midiMapSelector);
				fieldPanel.add(midiMapSelectorPanel);
			}
			fieldPanel.add(midiPartSelectorPanel);
			fieldPanel.add(midiControlSelectorPanel);
			fieldPanel.add(midiOptsSelectorPanel);
			clearObjects(midiSettingsSelector);
			if ("M".equals(typeVal)) {
				setMidiSettingsSelector(midiSettingsSelector, midiDevVal, midiMapVal, midiPartVal, midiControlVal);
			} else {
				setMidiSettingsSelector(midiSettingsSelector, midiDevVal,midiPartVal,midiControlVal);
			}
			fieldPanel.add(midiSettingsSelectorPanel);
			text="$"+typeVal+":"+midiDevVal+":"+
					("M".equals(typeVal)?midiMapVal+":":"")+
					midiPartVal+"_"+midiControlVal+":"+midiOptsVal;
		} else if (ae.getSource() == midiSettingsSelector) {
			String typeVal=getSelectedValue(typeSelector);
			String midiDevVal=getSelectedValue(midiDeviceSelector);
			String midiPartVal=getSelectedValue(midiPartSelector);
			String midiControlVal=getSelectedValue(midiControlSelector);
			String midiOptsVal=getSelectedValue(midiOptsSelector);
			String midiSettingVal=getSelectedValue(midiSettingsSelector);
			fieldPanel.add(midiDeviceSelectorPanel);
			String midiMapVal=null;
			if ("M".equals(typeVal)) {
				midiMapVal=getSelectedValue(midiMapSelector);
				fieldPanel.add(midiMapSelectorPanel);
			}
			fieldPanel.add(midiPartSelectorPanel);
			fieldPanel.add(midiControlSelectorPanel);
			fieldPanel.add(midiOptsSelectorPanel);
			fieldPanel.add(midiSettingsSelectorPanel);
			text="$"+typeVal+":"+midiDevVal+":"+
					("M".equals(typeVal)?midiMapVal+":":"")+
					midiPartVal+"_"+midiControlVal+":"+midiOptsVal+(!"".equals(midiSettingVal)?(":"+midiSettingVal):"");
		} else if (ae.getSource() == objectSelector) {
			String objVal=getEditableSelectedValue(objectSelector);
			if ("".equals(objVal)) {
				objectHelper.setVisible(true);
			}
			fieldPanel.add(objectSelectorPanel);
			fieldPanel.add(variableSelectorPanel);
			if (!"".equals(objVal)) {
				clearObjects(methodSelector);
				setMethodSelector(methodSelector,objVal);
				fieldPanel.add(methodSelectorPanel);
				fieldPanel.add(newObjectPanel);
			}
			text=genFunctionText();
		} else if (ae.getSource() == variableSelector) {
			String objVal=getEditableSelectedValue(objectSelector);
			String varVal=getEditableSelectedValue(variableSelector);
			fieldPanel.add(objectSelectorPanel);
			fieldPanel.add(variableSelectorPanel);
			if (!"".equals(varVal)) {
				Class theClass=null;
				Object o = timeLine.dynamicObjects.get(varVal);
				if (o==null) {
					theClass = getSelectedClass(objVal);
				} else {
					theClass=o.getClass();
				}
				if (theClass!=null) {
					clearObjects(methodSelector);
					setMethodSelector(methodSelector,theClass.getName());
					fieldPanel.add(methodSelectorPanel);
					fieldPanel.add(newObjectPanel);
				}
			}
			text=genFunctionText();
		} else if (ae.getSource() == methodSelector) {
			String methodSig=getSelectedName(methodSelector);
			fieldPanel.add(objectSelectorPanel);
			fieldPanel.add(variableSelectorPanel);
			fieldPanel.add(methodSelectorPanel);
			fieldPanel.add(newObjectPanel);
			text=genFunctionText();
			if (!set) {
				parameters.clear();
				processMethods( methodSig);
			}
			for (int i=0;i<parameters.size();i++) {
				fieldPanel.add(((JTextField)parameters.get(i)).getParent());
			}
		} else if (ae.getSource() == newObjectChk) {
			String methodSig=getSelectedName(methodSelector);
			fieldPanel.add(objectSelectorPanel);
			fieldPanel.add(variableSelectorPanel);
			fieldPanel.add(methodSelectorPanel);
			fieldPanel.add(newObjectPanel);
			text=genFunctionText();
//			if (!set) {
//				parameters.clear();
//				processMethods(methodSig);
//			}
			for (int i=0;i<parameters.size();i++) {
				fieldPanel.add(((JTextField)parameters.get(i)).getParent());
			}
		} else if (ae.getSource().getClass().equals(JTextField.class)) {

			fieldPanel.add(objectSelectorPanel);
			fieldPanel.add(variableSelectorPanel);
			fieldPanel.add(methodSelectorPanel);
			fieldPanel.add(newObjectPanel);
			text=genFunctionText();
			for (int i=0;i<parameters.size();i++) {
				fieldPanel.add(((JTextField)parameters.get(i)).getParent());
			}
			((JTextField)ae.getSource()).requestFocus();
		}
		input.setText(text);
		fieldPanel.validate();
	}
	
	private String genFunctionText() {
		String typeVal=getSelectedValue(typeSelector);
		String objVal=getEditableSelectedValue(objectSelector);
		String varVal=getEditableSelectedValue(variableSelector);
		String newVal=newObjectChk.isSelected()?"n":"";
		String methodVal=getSelectedValue(methodSelector);
		String methodSig=getSelectedName(methodSelector);
		String text="$"+typeVal+":"+varVal+(!"".equals(objVal)?"="+objVal:"")+":"+methodVal+":"+newVal+":";
		if (!set && methodSig!=null) {
			int pos = methodSig.indexOf("(");
			if (pos>-1) {
				String params = methodSig.substring(pos+1,methodSig.length()-1);
				if (!"".equals(params)) {
					String[] args=params.split(",");
					if (args.length>0) {
						text+="[";
						for (int i=0;i<args.length;i++) { 
							String argVal = "<"+i+">";
							try {
								String tst = ((JTextField)parameters.get(i)).getText();
								if (!"".equals(tst)) {argVal=tst;} 
							} catch (Exception e) {}
							text+=argVal+((i<args.length-1)?",":""); 
						}
						text+="]";
					}
				}
			}
		}
		return text;
	}
	
	private void processMethods( String methodSig) {
		if ("".equals(methodSig)) {return;}
		int pos = methodSig.indexOf("(");
		String params = methodSig.substring(pos+1, methodSig.length()-1);
		if (!"".equals(params)) {
			String[] args=params.split(",");
			if (args.length>0) {
				for (int i=0;i<args.length;i++) { 
					addParameter(i);
				}
			}
		}
	}

	private void addParameter(int number) {
		JPanel paramPanel = new JPanel();
		JLabel label= new JLabel("Param " + number);
		SwingStyler.setStyleLabel(label);
		paramPanel.add(label);
		JTextField paramField = getTextField(TEXT_TYPE_NORMAL);
		paramField.setName("exprFld"+number);
		SwingStyler.setStyleInput(paramField);
		paramField.setPreferredSize(new Dimension(80,15));
		paramPanel.add(paramField);
		 SwingStyler.ImgButton exprButton = new  SwingStyler.ImgButton(". . .");
		exprButton.setName("exprBut"+number);
		SwingStyler.setStyleButton(exprButton);
		exprButton.setPreferredSize(new Dimension(20,15));
		exprButton.addActionListener(new ExprButListner(paramField));
		paramPanel.add(exprButton);
		parameters.add(paramField);
		SwingStyler.setStyleCommon(paramPanel);
	}
	
	public class ExprOkButListner implements ActionListener {
		public int index=0;
		
		public ExprOkButListner(int index) {
			super();
			this.index = index;
		}
		public void actionPerformed(ActionEvent e) {
			JTextField txt = (JTextField)parameters.get(index);
			txt.setText(paramHelper.getInput());
			e.setSource(txt);
			updateSelection(e);
		}
	}
	
	public class ExprButListner implements ActionListener {
		private JTextField field;
		
		public ExprButListner(JTextField txt) {	field = txt;}
		
		public void actionPerformed(ActionEvent e) {
			if (paramHelper==null) {paramHelper = new ExprHelper(timeLine,true,false);}
			 SwingStyler.ImgButton but = ( SwingStyler.ImgButton)e.getSource();
			int index=Integer.parseInt(but.getName().substring("exprBut".length()));
			JTextField txtFld= (JTextField) parameters.get(index);
			paramHelper.setInput(txtFld.getText());
			paramHelper.setVisible(true);
			paramHelper.setOkAction(new ExprOkButListner(index));
		}
	}
	
	public class WrapBracketButListner implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String text = input.getText();
			if (text.indexOf("{")==-1) {// wrap
				text="{"+text;	
				if (text.indexOf("}")==-1) {text+="}";}
			} else {// unwrap
				if (text.indexOf("{")==0) { text=text.substring(1); }
				if (text.indexOf("}")== text.length()-1) { text=text.substring(0, text.length()-1); }
			}
			input.setText( text );
		}
	}
	
	private class ObjectOKListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			SelBean sb = new SelBean(objectHelper.getImportStr(),"");
			objectSelector.addItem(sb);
			objectSelector.setSelectedItem(sb);
		}
		 
	 }
}
