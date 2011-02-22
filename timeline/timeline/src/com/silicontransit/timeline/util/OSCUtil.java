/*
 * Created on 04-Oct-2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
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
import java.util.Vector;

import oscbase.OscIn;
import oscbase.OscMessage;
import oscbase.OscP5;
import promidi.Controller;

import com.silicontransit.timeline.TimeLine;
import com.silicontransit.timeline.model.Event;

/**
 * @author munror
 *
 */
public class OSCUtil {
	TimeLine t=null;
	public OSCUtil(TimeLine t) {
		this.t=t;
	}
	
	public void initOsc() {
		t.oscServers=new OscP5[ t.oscServerPorts.length];
		for (int i = 0; i < t.oscServerPorts.length; i++) {
			t.host = t.oscServerHost[i];
			t.oscP5event = "oscEvent"; // in main Timeline calss as this extends PApplet
			t.oscServers[i] = new OscP5(
					t,
					t.host,
					t.oscServerPorts[i],
					t.oscServerRcPorts[i],
					t.oscP5event
			);
		}
	}
	
	public long lastRecEventTime=0;
		Event lastEvent=null;
		boolean inLastMsg=false;
		
		public void unpackMessage(OscIn oscIn,int server) {  
		
//			println(oscIn.getAddrPattern()+":"+oscIn.getData().length+":"+oscIn.getTypetag());
			Vector v=new Vector();
			Object[] o = oscIn.getData();
			for(int i=0;i<o.length;i++) {
//				println(i+""+o[i]);
				v.add(o[i]);
			}
		
			if ("/key".equals(oscIn.getAddrPattern())) {
				String keyInput="";
				if (oscIn.getTypetag().charAt(0)=='s') {  	keyInput =oscIn.getString(0);}
				else if (oscIn.getTypetag().charAt(0)=='i') {  	 	keyInput =""+oscIn.getInt(0);
				}
				int keyState=oscIn.getInt(1);
				if (keyInput.trim().length()!=1) {
					t.key=TimeLine.CODED;
					if (keyInput.indexOf("Control")==0) {t.keyCode=TimeLine.CONTROL;}
					else if (keyInput.indexOf("Shift")==0) {t.keyCode=TimeLine.SHIFT;}
					else if (keyInput.indexOf("Alt")==0) {t.keyCode=TimeLine.ALT;}
					else  if (keyInput.indexOf("\t")==0) {t.keyCode=TimeLine.TAB;}
					else if (keyInput.indexOf("\r")==0) {t.keyCode=TimeLine.ENTER;}
					else if (keyInput.indexOf("Left")==0) {t.keyCode=TimeLine.LEFT;}
					else if (keyInput.indexOf("Right")==0) {t.keyCode=TimeLine.RIGHT;}
					else if (keyInput.indexOf("Down")==0) {t.keyCode=TimeLine.DOWN;}
					else if (keyInput.indexOf("Back")==0) {t.keyCode=TimeLine.BACKSPACE;}
					else if (keyInput.indexOf("Up")==0) {t.keyCode=TimeLine.UP;}
					else if (keyInput.indexOf(" ")==0) {t.key=' ';}
				} else {
					t.key=keyInput.charAt(0);;
					t.keyCode=(int)keyInput.charAt(0);
				}
				TimeLine.println("keyRecieved:"+t.key+":"+t.keyCode);
				if (keyState==1) {t.keyPressed();}
				else {t.keyReleased();}
			} if ("/midi".equals(oscIn.getAddrPattern())) {
				Controller ctlMsg=new Controller(oscIn.getInt(0),oscIn.getInt(1),oscIn.getInt(2),oscIn.getInt(3));
				t.midiUtil.controllerIn(ctlMsg,true);
			}   else if (t.timeLineObject.playMode.equals("r") ) {//&& !"/key".equals(oscIn.getAddrPattern())	
				System.out.println(t.timeLineObject.pos+":"+lastRecEventTime+":"+t.recordGranularity);
				if (t.timeLineObject.pos>=(lastRecEventTime+t.recordGranularity) || t.recordGranularity==-1) {
					String msg=oscIn.getAddrPattern();
					if ("/in1".equals(msg)) {// add values to end. there was som prob receiving lists more than 4 items in length 
						lastEvent.value.addAll(v);
						return;
					}
					if ("/in".equals(msg)) {
						Event lastEvent=t.getLastEvent();
						if (lastEvent!=null) msg=lastEvent.oscMsgName;
					}
					if  (t.recordInput.equals( "") ||  t.recordInput.indexOf(msg)==0) {
						Event newEvent=new Event();
					
						newEvent.value=v;
						newEvent.eventTime=(t.timeLineObject.pos/t.timeLineObject.quantize)*t.timeLineObject.quantize;
						newEvent.active=true;
						newEvent.oscIndex=t.timeLineObject.oscIndex;
						newEvent.oscP5=t.oscServers[newEvent.oscIndex];
						newEvent.oscMsgName=msg;
						t.timeLineObject.timeLine.add(newEvent);
						lastRecEventTime=newEvent.eventTime;
						lastEvent=newEvent;
						t.timeLineObject.rebuildTimeLine();
					}
				}
			} else {
				//oscServers[(1-server)].call(oscIn);
				for (int i=0;i<t.oscServers.length;i++) {
					if (i==server) {continue;}
					Event e=new Event();
					e.oscMsgName=oscIn.getAddrPattern();
					e.value.addAll(oscIn.getDataList());
					e.oscIndex=i;
					e.oscP5=t.oscServers[i];
					simpleOscMessage( e);
				}
			}
		}
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	  simpleOscMessage(Event e)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	//	  
		public void simpleOscMessage(Event e) {
			Vector v = simpleOscMessagePlay( e);
			int msgsLen=Math.max(e.extraOscMessages.size(), e.extraValues.size());
			if (msgsLen>1) {
				Event ep=e.getCopy(null);
				for (int i=1; i<msgsLen; i++) {
					ep.oscMsgName=(e.extraOscMessages.size()>i)?(String)e.extraOscMessages.get(i):"";
					ep.value=(e.extraValues.size()>i)?new Vector((Vector)e.extraValues.get(i)):new Vector();
					for (int j=0;j<ep.value.size();j++) {
						Object obj= ep.value.get(j);
						if (obj instanceof String) {
							String val=((String)obj);
							for (int k=0;k<v.size(); k++) {
								if ( val.equals("$"+k)) { obj=v.get(k); break;}
								else { val = val.replaceAll("\\$"+k, v.get(k).toString()); obj=val;}
								
							}
							ep.value.set(j , obj);
						}
					}
					v = simpleOscMessagePlay(ep);
				}
			}
		}
		
		public Vector simpleOscMessagePlay(Event e) {
		  	if (e.oscMsgName.startsWith("$")) {// note that this will not work for lists of $ values. only for single $values.
				  // 08/10/06: eval all expr to string list to send to setValueExpr - i.e. can send object to setValueExpr
				  // String value=e.getValueStr();
				  // String[] args=expr.split(" ");
				  Vector value=new Vector();
				  for (int i=0;i<e.value.size();i++) {
					  Object o=e.value.get(i);
					  if (o instanceof String && ((String)o).startsWith("$")) {
						  
						  Object val=t.exprUtil.getValueExpr((String)o); 
						  if (val!=null) {	
							  if (val instanceof Vector) {
								value.addAll((Vector)val);
							  }
							  else {value.add(val);} //						  +=val.toString()+" ";
					  		  //System.out.println((String)o+" = " +val.toString());
						  } else {
							  t.showMessage((String)o+" = null" );
						  }
					 } else {
						 value.add(o);//o.toString()+" ";
					 }
				  }
				  t.exprUtil.setValueExpr(e.oscMsgName,value);//.trim()
				  e.lastPlayed=System.currentTimeMillis();
				  return value;
		  	}
		  	String oscMsgName = e.oscMsgName;
		  	if (oscMsgName.indexOf("{")>-1) {oscMsgName=t.exprUtil.resolveExpr(oscMsgName);}
		  	e.oscP5 = t.oscServers[e.oscIndex];
	  	   	OscMessage oscMsg = e.oscP5.newMsg(oscMsgName);
	      	if (e.value.size()==0) {oscMsg.add(0);}
	      	else {
	      		for (int i=0;i<e.value.size();i++) {
	      			Object value=e.value.get(i);
				  	if (value instanceof String && ((String) value).startsWith("$")) {
				  		String expr=(String)value;
				  		value=t.exprUtil.getValueExpr(expr);
				  		//System.out.println(expr+" = " +value);
				  	} 
	      			if (value!=null) {
	      				if (value instanceof Vector) {
								oscMsg.add(((Vector)value).toArray());
	      				}
	      				else {oscMsg.add(new Object[] {value});} 
	      			}
	      		}
	      	}
		    if (oscMsgName!=null && oscMsgName.startsWith("/")) {
		 		e.oscMessage=oscMsg;
		        e.lastPlayed=System.currentTimeMillis();
		       // System.out.println("playing:"+e.oscMessage.getMsgName() +":"+e.oscMessage.getArgs().toString());
		        // send via the oscP5 bundle
		        e.oscP5.sendMsg(e.oscMessage);
		        t.playFilter(e.oscMessage);
		        t.debugBean.addOscMessage(e.oscMessage,e.oscP5.getPort());
		  	}
	        return new Vector(oscMsg.getArgs());
	  }
	 
	 public void simpleOscMessage(String msg,Vector val, int oscIndex) {
		 Event e = new Event();
		 e.oscIndex = oscIndex;
		 e.oscP5 = t.oscServers[e.oscIndex];
		 e.oscMsgName = msg;
		 e.value=val;
		 simpleOscMessagePlay(e);
		 
	 }
}
