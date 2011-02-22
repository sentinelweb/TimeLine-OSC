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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;

import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.event.MouseInputAdapter;

import com.silicontransit.timeline.TimeLine;
import com.silicontransit.timeline.bean.DebugBean;
import com.silicontransit.timeline.disp.DebugDraw;

public class DebugWindow extends JLabel implements Scrollable{
	
	private static final int WIN_WIDTH = 300;
	private static final int WIN_HEIGHT = 400;
	private DebugBean debugBean;
	private DebugDraw debugDraw=null;//WIN_WIDTH,WIN_HEIGHT
	public JFrame thisFrame;
	private boolean scrolled=false;
	
	public DebugWindow( TimeLine t) {
		this.debugBean=t.debugBean;
		debugDraw = new DebugDraw(700,500,0,0 , t.oscMessageColorMap, t.expressionColorMap);
	}
	
	public void init() {
		thisFrame=new JFrame();
		thisFrame.setSize(WIN_WIDTH, WIN_HEIGHT+34); // set window to appropriate size (for its elements)
		//thisFrame.setLocation(new Point(0,150));
		thisFrame.setVisible(false); // usual step to make frame visible
		thisFrame.setTitle("Debug");
		thisFrame.setLayout(new GridLayout(1,1));
		thisFrame.addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent evt) {
	            evt.getWindow().setVisible(false);
	        }
	    });
		this.addMouseListener(new MouseInputAdapter() {
			public void mouseClicked(MouseEvent e) {
				Object o=debugDraw.checkForObjectOnScreen(e.getX(),e.getY());
				if (o!=null) {
					debugBean.setMode((String)o);
					if (e.getButton()==MouseEvent.BUTTON3) {
						debugBean.clear((String)o);
					}
				}
			}
		});
		
		 this.setPreferredSize(new Dimension(700,600));
		// this.setSize(new Dimension(700,400));
		JScrollPane scroll=new JScrollPane(this);
		scroll.setPreferredSize(new Dimension(WIN_WIDTH,WIN_HEIGHT));
		scroll.addMouseMotionListener(new MouseInputAdapter() {
				public void mouseDragged(MouseEvent e) {scrolled=true;}
			}
		);
		
		//scroll.getVerticalScrollBar().setVisible(true);
		//scroll.getVerticalScrollBar().setSize(5,scroll.getHeight());
		thisFrame.addComponentListener(new WindowResizeHandler());
		JPanel  ctnrPanel=new JPanel();
	    ctnrPanel.add(scroll);
	    //ctnrPanel.setSize(WIN_WIDTH,WIN_HEIGHT);
	    ctnrPanel.setLayout(new GridLayout(1,1));
	    this.revalidate();
	    thisFrame.add(ctnrPanel);
	}
	public class WindowResizeHandler extends ComponentAdapter {
		public void componentResized(ComponentEvent e) {
			debugDraw.setHeight(thisFrame.getHeight());
			debugDraw.setWidth(thisFrame.getWidth());
		}
	}
	public void setVisible(boolean vis) {
		thisFrame.setVisible(vis);
	}
	public boolean getVisible() {
		return thisFrame.isVisible();
	}
	public void paint(Graphics g) {
		if (thisFrame.isVisible()  ){//&& (debugBean.isDirty()||scrolled)
			g.drawImage(
					debugDraw.getImage(this.debugBean),
					debugDraw.getLeft(),debugDraw.getTop(), Color.black, null
			 );
			//this.revalidate();
		}
	}
	public Dimension getPreferredScrollableViewportSize() {
		
		return this.getPreferredSize();
	}
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		
		return 1;
	}
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		
		return 1;
	}
	public boolean getScrollableTracksViewportWidth() {
		
		return false;
	}
	public boolean getScrollableTracksViewportHeight() {
		
		return false;
	}
}
