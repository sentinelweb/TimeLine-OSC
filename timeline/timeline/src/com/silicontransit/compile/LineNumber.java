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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.HashSet;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

public class LineNumber extends JPanel {
	// for this simple experiment, we keep the pane + scrollpane as members.
	JEditorPane editorPane;
	int offset=0;
	HashSet errLines=new HashSet();

	

	public LineNumber() {
		super();
		setMinimumSize(new Dimension(30, 30));
		setPreferredSize(new Dimension(30, 30));
		setMinimumSize(new Dimension(30, 30));
		editorPane =	new JEditorPane() {
			public void paint(Graphics g) {
				super.paint(g);
				LineNumber.this.repaint();
			}
		};
		this.setBackground(Color.white);
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		this.setFont(editorPane.getFont());
		Document doc = editorPane.getDocument();
		int startline = 0;
		int endline = doc.getDefaultRootElement().getElementCount();
		FontMetrics fontMetrics = g.getFontMetrics(editorPane.getFont());
		int fontHeight = fontMetrics.getHeight();
		int lineHeight=0;
		int lastSize=0;
		for (int line = startline, y = 0;	line <= endline; line++) {
			try {
				lineHeight+=lastSize;
				Element element = null;
				int start=0,fin=0;
				element = doc.getDefaultRootElement().getElement(line);
				if (element!=null) {
					start=element.getStartOffset();
					int linelen =  element.getEndOffset() - element.getStartOffset() ;
					int lineWidth = fontMetrics.stringWidth(
						doc.getText(element.getStartOffset(), linelen)
					);
					int wrappedlines=(int)  Math.ceil( lineWidth / editorPane.getWidth()	)+1;
					y=(lineHeight-(lastSize-1))*fontHeight;
					lastSize=wrappedlines;
					if (errLines.contains(new Integer(line + offset))) {
						g.setColor(Color.RED);
					} else {
						g.setColor(Color.BLACK);
					}
					if (line>0) {	g.drawString(Integer.toString( line + offset ), 0, y);	}
				}
			} catch (BadLocationException e) {
				System.out.println("badloc:"+e.offsetRequested());
			}
		}
	}

	public JEditorPane getEditorPane() {
		return editorPane;
	}

	public void setEditorPane(JEditorPane pane) {
		editorPane = pane;
	}

	public int getOffset() {		return offset;	}

	public void setOffset(int i) {		offset = i;	}
	public void addErrLine(int i) {
		errLines.add(new Integer(i));
	}
	public HashSet getErrLines() {
		return errLines;
	}

	public void setErrLines(HashSet errLines) {
		this.errLines = errLines;
	}

}
