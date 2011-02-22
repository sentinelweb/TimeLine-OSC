package com.silicontransit.timeline.util;
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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class SwingStyler {
	public static Color backgroundColor = new Color(220,220,220);
	public static Color inputColor = new Color(180,180,255);
	
	public static Color textColor = Color.black;
	
	public static Font valueFont = new Font("Arial", Font.BOLD, 11);
	public static Font lblFont = new Font("Arial", Font.BOLD, 11);
	private static Font buttonFont = new Font("Arial", Font.BOLD, 11);
	
	static ImageIcon buttonImage=null;
	
	static {
		URL imgURL = SwingStyler.class.getResource("/images/button_blue.gif");
		buttonImage = new ImageIcon(imgURL);
	}
	
	public static void setStyleCommon(JComponent c) {
		c.setFont(buttonFont);
		c.setBackground(backgroundColor);
		c.setForeground(textColor);
	}
	
	public static void setStyleButton(JButton c) {
		setStyleCommon(c);
	}
	
	public static void setStyleInput(JComponent c) {
		 setStyleInput( c, false);
	}
	
	public static void setStyleInput(JComponent c,boolean scroll) {
		c.setBorder(BorderFactory.createEtchedBorder());
		if (!scroll) c.setPreferredSize(new Dimension(150,18));
		setStyleCommon(c);
		c.setBackground(inputColor);
	}
	
	public static void setStyleLabel(JLabel lbl) {
		lbl.setBorder(BorderFactory.createEmptyBorder());
		lbl.setPreferredSize(new Dimension(100,15));
		setStyleCommon(lbl);
	}
	
	
	
	public static class ImgButton extends JButton implements MouseListener {
		boolean over=false;
		Color overColor=Color.yellow;//new Color(230,230,250);
		public ImgButton(String str) {
			super(str);
			this.addMouseListener(this);
			//this.setBackground(backgroundColor);
		}
		
		public void paint(Graphics g){
			
			Image i = buttonImage.getImage();
			//Color c = this.getBackground();
			g.drawImage(i, 0,0, this.getWidth(),this.getHeight(),  backgroundColor, null);
			String str=this.getText(); 
			FontMetrics fm = g.getFontMetrics(buttonFont);
			g.setFont(buttonFont);
			int txtWid=fm.stringWidth(str);
			

			int left = this.getWidth()/2-txtWid/2;
			int bottom = this.getHeight()/2+fm.getHeight()/2-2;
			g.setColor(over ? Color.black: Color.DARK_GRAY);
			g.drawString(str,left+1,bottom+1);
			g.setColor(over ? overColor:Color.white);
			g.drawString(str,left+(over?-1:0),bottom+(over?-1:0));
			//super.paint(g);
			
		}
		
		public void mouseEntered(MouseEvent e) {over = true;}
		public void mouseExited(MouseEvent e) {over= false;}
		
		public void mouseClicked(MouseEvent e) {		}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		
	}
	
	public static class ColorField extends JTextField implements MouseListener {

		public ColorField() {
			this.setEnabled(false);
		}
		public Color getColor() {
			return this.getBackground();
		}
		public void setColor(Color c) {
			this.setBackground(c);
		}
		public void mouseClicked(MouseEvent e) {
			//ActionEvent ae= new ActionEvent(this,0,"");
		}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		
	}
}
