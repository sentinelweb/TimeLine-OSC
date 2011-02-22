package com.silicontransit.timeline.window;
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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;


public class LogWindow extends NotesWindow {
	public static final String TYPE_ERROR="error";
	public static final String TYPE_WARN="warn";
	public static final String TYPE_MSG="msg";
	public static final String TYPE_GOOD="good";
	
	private static  Vector messagesWaiting = new Vector();
	private Timer guiTimerStart;
	private GuiTimer guiTimer=new GuiTimer();
	public LogWindow() {
		super(null);
		StyledDocument messagesDoc = noteEdit.getStyledDocument();
		Style def = StyleContext.getDefaultStyleContext(). getStyle(StyleContext.DEFAULT_STYLE);
		Style error = messagesDoc.addStyle(TYPE_ERROR, def);
		StyleConstants.setForeground( error, Color.RED);
		Style warn = messagesDoc.addStyle(TYPE_WARN, def);
		StyleConstants.setForeground( warn, new Color(120,120,0));
		Style msg = messagesDoc.addStyle(TYPE_MSG, def);
		StyleConstants.setForeground( msg, new Color(0,0,255));
		Style good = messagesDoc.addStyle(TYPE_GOOD, def);
		StyleConstants.setForeground( good, new Color(0,150,0));
		noteEdit.setEditable(false);
		thisFrame.setTitle("Log");
		guiTimerStart = new Timer();
		startGuiTimer();
	}
	
	private void addMessages() {
		try {
			for (Iterator addIter = messagesWaiting.iterator(); addIter.hasNext();) {
				String[] msg = (String[]) addIter.next();
				StyledDocument doc = noteEdit.getStyledDocument();
				try {
					doc.insertString(0, msg[0]+"\n",	doc.getStyle(msg[1]));
				} catch (BadLocationException e) {
					
				}
			}
			messagesWaiting.clear();
		} catch (ConcurrentModificationException e) {
			
		}
		
	}
	
	public static synchronized void log(String msg,String type) {
		messagesWaiting.add(new String[]{msg,type});
	}
	public static synchronized void error(String msg) {
		messagesWaiting.add(new String[]{msg,TYPE_ERROR});
	}
	public static synchronized void warn(String msg) {
		messagesWaiting.add(new String[]{msg,TYPE_WARN});
	}
	public static synchronized void ok(String msg) {
		messagesWaiting.add(new String[]{msg,TYPE_MSG});
	}
	public static synchronized void msg(String msg) {
		messagesWaiting.add(new String[]{msg,TYPE_GOOD});
	}
	public static synchronized void ex(Throwable e) {
		StringWriter sw= new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		messagesWaiting.add(new String[]{sw.toString(),TYPE_ERROR});
	}
	class GuiTimer extends TimerTask {
		public void run() {  guiEvents();	  }
	}
	
	public void startGuiTimer(){
		guiTimerStart.scheduleAtFixedRate(new GuiTimer(),0,200);
	}
	
	private void guiEvents(){
		addMessages();
	}
	
	
}
