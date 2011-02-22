package com.silicontransit.timeline.window.iface;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import com.silicontransit.timeline.TimeLine;

import processing.core.PApplet;

public class InterfaceBuilder extends JFrame {

	TimeLine timeline = null;
	public HashMap trig=new HashMap();
	JFrame theFrame = this;
	public InterfaceBuilder(TimeLine p) throws HeadlessException {
		 init();
		 //test();
		 this.timeline=p;
	}
	public void init() {
		setTitle("Interface");
		Container container = getContentPane();
		container.setLayout(new GridLayout(-1,1));
	}
	
	public void  reset(){
		this.getContentPane().removeAll();
		init();
	}
	
	public void test() {
		setTitle("Interface");
		Container container = getContentPane();
		container.setLayout(new GridLayout(-1,1));
		// read XML file ....
		HashMap<String,Object> sliderConf = new HashMap<String,Object>();
		sliderConf.put(InterfaceReader.PARAM_SLIDER_MAX, 100);
		sliderConf.put(InterfaceReader.PARAM_SLIDER_MIN, 0);
		sliderConf.put(InterfaceReader.PARAM_SLIDER_STEP, 1);
		sliderConf.put(InterfaceReader.PARAM_SLIDER_VALUE, 100);
		sliderConf.put(InterfaceReader.PARAM_SLIDER_WIDTH, 200);
		sliderConf.put(InterfaceReader.PARAM_SLIDER_INPUT, "$e:t1:0:v:0");
		sliderConf.put(InterfaceReader.PARAM_SLIDER_OUTPUT,"$e:t1:0:v:0");
		sliderConf.put(InterfaceReader.PARAM_SLIDER_LOG,false);
		sliderConf.put(InterfaceReader.PARAM_SLIDER_TITLE,"Slider");
		sliderConf.put(InterfaceReader.PARAM_SLIDER_TOOLTIP,"A General Slider");
		addSlider("sldr",sliderConf);
		
		HashMap<String,Object> buttonConf = new HashMap<String,Object>();
		buttonConf.put(InterfaceReader.PARAM_BUTTON_VALUE, 100);
		buttonConf.put(InterfaceReader.PARAM_BUTTON_WIDTH, 200);
		buttonConf.put(InterfaceReader.PARAM_BUTTON_OUTPUT,"$e:t1:0:v:0");
		buttonConf.put(InterfaceReader.PARAM_BUTTON_TITLE,"Button");
		buttonConf.put(InterfaceReader.PARAM_BUTTON_TOOLTIP,"A General Button");
		addButton("butt",buttonConf);
		
		buttonConf = new HashMap<String,Object>();
		buttonConf.put(InterfaceReader.PARAM_BUTTON_VALUE, 10);
		buttonConf.put(InterfaceReader.PARAM_BUTTON_WIDTH, 200);
		buttonConf.put(InterfaceReader.PARAM_BUTTON_OUTPUT,"$e:t1:0:v:0");
		buttonConf.put(InterfaceReader.PARAM_BUTTON_TITLE,"Button");
		buttonConf.put(InterfaceReader.PARAM_BUTTON_TOOLTIP,"A General Button");
		addButton("butt1",buttonConf);
		
		// add file popup  input
		HashMap fileConf = new HashMap<String,Object>();
		fileConf.put(InterfaceReader.PARAM_FILE_VALUE, "/home/robm/xxx.txt");
		fileConf.put(InterfaceReader.PARAM_FILE_WIDTH, 200);
		fileConf.put(InterfaceReader.PARAM_FILE_OUTPUT,"$e:t1:0:v:0");
		fileConf.put(InterfaceReader.PARAM_FILE_TITLE,"Video file");
		fileConf.put(InterfaceReader.PARAM_FILE_TOOLTIP,"A File Input Button");
		//fileConf.put(PARAM_FILE_LABEL,"Video file");
		addFile("file1",fileConf);
		
		// add text input
		HashMap textConf = new HashMap<String,Object>();
		textConf.put(InterfaceReader.PARAM_TEXT_VALUE, "Sardine");
		textConf.put(InterfaceReader.PARAM_TEXT_WIDTH, 200);
		textConf.put(InterfaceReader.PARAM_TEXT_OUTPUT,"$e:t1:0:v:0");
		textConf.put(InterfaceReader.PARAM_TEXT_TITLE,"Fish");
		textConf.put(InterfaceReader.PARAM_TEXT_TOOLTIP,"A Text Input Button");
		//textConf.put(PARAM_TEXT_LABEL,"Fish");
		addText("text1",textConf);
		
		pack();
		
	}

	public void addSlider(String string, HashMap sliderConf) {
		JComponent c = new JPanel();	add(c);
		if (sliderConf.containsKey(InterfaceReader.PARAM_SLIDER_TITLE)) {
			JLabel l = new JLabel((String)getValue(sliderConf, InterfaceReader.PARAM_SLIDER_TITLE,  "Title"));
			c.add(l);
		}
		JSlider theSlider = new JSlider((Integer)getValue(sliderConf, InterfaceReader.PARAM_SLIDER_MIN,  0), (Integer)getValue(sliderConf, InterfaceReader.PARAM_SLIDER_MAX,100));
		theSlider.setValue((Integer)getValue(sliderConf, InterfaceReader.PARAM_SLIDER_VALUE,  0));
		theSlider.setToolTipText((String)getValue(sliderConf, InterfaceReader.PARAM_SLIDER_TOOLTIP,  ""));
		if (sliderConf.containsKey(InterfaceReader.PARAM_SLIDER_STEP)) {
			theSlider.setMinorTickSpacing((Integer)getValue(sliderConf, InterfaceReader.PARAM_SLIDER_STEP,  5));
			theSlider.setPaintTicks(true);
			theSlider.setSnapToTicks(true);
		}
		theSlider.setPreferredSize(new Dimension((Integer)getValue(sliderConf, InterfaceReader.PARAM_SLIDER_WIDTH,  5),20));
		c.add(theSlider);
		JLabel sliderValueLabel = new JLabel();
		sliderValueLabel.setText(""+theSlider.getValue());
		c.add(sliderValueLabel);
		theSlider.addChangeListener(
					new SliderChangeListener( sliderValueLabel, 
							(String)getValue(sliderConf, InterfaceReader.PARAM_SLIDER_OUTPUT,  "")) 
					);
		if (sliderConf.containsKey(InterfaceReader.PARAM_SLIDER_INPUT)) {
			trig.put(sliderConf.get(InterfaceReader.PARAM_SLIDER_INPUT), theSlider);
		}
	}
	
	class SliderChangeListener implements ChangeListener{
		JLabel lbl= new JLabel();
		String outputExpr = "";
		
		public SliderChangeListener(JLabel lbl, String outputExpr) {
			super();
			this.lbl = lbl;
			this.outputExpr = outputExpr;
		}

		public void stateChanged(ChangeEvent e) {
			int valuei = ((JSlider)e.getSource()).getValue();
			lbl.setText(""+valuei);
			Vector value = new Vector();
			value.add(valuei);
			timeline.exprUtil.setValueExpr(this.outputExpr, value);
		}
		
	}
	
	public void addButton(String string, HashMap sliderConf) {
		JComponent c = new JPanel();	add(c);
		//if (sliderConf.containsKey(PARAM_BUTTON_LABEL)) {
		//	JLabel l = new JLabel((String)getValue(sliderConf, PARAM_BUTTON_LABEL,  "File"));
		//	c.add(l);
		//}
		JButton theButton = new JButton((String)getValue(sliderConf, InterfaceReader.PARAM_BUTTON_TITLE,  "Button"));
		theButton.setToolTipText((String)getValue(sliderConf, InterfaceReader.PARAM_BUTTON_TOOLTIP,  ""));
		theButton.setPreferredSize(new Dimension((Integer)getValue(sliderConf, InterfaceReader.PARAM_BUTTON_WIDTH,  5),20));
		c.add(theButton);
		theButton.addActionListener(new ButtonChangeListener( 
														(Object) getValue(sliderConf, InterfaceReader.PARAM_BUTTON_VALUE,  0), 
														(String)getValue(sliderConf, InterfaceReader.PARAM_BUTTON_OUTPUT,  "")) 
													);
	}
	
	class ButtonChangeListener implements ActionListener{
		Object value = null;
		String outputExpr = "";
		
		public ButtonChangeListener(Object value, String outputExpr) {
			super();
			this.value = value;
			this.outputExpr = outputExpr;
		}

		public void actionPerformed(ActionEvent e) {
			Vector valueVec = new Vector();
			valueVec.add(this.value);
			timeline.exprUtil.setValueExpr(this.outputExpr, valueVec);
		}
	}
	
	public void addFile(String string, HashMap fileConf) {
		JComponent c = new JPanel();	add(c);
		JLabel lbl = new JLabel((String)getValue(fileConf, InterfaceReader.PARAM_FILE_TITLE,  "title"));
		lbl.setToolTipText((String)getValue(fileConf, InterfaceReader.PARAM_FILE_TOOLTIP,  ""));
		c.add(lbl);
		JTextField theText = new JTextField((String)getValue(fileConf, InterfaceReader.PARAM_FILE_VALUE,  "~"));
		theText.setToolTipText((String)getValue(fileConf, InterfaceReader.PARAM_FILE_TOOLTIP,  ""));
		theText.setPreferredSize(new Dimension((Integer)getValue(fileConf, InterfaceReader.PARAM_FILE_WIDTH,  5)-50,20));
		c.add(theText);
		JButton theButton = new JButton("...");
		theButton.setToolTipText(  "Select file ...");
		theButton.setPreferredSize(new Dimension(50,20));
		c.add(theButton);
		theButton.addActionListener(new FileButtonListener( 
														(String)getValue(fileConf, InterfaceReader.PARAM_FILE_OUTPUT,  ""),
														theText
														)
													);
	}
	
	class FileButtonListener implements ActionListener {
		String outputExpr = null;
		JTextField fld = null;
		public FileButtonListener(String outputExpr, JTextField fld) {
			super();
			this.outputExpr = outputExpr;
			this.fld=fld;
		}

		public void actionPerformed(java.awt.event.ActionEvent e) {
			try {
				FileChooserThread fct = new FileChooserThread(this);
				fct.start();
			} catch(Exception ex) {}
		};
		
		void setText(String str){
			this.fld.setText(str);
			Vector valueVec = new Vector();
			valueVec.add(str);
			timeline.exprUtil.setValueExpr(this.outputExpr, valueVec);
		}
	}
	
	
	/**
	 * Class the run a file chooser in a separate thread
	 * @author robm
	 *
	 */
	JFileChooser fc = null;
	private class FileChooserThread extends Thread{
		FileButtonListener fbl = null;
		
		public FileChooserThread(FileButtonListener fbl){
			fc=new JFileChooser();
			this.fbl=fbl;
		}
		public void run() {
			this.setName("FileChooserThread");
			int returnVal = fc.showOpenDialog(theFrame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				String fileName=fc.getSelectedFile().getAbsolutePath();
				fbl.setText(fileName);
			}
		}
	}
	
	public void addText(String string, HashMap textConf) {
		JComponent c = new JPanel();	add(c);
		JLabel lbl = new JLabel((String)getValue(textConf, InterfaceReader.PARAM_TEXT_TITLE,  "title"));
		lbl.setToolTipText((String)getValue(textConf, InterfaceReader.PARAM_TEXT_TOOLTIP,  ""));
		c.add(lbl);
		JTextField theText = new JTextField((String)getValue(textConf, InterfaceReader.PARAM_TEXT_VALUE,  "~"));
		theText.setToolTipText((String)getValue(textConf, InterfaceReader.PARAM_TEXT_TOOLTIP,  ""));
		theText.setPreferredSize(new Dimension((Integer)getValue(textConf, InterfaceReader.PARAM_TEXT_WIDTH,  5)-50,20));
		c.add(theText);
		JButton theButton = new JButton(  ">>");
		theButton.setToolTipText("Set Value");
		theButton.setPreferredSize(new Dimension(50,20));
		c.add(theButton);
		theButton.addActionListener(new TextButtonListener( 
														(String)getValue(textConf, InterfaceReader.PARAM_TEXT_OUTPUT,  ""),
														theText
														)
													);
	}
	class TextButtonListener implements ActionListener {
		String outputExpr = null;
		JTextField fld = null;
		public TextButtonListener(String outputExpr, JTextField fld) {
			super();
			this.outputExpr = outputExpr;
			this.fld=fld;
		}

		public void actionPerformed(java.awt.event.ActionEvent e) {
			try {
				setText(fld.getText());
			} catch(Exception ex) {}
		};
		
		void setText(String str){
			this.fld.setText(str);
			Vector valueVec = new Vector();
			valueVec.add(str);
			timeline.exprUtil.setValueExpr(this.outputExpr, valueVec);
		}
	}
	/**
	 * Get a value from a Config hashmap
	 * @param m the hashmap
	 * @param key the key
	 * @param defaultObj the default in case it doesnt exist
	 * @return
	 */
	private Object getValue(HashMap m, String key, Object defaultObj){
		if (m.containsKey(key)) {
			return m.get(key);
		}else return defaultObj;
			
		
	}
	
}
