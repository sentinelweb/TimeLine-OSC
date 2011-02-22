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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import com.silicontransit.compile.DynCompilerObjectBean;
import com.silicontransit.timeline.TimeLine;
import com.silicontransit.timeline.bean.MIDIControlCfgBean;
import com.silicontransit.timeline.bean.MIDIDeviceCfgBean;
import com.silicontransit.timeline.model.ControlSettings;
import com.silicontransit.timeline.model.Event;
import com.silicontransit.timeline.model.TimeLineObject;
import com.silicontransit.timeline.model.TimeLineSet;
import com.silicontransit.timeline.util.SwingStyler;


/**
 * @author munror
 */
public abstract  class InputHelper extends JPanel {
	public static int TYPE_CONTROL =  0;
	public static int TYPE_NOTE =  1;
	public static int TYPE_EXPR =  2;
	public static int TYPE_OBJECT =  3;
	public static int TYPE_PROPS =  4;
	
	public static int TEXT_TYPE_NORMAL =  0;
	public static int TEXT_TYPE_INT =  1;
	public static int TEXT_TYPE_DOUBLE =  2;
	TimeLine timeLine;
	public JFrame thisFrame=null;
	JTextField input=new JTextField();
	protected  SwingStyler.ImgButton okButton;
	protected  SwingStyler.ImgButton insertButton;
	private int type = TYPE_CONTROL;
	protected JPanel fieldPanel;
	protected JPanel buttonPanel; 
	protected boolean suppressSelectionEvents=false;
	protected SelectedObjectHandler selectedObjectHandler;
	protected ActionListener okAction;
	// call back timer for keypress updates.
	private Timer guiTimerStart;
	private GuiTimer guiTimer=new GuiTimer();
	//private JComponent updateComponent = null;
	private KeyEvent updateEvent =null;
	public InputHelper(int type,TimeLine t,boolean insert) {
		this.type = type;
		this.timeLine=t;
		thisFrame= new JFrame();
		WindowListener l = new WindowCloseHandler(); 
		thisFrame.addWindowListener(l);
		thisFrame.getContentPane().add(this);
		thisFrame.setSize(new Dimension(350,300));
		ComponentListener cl = new WindowResizeHandler(); 
		thisFrame.getContentPane().setBackground(SwingStyler.backgroundColor);
		thisFrame.addComponentListener(cl);
		selectedObjectHandler = new SelectedObjectHandler();
		this.setLayout(new BorderLayout());
		//input.addKeyListener(new InputKeyHandler());
		input.setForeground(Color.yellow);
		SwingStyler.setStyleInput(input);
		this.add(input,BorderLayout.PAGE_START);
		fieldPanel = new JPanel();
		fieldPanel.setLayout(new GridLayout(0,1));
		this.add(fieldPanel,BorderLayout.CENTER);
		buttonPanel = new JPanel();
		this.buildComponents();
		okButton= new  SwingStyler.ImgButton("OK");
		okButton.addActionListener(new OKButtonHandler());
		okButton.setPreferredSize(new Dimension(40,15));
		buttonPanel.setBackground(SwingStyler.backgroundColor);
		SwingStyler.setStyleButton(okButton);
		buttonPanel.add(okButton);
		if (insert){
			insertButton = new  SwingStyler.ImgButton("Insert");
			insertButton.addActionListener(new OKButtonHandler());
			SwingStyler.setStyleButton(insertButton);
			insertButton.setPreferredSize(new Dimension(40,15));
			buttonPanel.add(insertButton);
		}
		SwingStyler.ImgButton cancelButton = new  SwingStyler.ImgButton("Cancel");
		cancelButton.addActionListener(new OKButtonHandler());
		cancelButton.setPreferredSize(new Dimension(40,15));
		buttonPanel.add(cancelButton);
		SwingStyler.setStyleButton(cancelButton);
		
		this.add(buttonPanel, BorderLayout.PAGE_END);
		thisFrame.pack();
		guiTimerStart = new Timer();
		startGuiTimer();
		//thisFrame.setVisible(true);
	}
	
	
	public abstract void buildComponents();
	public abstract String getInput();
	public abstract void setInput(String input);
	public abstract void updateSelection(EventObject ae);
	
	class GuiTimer extends TimerTask {
		public void run() {  guiEvents();	  }
	}
	
	public void startGuiTimer(){
		guiTimerStart.scheduleAtFixedRate(new GuiTimer(),0,200);
	}
	
	private void guiEvents(){
		if (updateEvent!=null ) {
			suppressSelectionEvents = true;
			updateSelection(updateEvent);
			suppressSelectionEvents = false;
			updateEvent =null;
		}
	}
	
	public ActionListener getOkAction() {
		return okAction;
	}


	public void setOkAction(ActionListener okAction) {
		this.okAction = okAction;
	}


	public void clearObjects (JComboBox select) {
		suppressSelectionEvents=true;
		try {
			select.setSelectedIndex(0);
			select.removeAllItems();
		} catch (Exception e) {}
		//if (select.getItemCount()==0) {// to stop np in swing class
		//	select.addItem(new SelBean("",""));
		//}
		suppressSelectionEvents=false;
	}
	public void setVisible(boolean vis) {
		thisFrame.setVisible(vis);
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Selectors 
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//	////////////////////// selector based on static Array //////////////////////// //////////////////////// //////////////////////// //////////////////////// //////////////////////// 
	protected JComboBox getSelector(SelBean[] list) {
		JComboBox select = new JComboBox(list );
		//select.addActionListener(new SelectedObjectHandler());
		select.addActionListener(selectedObjectHandler);
		SwingStyler.setStyleInput(select);
		return select;
	}
	protected static final SelBean[] exprTypes ={
		//new SelBean("",""),
		new SelBean("T","Main"),
		new SelBean("s","Set"),
		new SelBean("t","TimeLine"),
		new SelBean("e","Event"),
		new SelBean("M","MIDI (global)"),
		new SelBean("m","MIDI (current map)"),
		new SelBean("n","Null"),
		new SelBean("g","Group"),
		new SelBean("f","Function"),
		new SelBean("gc","Garbage Collect")
	};
	
	protected static final SelBean[] setOpts = {
		new SelBean("",""),
		new SelBean("p","Play"),
		new SelBean("l","Loop"),
		new SelBean("c","Count (Get)"),
		new SelBean("w","Pitch (Set)")
	};
	
	protected static final SelBean[] timelineOpts = {
		new SelBean("",""),
		new SelBean("p","Play"),
		new SelBean("l","Loop"),
		new SelBean("s","Stop"),
		new SelBean("x","Position"),
		new SelBean("v","Value"),
		new SelBean("w","Pitch"),
		new SelBean("q","Quantization"),
		new SelBean("b","Beat Length"),
		new SelBean("r","Bar Length"),
		new SelBean("c","Current Event")
	};
	protected static final SelBean[] playModeOpts = {
		new SelBean("","",""),
		new SelBean("p","Play","p"),
		new SelBean("l","Loop","l"),
	};
	protected static final SelBean[] midiOpts = {
		new SelBean("",""),
		new SelBean("v","Value"),
		new SelBean("s","Scale"),
		new SelBean("o","Offset"),
		new SelBean("t","Type")
	};
	
	protected static final SelBean[] eventOpts = {
		new SelBean("",""),
		new SelBean("v","Value"),
		new SelBean("i","OSC Message"),
		new SelBean("m","OSC Port Index"),
		new SelBean("f","Fire")
	};
	
	protected static final SelBean[] groupOpts = {
		new SelBean("",""),
		new SelBean("f","Fire"),
		new SelBean("a","Activate")
	};
	
	protected static final SelBean[] midiTypeOpts = {
		new SelBean("n","Linear","n"),
		new SelBean("a","Accumulator","a"),
		new SelBean("t","Toggle","t"),
		new SelBean("b","Buttton","b"),
		new SelBean("l","Logarithmic","l")
	};
//	////////////////////// set//////////////////////// //////////////////////// //////////////////////// //////////////////////// //////////////////////// 
	  protected JComboBox getTimelineSetSelector() {
		  JComboBox select = new JComboBox( );
		  setTimelineSetSelector( select) ;
		  SwingStyler.setStyleInput(select); 
		  return select;
	  }
	
	  protected void setTimelineSetSelector(JComboBox select) {
		  suppressSelectionEvents=true;
		  for (int i=0;i<timeLine.timeLineSets.size();i++) {
			  TimeLineSet ts = (TimeLineSet)timeLine.timeLineSets.get(i);
			  select.addItem(new SelBean(ts.getId(),ts.getId()+" ("+ts.getSet().size()+")"));
		  }
		
		  select.addActionListener(selectedObjectHandler);
		  suppressSelectionEvents=false;
		
	  }
//	////////////////////// timeline//////////////////////// //////////////////////// //////////////////////// //////////////////////// //////////////////////// 
	  protected JComboBox getTimelineSelector() {
		  JComboBox select = new JComboBox( );
		  setTimelineSelector( select) ;
		  SwingStyler.setStyleInput(select); 
		  return select;
	  }
	
	  protected void setTimelineSelector(JComboBox select) {
		  suppressSelectionEvents=true;
		  for (int i=0;i<timeLine.timeLines.size();i++) {
			  TimeLineObject to = (TimeLineObject)timeLine.timeLines.get(i);
			  select.addItem(new SelBean(to.id,to.id, to));
		  }
		  select.addItem(new SelBean("current","current"));
		  select.addItem(new SelBean("this","this"));
		
		  select.addActionListener(selectedObjectHandler);
		  suppressSelectionEvents=false;
		
	  }
//	  ////////////////////// event//////////////////////// //////////////////////// //////////////////////// //////////////////////// //////////////////////// 
	  protected JComboBox getEventSelector(TimeLineObject to) {
		  JComboBox select = new JComboBox(  );
		  setEventSelector( to, select);
		  SwingStyler.setStyleInput(select);
		  select.addActionListener(selectedObjectHandler);
		  return select;
	  }
	
	  protected void setEventSelector(TimeLineObject to,JComboBox select) {
		  suppressSelectionEvents=true;
		  for (int i=0;i<to.timeLine.size();i++){
			  Event e = (Event)to.timeLine.get(i);
			  String evId=(!"".equals(e.id))?e.id:""+i;
			  select.addItem(new SelBean(evId, i+":"+evId ));
		  }
		  select.addItem(new SelBean("current","current"));
		  select.addItem(new SelBean("this","this"));
		
		  //return select;
		  suppressSelectionEvents=false;
	  }
//	  ////////////////////// group //////////////////////// //////////////////////// //////////////////////// //////////////////////// //////////////////////// 
	  protected JComboBox getGroupSelector(TimeLineObject to) {
		  JComboBox select = new JComboBox(  );
		  setGroupSelector( to, select);
		  select.addActionListener(selectedObjectHandler);
		  SwingStyler.setStyleInput(select);
		  return select;
	  }
	
	  protected void setGroupSelector(TimeLineObject to,JComboBox select) {
		  suppressSelectionEvents=true;
		  Iterator i = to.groups.keySet().iterator();
		  while (i.hasNext()) { 
			  String groupName = (String)i.next();
			  Vector group = (Vector)to.groups.get(groupName);
			  select.addItem(new SelBean(groupName,groupName+"("+group.size()+")"));
		  }
		  suppressSelectionEvents=false;
	  }
//	////////////////////// midi device //////////////////////// //////////////////////// //////////////////////// //////////////////////// //////////////////////// 
	protected JComboBox getMidiDeviceSelector() {
		JComboBox select = new JComboBox( );
		for (int i=0;i<timeLine.midiDeviceNames.length;i++){
			select.addItem(new SelBean(""+i,timeLine.midiDeviceNames[i]));
		}
		//select.addActionListener(new SelectedObjectHandler());
		select.addActionListener(selectedObjectHandler);
		SwingStyler.setStyleInput(select);
		return select;
	}
//	////////////////////// midi part //////////////////////// //////////////////////// //////////////////////// //////////////////////// //////////////////////// 
	protected JComboBox getMidiPartSelector() {
		JComboBox select = new JComboBox( );
		for (int i=0;i<16;i++){
			 select.addItem(new SelBean(""+i,""+i,i));
		}
		//select.addActionListener(new SelectedObjectHandler());
		select.addActionListener(selectedObjectHandler);
		SwingStyler.setStyleInput(select);
		return select;
	}
//	////////////////////// midi note //////////////////////// //////////////////////// //////////////////////// //////////////////////// //////////////////////// 
	protected JComboBox getMidiNoteSelector() {
		JComboBox select = new JComboBox( );
		String note="";
		for (int i=0;i<128;i++){
			if ((i-62%12==0)) {note = "C";} else {note = "";}
			select.addItem(new SelBean(""+i , note+"("+i+")"));
		}
		//select.addActionListener(new SelectedObjectHandler());
		select.addActionListener(selectedObjectHandler);
		SwingStyler.setStyleInput(select);
		return select;
	}
//	////////////////////// midi control //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
	protected JComboBox getMidiControlSelector(String device) {
		JComboBox select = new JComboBox( );
		if (device!=null) {setMidiControlSelector( device, select); }
		//select.addActionListener(new SelectedObjectHandler());
		select.addActionListener(selectedObjectHandler);
		SwingStyler.setStyleInput(select);
		return select;
	}
	
	protected void setMidiControlSelector(String device,JComboBox select) {
		suppressSelectionEvents=true;
		MIDIDeviceCfgBean mdcb = (MIDIDeviceCfgBean) timeLine.midiDeviceConfigMaps.get(device);
		if (mdcb!=null) {
			Iterator i=mdcb.getControlCfg().keySet().iterator();
			while (i.hasNext()) {
				String key = (String)  i.next();
				select.addItem(new SelBean(key,((MIDIControlCfgBean)mdcb.getControlCfg().get(key)).getControlText(),key));
			}
		}
		suppressSelectionEvents=false;
	}
//	////////////////////// osc port /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
	protected JComboBox getOSCPortSelector() {
		JComboBox select = new JComboBox( );
		setOSCPortSelector(select);
		//select.addActionListener(new SelectedObjectHandler());
		select.addActionListener(selectedObjectHandler);
		SwingStyler.setStyleInput(select);
		return select;
	}
	
	protected void setOSCPortSelector(JComboBox select) {
		suppressSelectionEvents=true;
		for (int i=0;i<timeLine.oscServerHost.length;i++){
			select.addItem(new SelBean(""+i, timeLine.oscServerHost[i]+":"+ timeLine.oscServerPorts[i]+":"+ timeLine.oscServerRcPorts[i],new Integer(i)));
		}
		suppressSelectionEvents=false;
	}
	//////////////////////// midi Settings //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
	protected JComboBox getMidiSettingsSelector() {
		JComboBox select = new JComboBox( );
		select.addActionListener(selectedObjectHandler);
		SwingStyler.setStyleInput(select);
		return select;
	}
	// for global
	protected void setMidiSettingsSelector(JComboBox select,String devIndex,String mapName, String part, String ctrl) {
			suppressSelectionEvents=true;
			select.addItem(new SelBean("",""));
			try {
				HashMap devMap=(HashMap) timeLine.allMidiControlMaps.get(timeLine.midiDeviceNames[Integer.parseInt(devIndex)]);
				HashMap theMap=(HashMap)devMap.get(mapName);
				fillSettingSelect(select, part, ctrl, theMap);
			}catch (NumberFormatException n) {}
			suppressSelectionEvents=false;
	}
	//for current
	protected void setMidiSettingsSelector(JComboBox select,String devIndex,String part,String ctrl) {
		suppressSelectionEvents=true;
		select.addItem(new SelBean("",""));
		try {
			HashMap devMap=(HashMap) timeLine.currentMidiControlMaps.get(timeLine.midiDeviceNames[Integer.parseInt(devIndex)]);
			fillSettingSelect(select, part, ctrl, devMap);
		}catch (NumberFormatException n) {}
		suppressSelectionEvents=false;
	}

	private void fillSettingSelect(	JComboBox select,	String part,	String ctrl,	HashMap theMap) {
		if (theMap!=null) {
			Vector v = (Vector)theMap.get(part+"_"+ctrl);
			if ((v != null) && (v.size() > 0)) {
				for (int i=0;i<v.size();i++) {
					ControlSettings cs = (ControlSettings)v.get(i);
					select.addItem(new SelBean(""+i, cs.oscMsg+""));
				}
			}
		}
	}
//	////////////////////// midi Map //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
	protected JComboBox getMidiMapSelector() {
		JComboBox select = new JComboBox( );
		select.addActionListener(selectedObjectHandler);
		SwingStyler.setStyleInput(select);
		return select;
	}

	protected void setMidiMapSelector(JComboBox select, String devIndex) {
		suppressSelectionEvents=true;
		try {
			HashMap devMap=(HashMap) timeLine.allMidiControlMaps.get(timeLine.midiDeviceNames[Integer.parseInt( devIndex )]);
			if (devMap!=null) {
				for (Iterator mapIter = devMap.keySet().iterator(); mapIter.hasNext();) {
					String mapName = (String) mapIter.next();
					HashMap map=(HashMap)devMap.get(mapName);
					select.addItem(new SelBean(mapName, map.keySet().size()+" controls"));
				}
			}
		} catch (NumberFormatException n) {}
		suppressSelectionEvents=false;
	}
//	////////////////////// object selector //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
	protected JComboBox getObjectSelector() {
		JComboBox select = new JComboBox( );
		setObjectSelector(select);
		select.addActionListener(selectedObjectHandler);
		select.setEditable(true);
		SwingStyler.setStyleInput(select);
		return select;
	}
	
	protected void setObjectSelector(JComboBox select) {
		suppressSelectionEvents=true;
		select.addItem(new SelBean("",""));
		//Iterator i = t.dynamicObjects.keySet()
		for (int i=0;i<timeLine.dynCompiler.objectSelector.getItemCount();i++) {
			DynCompilerObjectBean dcob=(DynCompilerObjectBean)timeLine.dynCompiler.objectSelector.getItemAt(i);
			select.addItem(new SelBean(dcob.getName(),""));
		}
		Iterator i = TimeLine.stdObjectClasses.keySet().iterator();
		while (i.hasNext()) {
			String key = (String) i.next();
			Class objClass=(Class) TimeLine.stdObjectClasses.get(key);
			select.addItem(new SelBean(objClass.getName(),""));
		}
		suppressSelectionEvents=false;
	}
//	////////////////////// variable selector //////////////////////// //////////////////////// //////////////////////// //////////////////////// //////////////////////// 
	protected JComboBox getVariableSelector() {
		JComboBox select = new JComboBox( );
		setVariableSelector(select);
		select.addActionListener(selectedObjectHandler);
		select.setEditable(true);
		SwingStyler.setStyleInput(select);
		return select;
	}
	
	protected void setVariableSelector(JComboBox select) {
		suppressSelectionEvents=true;
		select.addItem(new SelBean("",""));
		Iterator i = timeLine.dynamicObjects.keySet().iterator();
		while (i.hasNext()) {
			String key = (String) i.next();
			Object o = timeLine.dynamicObjects.get(key);
			select.addItem(new SelBean(key,o.getClass().getName()));
		}
		suppressSelectionEvents=false;
	}
//	////////////////////// method selector //////////////////////// //////////////////////// //////////////////////// //////////////////////// //////////////////////// 
	protected JComboBox getMethodSelector() {
		JComboBox select = new JComboBox( );
		//setMethodSelector(select);
		select.addActionListener(selectedObjectHandler);
		SwingStyler.setStyleInput(select);
		return select;
	}
	
	protected void setMethodSelector(JComboBox select,String className) {
		suppressSelectionEvents=true;
		select.addItem(new SelBean("",""));
		Class classSel=getSelectedClass(className);
		if (classSel!=null) {
			Method[] methodList = classSel.getMethods();
			for (int i=0;i<methodList.length;i++) {
				String methodStr=methodList[i].getName()+"(";
				Class[] params=methodList[i].getParameterTypes();
				for (int j=0;j<params.length;j++) {
					methodStr+=params[j].getSimpleName()+(j<params.length-1?",":""); 
				}
				methodStr+=")";
				select.addItem(new SelBean(methodList[i].getName(),methodStr));
			}
		}
		 
		
		suppressSelectionEvents=false;
	}
	protected Class getSelectedClass(String objVal) {
		Class theClass=null;
		try {
			theClass=Class.forName(objVal,false,timeLine.dynCompiler.getClassLoader());
		} catch (ClassNotFoundException e) {	}
		if (theClass==null) {
			theClass=timeLine.dynCompiler.getClass(objVal);
		}
		return theClass;
	}
/////////////////////////////////
// ui obj generators.
////////////////////////////////////
	protected JTextField getTextField(int type) {
		JTextField txtFld = null;
		if (type==TEXT_TYPE_NORMAL) {
			txtFld = new JTextField();
		} else if (type==TEXT_TYPE_INT) {
			txtFld = new JFormattedTextField(new DecimalFormat("###"));
		} else if (type==TEXT_TYPE_DOUBLE) {
			txtFld = new JFormattedTextField(new DecimalFormat("###.0#########"));
		}
		txtFld.addKeyListener(new InputKeyHandler());
		// attempt to listen for keyUp and KeyDown ... failed :(
		//txtFld.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,0),txtFld.getActionMap());
		//txtFld.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0),txtFld.getActionMap());
		
		//txtFld.addActionListener(selectedObjectHandler);
		//txtFld.setToolTipText("Press enter to update");
		SwingStyler.setStyleInput(txtFld);
		return txtFld;
	}
	
	protected JPanel addField(String label, JComponent comp) {
		JPanel jPanel=new JPanel();
		jPanel.setBackground(SwingStyler.backgroundColor);
		jPanel.setBorder(BorderFactory.createEmptyBorder());
		((FlowLayout)jPanel.getLayout()).setVgap(0);
		jPanel.setBackground(SwingStyler.backgroundColor);
		jPanel.setForeground(SwingStyler.textColor);
		
		JLabel jLabel  = new JLabel(label); 
		
		SwingStyler.setStyleLabel(jLabel);
		
		jPanel.add(jLabel);
		jPanel.add(comp);
		fieldPanel.add(jPanel);
		return jPanel;
	}
	protected void setByObject(JComboBox select,Object value) {
		boolean oldsuppress = suppressSelectionEvents;
		suppressSelectionEvents = true;
		boolean set=false;
		for (int i =0;i<select.getItemCount();i++) {
			SelBean selItem = (SelBean)select.getItemAt(i);
			if (selItem.getObject()!=null && selItem.getObject().equals(value)) {select.setSelectedIndex(i);set=true;}
		}
		suppressSelectionEvents=oldsuppress ||false;
	
	}
	protected void setByValue(JComboBox select,String value) {
		boolean oldsuppress = suppressSelectionEvents;
		suppressSelectionEvents = true;
		boolean set=false;
		for (int i =0;i<select.getItemCount();i++) {
			SelBean selItem = (SelBean)select.getItemAt(i);
			if (selItem.getValue().equals(value)) {select.setSelectedIndex(i);set=true;}
		}
		if (!set && select.isEditable()) {
			SelBean selItem = new SelBean(value,"");
			select.addItem(selItem);
			select.setSelectedItem(selItem);
		}
		suppressSelectionEvents=oldsuppress ||false;
	}
	
	protected void setByName(JComboBox select,String name) {
		boolean oldsuppress = suppressSelectionEvents;
		suppressSelectionEvents = true;
		for (int i =0;i<select.getItemCount();i++) {
			SelBean selItem = (SelBean)select.getItemAt(i);
			if (selItem.getDescription().equals(name)) {select.setSelectedIndex(i);}
		}
		suppressSelectionEvents=oldsuppress ||false;
	}
	
	protected String getSelectedValue(JComboBox select) {
		SelBean selItem = (SelBean)select.getSelectedItem();
		if (selItem==null) return null;
		return selItem.getValue();
	}
	
	protected Object getSelectedObject(JComboBox select) {
		SelBean selItem = (SelBean)select.getSelectedItem();
		if (selItem==null) return null;
		return selItem.getObject(); 
	}
	
	protected String getEditableSelectedValue(JComboBox select) {
		Object o =select.getSelectedItem();
		if (o==null) return null;
		if (o instanceof String) {
			return (String)o;
		} else {
			SelBean selItem = (SelBean)o;
			return selItem.getValue();
		}
	}
	protected String getSelectedName(JComboBox select) {
		SelBean selItem = (SelBean)select.getSelectedItem();
		if (selItem==null) return null;
		return selItem.getDescription();
	}
	/////////// listeners ///////////////////////////
	// window closing
	public class WindowCloseHandler extends WindowAdapter {
		public void windowClosing(WindowEvent e) {thisFrame.setVisible(false);}		
	} 
	
	// window resize	
	public class WindowResizeHandler extends ComponentAdapter {
		public void componentResized(ComponentEvent e) {
		}
	}
	
//	protected class TextChangeHandler implements DocumentListener{
//		public JTextField text= null;
//		boolean sendEvent=true;
//		public TextChangeHandler (JTextField text) {this.text=text;}
//		public void changedUpdate(DocumentEvent e) {
//			doEvent();
//		}
//		public void insertUpdate(DocumentEvent e) {
//			doEvent();
//		}
//		public void removeUpdate(DocumentEvent e) {
//			doEvent();
//		}
//		public synchronized void doEvent() {
//			if (sendEvent) {
//				sendEvent=false;
//				updateSelection( new EventObject(text));
//				sendEvent=true;
//			}
//		}
//	}
	
	protected class InputKeyHandler implements KeyListener{
		public void keyPressed(KeyEvent ke) {	
			if ( (ke.getKeyCode()==KeyEvent.VK_UP) || (ke.getKeyCode()==KeyEvent.VK_DOWN) ) {
				updateEvent = ke;
			}
		}
		public void keyReleased(KeyEvent e) { 
			//System.out.println("re:"+e.getKeyChar());
		}
		public void keyTyped(KeyEvent e) { 
			//System.out.println("ty:"+e.getKeyChar());
			//updateComponent = (JComponent) e.getSource();
			updateEvent = e;
		}
	}
	
	public class SelectedObjectHandler implements ActionListener {
		public SelectedObjectHandler() {}
		public void actionPerformed(ActionEvent ae) {
			if (!suppressSelectionEvents) updateSelection( ae );
		}
	}
	public class OKButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource()==okButton) { 
				//t.setInputStr(input.getText());
				ae.setSource("OK");
				if (okAction!=null) {okAction.actionPerformed(ae);}
			} else if (ae.getSource()==insertButton) { 
				ae.setSource("Insert");
				if (okAction!=null) {okAction.actionPerformed(ae);}
			}
			thisFrame.setVisible(false);
		}
	}
}
