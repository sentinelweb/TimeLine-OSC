package com.silicontransit.timeline.bean;
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
import java.util.TreeMap;
import java.util.Vector;

import oscbase.OscMessage;


public  class DebugBean {
	public static final  String SHOW_OSC="OSC";
	public static final  String SHOW_EXPR="EXPR";
	public static final  String SHOW_LOG="LOG";
	private TreeMap oscMessages = new TreeMap();
	private Vector oscq=new Vector();
	private TreeMap expressions = new TreeMap();
	private Vector expq=new Vector();
	private Vector log = new Vector();
	private int logDispLines=40;
	private String mode=SHOW_OSC;
	private boolean isDrawing=false;
	private boolean isDirty=true;
	
	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void addOscMessage(OscMessage osc,int oscPort) {
		isDirty=true;
		if (isDrawing) {oscq.add(new Object[] {osc.getMsgName()+" ["+oscPort+"]", osc.getArgs().toString()});}
		else {
			oscMessages.put(osc.getMsgName()+" ["+oscPort+"]", osc.getArgs().toString());
			if (oscq.size()>0) {
				for (int i=0;i<oscq.size();i++) {Object[] oscv=(Object[])oscq.get( i);oscMessages.put(oscv[0], oscv[1]);}
				oscq.clear();
			}
		}
		
	}
	public void addSetExpr(String expr,String value) {
		isDirty=true;
		Vector v=new Vector();
		v.add("<");
		v.add(value);
		//v.add(value);
		if (isDrawing) {expq.add(new Object[] {expr,v});}
		else {
			expressions.put(expr, v);
			if (expq.size()>0) {
				for (int i=0;i<expq.size();i++) {Object[] exprv=(Object[])expq.get( i);expressions.put(exprv[0], exprv[1]);}
				expq.clear();
			}
		}
	}
	public void addGetExpr(String expr,String value) {
		isDirty=true;
		Vector v=new Vector();
		v.add(">");
		v.add(value);
		if (isDrawing) {expq.add(new Object[] {expr,v});}
		else {
			expressions.put(expr, v);
			if (expq.size()>0) {
				for (int i=0;i<expq.size();i++) {Object[] exprv=(Object[])expq.get( i);expressions.put(exprv[0], exprv[1]);}
				expq.clear();
			}
		}
	}
	public void  addLog(String msg) {
		isDirty=true;
		log.add(msg);
	}
	public void clear(String clr) {
		isDirty=true;
		if (clr.equals(DebugBean.SHOW_OSC)){
			oscMessages.clear();
		} else if (clr.equals(DebugBean.SHOW_EXPR)) {
			expressions.clear();
		} else if (clr.equals(DebugBean.SHOW_LOG)) {
			log.clear();
		}
	}
	public void clear() {
		isDirty=true;
		oscMessages.clear();
		expressions.clear();
		log.clear();
	}
	
	public TreeMap getExpressions() {
		return expressions;
	}

	public void setExpressions(TreeMap expressions) {
		this.expressions = expressions;
	}

	public Vector getLog() {
		return log;
	}

	public void setLog(Vector log) {
		this.log = log;
	}

	public int getLogDispLines() {
		return logDispLines;
	}

	public void setLogDispLines(int logDispLines) {
		this.logDispLines = logDispLines;
	}

	public TreeMap getOscMessages() {
		return oscMessages;
	}

	public void setOscMessages(TreeMap oscMessages) {
		this.oscMessages = oscMessages;
	}

	public boolean isDrawing() {
		return isDrawing;
	}

	public void setDrawing(boolean isDrawing) {
		this.isDrawing = isDrawing;
	}
}
