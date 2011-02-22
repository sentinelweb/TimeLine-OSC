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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import oscbase.OscIn;
import oscbase.OscMessage;
import oscbase.OscP5;
import processing.core.PApplet;
import promidi.Controller;
import promidi.MidiIO;
import promidi.MidiOut;
import promidi.Note;
import promidi.ProgramChange;

import com.silicontransit.compile.DynCompiler;
import com.silicontransit.compile.DynCompilerObjectBean;
import com.silicontransit.compile.RecompilationListener;
import com.silicontransit.timeline.bean.DebugBean;
import com.silicontransit.timeline.bean.FilterBean;
import com.silicontransit.timeline.disp.InputBarDraw;
import com.silicontransit.timeline.disp.ButtonsDraw;
import com.silicontransit.timeline.disp.DisplayObject;
import com.silicontransit.timeline.disp.PopupWindowDraw;
import com.silicontransit.timeline.disp.SlidersDraw;
import com.silicontransit.timeline.disp.TimeLineBounds;
import com.silicontransit.timeline.disp.TimeLineDraw;
import com.silicontransit.timeline.disp.TopBarDraw;
import com.silicontransit.timeline.model.*;
import com.silicontransit.timeline.obj.Poly;
import com.silicontransit.timeline.obj.Scales;
import com.silicontransit.timeline.obj.Standard;
import com.silicontransit.timeline.util.*;
import com.silicontransit.timeline.window.*;
import com.silicontransit.timeline.window.helpers.ControlHelper;
import com.silicontransit.timeline.window.helpers.ExprHelper;
import com.silicontransit.timeline.window.helpers.InputHelper;
import com.silicontransit.timeline.window.helpers.NoteHelper;
import com.silicontransit.timeline.window.helpers.PropertiesHelper;
import com.silicontransit.timeline.window.iface.InterfaceBuilder;
import com.silicontransit.timeline.window.iface.InterfaceReader;

public class TimeLine extends PApplet {
	private static final float FRAME_RATE = 8;
	
	public String DEFAULT_FILE_PATH = "E:\\src\\processing\\xml\\";
	public String MIDI_FILE_PATH = "E:\\src\\processing\\xml\\";
	public String dataFilePath = DEFAULT_FILE_PATH;
	public String configDirectoryPath = DEFAULT_FILE_PATH;
	public String configFilePath=configDirectoryPath+File.separator+"config.xml";
	public String mapFilePath=configDirectoryPath+File.separator+"ControlMaps.xml";
	
	private FileUtilDOM fileUtil = new FileUtilDOM(this);
	public CalcUtil calcUtil = new CalcUtil(this);
	public OSCUtil oscUtil= new OSCUtil(this);
	public ExprUtil exprUtil= new ExprUtil(this);
	public NumberScrollingUtil numberScrollingUtil = null;
	
	private static final int MAX_SUBEVENT_PLOT = 20;
	private static final int DOUBLE_CLICK_INTERVAL = 200;// in ms
	private long lastLeftClickTime = 0;
	private long lastRightClickTime = 0;
	private static final int TIMER_INTERVAL = 1;
	static final int DEF_SCREEN_HEIGHT=180;
	static final int DEF_SCREEN_WIDTH=900; 	
	public int screenHeight=DEF_SCREEN_HEIGHT;
	public int screenWidth=DEF_SCREEN_WIDTH; 	
	
	//static int BORDER_HEIGHT=34;
	static final int BORDER_HEIGHT=File.separator.equals("/")?35:34;
	static final int BORDER_WIDTH=File.separator.equals("/")?6:8;
//	Array of timelines.
	public TimeLineObject timeLineObject = null; 
	public TimeLineSet timeLineSet = null; 
	public Vector timeLines=null;
	public Vector timeLineSelection = new Vector(); // the timelines to display - chage this to (un)display an timeline
	public Vector timeLineDisplays = new Vector(); // used internally (get set in updateTimeLineDisplays)
	public Vector timeLineSets = new Vector();
	public Cue cue = new Cue();
	public ColorMap oscMessageColorMap = new ColorMap();
	public ColorMap expressionColorMap = new ColorMap();
	
	public int timeLineIndex=0;
	public int timeLineParentIndex=0;
	public Vector playing=new Vector();// playing timelines
	// dragging timeline objects
	public TimeLineObject cueTimeLineObject = null; // for cue
	public TimeLineObject eventTimeLineObject = null; // for dragging to create event
	// scroll variable
	// event drag
	public Object dragObject=null;
	public boolean draggedFlag=false;
	public int scrollAmt=0;
	public int scrollBarDragOffset=-1;
	
	public String additionalClassPath="";
	public static HashMap stdObjectClasses = new HashMap();
	static {
		stdObjectClasses.put("std", Standard.class);
		stdObjectClasses.put("poly", Poly.class);
		stdObjectClasses.put("scale", Scales.class);
		
	}
	public HashMap dynamicObjects = new HashMap();
	public DynCompiler dynCompiler = null;
	
	public DebugBean debugBean=new DebugBean();
	public DebugWindow debugWindow;
	public NotesWindow noteWindow;
	public LogWindow logWindow;
	
	public boolean isUnix=false;
	private boolean setup=false;
	
	//	font to use.
	public String fileName="openlab1-testmap.xml";  //testObj.xml
	public boolean dirtyFlag = false;
	public String title = "Timeline";
	
	// undo stuff
	public boolean undoOn=true;
	public int undoIndex=-1;
	public Vector undoVec=new Vector();
	
	//	keyboard state.
	public boolean shift=false;
	public boolean ctrl=false;
	public boolean alt=false;
	public String inputMode="";
	public String inputStr="";
	public int cursorPos=0;	
	
	// edit value vector for editing an index of the value.
	public int editValueIndex=-1;
	public Vector editValueVector=null;
	public Vector eventSelectorVector=null;// hold inactive events 
//	////////////////////////////	
	// to show messages.
//	////////////////////////////
	public int msgCtr=-1;// ctr to say when to hide msg
	public String msg="";// the msg
	public String msgType=LogWindow.TYPE_MSG;
	public boolean showTooltips=true;
	public boolean showCrosshair=false;
	
	public SimpleDateFormat timeFormat ;
	public Vector copySel=new Vector();
	public File currentDir;
	
	public Vector menu = null;
//////////////////////////////
//	timer stuff
//////////////////////////////
	public Timer timerStart;
	public TimeLineTimer timeLineTimer=new TimeLineTimer();
	
	public Timer guiTimerStart;
	public GuiTimer guiTimer=new GuiTimer();
	
	public Vector notes=new Vector();
	public HashMap filters;
	//public HashMap cue
	JFileChooser fc;
	JColorChooser cc;
	JPanel panelChooser=new JPanel();
	public static Frame thisFrame;
//	////////////////////////////
//	Record Stuff
//	///////////////////////////	
	public long recordGranularity=200;
	public String recordInput="";
	//////////////////////////////
	//OSC Stuff
	/////////////////////////////
	public OscP5 oscServers[]=new OscP5[] {null,null};
	public int oscServerPorts[]= new int[] {10001,10002};
	public String oscServerHost[]= new String[] {"localhost","192.168.0.6"};//"10.0.0.9",,"10.0.0.8"
	public int oscServerRcPorts[]= new int[] {10011,10012};
	public String host;
	public String oscP5event;
	
	
	//////////////////////////////
	// MIDI Stuff
	/////////////////////////////
	public String[] midiDeviceNames=new String[] {"Hercules DJ Console MIDI","1:EDIROL PCR 1","USB Audio Device"};
	public HashMap midiDeviceConfigMaps=new HashMap();
	public String currentMIDIInputDevice="";
	public MidiIO midiIO;
	//public String[][] midiNotes=new String[16][128];
	public HashMap allMidiNoteMaps=new HashMap(); // device: HashMap {mapId : String[][]}
	public HashMap currentMidiNoteMaps=new HashMap();
	public HashMap currentMidiNoteMapIds=new HashMap();
	public HashMap allMidiControlMaps=new HashMap(); // device: HashMap {mapId : HashMap {<part>_<ctlNum>:Vector}}
	public HashMap currentMidiControlMaps=new HashMap();
	public HashMap currentMidiControlMapIds=new HashMap();
	public HashMap midiKeyBindings=new HashMap();
	public HashMap midiOutputs=new HashMap();
	public MIDIUtil midiUtil=new MIDIUtil(this);
	public MIDIWindow midiWin;
	public Vector logVector=new Vector();
	public String[] midiOutDeviceNames=new String[] {};
	public MidiOut[] midiOutDevices= null;
	//////////////////////////////
	// Draw Objects
	/////////////////////////////
	//public TimeLineDraw timeLineDraw;
	public TopBarDraw topBarDraw;
	public InputBarDraw inputBarDraw;
	public ButtonsDraw buttonsDraw;
	public PopupWindowDraw popupWindowDraw;
	//public SlidersDraw slidersDraw;
	public TimeLineBounds tlBound;  //uses for getting the bound of the main areas
	BoundsChangeHandler boundsChangeHandler ;
	//	////////////////////////////
	//	 inputHelper
	//	///////////////////////////
	public ControlHelper controlHelper = null;
	public NoteHelper noteHelper = null;
	public ExprHelper exprGetHelper = null;
	public ExprHelper exprSetHelper = null;
	public PropertiesHelper propHelper = null;
	
	//	////////////////////////////
	//	 interface file.
	//	///////////////////////////
	public String interfaceFile = null;
	public InterfaceBuilder interfaceBuilder =null;
	public InterfaceReader interfaceReader =null;
	///////////////////////////////////////////////////////////////////////////////////////////////
	//	setup() : 
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	public void setup() {
		String title = "TimeLine";
		thisFrame = new JFrame(title);
		thisFrame.add(this, BorderLayout.CENTER);
		thisFrame.setSize( screenWidth+BORDER_WIDTH, screenHeight+BORDER_HEIGHT ); // set window to appropriate size (for its elements)
		thisFrame.setLocation(new Point(300,800));
		thisFrame.setVisible(true); // usual step to make frame visible
		
		thisFrame.addWindowListener(new TLWinAdapter(this));
		
		if ("/".equals(File.separator)) { isUnix=true;}
		// want to remove this fairly soon 
		if (isUnix) {
			midiDeviceNames=new String[] {"OSCMIDI:0","PCR","BCF2000"};
		}
		
		fileUtil.parseConfig();
		
		
		dynCompiler = new DynCompiler(
			this.dataFilePath,
			this.additionalClassPath
		);//E:\\processing\\TimeLine\\classes
		
		fileUtil.parseMidiControlMaps();
		midiWin=new MIDIWindow(this);
		midiUtil.initMidi();
		
		debugWindow = new DebugWindow( this);
		debugWindow.init();
		
		noteWindow = new NotesWindow(this );
		logWindow = new LogWindow( );
		
		fileUtil.loadData(); // loads last data
		
		
		dynCompiler.setReCompLsntr(new RecompLstnr());
		addStandardObjects();
		println((new File(".")).getAbsolutePath());
		initOsc();
		timeFormat=new SimpleDateFormat("mm:ss.SSS");
		timeLineObject = new TimeLineObject(this);// default to first osc port.
		timeLines=new Vector();
		timeLineIndex=0;
		timeLines.add(timeLineObject);
		//smooth();
		//frameRate(FRAME_RATE);//processing -154
		framerate(FRAME_RATE);
		size (screenWidth,screenHeight);
		background(0f);
		
		eventSelectorVector=new Vector();

		this.addMouseWheelListener(new InputHelpWheel());
		
		initLog();
		timerStart = new Timer();
		startTimer();
		guiTimerStart = new Timer();
		startGuiTimer();
		
		topBarDraw=new TopBarDraw(screenWidth, 10,0,0);
		inputBarDraw=new InputBarDraw(screenWidth, 20,0,10);
		buttonsDraw=new ButtonsDraw(screenWidth, 50,0,30);
		TimeLineDraw timeLineDraw=new TimeLineDraw(screenWidth, 100,0,80 , this.timeLineObject);
		timeLineDisplays.add(timeLineDraw);
		timeLineSelection.add(timeLineObject);
		timeLineDraw.setTimeLineObj(timeLineObject);
		tlBound = new TimeLineBounds(screenWidth,screenHeight,0,0);
		tlBound.addBoundedObject(topBarDraw);
		//tlBound.addBoundedObject(timeLineDraw);
		tlBound.addBoundedObject(inputBarDraw);
		tlBound.addBoundedObject(buttonsDraw);
		boundsChangeHandler = new BoundsChangeHandler();
		tlBound.setChangeListner(boundsChangeHandler);
		// have to add this after tlBpound to stop np
		thisFrame.addComponentListener(new WindowResizeHandler());
		
		popupWindowDraw=new PopupWindowDraw(300, 100, 0, 80);
		
		//slidersDraw=new SlidersDraw(300, 100, 0, 80);
		
		fc=new JFileChooser();
		fc.setMultiSelectionEnabled(false);
		fc.setCurrentDirectory(new File(this.dataFilePath));
		cc=new JColorChooser();
		
		filters=new HashMap();
		
		setup=true;
		setTitle(fileName);
		
		controlHelper = new ControlHelper(this);	
		controlHelper.setOkAction(new ControlHelperActionListener());
		noteHelper = new NoteHelper(this);	
		noteHelper.setOkAction(new NoteHelperActionListener());
		exprGetHelper = new ExprHelper(this,true);	
		exprGetHelper.setOkAction(new ExprHelperActionListener());
		exprSetHelper = new ExprHelper(this,false,false);	
		exprSetHelper.setOkAction(new ExprHelperActionListener());
		propHelper = new PropertiesHelper(this);
		
		// interface
		interfaceBuilder = new InterfaceBuilder(this);
		interfaceReader = new InterfaceReader();
		
		
		
		numberScrollingUtil = new NumberScrollingUtil(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cursorPos = e.getModifiers();
			}
		});
	}
	
	private class TLWinAdapter extends WindowAdapter{
		TimeLine t;
		public TLWinAdapter(TimeLine t){
			this.t=t;
		}
		public void windowClosing(WindowEvent evt) {
			backupLastUndo();
			fileUtil.saveData();
			t.destroy();
			// Exit the application
			System.exit(0);
		}
		
		
	}
	public void destroy() {
		midiUtil.closeMidi();
		fileUtil.save("lastTimeline.xml");
	}
	
///// DRAW METHOD ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	each draw object generates an image and this is copied directly to the applet canvas (no double buffering) 
// if a faster way of drawing the image was available then this moehtod would be faster still.
//	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private int gcCtr=0;
	private int timeLinesHeight = 0; // used to set the height of the popupDIsplay
	public void paint(Graphics g) {
		//
		//System.out.println("here1:"+this.hashCode()+":"+this.dirtyFlag1);
		if (setup ){

			//	draw top bar
			g.drawImage(
					topBarDraw.getImage(this),
					topBarDraw.getLeft(),topBarDraw.getTop(), Color.black, null
			);
			
//			draw buttons
			g.drawImage(
					buttonsDraw.getImage(this),
					buttonsDraw.getLeft(),buttonsDraw.getTop(), Color.black, null
			);
			popupWindowDraw.setPopupDisplay(this);
			Image popupDisp = null;
			if (popupWindowDraw.isVisible()) {
				popupWindowDraw.setTop(buttonsDraw.getTop()+buttonsDraw.getHeight());
				popupWindowDraw.setHeight(timeLinesHeight);
				popupDisp=popupWindowDraw.getImage(this);
			}
			timeLinesHeight=0;
			for (int i=0;i<timeLineDisplays.size(); i++) {
				TimeLineDraw tld= (TimeLineDraw) timeLineDisplays.get(i);
				Image tlDisp=tld.getImage(this);
				if (popupDisp!=null && timeLinesHeight<popupWindowDraw.getHeight()) {
					tlDisp.getGraphics().drawImage(popupDisp, 0, 0, popupWindowDraw.getWidth(), tld.getHeight(), 0, timeLinesHeight, popupWindowDraw.getWidth(), timeLinesHeight+tld.getHeight(), Color.black, null);
				}
				// draw timeline.
				g.drawImage(
						tlDisp,
						tld.getLeft(), tld.getTop(), Color.black, null
				);
				timeLinesHeight+=tld.getHeight();
			}
			//	 draw bottom bar
			g.drawImage(
					inputBarDraw.getImage(this),
					inputBarDraw.getLeft(),inputBarDraw.getTop(), Color.black, null
			);
			
			debugWindow.paint(debugWindow.getGraphics());
			midiWin.paint(midiWin.getGraphics());
			checkAndDrawTooltip(g);
		}
	}
	
	private Point tooltipLastPoint = new Point( 0, 0 );
	private int tooltipCtr = 0;
	private void checkAndDrawTooltip(Graphics g) {
		Point p=new Point(mouseX,mouseY);
		if (tooltipLastPoint.equals(p)) {tooltipCtr++;} else {tooltipCtr=0;}
		if (tooltipCtr>15 && showTooltips) {
			Object o = tlBound.checkForObjectOnScreen( mouseX , mouseY );
			if (o!=null && o instanceof DisplayObject) {
				String tooltip = ( (DisplayObject) o ).checkForTooltipOnScreen(mouseX , mouseY);
				if (tooltip != null) {
					String[] lines = tooltip.split("\n");
					int width=0;
					FontMetrics fontMetrics = g.getFontMetrics(tlBound.thisFont);
					for ( int i = 0; i < lines.length; i++ ) {
						width= Math.max(width, fontMetrics.stringWidth(lines[i])+2);
					}
					int fontHeight = fontMetrics.getHeight();
					int height = fontHeight*lines.length+2;
					// int width = fontMetrics.stringWidth(tooltip)+2;
					int left = mouseX, bottom = mouseY;
					if (left+width>screenWidth) { left = screenWidth-width; }
					if (bottom-height<0) { bottom = height;}
					g.setColor( Color.darkGray );
					g.fillRect( left, bottom-height, width, height);
					g.setColor( Color.white );
					g.drawRect( left, bottom-height, width, height );
					g.setFont(tlBound.thisFont);
					for ( int i = 0; i < lines.length; i++ ) {
						g.drawString( lines[i], left+1, bottom - (lines.length-1- i)*fontHeight-1 );
					}
				}
			}
		}
		tooltipLastPoint = p;
	}
	
	public void draw() {}
	public void showMessage (String msg){
		showMessage ( msg, LogWindow.TYPE_ERROR);
	}
	public void showMessage (String msg, String type) {
		this.msg=msg;
		this.msgType=type;
		msgCtr=0;
		//if (type<LogWindow.TYPE_MSG) debugBean.addLog(msg);
		LogWindow.log(msg, type);
	}
	public void  setTitle(String title) {
		this.title = "Timeline "+title;
		updateTitle();
	}
	public void  updateTitle() {
		thisFrame.setTitle(this.title+(this.dirtyFlag?"*":"")+" ("+this.dataFilePath+")") ;
	}
	public void  markDirty(boolean dirty) {
		//System.out.println("markDirty:"+":"+this.dirtyFlag); 
		this.dirtyFlag=dirty;
		updateTitle() ;
	}
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	File clear():
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public  void clearEveryThing() {
		dynamicObjects.clear();
		addStandardObjects();
		timeLines.clear();
		playing.clear();
		notes.clear();
		filters.clear();
		currentMidiControlMaps=new HashMap();
		currentMidiControlMapIds=new HashMap();
		allMidiControlMaps=new HashMap();
		currentMidiNoteMaps=new HashMap();
		currentMidiNoteMapIds=new HashMap();
		allMidiNoteMaps=new HashMap();
		midiKeyBindings.clear();
		debugBean.clear();
		dynCompiler.clear();
		timeLineSelection.clear();
		timeLineDisplays.clear();
		timeLineSets.clear();
		noteWindow.setText("");
		expressionColorMap.clear();
		oscMessageColorMap.clear();
	}
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	A timer for processing gui events.
//	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
	class GuiTimer extends TimerTask {
		public void run() {  guiEvents();	  }
	}
	
	public void startGuiTimer(){
		guiTimerStart.scheduleAtFixedRate(new GuiTimer(),0,200);
	}
	
	private void guiEvents(){
		//if ( scrollAmt >0 ) {scrollRight(scrollAmt);} 
		//else if ( scrollAmt < 0 ) {scrollLeft(-1*scrollAmt);}
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	TimeLineTimer: timer task to trigger event play.
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	class TimeLineTimer extends TimerTask {
		public void run() {  eventRunner();	  }
	}
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	startTimer() : schedule the timer.
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void startTimer(){
		timerStart.scheduleAtFixedRate(new TimeLineTimer(),0,TIMER_INTERVAL);
	}
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	eventRunner(): plays all events on all playing timelines since last called.
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void eventRunner() {//synchronized 
		//long startTime=System.currentTimeMillis();
		for (int i=0;i<playing.size();i++){
			TimeLineObject tmLine=(TimeLineObject)playing.get(i);
			if ("".equals(tmLine.playMode)|| ( "l".equals(tmLine.playMode) && (tmLine.pos>=tmLine.timeLineLength)) ) {//
				Vector v = cue.checkQue(tmLine);
				if (v.size()>0) {
					for (Iterator iter = v.iterator(); iter.hasNext();) {
						Cueable cueObj = (Cueable) iter.next();
						if (cueObj.getStop()) {tmLine.playMode="";}
						if (cueObj instanceof TimeLineObject) {
							playTimeline((TimeLineObject) cueObj, cueObj.getCueMode()) ;
						} else if (cueObj instanceof TimeLineSet) {
							playTimeLines(((TimeLineSet) cueObj).getSet(), cueObj.getCueMode());
						}
					}
					cue.deCueTriger(tmLine);// to have trigger stay make flag for this. 
				}
				if ("".equals(tmLine.playMode)) {
					playing.remove(tmLine);
					i--;
					continue;
				}// remove finished objects
				
			}
			// this.executingTimeLineObject=tmLine;
			Vector events=tmLine.playEvents();
			for (int j=0;j<events.size();j++){
				Event e = (Event)events.get(j);
				if (!timeLines.contains(e.target)) {
					//remove a timeline that doesn't exist anymore.
					e.target=null;
					continue;
				} 
				playEventFromPos(e,-1);
			}
		}
		//long endTime=System.currentTimeMillis();
		//addLogEntry(startTime+":"+endTime+":"+(startTime-endTime)+" - play");
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	playEventFromPos(Event e,long pos) : plays event 
//	TODO fix timeline play start and position setting bad bugs here
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void playTimeLines(Vector set, String playMode){
		for (int i=0;i<set.size();i++) {
			TimeLineObject tlo = (TimeLineObject)set.get(i);
			playTimeline(tlo, playMode);
		}
	}
	
	public void playTimeline(TimeLineObject selTimeLine, String playMode) {
//		Event e = new Event();
//		e.target=selTimeLine;
//		e.targetPlayMode=playMode;
//		playEventFromPos(e,-1);
		selTimeLine.playMode=playMode;
		if (!"".equals(playMode)) {
			selTimeLine.pos=-1;
			selTimeLine.lpos=-1;
			selTimeLine.nextEvent=0;
			selTimeLine.lastTime=System.currentTimeMillis()-1;
			selTimeLine.startTime = selTimeLine.lastTime;
			selTimeLine.startPos = selTimeLine.pos;
			if (!playing.contains(selTimeLine)) {
				playing.add(selTimeLine);
				eventRunner();
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	playEventFromPos(Event e,long pos) : plays event 
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void playEventFromPos(Event e, long setPos) {
		if (e.target!=null) {
			if (setPos == -1) {
				try{
					setPos=Integer.parseInt(e.targetData);
				}catch(NumberFormatException n) {}
			}
			e.lastPlayed=System.currentTimeMillis();
			if (!"".equals(e.targetPlayMode)) {
				e.target.setPosAndNextEvent((int) setPos);
				if (!playing.contains(e.target)) {playing.add(e.target);}
			}
			e.target.setPlayMode(e.targetPlayMode);
		}
	} 
	private void playEvent(Event event) {
		if ((event!=null) ){
			timeLineObject.playEvent(event);
			if ((event.target!=null)) {
				playEventFromPos(event,-1);
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	getTimeInFrame(long time): returns the x coordinate relating to the time given(depeds on the displayStart and DisplayEnd). 
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public int getTimeInFrame(long time1) {
		int displayPos=(int)(time1-timeLineObject.displayStart);
		int len=timeLineObject.displayEnd-timeLineObject.displayStart;
		int posx=displayPos*screenWidth/len;
		return posx;
	}
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	getTimeFromMousePos () : gets the time that the mouse x position relates to
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	int getTimeFromMousePos () {
		return timeLineObject.displayStart+ Math.round((((float)mouseX)/((float)screenWidth)*((float)(timeLineObject.displayEnd-timeLineObject.displayStart))));
	}
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	mouseMoved() :
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void mouseMoved() { 
//		if ("k".equals(inputMode)) {
//			if ((mouseY>screenHeight-17) && (mouseY<screenHeight)) {
//				int red=(screenHeight-mouseY-1)*16;
//				int green = (mouseX/16)*16;if (green>255) {green=255;}
//				int blue = (mouseX%16)*16;
//				inputStr=red+" "+green+" "+blue;
//				cursorPos=inputStr.length();
//				timeLineObject.setColor(red,green,blue);
//			}
//		}
	} 
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	  run file and color choosers in sepearte thread - stops hanging in main window
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private class FileChooserThread extends Thread{
		
		public void run() {
			this.setName("FileChooserThread");
			int returnVal = fc.showOpenDialog(thisFrame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				String fileName=fc.getSelectedFile().getAbsolutePath();
				if (!"".equals(inputMode)) {
					setInputStr(fileName);
					if ("Ff".indexOf(inputMode)>-1) {
						processInput();
					}
				}
			}
		}
	}
	private class ColorChooserThread extends Thread{
		public  Color startColor = Color.white;
		public void run() {
			this.setName("ColorChooserThread");
			Color c=JColorChooser.showDialog(thisFrame,"Choose Timeline color", startColor);
			if ("k".equals(inputMode)) {
				setInputStr("");
				insertInInputStr(c.getRed()+" "+c.getGreen()+" "+c.getBlue());
				timeLineObject.setColor(c.getRed(),c.getGreen(),c.getBlue());
				processInput();
			} else if ("K".equals(inputMode)) {
				if (inputStr.indexOf("/")==0) {
					oscMessageColorMap.put(inputStr,c);
				} else if (inputStr.indexOf("$")==0) {
					expressionColorMap.put(inputStr,c);
				}
				setInputStr("");
			}
		}
	}
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	  mousePressed() : various function when mouse is pressed.
//	  timeLines size buttons (left) (y>5 & <20) and (x>screen_width-60)
//	  set position (left) (ctrl pressed)
//	  select event (left) - last one before click position
//	  create event(right) 
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void mousePressed() { 
		Object boundedObj = getTimeLineDrawObject(true);
		TimeLineDraw timeLineDraw = (boundedObj instanceof TimeLineDraw)?((TimeLineDraw)boundedObj):null;
		if (mouseButton == LEFT) { 
			// println("left");
			if (cueTimeLineObject!=null) {cueTimeLineObject=null;}
			if (buttonsDraw.inside(mouseX,mouseY)) {
				Object o = buttonsDraw.checkForObjectOnScreen(mouseX,mouseY);
				if (o instanceof TimeLineObject) {
					timeLineButClickHndlr((TimeLineObject)o);
				} else if (o instanceof TimeLineSet) {
					timeLineSetButClickHandler((TimeLineSet) o);
				} 
			} else if (inputBarDraw.inside(mouseX,mouseY)) {
				Object o=inputBarDraw.checkForObjectOnScreen(mouseX,mouseY);
				if (o!=null && o.equals("compilerButton")) {
					dynCompiler.setVisible(!dynCompiler.getVisible());
				}	else if (o!=null && o.equals("debugButton")) {
					debugWindow.setVisible(!debugWindow.getVisible());
				}	else if (o!=null && o.equals("midiButton")) {
					midiWin.setVisible(!midiWin.getVisible());
				}	else if (o!=null && o.equals("notesButton")) {
					noteWindow.setVisible(!noteWindow.getVisible());
				}  else if (o!=null && o.equals("logButton")) {
					logWindow.setVisible(!logWindow.getVisible());
				}  else if (o!=null && o.equals("propButton")) {
					propHelper.setVisible(true);
				}  else if (o!=null && o.equals("ifaceButton")) {
					if (ctrl) {
						loadInterface(this.interfaceFile);
						interfaceBuilder.setVisible(true);
					} else {
						interfaceBuilder.setVisible(!interfaceBuilder.isVisible());
					}
				}  else if (o!=null && o.equals("keyButton")) {
					if ("".equals(inputMode) || "123456789X".indexOf(inputMode)==-1) {
						inputMode="X";
					} else {
						cancelInput();
					}
				}  else if (o!=null && o.equals("objectButton")) {
					if (!"O".equals(inputMode)) {
						inputMode="O";
					} else {
						cancelInput();
					}
				}  else if (o!=null && o.equals("filterButton")) {
					if (!"y".equals(inputMode)) {
						inputMode="y";
					} else {
						cancelInput();
					}
				}  else if (o!=null && o.equals("colorButton")) {
					if (!"K".equals(inputMode)) {
						inputMode="K";
					} else {
						cancelInput();
					}
				} else if (o!=null && o.equals("playButton")) {
					playTimeLines(timeLineSelection,"p");
				} else if (o!=null && o.equals("pauseButton")) {
					playTimeLines(timeLineSelection,"");
				} else if (o!=null && o.equals("inputButton")) {	
					if ("FfiJ".indexOf(inputMode)>-1) {
						FileChooserThread fct = new FileChooserThread();
						try {
							if (!"".equals(inputStr)) { fc.setSelectedFile(new File(inputStr)) ;}
						} catch (RuntimeException e) {
							showMessage(e.getMessage(), LogWindow.TYPE_ERROR);
						}
						fct.start();
					} else if ("Tv".indexOf(inputMode)>-1) {
						if (ctrl) {
							FileChooserThread fct = new FileChooserThread();
							fct.start();
							ctrl=false;
						} else {
							String exprString=inputStr;
							if (exprString.indexOf(" ")>-1) {
								int start=inputStr.lastIndexOf(" ", cursorPos-1);
								int end=inputStr.indexOf(" ", cursorPos);
								if (end==-1) {end=inputStr.length();}
								exprString=inputStr.substring(start+1,end);
							}
							if ("".equals(exprString) || exprString.trim().startsWith("$")) {
								exprGetHelper.setInput(exprString.trim());
							} else {exprGetHelper.setInput("");}
							exprGetHelper.setVisible(true);
						}
					} else if ("yI".indexOf(inputMode)>-1) {
						exprGetHelper.setInput(inputStr);
						exprGetHelper.setVisible(true);
					} else if ("k".indexOf(inputMode)>-1) {
						ColorChooserThread cct=new ColorChooserThread();
						cc.setColor(timeLineObject.getAwtColor());
						cct.start();
					} else if ("=".indexOf(inputMode)>-1) {
						if ("".equals(currentMIDIInputDevice) && midiDeviceNames.length>0) {currentMIDIInputDevice = midiDeviceNames[0];}
						controlHelper.setInput(inputStr);
						controlHelper.setVisible(true);
					} else if ("+".indexOf(inputMode)>-1) {
						noteHelper.setInput(inputStr);
						noteHelper.setVisible(true);
					} else if ("/".indexOf(inputMode)>-1) {
						if ("".equals(inputStr) || inputStr.startsWith("$")) {
							exprSetHelper.setInput(inputStr);
							exprSetHelper.setVisible(true);
						} else {
							showMessage("There is only input help for expressions",LogWindow.TYPE_ERROR);
						}
					}
				} else if (o!=null && o.equals("inputField")) {
					int desiredTextWid = mouseX -  inputBarDraw.inputTextLeft;
					int ctr=0;
					int diff=desiredTextWid;
					while (ctr<inputStr.length()) {
						ctr++;
						int ctrWidth =	inputBarDraw.thisFontMetrics.stringWidth(inputStr.substring(0, ctr));
						int newDiff =Math.abs( desiredTextWid - ctrWidth) ;
						if (newDiff > diff) {
							ctr--; break;
						}
						diff=newDiff;
					}
					cursorPos=ctr;
				}
			} else if (topBarDraw.inside(mouseX,mouseY)) {
				Object o=topBarDraw.checkForObjectOnScreen( mouseX, mouseY );
				if (o!=null && o instanceof String) {
					if (o.equals("menu")) {
						if (menu==null) {menu= MenuListing.menu;}
						else {menu=null;}
					} else if (o.equals("showCrosshair")) {
						showCrosshair=!showCrosshair;
					} else if (o.equals("showToolTips")) {
						showTooltips=!showTooltips;
					} 
				}
			} else if (popupWindowDraw.isVisible() && popupWindowDraw.inside(mouseX, mouseY)) {
				// select a event.
				// check for popup window
				Object o=popupWindowDraw.checkForObjectOnScreen(mouseX,mouseY);
				if (o!=null && o instanceof Integer) {
					int index=((Integer)o).intValue();
					if (!"".equals(inputMode) && "N=+".indexOf(inputMode)>-1){
						inputStr=(String)popupWindowDraw.getTexts().get(index);
						cursorPos=inputStr.length();
					} else if  (!"".equals(inputMode) && "y".indexOf(inputMode)>-1) {
						String filterStr=(String)popupWindowDraw.getTexts().get(index);
						filterStr=filterStr.substring(2,filterStr.length());
						toggleActivateFilter(filterStr);
						inputStr=filterStr;
						cursorPos=inputStr.length();
						popupWindowDraw.setFilterTexts(this);
					} else if  (!"".equals(inputMode) && "K".indexOf(inputMode)>-1) {
						String s = (String)popupWindowDraw.getTexts().get(index);
						if (s!=null) {
							ColorChooserThread cct=new ColorChooserThread();
							if (s.indexOf( "/")==0) {
								cct.startColor = oscMessageColorMap.getColorFor(s);
								cct.start();
							} else if (s.indexOf( "$")==0) {
								cct.startColor = expressionColorMap.getColorFor(s);
								cct.start();
							}
							setInputStr(s);
						}
					} else if  (!"".equals(inputMode) && "O".indexOf(inputMode)>-1) {
						String s = (String)popupWindowDraw.getTexts().get(index);
						if (s!=null) {	setInputStr(s);	}
					} else if  (!"".equals(inputMode) && "123456789X".indexOf(inputMode)>-1) {
						String s = (String)popupWindowDraw.getTexts().get(index);
						if (s!=null) {	
							String[] keyCombo=s.split("-");
							inputMode = keyCombo[0].trim();
							setInputStr(keyCombo[1].trim());
						}
					}   else if (menu!=null) {
						if (menu == MenuListing.menu) {
							Object[] menuItem =(Object[]) MenuListing.menu.get(index);
							menu = (Vector)menuItem[1];
						} else {
							String[] menuItem = (String[]) menu.get(index);
							char key;boolean alt=false;
							if (menuItem[1].indexOf("ctrl+")==0) {key=(char)(menuItem[1].charAt(5) - 96);}
							else if (menuItem[1].indexOf("alt+")==0) {alt=true;key=menuItem[1].charAt(4);}
							else {key=menuItem[1].charAt(0);}
							keyFunctionMap(key,alt);
							menu=null;
							showMessage(menuItem[2],LogWindow.TYPE_MSG);
						}
					}
				}
			} else if (!ctrl && timeLineDraw!= null &&timeLineDraw.inside(mouseX,mouseY)) {
				Object o=timeLineDraw.checkForObjectOnScreen(mouseX,mouseY);
				if (o instanceof Event) {
					eventClickHndlr((Event)o);
					if (shift) {addRemoveSelection();}
					if (alt) {selectSameEvents();} 
				}
				else if (o instanceof String) {
					String comp=(String) o;
					if (comp.equals("scroll")) {
						int[] point = timeLineDraw.getBoundObjectInsideCoord(o,mouseX,mouseY);
						scrollBarDragOffset=point[0];
					} else if (comp.equals("ScrollPosToView")) {
						timeLineObject.scrollPosToView=!timeLineObject.scrollPosToView;
					} else if (comp.equals("TypeDisplay")) {
						timeLineObject.typeDisplay=!timeLineObject.typeDisplay;
					}else if (comp.equals("Close")) {
						int index = timeLineDisplays.indexOf(timeLineDraw);
						timeLineSelection.remove(index);
						if (index>timeLineSelection.size()) {index--;}
						if (index>=0) {
							setTimelineFromTimeLineDraw(true, (TimeLineDraw)timeLineDisplays.get(index));
						}
						updateTimeLineDisplays();
					}else if (comp.indexOf("g_")==0) {
						groupClickHandler(comp);
					} else if (comp.indexOf("posBar")==0 ) {dragObject = "posBar"; }
				}else if (o==null){
					if ((System.currentTimeMillis() - lastLeftClickTime) <200) {
						timeLineObject.setPosAndNextEvent(getTimeFromMousePos());
					}else {
						timeLineObject.timeSelStart=getTimeFromMousePos();
					}
				}
			} 
			lastLeftClickTime = System.currentTimeMillis();
		} else if (mouseButton == RIGHT) { 
			if (buttonsDraw.inside( mouseX,  mouseY )) {
				Object o= buttonsDraw.checkForObjectOnScreen(mouseX,mouseY);
				if (o instanceof TimeLineObject) {
					timeLineButClickHndlr((TimeLineObject)o);
				} else if (o instanceof TimeLineSet) {
					timeLineSetButClickHandler((TimeLineSet)o);
				}
			} else  if (inputBarDraw.inside(mouseX,mouseY)) {
				Object o=inputBarDraw.checkForObjectOnScreen(mouseX,mouseY);
				if (o!=null && o.equals("playButton")) {
					playTimeLines(timeLineSelection,"l");
				} else  if (o!=null && o.equals("inputButton")) {	
					exprGetHelper.setInput("");
					exprGetHelper.setVisible(true);
				} else  if (o!=null && o.equals("inputField") ) {	
					if (System.currentTimeMillis() - lastRightClickTime<DOUBLE_CLICK_INTERVAL) {
						keyCode = ENTER;
						keyPressed();
					}
				}
			}
			//else if (timeLineDraw.inside( mouseX, mouseY )) {
			else if (timeLineDraw!= null && timeLineDraw.inside( mouseX, mouseY )) {
				//TimeLineDraw timeLineDraw = (TimeLineDraw)boundedObj;
				//return if click in popup window
				if (popupWindowDraw.isVisible() && popupWindowDraw.inside(mouseX,mouseY)) {return;}
				//if (slidersDraw.isVisible() && slidersDraw.inside(mouseX,mouseY)) {return;}
				
				Object o=timeLineDraw.checkForObjectOnScreen(mouseX,mouseY); 
				if (o instanceof Event) {eventClickHndlr((Event)o);}
				else if(o instanceof String)  {
					String key=( (String) o);
					if (key.indexOf("g_")==0 ) { groupClickHandler((String)o); }
					
				}
				if ("".equals(inputMode)) {// create event
					saveUndo();
					Event e = getLastEvent();
					if (e!=null && shift) {e=e.getCopy(timeLineObject);}
					else {e=new Event();}
					e.oscIndex=timeLineObject.oscIndex;
					e.oscP5=oscServers[timeLineObject.oscIndex];
					e.eventTime=(getTimeFromMousePos ()/timeLineObject.quantize)*timeLineObject.quantize;
					timeLineObject.timeLine.add(e);
					timeLineObject.rebuildTimeLine();
					timeLineObject.currentEvent=timeLineObject.timeLine.indexOf(e);
					timeLineObject.lastSelEvent=timeLineObject.currentEvent;
				}
			}
			lastRightClickTime = System.currentTimeMillis();
		} else if(mouseButton == CENTER){
			if (!"".equals(inputMode)) {
				pasteClipToInput();
			}
		}
	
	}

	private Object getTimeLineDrawObject(boolean setProps) {
		Object boundedObj = tlBound.checkForObjectOnScreen(mouseX, mouseY);
		if (boundedObj!=null && boundedObj instanceof TimeLineDraw) {
			int index=timeLineDisplays.indexOf(boundedObj);
			if (boundedObj instanceof TimeLineDraw) {
				TimeLineDraw timeLineDraw = ((TimeLineDraw) boundedObj);
				setTimelineFromTimeLineDraw(setProps, timeLineDraw);
			}
		}
		return boundedObj;
	}

	private void setTimelineFromTimeLineDraw(boolean setProps,	TimeLineDraw timeLineDraw) {
		TimeLineObject lastTimelineObject = timeLineObject;
		timeLineObject = timeLineDraw.getTimeLineObj();
		if (setProps) propHelper.setObject(timeLineObject, "timeline");
		timeLineIndex = timeLines.indexOf(timeLineObject);
		if (timeLineObject != lastTimelineObject) {   cancelInput(); dragObject = null;}
	}

	private void pasteClipToInput() {
		try {
			DataFlavor df=null;
			for (int i=0;i<this.getToolkit().getSystemClipboard().getAvailableDataFlavors().length;i++) {
				df=this.getToolkit().getSystemClipboard().getAvailableDataFlavors()[i]; 
				if (df.getHumanPresentableName().indexOf("Unicode String")>-1) {break;		}
			}
			//DataFlavor df=this.getToolkit().getSystemClipboard().getAvailableDataFlavors()[0].getTextPlainUnicodeFlavor();
			if (df!=null) {
				insertInInputStr(this.getToolkit().getSystemClipboard().getContents( null).getTransferData(df).toString());
			}
		} catch (HeadlessException e) {
			System.out.println(e.getMessage()+":"+e.getClass().getName());
		} catch (UnsupportedFlavorException e) {
			System.out.println(e.getMessage()+":"+e.getClass().getName());
		} catch (IOException e) {
			System.out.println(e.getMessage()+":"+e.getClass().getName());
		}
	}
	
	private void copyInputStrToClip(){
		StringSelection ss=new StringSelection(inputStr);
		this.getToolkit().getSystemClipboard().setContents(ss,ss);
	}
	
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	mouseDragged(): 
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void mouseDragged() 
	{ 
		Object boundedObj = getTimeLineDrawObject(false);
		TimeLineDraw timeLineDraw = (boundedObj instanceof TimeLineDraw)?((TimeLineDraw)boundedObj):null;
		if (scrollBarDragOffset>-1) {
			int distDisplayed=timeLineObject.displayEnd - timeLineObject.displayStart;
			timeLineObject.displayStart=Math.round(((float)(mouseX-scrollBarDragOffset)/(float)screenWidth)*(float)timeLineObject.timeLineLength);
			if (timeLineObject.displayStart<0) {timeLineObject.displayStart=0;}
			timeLineObject.displayEnd=timeLineObject.displayStart+distDisplayed;
			if (timeLineObject.displayEnd>timeLineObject.timeLineLength) {
				timeLineObject.displayEnd=timeLineObject.timeLineLength;
				timeLineObject.displayStart=timeLineObject.displayEnd-distDisplayed;
			}
		} else if (buttonsDraw.inside(mouseX,mouseY)  ) {
			Object o=buttonsDraw.checkForObjectOnScreen(mouseX,mouseY);
			if ((o instanceof TimeLineObject) && (cueTimeLineObject == null)) {
				TimeLineObject tlo=(TimeLineObject)o;
				if (tlo!=null) {
					this.timeLineIndex=timeLines.indexOf(tlo);
					timeLines.removeElement(timeLineObject);
					timeLines.insertElementAt(timeLineObject,this.timeLineIndex);
				}
			}
		} 
		else if (timeLineDraw != null && timeLineDraw.inside(mouseX,mouseY)) {
			if (dragObject!=null && dragObject instanceof Event) {
				Event dragEvent = (Event) dragObject;
				if (!draggedFlag) {draggedFlag=true;saveUndo();}
				if (timeLineObject.selection.size()>0 && timeLineObject.selection.contains(dragObject)) {
					long draggedTime=getTimeFromMousePos();
					long oldDragEventTime=dragEvent.eventTime;
					long timeDiffDragged=draggedTime-oldDragEventTime;
					// get Max/Min event times.
					long min=timeLineObject.timeLineLength;
					long max=0;
					for (int i=0;i<timeLineObject.selection.size();i++) {
						Event e =(Event)timeLineObject.selection.get(i);
						if (e.eventTime>max) {max=e.eventTime;}
						if (e.eventTime<min) {min=e.eventTime;}
					}
					if (min+timeDiffDragged<0) {timeDiffDragged=-1*min;}
					if (max+timeDiffDragged>timeLineObject.timeLineLength) {timeDiffDragged=timeLineObject.timeLineLength-max;}
					for (int i=0;i<timeLineObject.selection.size();i++) {
						Event e=(Event)timeLineObject.selection.get(i);
						e.eventTime+=timeDiffDragged;
						// quantise if shift pressed.
						if (alt) {}
						else if (ctrl) {e.eventTime-=e.eventTime%(timeLineObject.quantize*timeLineObject.beatLength);}
						else {e.eventTime-=e.eventTime%timeLineObject.quantize;} 
					}
				} else {
					dragEvent.eventTime=getTimeFromMousePos();
					if (alt) {}
					else if (ctrl) {dragEvent.eventTime-=dragEvent.eventTime%(timeLineObject.quantize*timeLineObject.beatLength);}
					else {dragEvent.eventTime-=dragEvent.eventTime%timeLineObject.quantize;} 
				}
			} else if (dragObject!=null && dragObject instanceof String) {
				if ("posBar".equals(dragObject)) {
					timeLineObject.setPosAndNextEvent( getTimeFromMousePos());
				}
			} else {
				if (timeLineObject.timeSelStart>-1 && (timeLineDraw!=null &&  timeLineDraw.inside(mouseX,mouseY))) {
					timeLineObject.timeSelEnd=getTimeFromMousePos();
				}
			}
		}
		
		
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	mouseReleased() : if left mouse button 
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void mouseReleased(){
		//if ((mouseY>SCREEN_HEIGHT-41) && (mouseY<SCREEN_HEIGHT-16)) {// block drag selection over event and timeline selectors
		//} else
		Object boundedObj = getTimeLineDrawObject(false);
		TimeLineDraw timeLineDraw = (boundedObj instanceof TimeLineDraw)?((TimeLineDraw)boundedObj):null;
		
		if (timeLineObject.timeSelStart>-1 && (timeLineDraw!=null) &&timeLineDraw.inside(mouseX,mouseY)) {
			timeLineObject.timeSelEnd=getTimeFromMousePos ();
			selectEventsFromTime();
			//if (timeLineObject.timeSelStart==timeLineObject.timeSelEnd) {timeLineObject.selection.clear();}
			timeLineObject.timeSelStart=-1;
			timeLineObject.timeSelEnd=-1;
			
		} else if (buttonsDraw.inside(mouseX,mouseY)  ) {
			Object o=buttonsDraw.checkForObjectOnScreen(mouseX,mouseY);
			if (cueTimeLineObject!=null) {enCueHandler((Cueable) o);}
			return;
		}
		if (dragObject!=null){
			timeLineObject.rebuildTimeLine();
			timeLineObject.lastSelEvent=timeLineObject.timeLine.indexOf(dragObject);
			timeLineObject.currentEvent=timeLineObject.lastSelEvent;
			dragObject=null;
		}
		draggedFlag=false;
		scrollAmt=0;// stop scrolling if doing so.
		scrollBarDragOffset=-1;
		//popupWindowDraw.setScrolling(false);
		//slidersDraw.stopSliding();
	}
	
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	keyPressed()
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// this overrides the esc key exit in the PApplet
	public void handleKeyEvent(KeyEvent event) {
		
	    key = event.getKeyChar();
	    keyCode = event.getKeyCode();
		if ((key == KeyEvent.VK_ESCAPE)) {
			event.setKeyChar(' ');
			event.setKeyCode(-1);
		}
		super.handleKeyEvent(event);
	}
	
	public void keyPressed() { 
		
		// the length of time in the current frame.
		//int lgth=timeLineObject.displayEnd-timeLineObject.displayStart;
		//println("keypress:"+keyCode+" key:"+key+"keyInt: "+(int)key);
		Event event=getLastEvent();
		// for testing event place when moving.
		Event testEvent=getLastEvent();
		// selection just for last event i.e if nothing selected use this
		Vector eventSelection=new Vector();
		eventSelection.add(event);
		timeLineObject.sortSelection();
		if ( keyCode == ESC ) {System.out.println("escpressed");}
		if ( keyCode == SHIFT ) {shift=true;}//slidersDraw.setYPosShiftPressed(mouseY);
		if ( keyCode == CONTROL ) {ctrl=true;}
		if ( keyCode == ALT ) {alt=true;}
		if ( key == CODED ) { 
			if (shift) {
				// get interval to move by.
				int interval=timeLineObject.quantize;
				if (ctrl && alt) {interval=timeLineObject.quantize*timeLineObject.beatLength*timeLineObject.beatPerBar;}
				else if (ctrl) {interval=timeLineObject.quantize*timeLineObject.beatLength;}
				else if (alt) {interval=1;} 
				
				switch(keyCode) {
				case RIGHT: // move last Selected event to the right.
					if (timeLineObject.selection.size()>0) {
						testEvent=(Event) timeLineObject.selection.get( timeLineObject.selection.size()-1);
						eventSelection=timeLineObject.selection;
					}
					if ((testEvent!=null) && (testEvent.eventTime+interval<=timeLineObject.timeLineLength)) {
						moveEvents(eventSelection,interval);
					} 
					break;
				case LEFT: // move last Selected event to the left.
					if (timeLineObject.selection.size()>0) {
						testEvent=(Event) timeLineObject.selection.get(0);
						eventSelection=timeLineObject.selection;
					}
					if ((testEvent!=null) && (testEvent.eventTime-interval>=0)) {
						moveEvents(eventSelection,-1*interval);
					}
					break;
				} 
			}  // if (shift) 
			else if ((!"".equals(inputMode) && ctrl) || ("".equals(inputMode))) {// if not in input mode lft, right select event - if in inoput mode the ctrl +left,right selectes event
				switch(keyCode) {
				case RIGHT: 
					if (timeLineObject.lastSelEvent<timeLineObject.timeLine.size()) {    	
						timeLineObject.currentEvent=timeLineObject.lastSelEvent+1;   
						
					}  else {      	timeLineObject.currentEvent=timeLineObject.timeLine.size()-1;       }
					
					if (event!=null && event.eventTime>timeLineObject.displayEnd) {
						int lgth=timeLineObject.displayEnd-timeLineObject.displayStart;
						scrollRight((int)event.eventTime-timeLineObject.displayEnd+Math.round(lgth*0.2f));
					}
					break;
				case LEFT: 
					if (timeLineObject.lastSelEvent>0){
						timeLineObject.currentEvent=timeLineObject.lastSelEvent-1;
						
					}
					if (event!=null && event.eventTime<timeLineObject.displayStart) {
						int lgth=timeLineObject.displayEnd-timeLineObject.displayStart;
						scrollLeft((int)timeLineObject.displayStart-(int)event.eventTime+Math.round(lgth*0.2f));
					}
					break;
				default:break;
				}
			}// if  (shift) else 
		}
		
		if (inputMode.equals("")){
			//if (keyCode!=17) {
			//	int i=key;
			//}
			System.out.println(((int)key)+":"+key);
			cursorPos=0;
			keyFunctionMap(key, alt);
			cursorPos=inputStr.length();
		}
		else {
			println("input:"+key);
			if ( keyCode == ENTER ){ processInput();}
			else if ( key == 3 ) {      	copyInputStrToClip();	        }
			else if ( key == 22 ) {      		pasteClipToInput();        }
			else if ( keyCode == TAB || keyCode == -1 ){inputMode="";inputStr="";}//exit input mode.
			else if ( keyCode == BACKSPACE ){
				if (shift) {inputStr=inputStr.substring(cursorPos,inputStr.length());cursorPos=0;}// erase input str
				else if (alt) {// erase inputstr back to last slash.
					int pos = inputStr.lastIndexOf("\\");
					if (pos==-1) {pos = inputStr.lastIndexOf("/");}
					if (pos > -1) {
						inputStr=inputStr.substring(0,pos+1);
						cursorPos=inputStr.length();
					}
				} else {
					if ((inputStr.length()>0) && (cursorPos>0)) {inputStr=inputStr.substring(0,cursorPos-1)+inputStr.substring(cursorPos,inputStr.length());cursorPos --;}// ersae last char in inputStr
					else {inputMode="";inputStr="";}// exit input mode
				}
			} else if ( keyCode == DELETE ){
					if ((inputStr.length()>0) && (cursorPos<inputStr.length()-1)) {inputStr=inputStr.substring(0,cursorPos)+inputStr.substring(cursorPos+1,inputStr.length());}// ersae last char in inputStr
			} else if ( keyCode == UP )  {

				 if ("vfFi".indexOf(inputMode)>-1) {//last file
					inputStr=getFileName(inputStr,-1);
					//send preview if wav file
					if (inputStr.toLowerCase().indexOf(".wav")>inputStr.length()-5) {sendPreview();}
					//cursorPos=inputStr.length();
				} 
				else if ("=".equals(inputMode)) {
					inputStr=midiUtil.changeControlMap(inputStr,-1);
					//cursorPos=inputStr.length();
				}
				
			}   else if ( keyCode == DOWN ) {

				if ("vfFi".indexOf(inputMode)>-1) {//next file
					inputStr=getFileName(inputStr,1);
					if (inputStr.toLowerCase().indexOf(".wav")>inputStr.length()-5) {sendPreview();}
					//cursorPos=inputStr.length();
				} 
				else if ("=".equals(inputMode)) {
					inputStr=midiUtil.changeControlMap(inputStr,1);
					//cursorPos=inputStr.length();
				}
				
			} else if (!ctrl && keyCode == LEFT ) {// note ctrl+LEFT && ctrl+ RIGHT to move cursor as left and right are caught above to select event
				if (cursorPos>0) {cursorPos--;}
				if (alt) {cursorPos=0;}
			} else if (!ctrl && keyCode == RIGHT ) {
				if (cursorPos<inputStr.length()) {cursorPos++;}
				if (alt) {cursorPos=inputStr.length();}
			}  else if (keyCode>=32) { 
				if (cursorPos>inputStr.length()) {cursorPos=inputStr.length();}
				inputStr=inputStr.substring(0,cursorPos)+(char)key+inputStr.substring(cursorPos,inputStr.length());cursorPos ++;
			}
		}
	}
	
	public void keyFunctionMap(char key,boolean alt) {
		Event event;
		event=getLastEvent();
		switch(key) {// unused letters so far: AjJLQXYZ
		case 'v': // edit osc msg value 
			if (event!=null) {
				inputMode="v";
				//int numSlider=0;
				if ((editValueIndex>-1) ) {
					while (editValueIndex>=event.value.size()) {event.value.add("");}
					inputStr=""+event.value.get(editValueIndex);
					editValueVector=event.value;
				} else {
					inputStr=event.getValueStr();
					//numSlider=slidersDraw.makeSliders(event.getValue());
				}
				//if (numSlider>0) {
					//slidersDraw.setTitle(event.oscMsgName);
					//slidersDraw.setVisible(true);
				//} else {
					//slidersDraw.setVisible(false);
				//}
			}
//			
			break;
		case 'V':inputMode="V";break;// set value index to edit.
		case 'T':inputMode="T";
		 				if ( timeLineObject.parameters.size()<editValueIndex) {editValueIndex=-1;}
						if (editValueIndex>-1 ) {inputStr=""+timeLineObject.parameters.get(editValueIndex);}
						else {inputStr=Event.getValueStr(timeLineObject.parameters);}
						break;
		case 'U':if (alt){clearUndo();} else  {undoOn=!undoOn;}break;// set value index to edit.
		case 'u':if (alt) {redo();} else {undo();};break;// loop
		case 'e': inputStr=(event!=null) ?event.getTargetId():""; inputMode="e"; break;// time line trigger
		case 'E': inputStr=""; inputMode="E"; break; // clear timeline
		case 'i':  inputMode="i";break;// enter osc message
		case 'J':  inputStr=""+this.interfaceFile;inputMode="J";break;// enter osc message
		case '/': inputStr=(event!=null) ?event.oscMsgName:"/"; inputMode="/";break;// enter osc message
		case 'f': inputStr=dataFilePath+fileName;inputMode="f";break;// enter filename
		case 'F': inputStr=dataFilePath+fileName;inputMode="F";break;// add timelines from filename
		case 's': fileUtil.save(this.fileName);break;// save timeline
		case 'S': inputMode="S";break;// save timeline
		case 'o': clearUndo();		fileUtil.open();break;
		case 'O': inputMode="O";break;// view del object list
		case 't': inputStr=""+timeLineObject.timeLineLength;inputMode="t";break;//enter  time line length
		case 'q': inputStr=""+timeLineObject.quantize;inputMode="q";break;// quantize
		case 'M':inputStr=""+timeLineObject.oscIndex; inputMode="M";break;// timeline osc sender index
		case 'm':inputStr=""+((event!=null) ?""+event.oscIndex:""); inputMode="m";break;// osc sender index
		case 'n': //timeline name / (alt) event name
			if (!alt) {
				inputStr=""+timeLineObject.id;inputMode="n";
			} else {
				if (getLastEvent() !=null) {inputStr=""+getLastEvent().id;inputMode="ne";}
			} break;// timeline/event name
		case 'b': inputStr=""+timeLineObject.beatLength+"-"+timeLineObject.beatPerBar;inputMode="b";break;// beat length in quantisation points.
		case 'B': inputStr=""+calcUtil.calculateBPM(timeLineObject);inputMode="B";break;// beat length in quantisation points.
		case 'w': 
			// 5/10/06 : changed to set pitch.
			inputMode="w";
			inputStr=""+timeLineObject.pitch;
			//Vector v = new Vector();
			//v.add(new Float(timeLineObject.pitch));
//			slidersDraw.makeSliders(v);
//			slidersDraw.setTitle("pitch:"+timeLineObject.id);
//			slidersDraw.setVisible(true);
			break;// set pitch
		case 'W': inputMode="W";break; // normalise timeline time on pitch i.e. resize timeline so pitch is 1.
		case 'k': inputStr=""+timeLineObject.getColorStr();inputMode="k";break;// set color.
		case 'K': if (alt) {this.oscMessageColorMap.clear();this.expressionColorMap.clear();} else {inputMode="K";} break;// set color maps.
		
		case 'd': inputMode="d";break;// delete current event
		case 'D': inputMode="D";break;// delete current timeliine
		case 'N': inputMode="N";break;// new note.
		case 'g': 
			if (alt) { selectSameEvents(); } 
			else { addRemoveSelection(); }  
			break;
		case 'G':selectDeselectAll();break; //selects all if all arent selected.
		case 7:inputMode="g";break; // ctrl+g - input group name
		case 'l':  togglePlayMode("l");break;// toggle loop
		case 'p': togglePlayMode("p");break;// toggle play
		case 'P': togglePlayMode("P");break;//stop all play
		case 'r': if (alt) {inputMode="r";inputStr=this.recordInput;} else { togglePlayMode("r");} break;// record , if alt then enter OSC Msg / MIDI device input for recording
		case 'R': inputStr=""+this.recordGranularity;inputMode="R";
		case '0': timeLineObject.setPosAndNextEvent(-1);break;
		case 'c': copyEvent();break;
		case 'C': copyTimeLine();break;
		case 'z': prevTimeLine();break;
		case 'x': nextTimeLine();break;
		case '`': gotoChildTimeLine();break;
		case '~': gotoParentTimeLine();break;
		case ')': gotoRootTimeLine();break;
		case 'a': 
			if (!timeLineObject.selection.contains(event) && (timeLineObject.selection.size()>0)) {event = (Event)timeLineObject.selection.get(0);}
			if (event!=null) {setEventSelection(Event.FIELD_ACTIVE, new Boolean(!event.active), timeLineObject.selection);} 	
			break;
		case 1: if (event!=null) {toggleActivateSameEvents( event ); } 	break;
		case '*':  timeLineObject.rebuildTimeLine(); 	break;
		case '+': inputMode="+";break;// input midi note range mapping.
		case '=': inputMode="=";break;// input midi control mapping.
		case ' ': // trigger event
			playEvent(event);
			break;
		case 'y': inputMode="y";break;// filter input.
		case 'I': inputMode="I";inputStr=timeLineObject.followOnExpr;break;// set follow on expression.
		case 3: saveUndo();copyEvents(); break;// copy selected events
		case 22:saveUndo();pasteEvents();break;// paste copied events
		case 'h':inputMode="h";break;// edit map id
		case 'H':inputMode="H";break;// delete map
		case 'X': inputMode="X";break;// view del keymap list
		case '1':if (alt) {inputMode="1";} else {midiUtil.setMaps(key);}break;//  1 - keymap
		case '2':if (alt) {inputMode="2";} else {midiUtil.setMaps(key);}break;//  2 - keymap
		case '3':if (alt) {inputMode="3";} else {midiUtil.setMaps(key);}break;//  3 - keymap
		case '4':if (alt) {inputMode="4";} else {midiUtil.setMaps(key);}break;//  4 - keymap
		case '5':if (alt) {inputMode="5";} else {midiUtil.setMaps(key);}break;//  5 - keymap
		case '6':if (alt) {inputMode="6";} else {midiUtil.setMaps(key);}break;//  6 - keymap
		case '7':if (alt) {inputMode="7";} else {midiUtil.setMaps(key);}break;//  7 - keymap
		case '8':if (alt) {inputMode="8";} else {midiUtil.setMaps(key);}break;//  8 - keymap
		case '9':if (alt) {inputMode="9";} else {midiUtil.setMaps(key);}break;//  9 - keymap
		}
	}
	
	public void insertInInputStr(String expr) {
		inputStr=inputStr.substring(0,cursorPos)+expr+inputStr.substring(cursorPos,inputStr.length());
		cursorPos+=expr.length();
	}
	public void setInputStr(String input) {
		inputStr="";cursorPos=0;
		insertInInputStr(input);
	}
	private void sendPreview() {
		//		        		 preivew sounnd sends preview osc message.
		Event e = new Event() ;
		e.oscMsgName="/preview/set";
		e.oscIndex=0;
		e.oscP5=oscServers[e.oscIndex];
		e.setValue("0 200000 44100 1 0.8");
		oscUtil.simpleOscMessage(e);
		
		e = new Event() ;
		e.oscMsgName="/preview/file";
		e.oscIndex=0;
		e.oscP5=oscServers[e.oscIndex];
		e.setValue(inputStr);
		oscUtil.simpleOscMessage(e);
		e = new Event() ;
		e.oscMsgName="/preview/trig";
		e.oscIndex=0;
		e.oscP5=oscServers[e.oscIndex];
		e.setValue(inputStr);
		oscUtil.simpleOscMessage(e);
	} 
	
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	getFileName(String fileName,int inc)  : value help for file names if the fileName str trys eval a path from the start of the string ( up to last /) 
//	then get the next for last file (or closest file to it).
//	fileName: the file name to test 
//	inc : incren=ment or decrement - represents next(1)/last(-1) file .
//	returns the evaluated file name or just the fileName string if it doesnt look like a path.
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	String  getFileName(String fileName,int inc) {
		if (ctrl) {
			if (fileName.indexOf("c:")==0) {fileName="/home/robm"+fileName.substring(2);} 
			else if (fileName.indexOf("/home/robm")==0) {fileName="c:"+fileName.substring(9);} 
			return fileName;
		}
		String fileNameBack=fileName.trim();
		if (!isUnix) { fileNameBack=fileName.replaceAll("/","\\\\");}
		File f=new File(fileNameBack);
		File parent=null;
		File[] dirList;
		String lastFragmet="";
		if (f.exists()) {
			//f ile exists
			if (f.isDirectory()) {
				if (fileNameBack.endsWith(File.separator)) {parent=f;	}
				else {parent=f.getParentFile();}
			}
			else {parent=f.getParentFile();}
		}else {
			//file doesnt exist.
			int pos=fileNameBack.lastIndexOf(File.separator);
			String testFileName="";
			//if there is a \ in the srting then take all up to the and assume its a diectory. 
			if (pos>0) {testFileName=fileNameBack.substring(0,pos);}
			else {return fileName;}
			if (testFileName.length()>0) {
				f=new File(testFileName);
				if (f.exists()) {parent =f;} 
				lastFragmet=fileNameBack.substring(pos+1,fileNameBack.length());
			}
		}
		if (parent==null) {return fileName;}
		dirList =parent.listFiles();
		// sort the list.
		List listSort=new ArrayList();
		for (int i=0;i<dirList.length;i++) {listSort.add(dirList[i]);}
		Collections.sort(listSort,new FileComparator());
		for (int i=0;i<dirList.length;i++) {dirList[i]=(File)listSort.get(i);}
		// the return file.
		String returnFile="";
		for (int i=0;i<dirList.length;i++) {
			if (fileNameBack.equals(dirList[i].getAbsolutePath())) {
				if (inc==1) {// next file.
					if (i<dirList.length-1) {returnFile= dirList[i+1].getAbsolutePath();} 
					else {returnFile=dirList[0].getAbsolutePath();}
				}
				else if (inc==-1) {// last file.
					if (i>1) {returnFile= dirList[i-1].getAbsolutePath();} 
					else {returnFile= dirList[dirList.length-1].getAbsolutePath();}
				}
			}
		}
		if ("".equals(returnFile)) {
			if (dirList.length>0) { 
				// check if there is a remaining fragmet and find the first file with the same first  letter.
				if (!"".equals(lastFragmet)) { 
					for (int i=0;i<dirList.length;i++) {
						if (dirList[i].getName().startsWith(""+lastFragmet.charAt(0))) {returnFile= dirList[i].getAbsolutePath();}
					}
					if ("".equals(returnFile)) {returnFile=dirList[0].getAbsolutePath();}
				}
				else {returnFile=dirList[0].getAbsolutePath();}// no fragment just return the first file.
			}
		}
		if ("".equals(returnFile)) {return fileName;}// nothing found. just return the original string.
		else if (!isUnix) {return returnFile.replaceAll("\\\\", "/");}
		else {return returnFile;}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	keyReleased()
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void keyReleased() { 
		//println("keyrelease:"+keyCode+" key:"+key);
		if (keyCode==SHIFT) {shift=false;timeLineObject.timeSelStart=-1;timeLineObject.timeSelEnd=-1;} //slidersDraw.setYPosShiftPressed(-1);
		if (keyCode==CONTROL) {ctrl=false;}
		if (keyCode==ALT) {alt=false;}
		if ((keyCode == RIGHT)||(keyCode == LEFT)){
			if (!shift) {
				timeLineObject.rebuildTimeLine();
				timeLineObject.lastSelEvent=timeLineObject.currentEvent;
			}
			if (getLastEvent()!=null) propHelper.setObject(getLastEvent(), "event");
		}
	} 
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	processInput():processes inputStr according to the inputMode. 
//	i:osc Message Name
//	v:osc msg (int) value
//	t:timeLine length
//	f:fileName for this timeline object // may need to change this to whole section.
//	n:timeLineObject id
//	e:timeObject to trigger as a target, if different to value then set new id of timeLineObject, if the same then cycles targetPlayMode
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void processInput() {
		saveUndo();
		
		println("procIn");
		Event e=getLastEvent();
		if (inputMode.equals("/")) {// input osc msg name
			if (!inputStr.startsWith("/") && !inputStr.startsWith("$") && !inputStr.startsWith("%")) {inputStr="/"+inputStr;}
			setEventSelection(Event.FIELD_OSCMSGNAME,inputStr,timeLineObject.selection); 
		} else if (inputMode.equals("v")) {// input value
			String valToSet=inputStr;
			if (valToSet.startsWith("%")) {
				exprUtil.setThis(timeLineObject, e);
				Object o=exprUtil.getValueExpr(valToSet);
				exprUtil.clearThis();
				if (o==null) {return;}
				valToSet=o.toString();
			}
			if ((editValueIndex>-1) &&(editValueIndex<editValueVector.size())) {
				//editValueVector.set(editValueIndex,Event.getValueFromStr(inputStr));
				valToSet=Event.getValueStr(editValueVector);
				setEventSelection(Event.FIELD_VALUEINDEX,editValueIndex+" " +inputStr,timeLineObject.selection);
			}
			else {
				setEventSelection(Event.FIELD_VALUE,valToSet,timeLineObject.selection);
			}
			//slidersDraw.setVisible(false);
		}  else if (inputMode.equals("V")) {// input value index
			try {
				editValueIndex=Integer.parseInt(inputStr);
			} catch (NumberFormatException n) {editValueIndex=-1;}
		}  else if (inputMode.equals("t")) {// inpput time line length
			try {
				timeLineObject.timeLineLength=Integer.parseInt(inputStr);
				timeLineObject.displayStart=0;
				timeLineObject.displayEnd= timeLineObject.timeLineLength;
			}  catch (NumberFormatException n) {showMessage("timeline length - bad int format: "+inputStr,LogWindow.TYPE_ERROR);}
		}
		else if (inputMode.equals("q")) {// input quantization step
			try {timeLineObject.quantize=Integer.parseInt(inputStr);}
			catch (NumberFormatException n) {showMessage("quantize - bad int format: "+inputStr,LogWindow.TYPE_ERROR);}
		}
		else if (inputMode.equals("k")) {// input color
			timeLineObject.setColor(inputStr);
		}
		else if (inputMode.equals("g")) {
			if (inputStr.lastIndexOf("-") == inputStr.length()-1) {
				timeLineObject.removeGroup(inputStr.substring(0,inputStr.length()-1));
			}
			else {timeLineObject.addGroup(inputStr);} 
		}
		else if (inputMode.equals("b")) {// input (quantisation steps/beat) - (beats/bar)
			timeLineObject.setBeats(inputStr);
		}
		else if (inputMode.equals("B")) {// input (quantisation steps/beat) - (beats/bar)
			try {
				calcUtil.setBPM(timeLineObject, Integer.parseInt(inputStr));
			}	catch (NumberFormatException n) {showMessage("bpm - bad int format: "+inputStr,LogWindow.TYPE_ERROR);}
		}
		else if (inputMode.equals("M")) {//set osc server index
			try {
				int chooseOsc=Integer.parseInt(inputStr);
				if (chooseOsc<oscServers.length) {
					timeLineObject.oscIndex=chooseOsc;
					//timeLineObject.oscP5= oscServers[ timeLineObject.oscIndex];
				}
			}
			catch (NumberFormatException n) {showMessage("set OscIndex(M) - bad int format: "+inputStr,LogWindow.TYPE_ERROR);}
		}
		else if (inputMode.equals("m")) {//set osc server index
			try {
				int chooseOsc=Integer.parseInt(inputStr);
				if (chooseOsc<oscServers.length) {
					setEventSelection(Event.FIELD_OSCINDEX,new Integer(chooseOsc),timeLineObject.selection);
					setEventSelection(Event.FIELD_OSCP5,oscServers[chooseOsc],timeLineObject.selection);
				}
			}
			catch (NumberFormatException n) {showMessage("set OscIndex(m) - bad int format: "+inputStr,LogWindow.TYPE_ERROR);}
		}
		else if ("Ff".indexOf(inputMode)>-1) {
			//input filename
			if (!isUnix) {inputStr=inputStr.replaceAll("/","\\\\");}
			String newFileName=inputStr.trim();
			if (!newFileName.endsWith(".xml")) { newFileName=newFileName+".xml";}
			// RM_FP: fix this so path get set, have to make compiler dirs here as well
			if ("F".equals(inputMode)) {fileUtil.loadAdditionalTimelines(newFileName);}
			else if ("f".equals(inputMode)) {
				if (newFileName.indexOf(this.dataFilePath)!=0) {
					String path=inputStr.substring( 0, inputStr.lastIndexOf(File.separator)+1 );
					setDataFilePath(path);
				}
				newFileName=newFileName.substring(this.dataFilePath.length(), inputStr.length());
				setFileName(newFileName);
			}
		}
		else if (inputMode.equals("n")) {
			TimeLineObject test = getTimeline(inputStr);
			TimeLineSet testSet = getTimelineSet(inputStr);
			if (test==null && testSet==null) {
				timeLineObject.setId(inputStr);
			} else {
				showMessage("Name exists: enter another", LogWindow.TYPE_WARN);
				setInputStr(getNextName(inputStr));
				return;
			}
		}
		else if (inputMode.equals("ne")) {
			setEventSelection(Event.FIELD_ID,inputStr,timeLineObject.selection);
			//getLastEvent().id=inputStr;
		}
		else if (inputMode.equals("w")) {// input superimpose timeline
			
			timeLineObject.pitch=Float.valueOf(inputStr).floatValue();
		}
		else if (inputMode.equals("W")) {// confirm delete timeline 
			if (inputStr.toLowerCase().equals("y")) {timeLineObject.normaliseOnPitch();}
		}
		else if (inputMode.equals("E")) {// confirm clear timeline
			if (inputStr.toLowerCase().equals("y")) {clearEveryThing();}
		}
		else if (inputMode.equals("d")) {// confirm delete event
			if (inputStr.toLowerCase().equals("y")) {deleteEvent();}
		}
		else if (inputMode.equals("D")) {// confirm delete timeline;
			if (inputStr.toLowerCase().equals("y")) {deleteTimeLine();}
		}
		else if (inputMode.equals("N")) {// iinput note, remove noter if trailed by a -
			if (inputStr.lastIndexOf("-") == inputStr.length()-1) {
				for (int i=0;i<notes.size();i++) {
					if (inputStr.indexOf((String)notes.get(i))==0) {notes.remove(i);}
				}
			} else {	notes.add(inputStr); }
		}
		else if (inputMode.equals("+")) {// iinput note
			if ("".equals(inputStr)) {midiUtil.dumpMidiNoteMap();}
			else midiUtil.setMidiNoteRange(inputStr);
		}
		else if (inputMode.equals("=")) {// iinput note
			if ("".equals(inputStr)) {midiUtil.dumpMidiCtlMap();}
			else midiUtil.setMidiControl(inputStr);
			currentMIDIInputDevice="";
		}
		else if (inputMode.equals("e")) {// input timeline target.
			if (e!=null) {     
				String[] input=inputStr.split(":");
				String targetPlayMode="p";
				if (input[0].equals(e.getTargetId())) {     
					if ("p".equals(e.targetPlayMode)){targetPlayMode="l";}
					else if ("l".equals(e.targetPlayMode)){targetPlayMode="";}
					//else if ("l".equals(e.targetPlayMode)){targetPlayMode="b";}
					//else if ("b".equals(e.targetPlayMode)){targetPlayMode="";}
					else if ("".equals(e.targetPlayMode)){targetPlayMode="p";}
				}
				setEventSelection(Event.FIELD_TARGET,getTimeline(input[0]),timeLineObject.selection); 
				setEventSelection(Event.FIELD_TARGETPLAYMODE,targetPlayMode,timeLineObject.selection);
				if (input.length==2) { 
					setEventSelection(Event.FIELD_TARGETDATA,input[1],timeLineObject.selection);
				}
			}
		}else if (inputMode.equals("I")) {
			timeLineObject.followOnExpr=inputStr;
		}else if (inputMode.equals("i")) {
			midiUtil.importMIDI(inputStr.trim());
		}else if (inputMode.equals("J")) {
			loadInterface(inputStr.trim());
		}else if (inputMode.equals("y")) {
			//add filter.
			boolean delete=false;
			boolean found=false;
			if (inputStr.endsWith("-")){delete=true;inputStr=inputStr.substring(0,inputStr.length()-1);}
			String filterSplit[] = inputStr.split(" ");
			Vector v=(Vector)filters.get(filterSplit[0]);
			if (v==null && !delete) {
				v=new Vector();
				filters.put(filterSplit[0],v);
			}
			for (int i=0;i<v.size();i++){
				FilterBean fb=(FilterBean)v.get(i);
				if (fb.getExpr().equals(filterSplit[1])){
					found=true;
					if (delete) {
						v.remove(fb);
					} //else {
					//	   fb.setActive(!fb.isActive());
					//   }
				}
			}
			if (!found && filterSplit.length>1) {
				FilterBean fb=new FilterBean() ;
				fb.setExpr(filterSplit[1]);
				v.add(fb);
			} else {
				showMessage("filter(y): '<src> <tgt>' expected.",LogWindow.TYPE_ERROR);
			}
		}else if (inputMode.equals("T")) {
			if (editValueIndex>-1) {
				if (timeLineObject.parameters.size()>editValueIndex) {
					timeLineObject.parameters.set(editValueIndex,Event.getValueFromStr(inputStr));
				}
			}
			else {
				Vector v=new Vector();
				String newVals[]=inputStr.split(" ");
				for (int i=0;i<newVals.length;i++) {
					v.add(Event.getValueFromStr(newVals[i]));
				}
				timeLineObject.parameters=v;
			}
		}else if (inputMode.equals("h")) {
			if (!"".equals(inputStr)) {
				String currMapEdit=midiWin.getCurrMapEdit();
				if (currMapEdit.indexOf("=")==0) {
					HashMap ctlDevMap=(HashMap)allMidiControlMaps.get(midiWin.getCurrMIDIDev());
					if (ctlDevMap.get(inputStr)!=null){showMessage("Name exists:"+inputStr+" ... delete first.");return;}
					Object o=ctlDevMap.get(currMapEdit.substring(1));
					ctlDevMap.remove(currMapEdit.substring(1));
					ctlDevMap.put(inputStr,o);
					if (currentMidiControlMapIds.get(midiWin.getCurrMIDIDev()).equals(currMapEdit.substring(1))) {
						currentMidiControlMapIds.put(midiWin.getCurrMIDIDev(),inputStr);
					}
				} else if (currMapEdit.indexOf("+")==0) {
					HashMap noteDevMap=(HashMap)allMidiNoteMaps.get(midiWin.getCurrMIDIDev());
					if (noteDevMap.get(inputStr)!=null){showMessage("Name exists:"+inputStr+" ... delete first.");return;}
					Object o=noteDevMap.get(currMapEdit.substring(1));
					noteDevMap.remove(currMapEdit.substring(1));
					noteDevMap.put(inputStr,o);
					if (currentMidiNoteMapIds.get(midiWin.getCurrMIDIDev()).equals(currMapEdit.substring(1))) {
						currentMidiNoteMapIds.put(midiWin.getCurrMIDIDev(),inputStr);
					}
				}
			}
		} else if (inputMode.equals("H")) {
			if ("yY".indexOf(inputStr)>=0) {
				String currMapEdit=midiWin.getCurrMapEdit();
				if (currMapEdit.indexOf("=")==0) {
					HashMap ctlDevMap=(HashMap)allMidiControlMaps.get(midiWin.getCurrMIDIDev());
					ctlDevMap.remove(currMapEdit.substring(1));
					if (currentMidiControlMapIds.get(midiWin.getCurrMIDIDev()).equals(currMapEdit.substring(1))) {
						currentMidiControlMapIds.put(midiWin.getCurrMIDIDev(),null);
						currentMidiControlMaps.put(midiWin.getCurrMIDIDev(),null);
					}
				} else if (currMapEdit.indexOf("+")==0) {
					HashMap noteDevMap=(HashMap)allMidiNoteMaps.get(midiWin.getCurrMIDIDev());
					noteDevMap.remove(currMapEdit.substring(1));
					if (currentMidiNoteMapIds.get(midiWin.getCurrMIDIDev()).equals(currMapEdit.substring(1))) {
						currentMidiNoteMapIds.put(midiWin.getCurrMIDIDev(),null);
						currentMidiNoteMaps.put(midiWin.getCurrMIDIDev(),null);
					}
				}			
			}
		} else if ("123456789".indexOf(inputMode)>-1) {
			String bindingVar= inputStr.trim();
			if ( bindingVar.lastIndexOf("-")==bindingVar.length()-1) {
				midiKeyBindings.remove(inputMode);
			}
			else if (!"".equals(bindingVar)) {midiKeyBindings.put(inputMode, bindingVar);}
		} else if (inputMode.equals("R")) {// set the recording granularity in ms.
			try {
				this.recordGranularity=Integer.parseInt(inputStr);
			} catch (NumberFormatException e1) {
				showMessage("recordGranularity - must be int.",LogWindow.TYPE_ERROR);
			}
		} else if (inputMode.equals("r")) {// set the recording granularity in ms.
			boolean checkValid=(inputStr.indexOf("/")==0);
			for (int i=0;i<midiDeviceNames.length && !checkValid;i++) {
				if (!"".equals(inputStr) && midiDeviceNames[i].indexOf(inputStr)==0) {checkValid=true;} 
			}
			if (checkValid || "".equals(inputStr)) {this.recordInput=inputStr;}
		}	else if (inputMode.equals("S")) {
			inputStr = inputStr.trim();
			if (!"".equals(inputStr)) {
				if (inputStr.charAt(inputStr.length()-1)=='-') {
					TimeLineSet s = getTimelineSet(inputStr.substring(0,inputStr.length()-1));
					if (s!=null ) {timeLineSets.remove(s);}
				} else {
					TimeLineObject testTimeLine = getTimeline(inputStr);
					TimeLineSet testSet = getTimelineSet(inputStr);
					if (testTimeLine==null && testSet==null) {
						if (timeLineSet!=null) {
							timeLineSet.setId(inputStr);
						}	else {
							boolean add=false;
							TimeLineSet s = getTimelineSet(inputStr);
							if (s==null)   {s = new TimeLineSet(this);add=true;} 
							s.clear();
							s.addAll(timeLineSelection);
							s.setId(inputStr);
							if (add) timeLineSets.add(s);
						}
					} else {
						showMessage("Name exists: enter another", LogWindow.TYPE_WARN);
						setInputStr(getNextName(inputStr));
						return;
					}
				}
			} else {showMessage("Nothing entered.", LogWindow.TYPE_ERROR);}
		}	else if (inputMode.equals("O")) {
			if (inputStr.lastIndexOf("-")==inputStr.length()-1) {
				String key = inputStr.substring(0,inputStr.length()-1);
				if (!stdObjectClasses.containsKey(key)) {
					dynamicObjects.remove(key);
				}else {
					showMessage("Do not delete standard objects.", LogWindow.TYPE_ERROR);
				}
			}
		} 
		cancelInput();
	}
	
	private void cancelInput() {
		inputMode="";
		inputStr="";
		cursorPos=inputStr.length();
	}
	
	public void setFileName(String newFileName) {
		this.fileName=newFileName;
		setTitle(this.fileName);
	}
	
	public void setDataFilePath(String path) {
		this.dataFilePath=path;
		dynCompiler.setBase(this.dataFilePath);
		clearUndo();
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	*  getTimeLine(String timeLineId) 
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public TimeLineObject getTimeline(String timeLineId) {
		for (Iterator timeIter = timeLines.iterator(); timeIter.hasNext();) {
			TimeLineObject element = (TimeLineObject) timeIter.next();
			if (element.id.equals(timeLineId)) {return element;}
		} 
		return null;
	}
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	  *  getTimeLineSet(String setId) 
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public TimeLineSet getTimelineSet(String setId) {
			for (Iterator timeIter = timeLineSets.iterator(); timeIter.hasNext();) {
				TimeLineSet element = (TimeLineSet) timeIter.next();
				if (element.getId().equals(setId)) {return element;}
			} 
			return null;
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	*  undo() :undo last action 
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private String getUndoFile(int index){
		String nm="undo"+File.separator+fileName+"-"+index;
		return nm;
	}
	
	private void undo() {
		//int index= timeLines.indexOf(timeLineObject);
		TimeLineSet undoSet = new TimeLineSet(this);
		undoSet.addAll(timeLineSelection);
		String resetList = undoSet.getList();
		if (undoIndex>-1) { 
			if (undoIndex==undoVec.size()-1) {
				String nm=getUndoFile(undoIndex+1);
				fileUtil.save(nm);
				undoVec.add(nm);
			}
			fileUtil.open( (String)undoVec.get(undoIndex), true, false );
			undoIndex--;
			//if (timeLines.size()>index) {timeLineObject=(TimeLineObject)timeLines.get(index);}
			if(!"".equals(resetList)) {
				timeLineSelection.clear();
				String[] list= resetList.split(",");
				for (int i=0;i<list.length;i++) {
					TimeLineObject tlo= getTimeline(list[i]);
					if (tlo!=null) timeLineSelection.add(tlo);
					
				}
				updateTimeLineDisplays();
			}
		} else {showMessage("No more undos");}
	}
	
	private void redo() {
		//int index= timeLines.indexOf(timeLineObject);
		TimeLineSet undoSet = new TimeLineSet(this);
		undoSet.addAll(timeLineSelection);
		String resetList = undoSet.getList();
		if (undoIndex>=-1 && undoIndex<undoVec.size()-1) { 
			undoIndex++;
			fileUtil.open((String)undoVec.get(undoIndex),true,false);
			//if (timeLines.size()>index) {timeLineObject=(TimeLineObject)timeLines.get(index);}
			if(!"".equals(resetList)) {
				timeLineSelection.clear();
				String[] list= resetList.split(",");
				for (int i=0;i<list.length;i++) {
					TimeLineObject tlo= getTimeline(list[i]);
					if (tlo!=null) timeLineSelection.add(tlo);
					
				}
				updateTimeLineDisplays();
			}
		} else {showMessage("No more redos");}
	}
	
	private void saveUndo() {
		markDirty(true);
		// RM_FP: undo files dont realy need to be in the data file path - but could be
		File f=new File(this.dataFilePath+"undo");
		
		if (!f.exists()) {f.mkdir();}
		if (undoOn) {
			undoIndex++;
			String nm=getUndoFile(undoIndex);
			if (undoVec.size()<undoIndex+1) {
				undoVec.add(nm);
			} else {
				undoVec.set(undoIndex,nm);
				while (undoVec.size()>undoIndex+1) {
					File delFile=new File(this.dataFilePath+undoVec.get(undoVec.size()-1));
					delFile.delete();
					undoVec.remove(undoVec.size()-1);
				}
			}
			fileUtil.save(nm);
			File delFile=new File(this.dataFilePath+nm);
			delFile.deleteOnExit();
		}
	}
	
	public void clearUndo() {
		for (int i=0;i<undoVec.size();i++) {
			File delFile=new File(this.dataFilePath+undoVec.get(i));
			delFile.delete();
		}
		undoVec.clear();
		undoIndex=-1;
		markDirty(false);
	}
	// Copy file (src) to File/directory dest.
	public   void copy(File src, File dest)  {
		try {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void backupLastUndo() {
		if (undoVec.size()>0) {
			File f = new File(dataFilePath+File.separator+((String)undoVec.get(undoVec.size()-1)));
			if (f!=null && f.exists()) {
				copy(f, new File(f.getParentFile().getParentFile(),"backup.xml"));
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	addRemoveSelection() :
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void addRemoveSelection() {
		Event e= getLastEvent();
		if (e!=null) {
			if (timeLineObject.selection.contains(e)) {
				timeLineObject.selection.remove(e);
			}
			else {
				timeLineObject.selection.add(e);
			}
		}  else {	timeLineObject.selection.clear();	}
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	addRemoveSelection() :
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void selectDeselectAll() {
		if (timeLineObject.selection.size()<timeLineObject.timeLine.size()) {
			timeLineObject.selection.addAll(timeLineObject.timeLine);
		}	else {
			timeLineObject.selection.clear();
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	* selectEventFromTime: adds the timeline to the palying vector and et the timeline to play(or loop). 
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	private void selectEventsFromTime() {
		for (int i=0;i<timeLineObject.timeLine.size();i++) {
			Event e=(Event)timeLineObject.timeLine.get(i);
			if ((e.eventTime>=timeLineObject.timeSelStart) &&(e.eventTime<=timeLineObject.timeSelEnd)) {
				if (!timeLineObject.selection.contains(e)) {timeLineObject.selection.add(e);}
				else {timeLineObject.selection.remove(e);}
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	* togglePlayMode: adds the timeline to the palying vector and et the timeline to play(or loop). 
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  public void togglePlayMode(String c){togglePlayMode( c,timeLineObject);}
	
  public void togglePlayMode(String c,TimeLineObject currentTimeLine) {
		while (currentTimeLine!=null) {
			if ("P".equals(c)) {// if P then stop all playing timelines
				currentTimeLine.playMode="";
				playing.remove(currentTimeLine);
				if (playing.size()>0) {currentTimeLine=(TimeLineObject) playing.get(0);}
				else {currentTimeLine=null;}
			}
			else {// otherwise just stop/play/loop this timeline.
				currentTimeLine.setPlayMode((currentTimeLine.playMode==c)?"":c);
				if ("".equals(currentTimeLine.playMode) ) {	  playing.remove(currentTimeLine);	}  
				else {	 
					if (!playing.contains(currentTimeLine)){playing.add(currentTimeLine);}
					if ("r".equals(c)) {oscUtil.lastRecEventTime=currentTimeLine.pos;}
				}
				currentTimeLine=null;
			}
		}
		
	}
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	* getLastEvent: gets the last event triggered in this timeLineObject.
//	* @return the last event
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public Event getLastEvent() {
		return timeLineObject.getLastEvent();
	}
	
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	prevTimeLine():go to previous timeline.
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void prevTimeLine() {
		if (timeLineIndex>0) {timeLineIndex--;}
		timeLineObject=(TimeLineObject)timeLines.get(timeLineIndex);
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	nextTimeLine(): go to next timeline.
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void nextTimeLine() {
		if (timeLineIndex<timeLines.size()-1) {timeLineIndex++;}
		else {
			//make a new timeline.
			timeLines.add(new TimeLineObject(this));//oscServers[timeLineObject.oscIndex]
			timeLineIndex=timeLines.size()-1;
		}
		timeLineObject=(TimeLineObject)timeLines.get(timeLineIndex);
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	gotoChildTimeLine(): goes to timeline specified in event.target
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void gotoChildTimeLine() {
		Event e=getLastEvent();
		if (e!=null) {
			if (e.target!=null) {
				this.timeLineParentIndex=timeLines.indexOf(timeLineObject);
				timeLineObject=e.target;
				this.timeLineIndex=timeLines.indexOf(timeLineObject);
			} 
		}
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	gotoRootTimeLine(): goes to timeline specified in event.target
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void gotoRootTimeLine() {
		this.timeLineIndex=0;
		this.timeLineObject=(TimeLineObject)timeLines.get(timeLineIndex);
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	gotoRootTimeLine(): goes to timeline specified in event.target
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void gotoTimeLine(int i) {
		this.timeLineIndex=i;
		this.timeLineObject=(TimeLineObject)timeLines.get(this.timeLineIndex);
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	gotoParentTimeLine(): goes to timeline specified in event.target
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void gotoParentTimeLine() {
		this.timeLineIndex=this.timeLineParentIndex;
		this.timeLineObject=(TimeLineObject)timeLines.get(timeLineIndex);
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	deleteEvent() : deletes event
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void deleteEvent(){
		if (timeLineObject.selection.size()>0) {
			for (int i=0;i<timeLineObject.selection.size();i++) {
				timeLineObject.removeFromGroups((Event)timeLineObject.selection.get(i));
				timeLineObject.timeLine.remove(timeLineObject.selection.get(i));
			}
			timeLineObject.selection.clear();
		}
		else  if (timeLineObject.lastSelEvent>-1) {
			timeLineObject.removeFromGroups((Event)timeLineObject.timeLine.get(timeLineObject.lastSelEvent));
			timeLineObject.timeLine.remove(timeLineObject.lastSelEvent);
			
		}
		timeLineObject.rebuildTimeLine();
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	deleteTimeLine() : deletes current timeline
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void deleteTimeLine(){
		if (timeLines.size()>0) {
			if (timeLineObject!=null) {
				timeLines.remove(timeLineObject);
				if (timeLineIndex>=timeLines.size()) {
					timeLineIndex=timeLines.size()-1;
				}
				if (timeLines.size()==0) {timeLines.add(new TimeLineObject(this));timeLineIndex=0;} //oscServers[timeLineObject.oscIndex]
				for (int i=0;i<timeLineSets.size();i++) {
					TimeLineSet set = (TimeLineSet) timeLineSets.get(i);
					set.getSet().remove(timeLineObject);
				}
				timeLineSelection.remove(timeLineObject);
				timeLineObject=(TimeLineObject)timeLines.get(timeLineIndex);
				updateTimeLineDisplays();
			}
		}
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	copyEvent() : copies an event
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void copyEvent()  {
		if ((timeLineObject.lastSelEvent>=0) && (timeLineObject.lastSelEvent<timeLineObject.timeLine.size())) {
			Event e=(Event)timeLineObject.timeLine.get(timeLineObject.lastSelEvent);
			Event newEvent=e.getCopy(timeLineObject);
			timeLineObject.timeLine.add(newEvent);
			timeLineObject.rebuildTimeLine();
			timeLineObject.currentEvent=timeLineObject.timeLine.indexOf(newEvent);
			timeLineObject.lastSelEvent=timeLineObject.currentEvent;
		}
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	copyEvents() & pasteEvents() : copies and pastes a selection of events.
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	void copyEvents() {
		if (timeLineObject.selection.size()>0) {
			copySel.clear();
			copySel.addAll(timeLineObject.selection);
		} else {
			Event e=getLastEvent();
			if (e!=null) {
				copySel.clear();
				copySel.add(e);
			}
		}
		showMessage(copySel.size()+" events copied. ");
	}
	void pasteEvents() {
		int offset = 0;
		int minTime = 0;
		for (int i=0;i<copySel.size();i++) {
			Event e=(Event)copySel.get(i);
			if (minTime ==0 || minTime>e.eventTime){minTime=e.eventTime;}
		}
		for (int i=0;i<copySel.size();i++) {
			Event e=(Event)copySel.get(i);
			// extend timeline if too short
			Event eventCopy = e.getCopy(timeLineObject);
			if (shift) { // paste at cursor
				eventCopy.eventTime = eventCopy.eventTime - minTime +timeLineObject.pos;
			}
			if (timeLineObject.timeLineLength<eventCopy.eventTime) {
				timeLineObject.timeLineLength = (int) eventCopy.eventTime+10;
			}
			timeLineObject.timeLine.add( eventCopy );
		} 
		timeLineObject.rebuildTimeLine();
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	getSameEvents() :get the same events on a timeline
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  	Vector  getSameEvents(TimeLineObject t) {
		Event e=t.getLastEvent();
		if (e!=null) {
			Vector sameEvents=new Vector();
			boolean selectByTarget=false;
			String selectValue=e.oscMsgName;
			if ("/none".equals(selectValue) || "".equals(selectValue) || "/".equals(selectValue)) {
				selectByTarget=true;
				if (e.target!=null) {
					selectValue=e.target.id;
				} else {selectValue=null;}
			}
			if (selectValue==null) {return new Vector();}

			for (int i=0;i<t.timeLine.size();i++) {
				Event timeLineEvent=(Event)timeLineObject.timeLine.get(i);
				String cmpStr=timeLineEvent.oscMsgName;
				if (selectByTarget) {cmpStr=(timeLineEvent.target!=null)?timeLineEvent.target.id:"";}
				if (cmpStr.equals(selectValue)) {
					sameEvents.add(timeLineEvent);
				}
			}
			return sameEvents;
		} 
		return new Vector();
  	}
//	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	  selectSameEvents() :selects event matching oscMsgName or target of current event
//	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void  selectSameEvents() {
		Event e=getLastEvent();
		if (e!=null) {
			Vector sameEvents = getSameEvents(timeLineObject);
			boolean remove=timeLineObject.selection.contains(e);
			if (!remove) {timeLineObject.selection.addAll(sameEvents);}
			else {timeLineObject.selection.removeAll(sameEvents);}
		}
	}
//	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	  selectSameEvents() :selects event matching oscMsgName or target of current event
//	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void toggleActivateSameEvents(Event e) {
		if (e!=null) {
			Vector sameEvents = getSameEvents(timeLineObject);
			boolean activate=e.active;
			setEventSelection(Event.FIELD_ACTIVE, new Boolean(!e.active),sameEvents);
		}
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	setEventSelection : set field in all events in selection.
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	void setEventSelection(int field,Object data,Vector events) {
		Event e = getLastEvent();
		if (e!=null &&timeLineObject.selection.size()==0 ) {e.setField(field,data);}
		for (int i=0;i<events.size();i++) {
			Event ev =(Event)events.get(i);
			ev.setField(field,data);
		}
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	copyTimeLine() : copies timeLine
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void copyTimeLine() {
		if (timeLineObject!=null) {
			TimeLineObject newtimeline=timeLineObject.getCopy();
			newtimeline.id=getNextName(newtimeline.id);
			timeLines.add(newtimeline);
		}
	}
	
	
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	Helper ActionListeners
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private class NoteHelperActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			setInputStr(noteHelper.getInput());
		}
	}
	private class ControlHelperActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			setInputStr(controlHelper.getInput());
		}
	}
	private class ExprHelperActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			boolean insert=e.getSource().equals("Insert");
			if ("V".equals(inputMode)) {
				if (insert) {
					insertInInputStr(exprGetHelper.getInput());
				} else {setInputStr(exprGetHelper.getInput());}
			} else if ("TvyI".indexOf(inputMode)>-1) {
				if (!insert) {
					String exprString=exprGetHelper.getInput();
					if (inputStr.indexOf(" ")>-1) {
						int start=inputStr.lastIndexOf(" ", cursorPos-1);
						//if (start==-1) {start=0;}
						int end=inputStr.indexOf(" ", cursorPos);
						if (end==-1) {end=inputStr.length();}
						inputStr = inputStr.substring(0,start+1)
									+exprString+
									inputStr.substring(end); 
					} else {
						setInputStr(exprString);
					}
				} else {
					insertInInputStr(exprGetHelper.getInput());
				}
			} else if ("i".equals(inputMode)) {
				setInputStr(exprSetHelper.getInput());
			} else {
				if (insert) {
					insertInInputStr(exprGetHelper.getInput());
				} else {setInputStr(exprGetHelper.getInput());}
			}
		} 
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//MouseWheel ActionListener
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private class InputHelpWheel implements MouseWheelListener{
	
		public void mouseWheelMoved(MouseWheelEvent e) {
			Object boundedObj = getTimeLineDrawObject(false);
			TimeLineDraw timeLineDraw = (boundedObj instanceof TimeLineDraw)?((TimeLineDraw)boundedObj):null;
			if (inputBarDraw.inside(mouseX,mouseY)) {
				Object o=inputBarDraw.checkForObjectOnScreen(mouseX,mouseY);
				if (!inputMode.equals("") && o!=null && o.equals("inputField")) {
					NumberScrollingUtil.NumberCursorData ncd  = numberScrollingUtil.getNumberNearCursor( inputStr, cursorPos ); 
					if ( ncd!=null ) {						
						inputStr = numberScrollingUtil.getNextNumber( e, ncd,inputStr, cursorPos );
					} else {
						if (e.getWheelRotation() > 0) 	{ keyCode = DOWN; keyPressed();	}
						if (e.getWheelRotation() <= 0) 	{ keyCode = UP; keyPressed();	}
					}
				}
			}else if (popupWindowDraw.isVisible() && popupWindowDraw.inside(mouseX,mouseY)) {
				popupWindowDraw.setScroll(e.getWheelRotation() > 0);
//			} else if (slidersDraw.inside(mouseX,mouseY)) {
//				slidersDraw.updateSliderWheel(mouseX,mouseY,ctrl,shift,e.getWheelRotation() );
//				if ("v".equals(inputMode)) {
//					Event copyEv=getLastEvent().getCopy(null);
//					slidersDraw.setBackValues(copyEv.getValue());
//					inputStr=copyEv.getValueStr();
//					cursorPos=inputStr.length();
//					if (alt) {	   oscUtil.simpleOscMessage(copyEv);   }
//				} else if ("w".equals(inputMode)) {
//					inputStr=""+slidersDraw.getSliderValue(0);
//					if (alt) {	   timeLineObject.pitch=slidersDraw.getSliderValue(0);  }
//				}
			}	else if (timeLineDraw!=null) {
				Object o = timeLineDraw.checkForObjectOnScreen(mouseX,mouseY);
				if (o instanceof String) {
					String stringObject=(String) o;
					int lgth=timeLineObject.displayEnd-timeLineObject.displayStart;
					int move=(int) Math.round((lgth)*0.1);
					int end=Math.round((lgth)*2);
					Vector selection =null;
					TimeLineObject lastTimeLineObject = timeLineObject;
					boolean lastScrollPosToView=lastTimeLineObject.scrollPosToView;
					if (ctrl) {
						selection=timeLineSelection;
					} else {selection=new Vector();selection.add(timeLineObject);}
					for (int i=0;i<selection.size();i++) {
						timeLineObject = (TimeLineObject) selection.get(i);
						if (stringObject.toLowerCase().equals("scroll") && (e.getWheelRotation() > 0)) {scrollLeft(move);} // works for scroll bar or scroll button
						else if (stringObject.toLowerCase().equals("scroll") && (e.getWheelRotation() <= 0)) {scrollRight(move);}
						else if (stringObject.equals("Zoom") && (e.getWheelRotation() < 0)) {zoomIn();}
						else if (stringObject.equals("Zoom") && (e.getWheelRotation() >= 0)) {zoomOut();}	
						else if (stringObject.equals("Height") && (e.getWheelRotation() < 0)) {adjustHeight(-5);}
						else if (stringObject.equals("Height") && (e.getWheelRotation() >= 0)) {adjustHeight(5);}	
						else if (stringObject.equals("ScrollPosToView") ) {timeLineObject.scrollPosToView=!lastScrollPosToView;}	
					}
					timeLineObject = lastTimeLineObject;
				}
			} 
		}
	}
	
//	public String getNextNumber(MouseWheelEvent e, NumberCursorData ncd,String inputStr2) {
//		int dirFactor=1;
//		if (e.getWheelRotation() > 0) 	{ dirFactor=-1;}
//		String newValue="";
//		int oldLength = ncd.stringVal.length();
//		if (ncd.value instanceof Float) {
//			int indexOfDot = ncd.stringVal.indexOf(".");
//			float digitsMultiplier = indexOfDot<ncd.posInNumber?
//													(indexOfDot - ncd.posInNumber+1) :
//														 indexOfDot -( ncd.posInNumber );//oldLength-
//			float newValueFloat = ncd.value.floatValue() + (dirFactor * (float) Math.pow(10f, digitsMultiplier-1));
//			//newValueFloat = (new BigDecimal(newValueFloat)).setScale(oldLength-indexOfDot).floatValue();
//			newValue=""+newValueFloat;
//			int oldLenComp = ncd.value.floatValue()>0?oldLength:oldLength-1;
//			while (newValue.length()< oldLenComp) {
//				newValue+="0";
//			}
//			if (newValue.length()>oldLength) {		cursorPos++;	}
//		} else {
//			int digitsMultiplier = oldLength - ncd.posInNumber;
//			int newValueInt = ncd.value.intValue() + (dirFactor * (int) Math.pow(10f, digitsMultiplier-1));
//			newValue=""+newValueInt;
//			if (newValue.length()>oldLength) {		cursorPos++;	}
//		}	
//		return inputStr2.substring( 0, ncd.start ) + newValue +((ncd.end<inputStr2.length())? inputStr2.substring( ncd.end, inputStr2.length() ):"");
//	}
/////////////////////////////////// ///////////////////////////////// ///////////////////////////////// ///////////////////////////////// 
//// get the number next to the cursor for use with mouse wheel
/////////////////////////////////////////////////////////////////// ///////////////////////////////// ///////////////////////////////// 
//	private class NumberCursorData{
//		public int posInNumber = 0;
//		public Number value = null ;	
//		public String stringVal = null ;	
//		public int start = 0;
//		public int end = 0;
//	}
//	
//	private NumberCursorData getNumberNearCursor(String inputStr) {
//		char beforeChar = cursorPos>0?inputStr.charAt(cursorPos-1):(char)-1;
//		char afterChar = cursorPos<inputStr.length()?inputStr.charAt(cursorPos):(char)-1;
//		if (Character.isDigit(beforeChar)||Character.isDigit(afterChar)) {
//			NumberCursorData ncd = new NumberCursorData();
//			// move left from cursor to get stat of number 
//			int start = cursorPos;
//			for (;start>-1 ;start--) {
//				if ("1234567890.-".indexOf(inputStr.charAt(start))==-1) {start ++;	break;	}
//			}
//			if (start==-1) {start=0;}
//			//	move right of cursor to get the end of the number.
//			int end = cursorPos;
//			for (; end<inputStr.length() ;end++) {
//				if ("1234567890.-".indexOf(inputStr.charAt(end))==-1) { break; }
//			}
//			if (start>=end) {return null;} 
//			String numStr = inputStr.substring(start, end);
//			
//			try  {
//				if (numStr.indexOf(".")>-1) {
//					ncd.value = new Float(numStr);
//				} else {
//					ncd.value = new Integer( numStr);
//				}
//			} catch (NumberFormatException n) {
//				return null;
//			}
//			ncd.posInNumber = cursorPos-start;
//			ncd.stringVal  = numStr;
//			ncd.start = start;
//			ncd.end = end;
//			return ncd;
//		}
//		return null;
//	}
	// adjust the height of the timeline.
	private void adjustHeight(int increment) {
		if ((timeLineObject.setHeight>60) && increment<=0) {
			timeLineObject.setHeight+=increment;
		} else if ((timeLineObject.setHeight<200)&& increment>0) {
			timeLineObject.setHeight+=increment;
		}
	}
	// zoom in (with wheel)
	private void zoomIn() {
		int lgth=timeLineObject.displayEnd-timeLineObject.displayStart;
		timeLineObject.displayEnd=timeLineObject.displayStart+Math.round((lgth)/2);
	}
	//	zoom out (with wheel)
	private void zoomOut() {
		int lgth=timeLineObject.displayEnd-timeLineObject.displayStart;
		timeLineObject.displayStart-=(lgth/2);
		timeLineObject.displayEnd+=(lgth/2);
		if (timeLineObject.displayStart<0) {
			timeLineObject.displayStart = 0;
		} 
		if (timeLineObject.displayEnd >= timeLineObject.timeLineLength) {
			timeLineObject.displayEnd = timeLineObject.timeLineLength;
		}
	}
	// scroll Left
	private void scrollLeft( int move) {
		int lgth=timeLineObject.displayEnd-timeLineObject.displayStart;
		if( timeLineObject.displayStart-move<=0 ){
			timeLineObject.displayStart=0;
			timeLineObject.displayEnd=lgth;
		} else {
			timeLineObject.displayStart-=move;
			timeLineObject.displayEnd-=move; 
		}
	}
	// scroll Right
	private void scrollRight(int move) {
		int lgth=timeLineObject.displayEnd-timeLineObject.displayStart;
		if((timeLineObject.displayEnd+move)>=timeLineObject.timeLineLength){ 
			timeLineObject.displayStart=timeLineObject.timeLineLength-lgth;
			timeLineObject.displayEnd=timeLineObject.timeLineLength;
		}
		else {
			timeLineObject.displayStart+=move;
			timeLineObject.displayEnd+=move;
		}
	} 
//	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	  window resize: event listeners
//	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public class WindowResizeHandler extends ComponentAdapter {
		public void componentResized(ComponentEvent e) {
			//set width here - get height from tlBounds.
			screenWidth = thisFrame.getWidth();
			screenHeight = tlBound.getTotalHeight() + BORDER_HEIGHT;
			tlBound.setWidth( screenWidth -BORDER_WIDTH);//
			thisFrame.setSize( screenWidth, screenHeight );
		}
		
		@Override
		public void componentMoved(ComponentEvent e) {
			//testing window move problems w. compiz.
			//super.componentMoved(e);
			Point location=((Component)e.getSource()).getLocation();
			//System.out.println(location.toString()+":"+thisFrame.getX()+":"+thisFrame.getY()+":"+thisFrame.getParent());
			thisFrame.setLocation( location );
		}
	}
	
	public class BoundsChangeHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			updateTimeLineDisplays();
		}
	}
	
	public void updateTimeLineDisplays() {
		timeLineDisplays.clear();
		tlBound.clearBoundedObjects();
		tlBound.addBoundedObject(topBarDraw);
		tlBound.addBoundedObject(inputBarDraw);
		tlBound.addBoundedObject(buttonsDraw);
		int ystart = 60;// start height
		int theTop=0;
		for (int i=0;i<timeLineSelection.size();i++) {
			TimeLineObject tlo = (TimeLineObject) timeLineSelection.get(i);
			TimeLineDraw tld  =  new TimeLineDraw(screenWidth, tlo.dispHeight, 0, ystart+theTop, tlo);
			timeLineDisplays.add(tld);
			tlBound.addBoundedObject(tld);
			theTop+=tlo.dispHeight;
		}
		thisFrame.setSize(screenWidth, tlBound.getTotalHeight()+BORDER_HEIGHT);
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	interface functions: moves selected events
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	loads interface file
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public void loadInterface(String fileName) {
		this.interfaceFile = fileName;
		interfaceBuilder.reset();
		interfaceReader.openFile(this.interfaceFile, interfaceBuilder);
		interfaceBuilder.setVisible(true);
	}
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	moveEvents: moves selected events
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void moveEvents(Vector events, int interval) {
		for (int i=0;i<events.size();i++) {
			Event event=(Event)events.get(i);
			event.eventTime+=interval;
			if (!alt) {event.eventTime=(event.eventTime/timeLineObject.quantize)*timeLineObject.quantize;}//quantise.
		}
	}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	MIDI Stuff: initialisation
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void noteOn(Note note){
		midiUtil.noteOn( note);
	}
	
	public  void closeMidi() {
		midiUtil.closeMidi();
	}
	
	public void noteOff(Note note){
		midiUtil.noteOff( note);
	}
	
	public void controllerIn(Controller controller){
		midiUtil.controllerIn(controller);
	}
	public void programChange ( ProgramChange programChange){// doesnt seem to work
		showMessage("progch:"+programChange.getNumber()+":"+programChange.getMidiPort()+":"+programChange.getMidiChannel());
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	OSC Stuff: initialisation
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  void initOsc() {
  	oscUtil.initOsc();
  }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	OSC Stuff: unpackMessage(OscIn oscIn): unpcak osc msg recievd and make event if recording.
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  public void oscEvent(OscIn oscIn) {
	  unpackMessage( oscIn,0);
  }
  void unpackMessage(OscIn oscIn,int server) {  
	oscUtil.unpackMessage( oscIn, server);
  }
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	OSC Stuff: simpleOscMessage(Event e, int index) - sends osc message (event e ) on specified OSC Port(array index) - used by midi stuff.
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  public void simpleOscMessage(Event e, int index) {
//	//oscUtil.simpleOscMessage( e,  index);
//	  oscUtil.simpleOscMessage( e);
//  }
	
	


//////////////////RecompLstnr ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * this allows the dynCompiler to access this dynamicObjects map and reload any classes theat are needed.
	 * @author robm
	 *
	 */
	private class RecompLstnr implements RecompilationListener {
		public void recompiledEvent(DynCompilerObjectBean o) {
			
		}
		public void reLoadedEvent(){
			//dynamicObjects.put("std",new Standard());
			// rm 11/11/07 dont think i neeed this ... removingo
			// addStandardObjects();
		}
		public HashMap getDynamicObjects() {
			return dynamicObjects;
		}
	}
	
	public void addStandardObjects() {
		Iterator i = stdObjectClasses.keySet().iterator();
		while (i.hasNext()) {
			String key=(String) i.next();
			Class c= (Class) stdObjectClasses.get(key);
			try {
				dynamicObjects.put(key, c.newInstance());
			} catch (InstantiationException e) {
				showMessage("could not create:"+key+"("+c.getName()+") - "+e.getMessage(), LogWindow.TYPE_ERROR);
			} catch (IllegalAccessException e) {
				showMessage("could not create:"+key+"("+c.getName()+") - "+e.getMessage(), LogWindow.TYPE_ERROR);
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	logging stuff.
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void addLogEntry(String str) {
		logVector.add(str);
		if (logVector.size()>10) {	writeLog();	}
	}
	FileWriter fw=null;
	public void initLog() {
		//RM_FP:
		try {
			fw=new FileWriter(new File(configDirectoryPath+"log.log"),false);
		} catch (Exception e) {
			System.out.println("open:"+e.getMessage()	);
		}
	}
	
	public void writeLog() {
		try {
			for (Iterator logVectorIter = logVector.iterator(); logVectorIter.hasNext();) {
				String s = (String) logVectorIter.next();
				fw.write(s+"\n");
				fw.flush();
			}
		} catch (Exception e) {
			System.out.println(":"+e.getMessage()	);
		}
		logVector.clear();
	}
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	  various click handlers
//  the button has an activate part and a name part ( | a |  name   |
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	  timeline  click handler
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void timeLineButClickHndlr(TimeLineObject selTimeLine) {
		if (selTimeLine!=null) {
			propHelper.setObject(selTimeLine, "timeline");
			int[] point = buttonsDraw.getBoundObjectInsideCoord(selTimeLine,mouseX,mouseY);
			if (point[0] > 35){ 
				cueTimeLineObject = selTimeLine;
			} else if (!"".equals(inputMode)  && (mouseButton==RIGHT) && (point[0]>12)){// if in inputmode and right click on name.
				if ( "e".indexOf(inputMode)>-1) {
					inputStr=selTimeLine.id;
					cursorPos=inputStr.length();
				} else  {
					// insert timeline expr.
					String expr="$t:"+selTimeLine.id+":";
					insertInInputStr(expr);
				}
			} 
			else if (point[0] > 12){ // if click on name.
				// create event to trigger right +ctrl clicked timeline
				 if (ctrl && mouseButton==RIGHT) {
					 if (selTimeLine!=timeLineObject && timeLineObject!=null ) {
						Event e = new Event();
						e.target=selTimeLine;
						e.targetPlayMode=shift?"l":"p";
						e.setValue("0") ;
						e.eventTime=0;
						e.oscIndex=timeLineObject.oscIndex;
						e.oscP5=oscServers[e.oscIndex];
						timeLineObject.timeLine.add(e);
						timeLineObject.rebuildTimeLine();
					 }
					return;
				}
				//set the visible timeline.
				if (shift && timeLineSet !=null) {
					if (timeLineSet.getSet().contains(selTimeLine)) {
						timeLineSet.getSet().remove(selTimeLine);
					} else {
						timeLineSet.getSet().add(selTimeLine);
					}
				} else {
					timeLineSet=null;
				}
				
				if (!ctrl) {
					if (timeLineSelection.contains(selTimeLine) &&timeLineSelection.size()>0 && timeLineObject==selTimeLine) {
						timeLineSelection.remove(selTimeLine);
					} else if (!timeLineSelection.contains(selTimeLine)) {timeLineSelection.add(selTimeLine);}
				} else {
					timeLineSelection.clear();
					timeLineSelection.add(selTimeLine);
				}
				this.timeLineIndex=timeLines.indexOf(selTimeLine);
				timeLineObject=selTimeLine;
				updateTimeLineDisplays();
			} else { // if click on activation part.
				//play/loop the cilcked timeline   this event is used just to send data
/* logic here is :-
 *
 * if (timeline playing) {stop timeline}
 * if (timeline is cued) {decue it}
 * if (ctrl || none playing ) {
 * 			dont que - play now
 *  		if (!shift) { stop others}
 * }
 * if (other timeline in group playing) {cue timeline &decue others cued  }
 */
				String playMode="p";
				if (mouseButton==RIGHT) {	playMode="l";	}
				if (!"".equals(selTimeLine.playMode)) {playMode="";} 

				Vector group = getTimeLineGroup(selTimeLine.id, true);
				Vector grpPlaying = checkOthersPlaying(selTimeLine.id);
				
				if (!"".equals(selTimeLine.playMode)) {
					selTimeLine.playMode="";return;
				}
				if (cue.checkQued(selTimeLine) ) {
					cue.deCueEveryWhere(selTimeLine);return;
				}
				if (ctrl || (grpPlaying.size()==0)) {
					if (!shift) {
						for (int j=0; j<group.size(); j++) {
							Cueable to = (Cueable)group.get(j);//TimeLineObject
							if (to!=selTimeLine && to instanceof TimeLineObject) {
								TimeLineObject t = (TimeLineObject) to;
								t.playMode = "";
							}
						}
					}
					playTimeline(selTimeLine, playMode);
					return;
				}
				if ( (grpPlaying.size()>0)) {//grpPlaying.contains(selTimeLine)
					// decue others
					for (Iterator i = group.iterator();i.hasNext()  ; ) {
						Cueable groupObject=(Cueable) i.next();
						cue.deCueEveryWhere(groupObject);
					}
					// encue timeline	
					for (Iterator i = grpPlaying.iterator();i.hasNext()  ; ) {
						TimeLineObject trig=(TimeLineObject) i.next();
						cue.addToCue(trig, selTimeLine, playMode, true, true);
					}
				}
			} 
		}
	}
	
	public boolean checkSameGroup(String to1, String  to2) {
		String test1=to1.split("-")[0];
		String test2=to2.split("-")[0];
		return test1.equals(test2);
	}
	public Vector getTimeLineGroup(String id, boolean getSets) {
		String base=id;
		if (base.indexOf("-")>-1) {base = base.substring(0,base.indexOf("-")+1);}
		else {return new Vector();}
		Vector returnVec = new Vector();
		for (int i=0;i<timeLines.size();i++) {
			TimeLineObject to=(TimeLineObject)timeLines.get(i);
			if (to.id.indexOf(base)==0 ){
				returnVec.add(to);
			}
		}
		if (getSets) {
			for (int i=0;i<timeLineSets.size();i++) {
				TimeLineSet ts=(TimeLineSet)timeLineSets.get(i);
				if (ts.getId().indexOf(base)==0 ){
					returnVec.add(ts);
				}
			}
		}
		return returnVec;
	}
	private String getNextName(String id) {
		if ( id.indexOf("-")>-1) {	id=id.substring(0, id.indexOf("-"));	}
		int cnt=0;
		int highIndex=0;
		for (int i=0;i<timeLines.size();i++) {
			TimeLineObject t=(TimeLineObject)timeLines.get(i);
			if (t.id.indexOf(id+"-")==0) {
				try {highIndex=Math.max(highIndex, Integer.parseInt(t.id.substring(t.id.indexOf("-")+1)));}catch(NumberFormatException n ){}
				cnt++;
			}
		}
		for (int i=0;i<timeLineSets.size();i++) {
			TimeLineSet t=(TimeLineSet)timeLineSets.get(i);
			if (t.getId().indexOf(id+"-")==0) {
				try {highIndex=Math.max(highIndex, Integer.parseInt(t.getId().substring(t.getId().indexOf("-")+1)));}catch(NumberFormatException n ){}
				cnt++;
			}
		}
		String newTimeLineId=id+"-"+(++highIndex);
		return newTimeLineId;
	}
	public Vector checkOthersPlaying(String id) {
		Vector returnVec = getTimeLineGroup(id, false);
		for (int i=0;i<returnVec.size();i++) {
			TimeLineObject to=(TimeLineObject)returnVec.get(i);
			if ("".equals(to.playMode)){
				returnVec.remove(to);
				i--;
			}
		}
		return returnVec;
	}

//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	  timeline set click handler
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void timeLineSetButClickHandler(TimeLineSet set) {
		propHelper.setObject(set, "timelineSet");
		int[] point = buttonsDraw.getBoundObjectInsideCoord(set, mouseX, mouseY);
		if (point[0]>12){
			if ((mouseButton==RIGHT) && (!"".equals(inputMode))) {
				setInputStr(set.getId());
			}else {
				if (timeLineSet==null) {
					timeLineSelection.clear();
					timeLineSelection.addAll(set.getSet());
					timeLineSet = set;
					updateTimeLineDisplays();
				} else {
					timeLineSet=null;
				}
			}
		} else if (point[0]<=12) {
/* logic here is :-
 *
 * if (set is cued) {decue it}
 * if (ctrl || none playing ) {
 * 			dont que - play now
 *  		if (!shift) { stop others}
 * }
 * if (other timeline in group playing) {cue timeline & decue others cued  }
 */
			//play/loop the cilcked timeline   this event is used just to send data
			String playMode="p";
			if (mouseButton==RIGHT) {	playMode="l";	}
			
			Vector group = getTimeLineGroup(set.getId(), true);
			Vector grpPlaying = checkOthersPlaying(set.getId());
			
			if (cue.checkQued(set) ) {
				cue.deCueEveryWhere(set);return;
			}
			if (ctrl || (grpPlaying.size()==0)) {
				if (!shift) {
					for (int j=0; j<group.size(); j++) {
						Cueable to = (Cueable)group.get(j);//TimeLineObject
						if ( to instanceof TimeLineObject) {
							TimeLineObject t = (TimeLineObject) to;
							t.playMode = "";
						}
					}
				}
				playTimeLines( set.getSet(), playMode );
				return;
			}
			if ( (grpPlaying.size()>0)) {//grpPlaying.contains(selTimeLine)
				// decue others
				for (Iterator i = group.iterator();i.hasNext()  ; ) {
					Cueable groupObject=(Cueable) i.next();
					cue.deCueEveryWhere(groupObject);
				}
				// encue timeline	
				for (Iterator i = grpPlaying.iterator();i.hasNext()  ; ) {
					TimeLineObject trig=(TimeLineObject) i.next();
					cue.addToCue(trig, set, playMode, true, true);
				}
			}
		}
	}
	
	public void enCueHandler(Cueable tgt) {
		if (cueTimeLineObject!=null && tgt!=null) {
			if (cue.checkQued(cueTimeLineObject, tgt)) {
				cue.deCue(cueTimeLineObject, tgt);
			} else if (cueTimeLineObject != tgt)  {
				cue.addToCue(cueTimeLineObject, tgt, (mouseButton==LEFT?"p":"l"),false, 
										checkSameGroup(tgt.getId(),cueTimeLineObject.getId()) || shift  //  stop if shift or in the same group
								);
			}
			cueTimeLineObject = null;
		}
	}
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	  event click handler
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void eventClickHndlr(Event e) {
		if (e!=null) {
			propHelper.setObject(e, "event");
			if (!"".equals(inputMode)  && (mouseButton==RIGHT)) {
				String expr="$e:"+timeLineObject.id+":"+("".equals(e.id)?""+timeLineObject.timeLine.indexOf(e):e.id)+":";
				insertInInputStr(expr);
			} else if (mouseButton==CENTER) {
				playEvent(e);
			} else {
				int index=timeLineObject.timeLine.indexOf(e);
				timeLineObject.currentEvent=index;
				timeLineObject.lastSelEvent=index;
				dragObject=e;
			}
		}
	}
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	  group click handler
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void groupClickHandler(String groupObjStr) {
		Object boundedObj = getTimeLineDrawObject(false);
		TimeLineDraw timeLineDraw = (boundedObj instanceof TimeLineDraw)?((TimeLineDraw)boundedObj):null;
		if (timeLineDraw==null) return;
		int[] point = timeLineDraw.getBoundObjectInsideCoord(groupObjStr,mouseX,mouseY);
		String groupName=groupObjStr.substring(2);
		if ("g".equals(inputMode) && (mouseButton==RIGHT)) {
			//inputStr=groupName;cursorPos=inputStr.length();
			insertInInputStr(groupName);
		}else if (!"".equals(inputMode)  && (mouseButton==RIGHT)) {
			String expr="$g:"+timeLineObject.id+":"+groupName+":";
			insertInInputStr(expr);
		} else if  (mouseButton==LEFT){
			Vector group=(Vector)timeLineObject.groups.get(groupName);
			if ("selection".equals(groupName)) {group=timeLineObject.selection;}
			boolean activeState=false;
			boolean selectState=false;
			if ("selection".equals(groupName) && (point[0]>12)) {timeLineObject.selection.clear();return;}
			for (int i=0;i<group.size();i++) {
				Event e=(Event)group.get(i);
				if (i==0) { activeState=e.active;selectState=timeLineObject.selection.contains(e); }
				if (point[0]<12) {	e.active=!activeState;}
				else {
					if (!selectState) {
						if (!timeLineObject.selection.contains(e)) {timeLineObject.selection.add(e);}
					} else {
						timeLineObject.selection.remove(e);
					}
				}
			}
		}
	}
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	  filter resloution.
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void playFilter(OscMessage oscMsg) {
		Vector v=(Vector)filters.get(oscMsg.getMsgName());
		for (int i=0;v!=null && i<v.size();i++){
			FilterBean fb=(FilterBean)v.get(i);
			if (fb.isActive()) {
				exprUtil.setValueExpr(fb.getExpr(), new Vector(oscMsg.getArgs()));
			}
		}
	}
	
	private void toggleActivateFilter(String filterSpec) {
		String filterSplit[] = filterSpec.split(" ");
		Vector v=(Vector)filters.get(filterSplit[0]);
		if (v!=null ){
			for (int i=0;i<v.size();i++){
				FilterBean fb=(FilterBean)v.get(i);
				if (fb.getExpr().equals(filterSplit[1])){
					fb.setActive(!fb.isActive());
				}
			}
		}
	}
	
	
}
