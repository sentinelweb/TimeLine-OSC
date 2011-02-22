
package com.silicontransit.timeline.disp;
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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Vector;


public abstract class DisplayObject {
	protected HashMap builtImageCache=new HashMap();
	protected int top;
	protected int left;
	protected int width;
	protected int height = 0 ;
	private Color background = Color.BLACK;
	protected BufferedImage img;
	protected Graphics2D g2;
	protected Color textColor=Color.WHITE;
	protected Vector boundsArray=new Vector();
	public Font thisFont = new Font("Arial", Font.PLAIN, 10);
	public FontMetrics thisFontMetrics;// = new Font("Arial", Font.PLAIN, 10);
	
	public DisplayObject(int w,int h,int l,int t) {
		this.width=w;
		this.height=h;
		this.top=t;
		this.left=l;
		
		this.img = new BufferedImage(w,h,BufferedImage.TYPE_INT_BGR);
		//g2.setFont(thisFont);
		this.g2 = (Graphics2D)img.getGraphics();
		thisFontMetrics = g2.getFontMetrics(thisFont);
		
	}
	//public abstract PImage getImage();
	
	public void cache(String key){		builtImageCache.put(key,this.img);	}
	public boolean isCached(String key){return (builtImageCache.get(key)!=null);}
	public Image getCached(String key) {		return (Image) builtImageCache.get(key);	}
	public void clearCached(String key) {builtImageCache.remove(key);}

	public void clearCache() {builtImageCache=new HashMap();}
	
	protected Graphics2D getG2() {		return g2;	}
	public Image getImg() {		return img;	}
	protected void setG2(Graphics2D graphics2D) {		g2 = graphics2D;	}
	protected void setImg(BufferedImage image) {		img = image;	}
	public Color getTextColor() {		return textColor;	}
	public void setTextColor(Color color) {		textColor = color;	}
	
	public int getLeft() {		return left;	}
	public int getTop() {		return top;	}
	public int getWidth() {		return width;	}
	public int getHeight() {		return height;	}
	
	public void setHeight(int i) {		height = i;	}
	public void setLeft(int i) {		left = i;	}
	public void setTop(int i) {		top = i;	}
	public void setWidth(int i) {		width = i;	}
	

//	public void loadCached(String key){
//		this.img = new BufferedImage(this.width,this.height,BufferedImage.TYPE_INT_BGR);
//		this.g2 = (Graphics2D)img.getGraphics();
//		g2.drawImage(getCached(key), 0,0, null);
//	}
	
	public void clearImage() {
		this.img = new BufferedImage(this.width,this.height>0?this.height:1,BufferedImage.TYPE_INT_BGR);
		this.g2 = (Graphics2D)img.getGraphics();
		g2.setFont(thisFont);
		g2.setColor(this.background);
		g2.fillRect(0,0,this.width,this.height);
	}

	protected void text(String text,int x,int y) {
		text(text,x,y,null);
	}
	
	protected void text(String text,int x,int y,Color c) {
		text(text,x,y,c,this.g2);
	}
	protected void text(String text,int x,int y,Color c,Graphics2D g2) {
		g2.setFont(thisFont);
		Color current = g2.getColor();
		if (c==null) {c=textColor;}
		g2.setColor(c);
		g2.drawString( text, x, y );
		g2.setColor(current);
	}
	
	protected void clearBoundedObjects() {
		boundsArray.clear();
	}
	protected void addBoundedObject(Object o,int left,int top,int wid,int hgt) {
		addBoundedObject( o, left, top, wid, hgt, null);
	}
	
	protected void addBoundedObject(Object o,int left,int top,int wid,int hgt,String tooltip) {
		BoundedObject bo = new BoundedObject();
		bo.top=top;
		bo.left=left;
		bo.bottom=top+hgt;
		bo.right=left+wid;
		bo.w=wid;
		bo.h=hgt;
		bo.boundedObject=o;
		bo.tooltip = tooltip;
		boundsArray.add(bo);
	}
	
	public int[] getBoundObjectInsideCoord(Object o, int x, int y){
		int[] pos=new int [2];
		BoundedObject bo=null;
		for (int i=0;i<boundsArray.size();i++) {
			bo = (BoundedObject)boundsArray.get(i);
			if (bo.boundedObject==o) {break;}
		}
		pos[0]=x-this.getLeft()-bo.left;
		pos[1]=y-this.getTop()-bo.top;
		return pos;
	}
	
	public String checkForTooltipOnScreen(int x,int y){
		BoundedObject o = checkForObjectAt(x-left, y-top);
		if (o!=null) return o.tooltip;
		else return null; 
	}
	
	public Object checkForObjectOnScreen(int x,int y){
		BoundedObject o = checkForObjectAt(x-left, y-top);
		if (o!=null) return o.boundedObject;
		else return null; 
	}
		
	protected BoundedObject checkForObjectAt(int x,int y){
		Vector foundObjects=new Vector();
		for (int i=0;i<boundsArray.size();i++) {
			BoundedObject bo = (BoundedObject)boundsArray.get(i);
			if (bo.top<y && bo.bottom>y && bo.left<x && bo.right>x) {
				foundObjects.add(bo);
			}
		}
		// if more than one object found then return the one whose middle closest to the clickpoint
		int smallestDistSquared=1000;//start high
		BoundedObject  smallesDistObj=null;
		for (int i=0;i<foundObjects.size();i++) {
			BoundedObject bo = (BoundedObject)foundObjects.get(i);
			int midx=bo.left+bo.w/2 - x ;
			int midy=bo.top+bo.h/2 - y;
			int distSquared=midx^2+midy^2;
			if (smallestDistSquared>distSquared) {
				smallestDistSquared=distSquared;
				smallesDistObj=bo;//.boundedObject;
			}
		}
		return smallesDistObj;
	}
	
	
	protected class BoundedObject{
		public int top;
		public int left;
		public int bottom;
		public int right;
		public int w;
		public int h = 0 ;
		public Object boundedObject;
		String tooltip = null;
	}
	
	public boolean inside(int x,int y) {
		return (top<y && top+height>y && left<x && left+width>x);
	}

	protected void  drawButton(int x, int y, int w , int h, String text, boolean fill, String tooltip, Object bounded, Color txtCol, Color  borderCol) {
		g2.setColor(borderCol);
		if (fill) {g2.fillRect(x,y,w,h);	}
		else {g2.drawRect(x,y,w,h);}
		if (text!=null && !"".equals(text)) {
			int txtWid=thisFontMetrics.stringWidth(text);
			int txtHgt=thisFontMetrics.getHeight();
			int textY=y+h-(h-txtHgt)/2;
			if (textY>h) {textY=y+h-1;}
			text(text, x+(w-txtWid)/2, textY , txtCol);
		}
		addBoundedObject(bounded, x, y, w, h, tooltip);
	}
}
