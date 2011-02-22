
package com.silicontransit.compile;
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
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;



import com.silicontransit.timeline.util.SwingStyler;
import com.silicontransit.timeline.window.LogWindow;
import com.silicontransit.timeline.window.helpers.InputHelper;
import com.silicontransit.timeline.window.helpers.ObjectHelper;
import com.sun.tools.javac.Main;

import examples.javakit.JavaContext;
import examples.javakit.JavaEditorKit;
import examples.javakit.Token;


public class DynCompiler extends JPanel {
	private String base="E:\\src\\Compile\\";
	private static final String DYNAMIC_CLASS_PATH="dynclass";
	public static final String XML_PATH="codeXml";
	private static final String DYNAMIC_SRC_PATH="dynsrc";
	private static final String DYNAMIC_LIB_PATH="lib";
	 	
	private String additionalClassPath="";
	
	private RecompilationListener reCompLsntr=null;
	private HashMap jarsModified;
	private HashMap testObjects=new HashMap();
	boolean compileSuccess=false;
	boolean generateDirty=false;
	boolean xmlDirty=false;
	
	ImageIcon buttonImage=null;
	private Color okColor=new Color(240,250,240);
	private Color errorColor=new Color(250,240,240);
	private Color backgroundColor = new Color(180,180,200);
	
	
	private int  undoPos=0;
	private Vector undo = new Vector();
	
	Vector objects=new Vector(); 
	public JComboBox objectSelector = new JComboBox();
	int lastSelection=-1;
	
	JButton objectAddBut=new SwingStyler.ImgButton("A");
	JButton objectDelBut=new SwingStyler.ImgButton("D");
	JTextField name=new JTextField();
	JTextField exec=new JTextField();
	JTextField superClass=new JTextField();
	JPanel importsPanel = new JPanel();
	JTextArea imports=new JTextArea(); 
	JTextArea jars=new JTextArea(); 
	//JTextArea codeEditor=new JTextArea(); 
	
//	// Java editor kit defualt impl ///////////////////////////////////
	//int lineNumberOffset=0;
	LineNumber lineNumber = new LineNumber();
	JEditorPane codeEditor = lineNumber.getEditorPane();
	JPanel codePanel = new JPanel();
	JavaEditorKit javaKit = new JavaEditorKit();
	Highlighter.HighlightPainter linePainter = new DefaultHighlighter.DefaultHighlightPainter( new Color(255,220,220) );
	Highlighter.HighlightPainter offsetPainter = new DefaultHighlighter.DefaultHighlightPainter( new Color(255,150,150) );
	
	////////////////////////////////////////////////////////////////////////////////////////

	JCheckBox autoCompile=new JCheckBox(); 
	JCheckBox rebuildDeps=new JCheckBox(); 
	JCheckBox reInstantiate=new JCheckBox(); 
	//JTextArea messages=new JTextArea();
	JTextPane messages=new JTextPane();
	JScrollPane messagesScroll=new JScrollPane(messages);
	JPanel messagesPanel = new JPanel();
	
	//JButton compileBut=new JButton("Compile");
	JButton compileBut=new SwingStyler.ImgButton("Compile");
	
	JButton rebuildAllBut=new SwingStyler.ImgButton("Rebuild All");
	JButton executeBut=new SwingStyler.ImgButton("Execute");
	JButton genCodeBut=new SwingStyler.ImgButton("Generate");
	JButton messageHeightToggle=new SwingStyler.ImgButton("^");
	int messageHeight = 80;
	JButton selectJarsBut=new SwingStyler.ImgButton("Jars");
	JScrollPane jarsScroll=null;
	JFileChooser fc;// file select dialog
	
	JButton saveBut=new SwingStyler.ImgButton("Save");
	JButton loadBut=new SwingStyler.ImgButton("Load");

	Timer modTimer;
	TimerTask modTimerTask;
	
	String out;
	String err = "" ;
	public String fileName="";
	ClassLoader lastClassLoader=ClassLoader.getSystemClassLoader();
	public JFrame thisFrame=null;
	String classpathSep=";";
	private Font thisFont = new Font("Arial", Font.BOLD, 11);;
	private Font normFont = new Font("Arial", Font.PLAIN, 10);
	private Font buttonFont = new Font("Arial", Font.BOLD, 10);
	
	private ObjectHelper objectHelper;
	
	
	public DynCompiler(String base,String addCP) {
		thisFrame = new JFrame("Compiler");
		setBase(base);
		this.additionalClassPath=addCP;
		String ver=System.getProperty("java.version");
		if ((ver.indexOf("1.5")==0 || ver.indexOf("1.6")==0)  && "/".equals(File.separator)) {classpathSep=":";}
		WindowListener l = new WindowCloseHandler(); 
		thisFrame.addWindowListener(l);
		thisFrame.getContentPane().add(this);
		thisFrame.setSize(new Dimension(450,550));
		thisFrame.setLocation(new Point(700,400));
		ComponentListener cl = new WindowResizeHandler(); 
		thisFrame.addComponentListener(cl);
		// set up object helper
		objectHelper = new ObjectHelper(InputHelper.TYPE_OBJECT, null,true) ;
		objectHelper.setClassLoader(lastClassLoader);
		objectHelper.setOkAction(new HelperOKListner()	);
		SwingStyler.setStyleCommon(this);
		
		fc=new JFileChooser();
		
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setVgap(0);
		this.setLayout(flowLayout);
		this.setBorder(BorderFactory.createEmptyBorder());
		JPanel jarPanel  = new JPanel();
		jarPanel.setBorder(BorderFactory.createEmptyBorder());
		jarPanel.setBackground(backgroundColor);
		jars.setToolTipText("Jar files");
		jars.addKeyListener(new InputKeyHandler(jars));
		jarsScroll=new JScrollPane(jars);
		//jarsScroll.setPreferredSize(new Dimension(350,40));
		jarPanel.add(jarsScroll);
		SwingStyler.setStyleInput(jars,true);
		
		selectJarsBut.setToolTipText("Select jar files");
		selectJarsBut.setPreferredSize(new Dimension(45,40));
		selectJarsBut.addActionListener(new SelectJarsButtonHandler());
		selectJarsBut.setFont(thisFont);
		
		jarPanel.add(selectJarsBut);
		((FlowLayout)jarPanel.getLayout()).setVgap(0);
		SwingStyler.setStyleCommon(jarPanel);
		this.add(jarPanel);
		
		imports.setFont(thisFont);
		imports.setToolTipText("Imports");
		imports.addKeyListener(new InputKeyHandler(imports));
		SwingStyler.setStyleInput(imports,true);
		JScrollPane importsScroll = new JScrollPane(imports);
		importsScroll.setPreferredSize(new Dimension(400,40));
		importsPanel.setLayout(new GridLayout(1,1));
		importsPanel.add(importsScroll);
		((GridLayout)importsPanel.getLayout()).setVgap(0);
		SwingStyler.setStyleCommon(importsPanel);
		this.add(importsPanel);
		
		JPanel namePanel = new JPanel();
		namePanel.setBorder(BorderFactory.createEmptyBorder());
		namePanel.setBackground(backgroundColor);
		name.setToolTipText("Class name");
		name.addKeyListener(new InputKeyHandler(name));
		name.setVisible(false);
		SwingStyler.setStyleInput(name);
		name.setPreferredSize(new Dimension(210,15));
		namePanel.add(name);
		
		objectSelector.setToolTipText("Class selector");
		objectSelector.addActionListener(new SelectedObjectHandler());
		objectSelector.setVisible(true);
		SwingStyler.setStyleInput(objectSelector);
		objectSelector.setPreferredSize(new Dimension(190,15));
		namePanel.add(objectSelector);
		
		objectAddBut.setToolTipText("Add object");
		objectAddBut.addActionListener(new AddObjectHandler());
		objectAddBut.setPreferredSize(new Dimension(25,15));
		objectAddBut.setVisible(true);
		objectAddBut.setMargin(new Insets(1,1,1,1));
		namePanel.add(objectAddBut);
		
		objectDelBut.setToolTipText("Del object");
		objectDelBut.addActionListener(new DelObjectHandler());
		objectDelBut.setPreferredSize(new Dimension(25,15));
		objectDelBut.setVisible(true);
		objectDelBut.setMargin(new Insets(1,1,1,1));
		namePanel.add(objectDelBut);
		
		superClass.setToolTipText("Super class name");
		superClass.addKeyListener(new InputKeyHandler(superClass));
		superClass.setPreferredSize(new Dimension(150,15));
		SwingStyler.setStyleInput(superClass);
		namePanel.add(superClass);
		((FlowLayout)namePanel.getLayout()).setVgap(0);
		SwingStyler.setStyleCommon(namePanel);
		this.add(namePanel);
		
//		// Java editor kit defualt impl ///////////////////////////////////
		codeEditor.setEditorKitForContentType("text/java", javaKit);
		codeEditor.setContentType("text/java");
		codeEditor.setFont(new Font("Courier", 0, 12));
		codeEditor.setEditable(true);
		codeEditor.setBackground(okColor);
		
	  	setColorForTokens(Token.statements,new Color(102, 153, 102));
	  	setColorForTokens(Token.modifiers,new Color(0, 0, 102));
	  	setColorForTokens(Token.punctuations,new Color(120, 120, 120));
	  	setColorForTokens(Token.values,new Color(150, 50, 50));
	  	setColorForTokens(Token.types,new Color(20, 180, 20));
	  	setColorForToken(Token.STRINGVAL,new Color(120, 120, 20));
	  	setColorForToken(Token.COMMENT,new Color(102, 153, 153));
	  	setColorForToken(Token.IDENT,new Color(50,20,150));
//		//////////////////////////////////////////////////////////////////	
		
		codeEditor.setToolTipText("Class body");
		codeEditor.addKeyListener(new InputKeyHandler(codeEditor));
		codeEditor.setSize(new Dimension(20,250));
		codeEditor.setFont(thisFont);
		lineNumber.setFont(thisFont);
		
		JPanel p = new JPanel();

		p.setLayout(new BorderLayout());
		p.add(lineNumber, BorderLayout.WEST);
		p.add(codeEditor, BorderLayout.CENTER);

		JScrollPane codeScroll = new JScrollPane(p);
		codeScroll.setSize(new Dimension(400,250));//Preferred
		codePanel.setLayout(new GridLayout(1,1));
		codePanel.add(codeScroll);
		codePanel.setBorder(BorderFactory.createEmptyBorder());
		((GridLayout)codePanel.getLayout()).setVgap(0);
		SwingStyler.setStyleCommon(codePanel);
		this.add(codePanel);
		
		
		exec.setToolTipText("Execute method name: clr=clr msg | clrObj=clear test objects | dumpObj=dump test objects");
		SwingStyler.setStyleInput(exec);
		exec.setPreferredSize(new Dimension(400,15));
		this.add(exec);
		
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setBackground(backgroundColor);
		buttonsPanel.setBorder(BorderFactory.createEmptyBorder());
		((FlowLayout)buttonsPanel.getLayout()).setVgap(0);
		
		genCodeBut.setPreferredSize(new Dimension(60,15));
		genCodeBut.addActionListener(new SaveCodeButtonHandler());
		genCodeBut.setMargin(new Insets(1,1,1,1));
		genCodeBut.setToolTipText("Generate java code");
		buttonsPanel.add(genCodeBut);
		
		compileBut.setPreferredSize(new Dimension(50,15));
		compileBut.addActionListener(new CompileButtonHandler());
		compileBut.setMargin(new Insets(1,1,1,1));
		compileBut.setToolTipText("Compile java code");
		buttonsPanel.add(compileBut);
		
		autoCompile.setToolTipText("Auto compile");
		autoCompile.setMargin(new Insets(1,1,1,1));
		autoCompile.setBackground(backgroundColor);
		//autoCompile.setSelected(true);
		SwingStyler.setStyleCommon(autoCompile);
		buttonsPanel.add(autoCompile);
		
		rebuildDeps.setToolTipText("Rebuild dependancies");
		rebuildDeps.setMargin(new Insets(1,1,1,1));
		SwingStyler.setStyleCommon(rebuildDeps);
		buttonsPanel.add(rebuildDeps);
		
		reInstantiate.setToolTipText("Reinstantiate rebuilt objects");
		reInstantiate.setMargin(new Insets(1,1,1,1));
		reInstantiate.setSelected(true);
		SwingStyler.setStyleCommon(reInstantiate);
		buttonsPanel.add(reInstantiate);
		
		rebuildAllBut.setToolTipText("Rebuild all objects");
		rebuildAllBut.setPreferredSize(new Dimension(80,15));
		rebuildAllBut.addActionListener(new RebuildAllButtonHandler());
		rebuildAllBut.setFont(normFont);
		rebuildAllBut.setMargin(new Insets(1,1,1,1));
		buttonsPanel.add(rebuildAllBut);
		
		executeBut.setToolTipText("execute command");
		executeBut.setPreferredSize(new Dimension(60,15));
		executeBut.addActionListener(new ExecButtonHandler());
		executeBut.setMargin(new Insets(1,1,1,1));
		buttonsPanel.add(executeBut);
		
		saveBut.setToolTipText("Save code XML file");
		saveBut.setPreferredSize(new Dimension(30,15));
		saveBut.addActionListener(new SaveButtonHandler());
		saveBut.setMargin(new Insets(1,1,1,1));
		buttonsPanel.add(saveBut);
		
		loadBut.setToolTipText("Load code as XML file");
		loadBut.setPreferredSize(new Dimension(40,15));
		loadBut.addActionListener(new LoadButtonHandler());
		loadBut.setMargin(new Insets(1,1,1,1));
		buttonsPanel.add(loadBut);				
		
		messageHeightToggle.setPreferredSize(new Dimension(20,15));
		messageHeightToggle.addActionListener(new ActionListener (){
				public void actionPerformed(ActionEvent e) {
					if (messageHeight==80) {
						messageHeight=250;
						messageHeightToggle.setText("v");
					} else {
						messageHeight=80;
						messageHeightToggle.setText("^");
					}
					setComponentSizes() ;
				}
			}
		);
		buttonsPanel.add(messageHeightToggle);		
		SwingStyler.setStyleCommon(buttonsPanel);
		this.add(buttonsPanel);		
		
		// set styles for doc
		StyledDocument messagesDoc = messages.getStyledDocument();
		Style def = StyleContext.getDefaultStyleContext(). getStyle(StyleContext.DEFAULT_STYLE);

		Style error = messagesDoc.addStyle(LogWindow.TYPE_ERROR, def);
		StyleConstants.setForeground(error, Color.RED);
		Style warn = messagesDoc.addStyle(LogWindow.TYPE_WARN, def);
		StyleConstants.setForeground(warn, new Color(150,90,0));
		Style msg = messagesDoc.addStyle(LogWindow.TYPE_MSG, def);
		StyleConstants.setForeground(msg, Color.BLUE);
		Style good = messagesDoc.addStyle(LogWindow.TYPE_GOOD, def);
		StyleConstants.setForeground(good, new Color(0,120,0));
		
		messages.setToolTipText("Output messages");
		messagesPanel.setLayout(new GridLayout(1,1));
		messagesPanel.setPreferredSize(new Dimension(380,80));
		messagesPanel.add(messagesScroll);
		((GridLayout)messagesPanel.getLayout()).setVgap(0);
		SwingStyler.setStyleCommon(messagesPanel);
		this.add(messagesPanel);
		
		init();
	}

	public void setBase(String base) {
		if (base!=null) {this.base=base;}
		
		File srcPath= new File(this.base+DYNAMIC_SRC_PATH);
		if (!srcPath.exists()) {srcPath.mkdirs();srcPath.deleteOnExit();}
		File classPath= new File( this.base+DYNAMIC_CLASS_PATH);
		if (!classPath.exists()) {classPath.mkdirs();classPath.deleteOnExit();}
		File xmlPath= new File( this.base+XML_PATH);
		if (!xmlPath.exists()) {xmlPath.mkdirs();}
		File libPath= new File( this.base+DYNAMIC_LIB_PATH);
		if (!libPath.exists()) {libPath.mkdirs();}
		rebuildAll();
	}

	private void setColorForTokens(Token[] set , Color c) {
		JavaContext styles = javaKit.getStylePreferences();
		Style s;
		for (int i=0;i<set.length;i++) {
			  s = styles.getStyleForScanValue(set[i].getScanValue());
			  StyleConstants.setForeground(s, c);
		  }
	}
	private void setColorForToken(Token t , Color c) {
		JavaContext styles = javaKit.getStylePreferences();
		Style s = styles.getStyleForScanValue(t.getScanValue());
		StyleConstants.setForeground(s, c);
	}
	        
	private void init() {
		modTimer = new Timer();
		modTimerTask= new ModifiedTask() ;
		modTimer.scheduleAtFixedRate(modTimerTask,0,1000);
		setComponentSizes() ;
	}
	
	private void setComponentSizes() {
		int width=(int) Math.round(thisFrame.getWidth()-20);
		int winHeight=(int) Math.round(thisFrame.getHeight());
		Dimension codeDimension = new Dimension(width, winHeight-180-messageHeight);
		codePanel.setPreferredSize(codeDimension);
		codePanel.revalidate();
		Dimension importsDimension = new Dimension(width, 40);
		importsPanel.setPreferredSize(importsDimension);
		importsPanel.revalidate();
		Dimension messagesDimension = new Dimension(width, messageHeight);
		messagesPanel.setPreferredSize(messagesDimension);
		messagesPanel.revalidate();
		Dimension execDimension = new Dimension(width, 15);
		exec.setPreferredSize(execDimension);
		exec.revalidate();
		Dimension jarsDimension = new Dimension(width-40, 50);
		jarsScroll.setPreferredSize(jarsDimension);
		jarsScroll.revalidate();
	}
	
	public void open() {
		thisFrame.setVisible(true);
	}
	
////////////////special button  ////////////////////////////////////////////
	
	//////////////// event handlers ////////////////////////////////////////////
	private class HelperOKListner implements ActionListener {	
		public void actionPerformed(ActionEvent e) {	
			int start = codeEditor.getSelectionStart();
			int end = codeEditor.getSelectionEnd();
			String val = objectHelper.getInput();
			if (!"".equals(val)) {
				objectHelper.getHelpFor().setText(
						objectHelper.getHelpFor().getText().substring(0,start)+val+objectHelper.getHelpFor().getText().substring(end)
				);
				if (imports.getText().indexOf(objectHelper.getImportStr())==-1) {
					imports.setText(objectHelper.getImportStr()+"\n"+imports.getText());
				}
			}
		}
	}
	
	public class WindowCloseHandler extends WindowAdapter {
		public void windowClosing(WindowEvent e) {thisFrame.setVisible(false);}
		
	}; 
	
	public class WindowResizeHandler extends ComponentAdapter {
		public void componentResized(ComponentEvent e) {
			setComponentSizes();
			//System.out.println("resize"+e.getComponent().getWidth());
		}
	}

	private class InputKeyHandler implements KeyListener,FocusListener {
		
		String sel = "";
		boolean ctrl=false;
		boolean shift=false;
		
		JTextComponent cmp=null;
		public InputKeyHandler(JTextComponent cmp) {
			this.cmp=cmp;
			cmp.addFocusListener(this);
		}

		public void keyPressed(KeyEvent e) {
			if (undoPos>undo.size()) {undoPos=undo.size()-1;}
			if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
				ctrl=true;
			}
			if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
				shift=true;
			}
			if (	!ctrl &&
					!(ctrl && e.getKeyCode() == 90) 
					&& e.getKeyCode() !=  KeyEvent.VK_UP
					&& e.getKeyCode() !=  KeyEvent.VK_DOWN
					&& e.getKeyCode() !=  KeyEvent.VK_LEFT
					&& e.getKeyCode() !=  KeyEvent.VK_RIGHT
					&& e.getKeyCode() !=  KeyEvent.VK_SHIFT
					&& e.getKeyCode() !=  KeyEvent.VK_ALT
					&& e.getKeyCode() !=  KeyEvent.VK_CONTROL
					) {
				saveCodeUndo();
			}
			if ( e.getKeyCode() == KeyEvent.VK_F1) {// object help
				objectHelper.setVisible(true);
				objectHelper.setInput("");
				objectHelper.requestFocus();
				objectHelper.setHelpFor(cmp);
			}
		}
			 
		public void keyReleased(KeyEvent e) {
			System.out.println("rel:"+ctrl +":"+ e.getKeyCode() +":"+'z'+undo.size());
			if (e.getKeyCode()== KeyEvent.VK_TAB && sel!=null && sel.length()>0) {
				String text=cmp.getText();
				int selStart = cmp.getSelectionStart()-1;// deletes tab (which was just inserted)
				int selEnd = cmp.getSelectionEnd();
				if (text.charAt(selStart-1) =='\n') {selStart--;sel="\n"+sel;	}// select preceding \n
				String selRep=sel.replaceAll("\n", "\n\t");
				if (shift) {	 selRep=sel.replaceAll("\n\t", "\n");			}
				cmp.setText(
					text.substring(0,selStart)+
					selRep+
					text.substring(selEnd)
				);
				cmp.select(selStart, selStart+selRep.length());
			}  else if (ctrl && e.getKeyCode() == 90 && undoPos>-1) {
				restoreCodeUndo();
			}  else if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
				ctrl=false;
			} else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
				shift=false;
			}
		}
		
		public void keyTyped(KeyEvent e) { 
			generateDirty=true;	
			xmlDirty=true;
		}
		
		private void saveCodeUndo() {
			sel=codeEditor.getSelectedText();	
			// save undo.
			DynCompilerObjectBean backupObj=new DynCompilerObjectBean();
			//	name.getText(),	imports.getText(),	superClass.getText(),codeEditor.getText());
			updateBeanFromUI(backupObj);
			for (;undo.size()>undoPos && undoPos>-1;) { undo.remove(undoPos);	}
			undo.add(backupObj);
			undoPos++;
			System.out.println("undo:"+undoPos+":"+undo.size());
		}
		
		private void restoreCodeUndo() {
			int cursorPos=cmp.getSelectionStart();
			if (undoPos==undo.size() && undoPos>-1 && !shift ) {saveCodeUndo(); undoPos--;}// save current if 
			if (shift) {
				if (undoPos<undo.size()-1) {undoPos++;}
			} else {
				if (undoPos>0) {undoPos--;}
			}
			if (undoPos>-1 && undoPos<undo.size()) {
				DynCompilerObjectBean restore = (DynCompilerObjectBean)undo.get(undoPos);
				updateUIFromBean(restore);
				cmp.select(cursorPos, cursorPos);
			}
			System.out.println("redo:"+undoPos+":"+undo.size());
		}

		public void focusGained(FocusEvent e) {	}

		public void focusLost(FocusEvent e) {
			this.ctrl=false;
			this.shift=false;
		}
	}
	private class RebuildAllButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent ae) {			rebuildAll();		}
	}
	private class ExecButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent ae) {			invoke();		}
	}
	
	private class SaveButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent ae) {			
			fc.setMultiSelectionEnabled(false);
			fc.setCurrentDirectory(new File(base+XML_PATH));
		
			int returnVal = fc.showSaveDialog(DynCompiler.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				saveXML(fileName);	
			}
		}
	}
		
	private class LoadButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent ae) {		
			fc.setMultiSelectionEnabled(false);
			fc.setCurrentDirectory(new File(base+XML_PATH));
			int returnVal = fc.showOpenDialog(DynCompiler.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				String fileName=fc.getSelectedFile().getAbsolutePath();
				loadXML(fileName);	
			}
		}
	}
	
	public class SelectedObjectHandler implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			if (lastSelection>-1){
				DynCompilerObjectBean lastObj=(DynCompilerObjectBean)objectSelector.getItemAt(lastSelection);
				if (generateDirty && !confirm("Changes will be lost ...")) {
					objectSelector.setSelectedIndex(lastSelection);
					return;
				}
			}
			DynCompilerObjectBean thisObj=(DynCompilerObjectBean)objectSelector.getSelectedItem();
			if (thisObj!=null) {updateUIFromBean(thisObj);}
			else {
				codeEditor.setText("");
				imports.setText("");
			}
			lastSelection=objectSelector.getSelectedIndex();
			undo.clear();
		}
	}
	
	private class AddObjectHandler implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			String buttonText=objectAddBut.getText();
			if ("A".equals(buttonText)) {
				if (generateDirty && !confirm("Changes will be lost ...")) {return; } 
				objectSelector.setVisible(false);
				name.setVisible(true);
				DynCompilerObjectBean dcob=(DynCompilerObjectBean)objectSelector.getSelectedItem();
				if (objectSelector.getItemCount()>0 && dcob!=null && dcob.getName()!=null) {
					name.setText(dcob.getName().substring(0,dcob.getName().lastIndexOf(".")+1));
				}
				objectAddBut.setText("Y");
				objectDelBut.setVisible(false);
			} else {
				if (!"".equals(name.getText())) {
					boolean found=false;
					for (int i=0;i<objectSelector.getItemCount();i++) {
						DynCompilerObjectBean dcob=(DynCompilerObjectBean)objectSelector.getItemAt(i);
						if (name.getText().equals(dcob.getName())) {found=true;}
					}
					if (found) {addMessage("This name already exists please change ...\n",LogWindow.TYPE_ERROR);return;			}
					if ((name.getText().charAt(name.getText().length()-1)=='.') || (name.getText().indexOf(" ")>-1)) {
						addMessage("This name is illegal please change ...\n",LogWindow.TYPE_ERROR);return;
					}
				
					DynCompilerObjectBean newObject=new DynCompilerObjectBean();
					//strip bad chars.
					String[] newNameParts=name.getText().split("\\.");
					String newName="";
					for (int i=0;i<newNameParts.length;i++) {
						String addText=newNameParts[i].replaceAll("[\\W]*","");
						if (i<newNameParts.length-1) { addText=addText.toLowerCase()+".";}
						else {addText=addText.substring(0,1).toUpperCase()+addText.substring(1,addText.length());}
						newName+=addText;
					}
					newObject.setName(newName);
					objectSelector.addItem(newObject);
					objectSelector.setSelectedItem(newObject);
					updateUIFromBean(newObject);
					xmlDirty=true;
				}
				objectSelector.setVisible(true);
				name.setVisible(false);
				objectAddBut.setText("A");
				objectDelBut.setVisible(true);
			}
		}
	}
	private class DelObjectHandler implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			String buttonText=objectAddBut.getText();
			if (confirm("Object will be permanently deleted ...")) {
				objectSelector.removeItem(objectSelector.getSelectedItem());
			} 
		}
	}
	private boolean confirm(String title) {
		Object[] options = {"Yes", "No"};
		int n = JOptionPane.showOptionDialog(
				thisFrame,
				title+"\nAre you sure?", 
				"Confirm",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[0]
			);
		return (n == JOptionPane.YES_OPTION);
	}
	
	private class CompileButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {	compile();}
	}
	
	private class SaveCodeButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent ae) {			generateFile();		}
	}
	
	private class SelectJarsButtonHandler implements ActionListener {

		public SelectJarsButtonHandler() {
			super();
			fc.setFileFilter(new JarFileFilter());
		}

		public void actionPerformed(ActionEvent ae) {
			fc.setMultiSelectionEnabled(true);
			fc.setCurrentDirectory(new File(base+DYNAMIC_LIB_PATH));
			
			int returnVal = fc.showOpenDialog(DynCompiler.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File[] files = fc.getSelectedFiles();
				for (int i=0;i<files.length;i++) {
					jars.append(files[i].getAbsolutePath()+"\n");
				}
				if (files.length==0) {jars.append(fc.getSelectedFile().getAbsolutePath()+"\n");}
			}
		}
		
		class JarFileFilter extends FileFilter{
			public boolean accept(File f) {
				if (!f.isDirectory() && f.getAbsolutePath().endsWith("jar")) {return true;}
				if (f.isDirectory()) {return true;}
				return false;
			}
			public String getDescription() {		return "Jar files";	}
		}
		
	}
//	////////////// end event handlers ////////////////////////////////////////////
	public void  clear() {
		try {
			while (objectSelector.getItemCount()>1) {
				objectSelector.removeItemAt(0);
			}
		} catch (Exception e) {}
		codeEditor.setText("");
		imports.setText("");
		name.setText("");
		superClass.setText("");
		jars.setText("");
		messages.setText("");
		
	}
	private void showErrorAt(int line,int offset,String error) {
		Document doc = codeEditor.getDocument();
		Element errLineElement=doc.getDefaultRootElement().getElement(line - lineNumber.getOffset() - 1);// line number start at zero in doc
		try {
			int startLine=errLineElement.getStartOffset();
			codeEditor.getHighlighter().addHighlight( startLine+offset, startLine+offset+1, offsetPainter );
			codeEditor.getHighlighter().addHighlight(startLine , errLineElement.getEndOffset() , linePainter );
		} catch (BadLocationException e) {
			
		}
	}

	public class ModifiedTask extends TimerTask{
		private int counter=0;
		public void run() {
			try {
				counter++;
				String[] jarList=jars.getText().split("\n");
				String jarListOut="";
				HashMap nextMod=new HashMap();
				for (int i=0;i<jarList.length;i++) {
					File jar=new File(jarList[i]);
					if (jar.exists()) {
						Long lastModTime=(Long)jarsModified.get(jarList[i]);
						if (lastModTime==null || (lastModTime!=null && jar.lastModified()>lastModTime.longValue())) {
							if (reLoadJar(jar)) {
								jarListOut+=jar.getAbsolutePath()+"\n";
							}
						} else {
							jarListOut+=jar.getAbsolutePath()+"\n";
						}
						nextMod.put(jarList[i], new Long(jar.lastModified()));
					}
				}
				jarsModified = nextMod;
				// auto compile.
				if (((counter%=5)==0) && autoCompile.isSelected() && ( !compileSuccess || generateDirty) ) {
					generateFile();
					compile();
				}
				jars.setText(jarListOut);
			} catch (Exception e) {
				addMessage(""+e.getClass().getName()+":"+e.getMessage(),LogWindow.TYPE_ERROR);
			}
		}
	}
	
	private void clearMessages() {
		messages.setText("");
	}
	public void addMessage(String msg, String type) {
		
		addMessage(msg,false, type);	
	}
	public void addMessage(String msg, boolean clear, String type) {
		if (clear) {clearMessages();}
		try {
			StyledDocument doc = messages.getStyledDocument();
			doc.insertString(doc.getLength(), msg+"\n",	doc.getStyle(type));
			messages.scrollRectToVisible(new Rectangle(1,messages.getHeight()));
			LogWindow.log(msg, type);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	public void generateFile() {
		DynCompilerObjectBean o = (DynCompilerObjectBean)objectSelector.getSelectedItem();
		updateBeanFromUI(o);
		generateFile(o);
	}
	
	private void generateFile(DynCompilerObjectBean o) {
		File srcFile;
		String codeString;
		o.setFile(null);
		if (!"".equals(o.getName())) {
			codeString="";
			srcFile=new File(base+DYNAMIC_SRC_PATH+File.separator+o.getName().replace('.', File.separatorChar)+".java");
			String packageName=null;
			
			if (o.getName().indexOf(".")>-1) {
				packageName=o.getName().substring(0,o.getName().lastIndexOf("."));
			}
			String className=o.getName();
			if (packageName!=null) {
				className=o.getName().substring(o.getName().lastIndexOf(".")+1,o.getName().length());
			}
			int lineNumberOffset=0;
			if (packageName!=null) {
				codeString="package "+packageName+";\n";
				lineNumberOffset++;
			}
			if (!"".equals(o.getImports())) {
				String[] importList=o.getImports().split("\n");
				lineNumberOffset++;
				for (int i=0;i<importList.length;i++) {
					codeString+= "import "+importList[i]+";\n";
					lineNumberOffset++;
				}
			}
			codeString+= "\npublic class "+className;
			lineNumberOffset++;
			if (!"".equals(o.getSuperClass())) {
				codeString+= " extends "+o.getSuperClass();
			}
			codeString+=" {\n"+o.getCode()+"\n}\n";
			lineNumberOffset++;
			lineNumber.setOffset(lineNumberOffset);
			try {
		
				if (!srcFile.getParentFile().exists()) {
					srcFile.getParentFile().mkdirs();
				}
				FileWriter fo=new FileWriter(srcFile);
				fo.write(codeString);
				fo.flush();
				fo.close();
				o.setFile(srcFile);
				generateDirty=false;
				addMessage("File saved: "+srcFile.getAbsolutePath(),LogWindow.TYPE_MSG);
			} catch (IOException e) {
				addMessage("io exception:"+e.getMessage(),LogWindow.TYPE_ERROR);
			}
		}
	}
	private void rebuildAll() {
		for (int i=0;i<objectSelector.getItemCount();i++) {
			DynCompilerObjectBean o = (DynCompilerObjectBean)objectSelector.getItemAt(i);
			generateFile(o);
			compile(o);
		}
	}
	private void rebuildDeps(DynCompilerObjectBean o) {
		for (int i=0;i<objectSelector.getItemCount();i++) {
			DynCompilerObjectBean checkObj = (DynCompilerObjectBean)objectSelector.getItemAt(i);
			if (checkObj.getImports().indexOf(o.getName())>-1) {
				generateFile(checkObj);
				compile(checkObj);
			}
		}
	}
	
	public DynCompilerObjectBean getDCOB(String name){
		for (int i=0;i<objectSelector.getItemCount();i++) {
			DynCompilerObjectBean dcob=(DynCompilerObjectBean)objectSelector.getItemAt(i);
			if (dcob.getName().equals(name)) {return dcob;}
		} 
		return null;
	}
	
	private void compile() {
		DynCompilerObjectBean o = (DynCompilerObjectBean)objectSelector.getSelectedItem();
		compile(o);
	}
	
	private void compile(DynCompilerObjectBean o) {
		if (o.getFile()!=null) {
			//clearMessages();
			out="";err="";
			int ctr=0;
			
			String classPath=jars.getText().replaceAll("\n",classpathSep);
			classPath+=classpathSep+base+DYNAMIC_CLASS_PATH;
			if (!"".equals(additionalClassPath)) {classPath+=classpathSep+additionalClassPath.replaceAll(";",classpathSep);}
			String[] args = new String["".equals(classPath)?3:5];
			args[ctr++]= "-d";
			args[ctr++]= base+DYNAMIC_CLASS_PATH;
			if (!"".equals(classPath)) {
				args[ctr++]= "-classpath";
				args[ctr++]= classPath;
			}
			args[ctr++]= o.getFile().getAbsolutePath();
			
			if (o==objectSelector.getSelectedItem()) {compileSuccess=false;	}
			String compileCall="java";
			for (int i=0;i<args.length;i++) {compileCall+=" "+args[i];}
			addMessage("compile:"+compileCall, LogWindow.TYPE_MSG);
			codeEditor.getHighlighter().removeAllHighlights();
			lineNumber.getErrLines().clear();
			try {
				StringWriter sw = new StringWriter();
				PrintWriter compilerOutput=new PrintWriter(sw);
				int status = Main.compile(args, compilerOutput);
				if (status==0) {				
					reLoadClass(o);
					if (rebuildDeps.isSelected()) {rebuildDeps(o);}
					if (reInstantiate.isSelected()) {
						reInstantiate(o.getGeneratedClass(),testObjects);
						if (reCompLsntr!=null) {
							reInstantiate(o.getGeneratedClass(),reCompLsntr.getDynamicObjects());
							reCompLsntr.reLoadedEvent();
						}
					}
					codeEditor.setBackground(okColor);
				} else {
					addMessage("Compile unsuccessful - problem:"+sw.getBuffer().toString(),LogWindow.TYPE_ERROR);
					codeEditor.setBackground(errorColor);
					String errs=sw.getBuffer().toString();
					int pos = 0;
					pos = errs.indexOf(o.getFile().getAbsolutePath(), pos);
					int count=0;
					while (pos>-1) {
						count++;
						pos+=o.getFile().getAbsolutePath().length();
						int firstColon=errs.indexOf(":",pos);
						int secondColon=errs.indexOf(":",firstColon+1);
						int errorNumLine = Integer.parseInt(errs.substring(firstColon+1,secondColon));
						int endErrorDescLine=errs.indexOf("\n",secondColon+1);
						String errorStr=errs.substring(secondColon+1,endErrorDescLine);
						int endErrorCodeLine=errs.indexOf("\n",endErrorDescLine+1);
						int caretMark=errs.indexOf("^",endErrorCodeLine+1);
						int offset=caretMark-errs.lastIndexOf("\n", caretMark)-1;
						showErrorAt(errorNumLine, offset, errorStr);
						lineNumber.addErrLine(errorNumLine);
						//System.out.println("errs:"+errorNumLine+":"+ offset+":"+ errorStr);
						pos=errs.indexOf(o.getFile().getAbsolutePath(), pos);
						
					}
					
				}
			} catch (Exception ex) {
				addMessage(ex.getClass().getName()+":"+ex.getMessage(),LogWindow.TYPE_ERROR);
			}
		}
	}

	public void reLoadAllClasses() {
		for (int i=0;i<objectSelector.getItemCount();i++) {
			DynCompilerObjectBean o = (DynCompilerObjectBean)objectSelector.getItemAt(i);
			reLoadClass(o);
		}
	}
	
	public void reLoadClass(DynCompilerObjectBean o){
		// load the Class.
		if (o==objectSelector.getSelectedItem()) {compileSuccess=true;}
		
		String srcPath=o.getFile().getAbsolutePath();
		String basePath=srcPath.substring((base+DYNAMIC_SRC_PATH).length()+1,srcPath.lastIndexOf("."));
		File classFile=new File(base+DYNAMIC_CLASS_PATH+File.separator+basePath+".class");
		URL[] urls;
		File dynamicClassPathDir=new File(base+DYNAMIC_CLASS_PATH);
		try {
			urls = new URL[]  { dynamicClassPathDir.toURL()};
			URLClassLoader ucl=new URLClassLoader( urls, lastClassLoader );
			String className=classFile.getName().substring( 0, classFile.getName().length()-6 );
			o.setGeneratedClass( ucl.loadClass( o.getName() ) );
			addMessage("Reloaded class: "+className  ,LogWindow.TYPE_GOOD);
			//lastClassLoader = ucl;  // this kills it for some reason the old class isnt instantialted.
		} catch (MalformedURLException e) {
			addMessage("mu:"+e.getMessage(),LogWindow.TYPE_ERROR);
		} catch (ClassNotFoundException e) {
			addMessage("cnf:"+e.getMessage(),LogWindow.TYPE_ERROR);
		} 
	}
	
	public Class getClass(String name)  {
		for (int i=0; i<objectSelector.getItemCount();i++) {
			DynCompilerObjectBean dcob= (DynCompilerObjectBean)objectSelector.getItemAt(i);
			if (dcob.getName().equals(name)) {
				return dcob.getGeneratedClass();
			}
		}
		return null;
	}
	public Object getInstance(String name,Object[] args) throws Exception {
		Class[] sign = {};
		if (args!=null ) {
			sign=new Class[args.length];
			for (int j=0;j<args.length;j++) {sign[j]=args[j].getClass();}
		}
		for (int i=0; i<objectSelector.getItemCount();i++) {
			DynCompilerObjectBean dcob= (DynCompilerObjectBean)objectSelector.getItemAt(i);
			if (dcob.getName().equals(name)) {
				
				try {
					if (args!=null ) {
						Constructor constructor=dcob.getGeneratedClass().getDeclaredConstructor(sign);
						return constructor.newInstance(args);
					} else {
						return dcob.getGeneratedClass().newInstance();
					}
				} catch (InstantiationException e) {		throw e;	} 
				catch (IllegalAccessException e) {throw e;	}
			}
		}
		try {
			Class theClass=Class.forName(name,true,lastClassLoader);
			if (theClass!=null) {
				if (args!=null ) {
					Constructor constructor=theClass.getConstructor(sign);
					return constructor.newInstance(args);
				}
				else return theClass.newInstance();
			}
		}catch (Exception e) {
			
		}
		return null;
	}
	
	public Object invoke(Object currentObject, String methodName, Object[] args) throws Exception {
		try {
			Class[] sign=new Class[args.length];
			for (int i=0;i<args.length;i++) {sign[i]=args[i].getClass();}
			/*
			// 
			
			Method m = null;
			Method[] methods=currentObject.getClass().getMethods();
			for (int i=0;i<methods.length;i++) {
				if (methods[i].getName().equals(methodName)) {
					Class[] params = methods[i].getParameterTypes();
					boolean isTheSame=true;
					if( params.length!=sign.length) {continue;}
					for (int j=0;j<params.length;j++) {
						isTheSame=isTheSame&&(params[i].equals(sign[i]));
					}
					if (isTheSame) {
						m=methods[i];break;
					}
				}
					
			}
			if (m==null) {return null;}
			 */
			Method m=currentObject.getClass().getMethod( methodName , sign );
			Object output=m.invoke( currentObject, args );
			return output;
		} catch (InvocationTargetException e) {
			addMessage(e.getClass().getName()+" - "+e.getTargetException().getClass().getName()+":"+e.getMessage(),LogWindow.TYPE_ERROR);
		} catch (IllegalAccessException e) {
			addMessage("ia:"+e.getMessage(),LogWindow.TYPE_ERROR);
		} catch (Exception e) {
			addMessage(e.getClass().getName()+":"+e.getMessage(),LogWindow.TYPE_ERROR);
		} 
		return null;
	}
	
	private void invoke() {
		clearMessages();
		DynCompilerObjectBean dcob = (DynCompilerObjectBean)objectSelector.getSelectedItem();
		if (dcob.getGeneratedClass()!=null) {
			Class[] sign=new Class[]{};// empty methods
			try {
				String execText=exec.getText();
				if (execText.equals("clr")) {addMessage("cleared", true, LogWindow.TYPE_ERROR);return;	}
				if (execText.equals("clrObj")) {testObjects.clear();return;	}
				if (execText.equals("dumpObj")) {
					Iterator i= testObjects.keySet().iterator();
					while (i.hasNext()) {
						String key=(String)i.next();
						addMessage(key+" = "+testObjects.get(key).getClass(),LogWindow.TYPE_MSG);	
					}
					return;
				}
				String object=execText.substring(0,execText.indexOf("."));
				String methodName=execText.substring(execText.indexOf(".")+1);
				Object currentObject=null;
				if (Character.isLowerCase(object.charAt(0))) {
					currentObject=testObjects.get(object);
					if (currentObject==null) {
						currentObject = getInstance(dcob.getName(),null);
						testObjects.put(object,currentObject);
					}
				} else {
					currentObject = getInstance(dcob.getName(),null);
				}
				//Method m=currentObject.getClass().getMethod( methodName , sign );//Declared 
				Object[] args=new Object[]{};
				Object output=invoke(currentObject,methodName,args);
				addMessage( output.toString() ,LogWindow.TYPE_MSG);
				
			} catch (Exception e) {
				addMessage(e.getClass().getName()+":"+e.getMessage(),LogWindow.TYPE_ERROR);
			} 
		}
	}
//	////////////// bean <-> UI fields copy ///////////////////////////////////////////////////////////////////////////////////////////
	public void updateUIFromBean(DynCompilerObjectBean o) {
		if (o==null) {o=new DynCompilerObjectBean();}
		codeEditor.setText(o.getCode());
		imports.setText(o.getImports());
		superClass.setText(o.getSuperClass());
		name.setText(o.getName());
	}
	
	public void updateBeanFromUI(DynCompilerObjectBean o) {
		if (o==null) {return;}
		o.setCode(codeEditor.getText());
		o.setImports(imports.getText());
		o.setSuperClass(superClass.getText());
		o.setName(name.getText());
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////// object reInstantiate ///////////////////////////////////////////////////////////////////////////////////////////
	
	private boolean containsClass(Class clazz, HashMap h) {
		for (Iterator iterator = h.keySet().iterator(); iterator.hasNext();) {
			Object key = (Object) iterator.next();
			Object obj = h.get(key);
			if (obj.getClass().getName().equals(clazz.getName())) {
				return true;
			}
		}
		return false;
	}
	
	// re instantiate all instances of the class in the hashMap
	public void reInstantiate(Class clazz, HashMap objectMap) {
		Iterator i=objectMap.keySet().iterator();
		while (i.hasNext()) {
			String key=(String)i.next();
			Object object=objectMap.get(key);
			if (object.getClass().getName().equals(clazz.getName())) {
				objectMap.put(key, reInstantiateAndCopy( object, clazz));
				addMessage("Reinstantiated:"+key+"("+object.getClass().getSimpleName()+")",LogWindow.TYPE_MSG);
			}
		}
	}
	
	/**
	 * this doesnt re instantiate deeply. member object are re-assigned to the newly created obect.
	 * @param oldObject
	 * @param clazz
	 * @return
	 */
	public Object reInstantiateAndCopy(Object oldObject, Class clazz) {
		
		try {// still ned to reInstantiate dependant objects? maybe.
			
//			if (dcob.getGeneratedClass()==null) {
//				generateFile(dcob);
//				compile(dcob);
//			}
			
			Object newObject = clazz.newInstance();
			Field[] fields=clazz.getFields();
			for (int i=0; i<fields.length;i++) {
				String type=fields[i].getType().getName();
				try {
					Field oldField=oldObject.getClass().getField(fields[i].getName());
					if ("boolean".equals(type)) {
						fields[i].setBoolean(newObject,oldField.getBoolean(oldObject));
					} else if ("byte".equals(type)) {
						fields[i].setByte(newObject,oldField.getByte(oldObject));
					} else if ("char".equals(type)) {
						fields[i].setChar(newObject,oldField.getChar(oldObject));
					} else if ("double".equals(type)) {
						fields[i].setDouble(newObject,oldField.getDouble(oldObject));
					} else if ("float".equals(type)) {
						fields[i].setFloat(newObject,oldField.getFloat(oldObject));
					} else if ("int".equals(type)) {
						fields[i].setInt(newObject,oldField.getInt(oldObject));
					} else if ("long".equals(type)) {
						fields[i].setLong(newObject,oldField.getLong(oldObject));
					} else if ("short".equals(type)) {
						fields[i].setShort(newObject,oldField.getShort(oldObject));
					} else {
						fields[i].set(newObject,oldField.get(oldObject));
					} 
				} catch (Exception e) {
					addMessage(e.getClass().getName()+" : "+e.getMessage(),LogWindow.TYPE_ERROR);
				}
			}
			Method[] methods=clazz.getMethods();
			for (int i=0; i<methods.length;i++) {
				if (methods[i].getName().indexOf("set")==0) {
					try {
						Class[] params=methods[i].getParameterTypes();
						String methodName=methods[i].getName().replaceFirst("s","g");
						Method oldGetMethod=oldObject.getClass().getMethod(methodName,new Class[]{});
						if (oldGetMethod!=null) {
							Object oldFieldObject=oldGetMethod.invoke(oldObject,new Object[]{});
							methods[i].invoke(newObject, new Object[] {oldFieldObject});
						}
					} catch(Exception e) {
						addMessage(e.getClass().getName()+" : "+e.getMessage(),LogWindow.TYPE_ERROR);
					}
				}
			}
			return newObject;
		} catch (InstantiationException e) {
			addMessage("ie:"+e.getMessage(),LogWindow.TYPE_ERROR);
		} catch (IllegalAccessException e) {
			addMessage("ia:"+e.getMessage(),LogWindow.TYPE_ERROR);
		} 
		return null;
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////// jar reload stuff ///////////////////////////////////////////////////////////////////////////////////////////////////
	private class CustomClassLoader extends ClassLoader{
		public CustomClassLoader(ClassLoader c){
			super(c);
		}
		public Class defClass(String name, byte[] b){
			try {
				if (findLoadedClass(name)==null) {
					return super.defineClass( name,  b,0,b.length);
				} else return null;
			} catch (ClassFormatError e) {
				addMessage("ClassFormatError: "+e.getMessage()+"-"+name,LogWindow.TYPE_ERROR);
				return null;
			} catch (NoClassDefFoundError e) {
				addMessage("ClassNotFoundException: "+e.getMessage()+"-"+name,LogWindow.TYPE_ERROR);
				return null;
			} catch (LinkageError e) {
				addMessage("LinkageError: "+e.getMessage()+"-"+name,LogWindow.TYPE_ERROR);
				return null;
			} 
		}
		
		public void resClass(Class c) {
			super.resolveClass(c);
		}
		public Class ldClass(String name) {
			try {
				return super.loadClass(name,true);
			} catch (ClassNotFoundException e) {
				addMessage("ClassNotFoundException: "+e.getMessage(),LogWindow.TYPE_ERROR);
			}
			return null;
		}
	}
	
	public boolean reLoadJar(File jar) {
		boolean returnValue=false;
		try{
			JarFile jarFile=new JarFile(jar);
			Enumeration e=jarFile.entries();
			CustomClassLoader ccl=new CustomClassLoader(lastClassLoader);
			Vector retryClasses=new Vector();
			Vector classesLoaded=new Vector();
			while (e.hasMoreElements()) {
				JarEntry je=(JarEntry)e.nextElement();
				if (je.getName().endsWith(".class")) {
					InputStream is=jarFile.getInputStream(je);
					int av = is.available();
					byte[] buffer=new byte[av];
					int numRead = 0;
					while (av>numRead) {
						numRead += is.read(buffer,numRead,av-numRead);
					}
					if (numRead != av) {addMessage("Couldnt read all bytes re:"+numRead+" av:"+av, LogWindow.TYPE_WARN);}
					String name=je.getName().substring(0,je.getName().length()-6).replaceAll("/",".");
					try {
						Class classLoaded=ccl.defClass(name,buffer);
						if (classLoaded!=null) {
							ccl.resClass(classLoaded);
							classesLoaded.add(ccl.ldClass(name));
							addMessage("loaded: "+name,LogWindow.TYPE_MSG);
							returnValue=true;
						}else {
							Vector v=new Vector();
							v.add(name);v.add(buffer);
							retryClasses.add(v);
						}
					} catch (Exception ex) {
						addMessage("ex: ("+ex.getClass().getSimpleName()+") :"+ex.getMessage(),LogWindow.TYPE_ERROR);
					} 
				} else {
					addMessage(je.getName() + " skipped.", LogWindow.TYPE_WARN);
				}
			}
			addMessage("reloading "+retryClasses.size()+" skipped..", LogWindow.TYPE_WARN);
			boolean noneReloaded=false;
			while (!noneReloaded &&retryClasses.size()>0 ) {
				noneReloaded=true;
				for (int i=0;i<retryClasses.size();i++) {
					Vector v=(Vector) retryClasses.get(i);
					try {
						String clname = (String)v.get(0);
						Class classLoaded=ccl.defClass(clname, (byte[])v.get(1));
						if (classLoaded!=null) {
							ccl.resClass(classLoaded);
							classesLoaded.add(ccl.ldClass(clname));
							addMessage("loaded: "+clname,LogWindow.TYPE_MSG);
							noneReloaded=false;
							retryClasses.remove(v);
							i--;
						}
					} catch (Exception ex) {
						addMessage("ex: ("+ex.getClass().getSimpleName()+") :"+ex.getMessage(),LogWindow.TYPE_ERROR);
					} 
				}
			}
			lastClassLoader=ccl;
			objectHelper.setClassLoader(lastClassLoader);
			objectHelper.setAdditionalCP(jars.getText().replaceAll("\n",classpathSep));
			reLoadAllClasses();
			if (reInstantiate.isSelected()) {
				for (int y=0;y<classesLoaded.size();y++) {
					Class clazz = (Class) classesLoaded.get(y);
					if ( containsClass(clazz,testObjects)) {
						reInstantiate(clazz,testObjects);
					}
					if (reCompLsntr!=null) {
						if ( containsClass(clazz,reCompLsntr.getDynamicObjects())) {
							reInstantiate(clazz,reCompLsntr.getDynamicObjects());
//							reCompLsntr.reLoadedEvent();
						}
					}
				}
			}
		} catch (MalformedURLException e) {
			addMessage("mu:"+e.getMessage(),LogWindow.TYPE_ERROR);
		} catch (IOException e) {
			addMessage("io:"+e.getMessage(),LogWindow.TYPE_ERROR);
		} catch (Exception e) {
			addMessage("ex: ("+e.getClass().getSimpleName()+") :"+e.getMessage(),LogWindow.TYPE_ERROR);
		} 
		return returnValue;
	}

	
//	////////////// end jar reload stuff ///////////////////////////////////////////////////////////////////////////////////////////////////
	public boolean isXMLDirty() {
		return xmlDirty;
	}
	public void saveXML(String fileName) {
		if (!fileName.endsWith(".xml")) {fileName+=".xml";}
		if (fileName.indexOf(File.separator)==-1) {fileName=base+XML_PATH+File.separator+fileName;}
		if (generateDirty) {
			DynCompilerObjectBean thisObj=(DynCompilerObjectBean)objectSelector.getSelectedItem();
			updateBeanFromUI(thisObj);
		}
		DynCompilerLoadSave dcls=new DynCompilerLoadSave();
		dcls.save(fileName,this);
		this.fileName=fileName;
		xmlDirty=false;
	}
	
	public org.w3c.dom.Element saveElement(org.w3c.dom.Document d) {
		if (generateDirty) {
			DynCompilerObjectBean thisObj=(DynCompilerObjectBean)objectSelector.getSelectedItem();
			updateBeanFromUI(thisObj);
		}
		DynCompilerLoadSave dcls=new DynCompilerLoadSave();
		org.w3c.dom.Element objectsElement = dcls.writeSaveElement(this,d);
		xmlDirty=false;
		return objectsElement;
	}
	
	public void loadXML(String fileName) {
		if (!fileName.endsWith(".xml")) {fileName+=".xml";}
		DynCompilerLoadSave dcls=new DynCompilerLoadSave();
		dcls.load(fileName,this);
		
		this.fileName=fileName;
		rebuildAll();
		xmlDirty=false;
	}
	
	public void loadElement(org.w3c.dom.Element objectElement) {
		DynCompilerLoadSave dcls=new DynCompilerLoadSave();
		dcls.clearObjects(this);
		dcls.readLoadElement(this,objectElement);
		rebuildAll();
		xmlDirty=false;
	}
	
///////////////////////////////// main method - not used //////////////////////////////////////////////////////////////
	public static void main(String s[]) {
		new DynCompiler(null,"");
	}
///////////////////////////////// end main method //////////////////////////////////////////////////////////////
	/**
	 * @return
	 */
	public RecompilationListener getReCompLsntr() {
		return reCompLsntr;
	}

	/**
	 * @param listener
	 */
	public void setReCompLsntr(RecompilationListener listener) {
		reCompLsntr = listener;
	}
	public void setVisible(boolean vis) {
		thisFrame.setVisible(vis);
	}
	public boolean getVisible() {
		return thisFrame.isVisible();
	}

	public ClassLoader getClassLoader() {
		return lastClassLoader;
	}

	public void setClassLoader(ClassLoader lastClassLoader) {
		this.lastClassLoader = lastClassLoader;
	}
	
}


