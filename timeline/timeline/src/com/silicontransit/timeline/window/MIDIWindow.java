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
import java.awt.Graphics;
import java.awt.Image;

import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

import promidi.Controller;
import promidi.MidiOut;
import promidi.Note;
import promidi.ProgramChange;

import com.silicontransit.timeline.TimeLine;
import com.silicontransit.timeline.bean.MIDIControlCfgBean;
import com.silicontransit.timeline.bean.MIDIDeviceCfgBean;
import com.silicontransit.timeline.disp.MIDIDraw;
import com.silicontransit.timeline.model.ControlSettings;

public class MIDIWindow extends JPanel{
	private static final int WIN_WIDTH = 640;
	private static final int WIN_HEIGHT = 600;
	private MIDIDraw midiDraw;//WIN_WIDTH,WIN_HEIGHT
	public JFrame thisFrame;
	private TimeLine t;
	private boolean selChanged=true;
	
	Object pressedObject=null;
	Object hoveredObject=null;
	public MIDIWindow(TimeLine t) {
		thisFrame=new JFrame();
		thisFrame.setSize(WIN_WIDTH,WIN_HEIGHT+34); // set window to appropriate size (for its elements)
		//thisFrame.setLocation(new Point(0,150));thisFrame.
		thisFrame.setVisible(false); // usual step to make frame visible
		thisFrame.setTitle("MIDI");
		this.t=t;
		midiDraw=new MIDIDraw(WIN_WIDTH,WIN_HEIGHT,0,0,t);
		thisFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				evt.getWindow().setVisible(false);
			}
		});
		//this.addKeyListener(new MIDIKeyListener());
		thisFrame.addKeyListener(new MIDIKeyListener());
		this.addMouseWheelListener(new MIDIWinMouseWheelAdapter());
		MIDIWinMouseInputAdapter mouseEvents=new MIDIWinMouseInputAdapter();
		this.addMouseListener( mouseEvents);
		this.addMouseMotionListener(mouseEvents);
		thisFrame.add(this);
	}
	public void setVisible(boolean vis) {
		thisFrame.setVisible(vis);
		if (vis) {setMidiDevice(midiDraw.getCurrMIDIDev());}
	}
	public boolean getVisible() {
		return thisFrame.isVisible();
	}
	public void paint(Graphics g) {
		if (thisFrame.isVisible() ){
			Image img=midiDraw.getImage();
			g.drawImage(
					img,
					midiDraw.getLeft(),
					midiDraw.getTop(), 
					Color.black,
					null
			 );
			if (selChanged) {
				thisFrame.setSize(img.getWidth(null),img.getHeight(null)+25);
				selChanged=false;
			}
			this.revalidate();
		}
	}
	private class MIDIKeyListener implements  KeyListener{
		public void keyTyped(KeyEvent e) {
			t.keyReleased(e);
		}
		public void keyPressed(KeyEvent e) {
			t.handleKeyEvent(e);
//			t.key=e.getKeyChar();
//			t.keyCode=e.getKeyCode();
//			t.keyPressed();
		}
		public void keyReleased(KeyEvent e) {	}
	}
	private class MIDIWinMouseWheelAdapter implements MouseWheelListener {
		private long lastTime=-1;
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (hoveredObject!=null) {
				if (hoveredObject instanceof MIDIControlCfgBean ) {
					long thisTime=System.currentTimeMillis();
					MIDIControlCfgBean mccb =( MIDIControlCfgBean)hoveredObject;
					if (mccb.getType()!= MIDIControlCfgBean.TYPE_BUTTON) {
						int multiplier=1;
						if ((thisTime-lastTime)<100) {multiplier=3;	}
						if ((thisTime-lastTime)<50) {multiplier=5;	}
						if ((thisTime-lastTime)<25) {multiplier=10;	}
						
						mccb.setWinValue(mccb.getWinValue()-(e.getWheelRotation()*multiplier));
						if (mccb.getWinValue()>127) {mccb.setWinValue(127);	}
						if (mccb.getWinValue()<0) {mccb.setWinValue(0);	}
						System.out.println(""+mccb.getWinValue());
					} else {
						if (e.getWheelRotation()<=0) {mccb.setWinValue(127);	}
						if (e.getWheelRotation()>0) {mccb.setWinValue(0);	}
					}
					Controller c=new  Controller(midiDraw.part, mccb.getControlNum() , mccb.getWinValue());
					t.midiUtil.controllerIn(c,midiDraw.getCurrMIDIDev());
					t.midiUtil.sendMIDIOut(midiDraw.getCurrMIDIDev(),c);
					lastTime=thisTime;
				}
			}
		}
	}
		
	private class MIDIWinMouseInputAdapter extends MouseInputAdapter{
		private int isDragging=-1;
		public MIDIWinMouseInputAdapter() {
			
		}
		public void mouseClicked(MouseEvent e) {
			//Object o=midiDraw.checkForObjectOnScreen(e.getX(),e.getY());
			if (pressedObject!=null) {
				if (pressedObject instanceof String) {
					String s=(String )pressedObject;
					String device = s.substring(1);
					System.out.println("click:"+s);
					
					if (s.equals(MIDIDraw.COPY)) {
						if (midiDraw.selTop>-1) {
							StringSelection ss=new StringSelection("width=\""+midiDraw.selWidth+"\" "+"height=\""+midiDraw.selHeight+"\" "+"top=\""+midiDraw.selTop+"\" "+"left=\""+midiDraw.selLeft+"\" ");
							 thisFrame.getToolkit().getSystemClipboard().setContents(ss,ss);
						}
					}
					else if (s.equals(MIDIDraw.PARTDN)) {
						if (midiDraw.part>0)  {
							midiDraw.part--;
							//sendMidiProgChange(midiDraw.part);
						}
					}	else if (s.equals(MIDIDraw.PARTUP)) {
						if (midiDraw.part<15)  {
							midiDraw.part++;
							//sendMidiProgChange(midiDraw.part);
						}
					}else if (s.equals(MIDIDraw.OCTAVEDN)) {
						if (midiDraw.octave>-4)  midiDraw.octave--;
					}	else if (s.equals(MIDIDraw.OCTAVEUP)) {
						if (midiDraw.octave<4)  midiDraw.octave++;
					} else if (s.equals(MIDIDraw.VIEW_SETTINGS)||s.equals(MIDIDraw.VIEW_NAME)||s.equals(MIDIDraw.VIEW_DATA)||s.equals(MIDIDraw.VIEW_OSC)||s.equals(MIDIDraw.VIEW_NUM)||s.equals(MIDIDraw.VIEW_CONTROL)) {
						if (midiDraw.viewFields.contains(s)) {
							midiDraw.viewFields.remove(s);
						} else {midiDraw.viewFields.add(s);}
					}else if (s.indexOf("=")==0) {
						System.out.println("map clicmk:"+s+":"+device);
						midiDraw.setCurrMapEdit(s);
						if (e.getButton()==MouseEvent.BUTTON3) {//edit map ide
							t.inputMode="h";
							t.inputStr=device;
							t.cursorPos=t.inputStr.length();
						} else if (e.getButton()==MouseEvent.BUTTON2) {// delete map
							t.inputMode="H";
						}else{
							if (s.charAt(1)=='+') {t.midiUtil.newControllerMap();}
							else {
								t.currentMidiControlMaps.put(midiDraw.getCurrMIDIDev(),((HashMap)t.allMidiControlMaps.get(midiDraw.getCurrMIDIDev())).get(device));
								t.currentMidiControlMapIds.put(midiDraw.getCurrMIDIDev(),device);
								t.midiUtil.sendMIDIMapOut(midiDraw.getCurrMIDIDev());
								sendMIDIMapDraw(midiDraw.getCurrMIDIDev());
							}
						}
					}else if (s.indexOf("+")==0) {
						midiDraw.setCurrMapEdit(s);
						if (e.getButton()==MouseEvent.BUTTON3) {//edit map ide
							t.inputMode="h";
							t.inputStr=device;
							t.cursorPos=t.inputStr.length();
						} else if (e.getButton()==MouseEvent.BUTTON2) {// delete map
							t.inputMode="H";
						}else{
							if (s.charAt(1)=='+') {t.midiUtil.newNoteMap();}
							else {
								t.currentMidiNoteMaps.put(midiDraw.getCurrMIDIDev(),((HashMap)t.allMidiNoteMaps.get(midiDraw.getCurrMIDIDev())).get(device));
								t.currentMidiNoteMapIds.put(midiDraw.getCurrMIDIDev(),device);
								
							}
						}
					}
					else if (s.indexOf("#")==0) {				
						int noteNum=Integer.parseInt(device);
						if (e.getButton()==MouseEvent.BUTTON3) {
							if ("+".equals(t.inputMode)) {
								t.midiUtil.updateMidiNoteStr(noteNum,midiDraw.part);
							}else {
								String oscMessage=null;
								String[][] devNoteMap=(String[][])t.currentMidiNoteMaps.get(midiDraw.getCurrMIDIDev());
								if (devNoteMap!=null) {
									oscMessage=devNoteMap[midiDraw.part][noteNum];
								}
								t.inputMode="+";
								t.inputStr=midiDraw.part+" "+device+" "+device+" "+t.timeLineObject.oscIndex+" "+(oscMessage!=null?oscMessage.substring(oscMessage.indexOf(",")+1):"/");
								t.cursorPos=t.inputStr.length();
							}
						} else if (e.getButton()==MouseEvent.BUTTON2) {
							
						} else {
							Note n=new Note(midiDraw.part,noteNum,62);
							t.midiUtil.noteOn(n,midiDraw.getCurrMIDIDev());
						}
					} else if (s.indexOf("~")==0) {// add control setting
						t.currentMIDIInputDevice=midiDraw.getCurrMIDIDev();
						t.inputMode="=";
						t.inputStr=midiDraw.part+" "+device+" "+t.timeLineObject.oscIndex +" / 1 0 n";
						t.cursorPos=t.inputStr.length();
					}
					else if (s.indexOf("¬")==0) {// sel the midi device
						setMidiDevice(device);
					}// midi click
				} else if (pressedObject instanceof ControlSettings) {
					ControlSettings cs =( ControlSettings)pressedObject;
					t.currentMIDIInputDevice=midiDraw.getCurrMIDIDev();
					t.propHelper.setObject(cs, "midiCtl");
					t.inputMode="=";
					t.inputStr=cs.toString();
					t.cursorPos=t.inputStr.length();
				}
//				else if (o instanceof MIDINoteCfgBean) {
//					MIDINoteCfgBean mccb =( MIDINoteCfgBean)o;
//					t.inputMode="+";
//					t.inputStr=midiDraw.part+" "+mccb.getControlNum()+" "+t.timeLineObject.oscIndex +"/ 1 0 n";
//					t.cursorPos=t.inputStr.length();
//				}
			}
		}
		
		//this doesnt seem to work as yet play withit more
		private void sendMidiProgChange(int part) {
			MidiOut mo=(MidiOut)t.midiOutputs.get(midiDraw.getCurrMIDIDev());
			if (mo!=null) {
				mo.sendProgramChange(new ProgramChange(0,part));
			}
		}
		
		
		public void mousePressed(MouseEvent e) {
			pressedObject=midiDraw.checkForObjectOnScreen(e.getX(),e.getY());
			isDragging=e.getButton();
			if (e.getButton()==MouseEvent.BUTTON2 && e.getY()>MIDIDraw.TOP_BAR_OFFSET) {
				// middle click sq gets area dimensions
				midiDraw.selTop=e.getY()-MIDIDraw.TOP_BAR_OFFSET;
				midiDraw.selLeft=e.getX();
				midiDraw.selHeight=0;
				midiDraw.selWidth=0;
			} 
//			else if (pressedObject instanceof ControlSettings) {
//				
//			}
			else if (isDragging==MouseEvent.BUTTON1  && (pressedObject instanceof MIDIControlCfgBean)) {
				midiDraw.dragPoint= e.getPoint();
			}
		}
		public void mouseDragged(MouseEvent e) {
//			if (isDragging==MouseEvent.BUTTON2 && e.getY()>MIDIDraw.TOP_BAR_OFFSET) {
//				midiDraw.selHeight=(e.getY()-MIDIDraw.TOP_BAR_OFFSET)-midiDraw.selTop;
//				midiDraw.selWidth=e.getX()-midiDraw.selLeft;
//			}
			midiDraw.curX=e.getX();
			midiDraw.curY=e.getY();
		}
		private long lastTime=-1;
		public void mouseMoved(MouseEvent e) {
			
			midiDraw.curX=e.getX();
			midiDraw.curY=e.getY();
			long thisTime=System.currentTimeMillis();
			if (thisTime-lastTime>50) {
				hoveredObject=midiDraw.checkForObjectOnScreen(e.getX(),e.getY());
			} 
			lastTime=thisTime;
		}
		public void mouseReleased(MouseEvent e) {
			System.out.println("rel"+(e.getButton()==MouseEvent.BUTTON2)+":"+(e.getY()>MIDIDraw.TOP_BAR_OFFSET));
			if (e.getButton()==MouseEvent.BUTTON2 && e.getY()>MIDIDraw.TOP_BAR_OFFSET) {
				midiDraw.selHeight=(e.getY()-MIDIDraw.TOP_BAR_OFFSET)-midiDraw.selTop;
				midiDraw.selWidth=e.getX()-midiDraw.selLeft;
			}
//			else 
//			if (isDragging==MouseEvent.BUTTON1  && pressedObject instanceof MIDIControlCfgBean ) {
//				MIDIControlCfgBean mccb =( MIDIControlCfgBean)pressedObject;
//				mccb.setWinValue(mccb.getWinValue()+(int)(midiDraw.dragPoint.getY()-e.getY()));
//				System.out.println(""+mccb.getWinValue());
//				Controller c=new  Controller(midiDraw.part, mccb.getControlNum() , mccb.getWinValue());
//				t.midiUtil.controllerIn(c,midiDraw.getCurrMIDIDev());
//			}
			isDragging=-1;
			midiDraw.dragPoint=null;
		}
	}
	public String getCurrMIDIDev() {
		return midiDraw.getCurrMIDIDev();
	}
	
	public String getCurrMapEdit() {
		return midiDraw.getCurrMapEdit();
	}

	public void setCurrMapEdit(String currMapEdit) {
		midiDraw.setCurrMapEdit(currMapEdit);
	}
	
	public void setMidiDevice(String device) {
		midiDraw.setCurrMIDIDev(device);
		t.currentMIDIInputDevice=device;
		selChanged=true;
	}
	public void sendMIDIMapDraw(String device) {
		int index =-1;
		for (int i=0;i<t.midiOutDeviceNames.length;i++) {
			if (device.equals(t.midiOutDeviceNames[i])) {
				index =i; break;
			}
		}
		MIDIDeviceCfgBean mdcb=(MIDIDeviceCfgBean)t.midiDeviceConfigMaps.get(device);
		
		if (index >-1) {
				HashMap midiControlMap=(HashMap) t.currentMidiControlMaps.get(device);
				System.out.println("midiControlMap:"+midiControlMap.size());
				Iterator i = midiControlMap.keySet().iterator();
				while (i.hasNext()) {
					String next =(String) i.next();
					Vector v = (Vector)midiControlMap.get(next);
					System.out.println("v."+v +" - id:"+next);
					if (v!=null) {
						for (int j = 0;j<v.size();j++){
							ControlSettings cs = (ControlSettings)v.get(j);
							if (mdcb!=null) {
								MIDIControlCfgBean mccb=mdcb.getControl(cs.control);
								if (mccb!=null) {mccb.setWinValue(cs.midiValue);}
							}
						}
					}
				}
	
		}
	}
}
