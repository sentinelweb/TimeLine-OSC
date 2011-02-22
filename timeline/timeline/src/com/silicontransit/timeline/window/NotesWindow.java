/*
 * Created on 29-Oct-2007
 */
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
package com.silicontransit.timeline.window;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import com.silicontransit.timeline.TimeLine;
import com.silicontransit.timeline.util.SwingStyler;

public class NotesWindow extends JPanel{
	
	protected JFrame thisFrame;
	JScrollPane noteScroll;
	JTextPane noteEdit ;
	TimeLine t=null;
	public NotesWindow(TimeLine t) {
		this.t = t;
		thisFrame = new JFrame("Notes");
		WindowListener l = new WindowCloseHandler(); 
		thisFrame.addWindowListener(l);
		thisFrame.getContentPane().add(this);
		thisFrame.setSize(new Dimension(450,200));
		ComponentListener cl = new WindowResizeHandler(); 
		thisFrame.addComponentListener(cl);
		
		this.noteEdit = new JTextPane();
		SwingStyler.setStyleCommon(noteEdit);
		this.noteScroll = new JScrollPane(noteEdit);
		this.setLayout(new GridLayout(1,1));
		this.add(noteScroll);
		this.setBorder(BorderFactory.createEmptyBorder());
		if (this.t!=null) {
			noteEdit.addKeyListener(new KeyHandler());
		}
		
	}
	private class KeyHandler implements KeyListener {
		public void keyPressed(KeyEvent e) {
			t.markDirty(true);
		}
		public void keyReleased(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {}
	}
	
	public class WindowCloseHandler extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			thisFrame.setVisible(false); 
		}
	}; 
	
	public class WindowResizeHandler extends ComponentAdapter {
		public void componentResized(ComponentEvent e) {
			//setComponentSizes();
			noteScroll.setPreferredSize(e.getComponent().getSize());
			noteEdit.setPreferredSize(e.getComponent().getSize());
			//System.out.println("resize"+e.getComponent().getWidth());
		}
	}
	
	public void setVisible(boolean vis) {
		thisFrame.setVisible(vis);
	}
	public boolean getVisible() {
		return thisFrame.isVisible();
	}
	
	public String getText(){
		return noteEdit.getText();
	}
	public void  setText(String text){
		 noteEdit.setText(text);
	}
}
