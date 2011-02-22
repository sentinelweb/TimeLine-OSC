/*
 * Created on 04-Oct-2007
 *
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.silicontransit.timeline.TimeLine;
import com.silicontransit.timeline.bean.FilterBean;
import com.silicontransit.timeline.model.ControlSettings;
import com.silicontransit.timeline.model.Event;
import com.silicontransit.timeline.model.TimeLineObject;
import com.silicontransit.timeline.model.TimeLineSet;
import com.silicontransit.timeline.window.LogWindow;

/**
 * @author munror
 *
 */
public class ExprUtil {
	TimeLine t=null;
	public TimeLineObject thisTimeLine=null;
	public Event thisEvent = null;
	public ExprUtil(TimeLine t) {
		this.t=t;
	}
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	  setValue: sets a value based on an expression.
//	  5/10/06: need to re-write this method to handle setting lists no doing now as dont want to risk new bugs 
//	  and can get away with object use ib getValueExpr. 
//	  would be good to set lists to target objects and possiblty set lists as offsets to targets ie if ($t:x:v = [1 2 3 4]) then setValueExpr($t:x:v:2,"a b c") makes $t:x:v = [1 2 a b c]
//	  and need to set dynamic objects from values in a similar way to getValueExpr()
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public Object setValueExpr(String expr, String value) {
		String[] params=value.split(" ");
		Vector v=new Vector();
		v.addAll(Arrays.asList(params));
		return setValueExpr( expr, v,true);
	}
	public Object setValueExpr(String expr, Vector value) {
		return setValueExpr( expr, value,false);
	}
	public Object setValueExpr(String expr, Vector value, boolean fromString) {// fromString denoted the value is provided as a String so values need to be parsed
		Object o=setValueExprMethod( expr, value, fromString );
		t.debugBean.addSetExpr( expr, value.toString() );
		return o;
	}
	public Object setValueExprMethod(String expr, Vector value, boolean fromString) {
		//System.out.println(expr);
		if (expr.indexOf("{")>-1) {expr=resolveExpr(expr);}
		String[] exprBreak=expr.substring(1,expr.length()).split(":");
		float fvalue=-999;
		String svalue="";
		if (value.size()>0) {
			svalue=value.get(0).toString();
			try {fvalue=Float.parseFloat( value.get(0).toString());} 
			catch (NumberFormatException n) { }
		}
		try {
			if ("n".equals(exprBreak[0])) {return null;}
			else if ("m".equals(exprBreak[0])) {//$M:devIndex:<part>_<ctlNum>:[vsot]:ctlIndex
				int devIndex=Integer.parseInt(exprBreak[1]);
				HashMap devMap=(HashMap) t.currentMidiControlMaps.get(t.midiDeviceNames[devIndex]);
				Vector v=(Vector)devMap.get(exprBreak[2]);
				//this just gets the the sleected ctl index and put it in a new vector so only mod that one.
				if (exprBreak.length==5) {Vector x=new Vector();x.add(v.get(Integer.parseInt(exprBreak[4])));v=x;}
				for (int i=0;i<v.size();i++) {
					ControlSettings c=(ControlSettings)v.get(i);
					switch(exprBreak[3].charAt(0)){
						case 'v':c.value=((fvalue*c.scale)+c.offset);break;
						case 's':c.scale=fvalue;break;
						case 'o':c.offset=fvalue;break;
						case 't':c.setType(svalue);break;
					}
				}
				return v;
			} else if ("M".equals(exprBreak[0])) {//$M:devIndex:mapName:<part>_<ctlNum>:[vsot]:ctlIndex
				int devIndex=Integer.parseInt(exprBreak[1]);
				HashMap devMap=(HashMap) t.allMidiControlMaps.get(t.midiDeviceNames[devIndex]);
				HashMap theMap=(HashMap) devMap.get(exprBreak[2]);
				Vector v=(Vector)theMap.get(exprBreak[3]);
				//this just gets the the sleected ctl index and put it in a new vector so only mod that one.
				if (exprBreak.length==6) {Vector x=new Vector();x.add(v.get(Integer.parseInt(exprBreak[5])));v=x;}
				for (int i=0;i<v.size();i++) {
					ControlSettings c=(ControlSettings)v.get(i);
					switch(exprBreak[4].charAt(0)){
						case 'v':c.value=((fvalue*c.scale)+c.offset);break;
						case 's':c.scale=fvalue;break;
						case 'o':c.offset=fvalue;break;
						case 't':c.setType(svalue);break;
					}
				}
				return v;
			}	else if ("s".equals(exprBreak[0])) {//$s:setName:[pl]
				TimeLineSet ts=t.getTimelineSet(exprBreak[1]);
				if (ts!=null) {
					switch(exprBreak[2].charAt(0)){
						case 'p':t.playTimeLines(ts.getSet(),"p");break;
						case 'l':t.playTimeLines(ts.getSet(),"l");break;
						case 'w':
								for (Iterator iter = ts.getSet().iterator();		iter.hasNext();) {
									TimeLineObject to = (TimeLineObject) iter.next();
									to.pitch=fvalue;
								}
								break;
					}
				}
			}	else if ("y".equals(exprBreak[0])) {//$y:fitlerTrig:[filterTgtExpr]:[pl]
				Vector v=(Vector)t.filters.get(exprBreak[1]);
				String filterTgtExpr = null;
				String act = exprBreak[2];
				if (exprBreak[2].length()!=1 ){
					filterTgtExpr = exprBreak[2];
					act = exprBreak[3];
				}
				for (int i=0;i<v.size();i++){
					FilterBean fb=(FilterBean)v.get(i);
					if (fb.getExpr().equals(filterTgtExpr) || filterTgtExpr==null){
						switch(exprBreak[2].charAt(0)){
							case 'a': fb.setActive(fvalue!=0);break;
						}
					}
				}
				
			} else if ("teg".indexOf(exprBreak[0])>-1) {
				TimeLineObject to=null;
				if ("current".equals(exprBreak[1])) {to=t.timeLineObject;}
				else if ("this".equals(exprBreak[1])) {to=thisTimeLine;}
				else {to=t.getTimeline(exprBreak[1]);}
				if (to==null) {throw new Exception();}
				if ("t".equals(exprBreak[0])) {//$t:timeLineId:[plxvwqbrc][:v_index]
					switch(exprBreak[2].charAt(0)){
					case 'p': // play
						//if (to.playMode=="p"){to.playMode="";}
						//else {
							t.playTimeline(to,"p");
						//}
						break;
					case 'l': // loop
						//if (to.playMode=="l"){to.playMode="";}
						//else {
							t.playTimeline(to,"l");	
						//}
						break;
					case 's':to.playMode="";break;
					case 'x':if ((fvalue>=0) && (fvalue<=to.timeLineLength)) {to.setPosAndNextEvent(Math.round(fvalue));} break; // set position
					case 'v': // set variables
						if (exprBreak.length==4) {
							to.setParameters(Event.getValueFromStr(svalue),Integer.parseInt(exprBreak[3]));
						}
						else {
							if (!fromString) {to.parameters=value;}// already vector
							else {// need to convert string values.
								for (int i=0;i<value.size();i++) {
									to.setParameters(Event.getValueFromStr(value.get(i).toString()), i);
								}
							}
						}
						break;
					case 'w':to.pitch=fvalue;break; // set pitch
					case 'q':to.quantize=Math.round(fvalue);break; // set quantization
					case 'b':to.beatLength=Math.round(fvalue/(float)to.quantize);break; //set beats
					case 'r':to.beatPerBar=Math.round(fvalue/(float)to.quantize/(float)to.beatLength);break; // set beats/bar
					case 'c':to.lastSelEvent=Math.round(fvalue);break;  // set 
					}
				} else if ("g".equals(exprBreak[0])) {// $g:timelIneId:groupId:[fa]
					Vector g = (Vector)to.groups.get(exprBreak[2]);
					if (g!= null) {
						switch(exprBreak[3].charAt(0)){
							case 'f':for (int i =0;i<g.size();i++) {
													Event e= (Event) g.get(i);
													if (e.value.size()==0 ) {	e.value=value;	}
													t.timeLineObject.playEvent(e);
													if (e.value==value) {e.value=new Vector();}
													if  (e.target!=null) {t.togglePlayMode(e.targetPlayMode,e.target);}
											}
											break;
							case 'a':for (int i =0;i<g.size();i++) {
													Event e= (Event) g.get(i);
													boolean activeState=false;
													if (i==0) {activeState=e.active;		}
													e.active=!activeState;
											}
											break;
						}
					}
				} else if ("e".equals(exprBreak[0])) {//$e:timelIneId:eventId | eventIndex:[vimf][:v_index]
					Event e;
					if ("current".equals(exprBreak[2])) {e=(Event)to.timeLine.get(to.lastSelEvent);}
					else if ("this".equals(exprBreak[2])) {e=thisEvent;}
					else {
						try {
							e=(Event) to.timeLine.get(Integer.parseInt(exprBreak[2]));
						} catch (NumberFormatException n) {
							e=(Event) to.getEvent(exprBreak[2]);
						}
					}
					if (e==null) {return null;}
					switch(exprBreak[3].charAt(0)){
					case 'v':	
						if (exprBreak.length==5) {
							// format: $e:tl:ev:v:col -  row 0 assumed
							e.value.set(Integer.parseInt(exprBreak[4]),Event.getValueFromStr(svalue));
						} else if (exprBreak.length==6) {
							// format: $e:tl:ev:v:col:row
							int row = Integer.parseInt(exprBreak[5]);
							int col =  Integer.parseInt(exprBreak[4]);
							
							Vector rowVec= e.value;
							if (row>0) {	rowVec = (Vector)e.extraValues.get(row);	}
							rowVec.set(col,Event.getValueFromStr(svalue));
						}
						else {
							if (!fromString) {e.value=value;}// already vector
							else {// need to convert string values.
								Vector v=new Vector();
								for (int i=0;i<value.size();i++) {
									v.add(Event.getValueFromStr(value.get(i).toString()));
								}
								e.value=v;
							}
						}
						break;
					case 'i':
						if (exprBreak.length==5) {
							 // $e:tl:ev:i:row
							 int index = Integer.parseInt(exprBreak[4]);
							 e.extraOscMessages.set(index , svalue);
							 if (index==0) { e.oscMsgName = svalue;  }
						}else { 
							 // $e:tl:ev:i - assume row 0
							  e.oscMsgName= svalue;
							  e.extraOscMessages.set(0 , svalue);
						 }
						break;
					case 'm': e.oscIndex=Integer.parseInt(svalue);e.oscP5=t.oscServers[e.oscIndex];break;
					case 'f':// if event has empty value then set sent value
						if (e.active) {
							if (e.value.size()==0 ) {	e.value=value;	}
							t.timeLineObject.playEvent(e);
							if (e.value==value) {e.value=new Vector();}
							if  (e.target!=null) {t.togglePlayMode(e.targetPlayMode,e.target);}
						}
						break;
					}
				}
			}
			else if ("f".equals(exprBreak[0])) {
				Object[] methodParams= value.toArray();
				String methodName=exprBreak[2];
				String modifier=(exprBreak.length>3)?exprBreak[3]:"";
				String[] objSplit=exprBreak[1].split("=");
				Object o=invokeMethod(	methodParams,	methodName,	modifier,	objSplit);
				return o;
			}
			else if ("gc".equals(exprBreak[0])) {System.gc();}
		} catch (Exception e) {t.showMessage(expr+" is invalid: "+e.getClass().getName().substring(e.getClass().getName().lastIndexOf(".")+1) +":"+e.getMessage());}
		return null;
	}
	
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	  getValue: gets a value based on an expression.
//	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	  public Object getValueExpr(String expr) {
		  Object o=getValueExprMethod(expr);
			t.debugBean.addGetExpr(expr,o!=null?o.toString():"Null");
		  return o;
	  }
	  public Object getValueExprMethod(String expr) {
		  //System.out.println(expr);
			if (expr.indexOf("{")>-1) {expr=resolveExpr(expr);}
		  String[] exprBreak=expr.substring(1,expr.length()).split(":");
		  int ctlvl=0;
		  try {
			  if ("m".equals(exprBreak[0])) {
				  ctlvl++;
				  int devIndex=Integer.parseInt(exprBreak[1]);
				  ctlvl++;
				  HashMap devMap=(HashMap)t.currentMidiControlMaps.get(t.midiDeviceNames[devIndex]);
				  Vector v=(Vector)devMap.get(exprBreak[2]);
				  ctlvl++;
				  ControlSettings c=(ControlSettings)v.get(Integer.parseInt(exprBreak[4]));
				  if (exprBreak.length<4) {return c;}
				  switch(exprBreak[3].charAt(0)){
					  	case 'v':return new Float(c.value);
					  	case 's':return new Float(c.scale);
					  	case 'o':return new Float(c.offset);
						case 't':return c.getType();
				  }
				  return null;
			} else if ("M".equals(exprBreak[0])) {
				ctlvl++;
				int devIndex=Integer.parseInt(exprBreak[1]);
				ctlvl++;
				HashMap devMap=(HashMap)t.currentMidiControlMaps.get(t.midiDeviceNames[devIndex]);
				HashMap theMap=(HashMap) devMap.get(exprBreak[2]);
				Vector v=(Vector)devMap.get(exprBreak[3]);
				ctlvl++;
				ControlSettings c=(ControlSettings)v.get(Integer.parseInt(exprBreak[5]));
				if (exprBreak.length<5) {return c;}
				switch(exprBreak[4].charAt(0)){
					  case 'v':return new Float(c.value);
					  case 's':return new Float(c.scale);
					  case 'o':return new Float(c.offset);
					  case 't':return c.getType();
				}
				return null;
		  	} else if ("s".equals(exprBreak[0])) {
				  TimeLineSet ts=t.getTimelineSet(exprBreak[1]);
				  if (ts!=null) {
					  switch(exprBreak[2].charAt(0)){
						 	case 'p':	t.playTimeLines(ts.getSet(),"p");break; // play
						  	case 'l':	t.playTimeLines(ts.getSet(),"l");break; // loop
							case 'c':	return new Float(ts.getSet().size()); // get set timeline count
							case 'w':for (Iterator iter = ts.getSet().iterator();	iter.hasNext();) {
													TimeLineObject to = (TimeLineObject) iter.next();
													return  new Float(to.pitch);
												}
												break;
					  }
				  }
			} else if ("y".equals(exprBreak[0])) {//$y:fitlerTrig:[filterTgt]:[pl]
				Vector v=(Vector)t.filters.get(exprBreak[1]);
				String filterTgtExpr = null;
				String act = exprBreak[2];
				if (exprBreak[2].length()!=1 ){
					filterTgtExpr = exprBreak[2];
					act = exprBreak[3];
				}
				for (int i=0;i<v.size();i++){
					FilterBean fb=(FilterBean)v.get(i);
					if (fb.getExpr().equals(filterTgtExpr) || filterTgtExpr==null){
						switch(exprBreak[2].charAt(0)){
							case 'a': return fb.isActive(); 
						}
					}
				}
				
			} else if ("teg".indexOf(exprBreak[0])>-1) {
				  ctlvl++;
				  TimeLineObject to=null;
				  if ("current".equals(exprBreak[1])) {to=t.timeLineObject;}
				  else if ("this".equals(exprBreak[1])) {to=thisTimeLine;}
				  else {to=t.getTimeline(exprBreak[1]);}
				  if (to==null) {throw new Exception();}
				  float result=-999;ctlvl++;
				  if (exprBreak.length<3) {return to;}
				  if ("t".equals(exprBreak[0])) {
					  switch(exprBreak[2].charAt(0)){
					  case 'p':result=("p".equals(to.playMode))?1:0;break;// play
					  case 'l':result=("l".equals(to.playMode))?1:0;break; // loop
					  case 's':result=("".equals(to.playMode))?1:0;break; // loop
					  case 'x':result=to.pos; break; // position
					  case 'v':   // value
					  	if (exprBreak.length>3) {
							ctlvl++;
							return resolveDeep( to.parameters.get(Integer.parseInt(exprBreak[3])));
					  	} else {
							return resolveDeep(to.parameters); 
					  	}
					  case 'q':result=to.quantize;break; // get quantization (ms)
					  case 'b':result=to.beatLength*to.quantize;break; // get beat length (ms)
					  case 'r':result=to.beatPerBar*to.beatLength*to.quantize;break;  // getbar length
					  case 'w':result=to.pitch;break;
					  case 'c':result=to.lastSelEvent;break;
					  }
				  }  else if ("g".equals(exprBreak[0])) {
					  Vector g = (Vector)to.groups.get(exprBreak[2]);
					  if (g!= null) {
						  switch(exprBreak[3].charAt(0)){
							  case 'f':for (int i =0;i<g.size();i++) {
													  	Event e= (Event) g.get(i);
														t. timeLineObject.playEvent(e);
										}
										break;
						  }
					  }
				  } else if ("e".equals(exprBreak[0])) {
					  Event e;
					  if ("current".equals(exprBreak[2])) {e=(Event)to.timeLine.get(to.lastSelEvent);}
					  else if ("this".equals(exprBreak[2])) {e=thisEvent;}
					  else {
						  try {
							  e=(Event) to.timeLine.get(Integer.parseInt(exprBreak[2]));
						  } catch (NumberFormatException n) {
							  e=(Event) to.getEvent(exprBreak[2]);
						  }
					  }
					  ctlvl++;
					  if (exprBreak.length<4) {return e;}
					  switch(exprBreak[3].charAt(0)){
						  case 'v':	// rm make this eval expression returned here.
							  if (exprBreak.length==5) {
							  		ctlvl++;
							  		// format: $e:tl:ev:v:col -  row 0 assumed
							  		return resolveDeep( e.value.get(Integer.parseInt(exprBreak[4])) );
							  }else  if (exprBreak.length==6) {
								  // format: $e:tl:ev:v:col:row
								  	int row = Integer.parseInt(exprBreak[5]);
									int col =  Integer.parseInt(exprBreak[4]);
									
									Vector rowVec= e.value;
									if (row>0) {	rowVec = (Vector)e.extraValues.get(row);	}
									return resolveDeep(rowVec.get(col));
							  }else {return resolveDeep( e.value );} // rm 7/10/7 :- was e.value.get(0);
							  
						  case 'i':
							  if (exprBreak.length==5) {
								  return e.extraOscMessages.get(Integer.parseInt(exprBreak[4]));
							  }else {return  e.oscMsgName;}
							 
						  case 'm':return new Integer(e.oscIndex);
						  case 'f':
							  to.playEvent(e);
							  break;
					  }
				  }
				  if (result!=-999) {return new Float(result);}
				  else return null;
			  } else if ("f".equals(exprBreak[0])) {
				  Object[] methodParams= {};
				  if (exprBreak.length>4) {
					  String params="";
					  for (int i=4;i<exprBreak.length;i++) { params+= (exprBreak[i] + ((i<exprBreak.length-1)?":":""));}
					  String paramsString = params.substring(1,params.length()-1);
					  if (!"".equals(paramsString)) {
						String[] paramSplit=paramsString.split(",");
						  methodParams= new Object[paramSplit.length];
						  for (int i=0;i<paramSplit.length;i++) {
							  if (paramSplit[i].indexOf("$")==0) {
								  methodParams[i]=getValueExpr(paramSplit[i]);//.replaceAll("\\|",":")
							  } else {
								  methodParams[i]=Event.getValueFromStr(paramSplit[i]);
							  }
						  }
					  }
				  }
				  String methodName=exprBreak[2];
				  String modifier=(exprBreak.length>3)?exprBreak[3]:"";
				  String[] objSplit=exprBreak[1].split("=");
				  Object o=invokeMethod(	methodParams,	methodName,	modifier,	objSplit);
				  return resolveDeep(o);
			  } else if ("T".equals(exprBreak[0])) {return t;}
				else if ("n".equals(exprBreak[0])) {return null;}
				else if ("gc".equals(exprBreak[0])) {System.gc();return null;}
		  } catch (Exception e) {
				t.showMessage(expr+"("+ctlvl+") is invalid - "+e.getClass().getName().substring(e.getClass().getName().lastIndexOf(".")+1) + ":"+e.getMessage(),LogWindow.TYPE_WARN);
		  }
		  return null;
	  }
	
//	////////////////////invokeMethod ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	/
	  /**
	   * the actual dynamic method invocation subroutine. actually the method is invoked in DynCompiler::invoke
	   * this sets/gets object into/from the global namespace hashmap if objSplit[0] != "".  
	   * @param methodParams the method parameters (or constructor parametrs if no method name supplied or 'n' modifier used)
	   * @param methodName the method name
	   * @param modifier modifier flags(currently only 'n' for new object)
	   * @param objSplit varName=packageAndClassName : if no varName(i.e. objSplit[0]="") then new instance is created.
	   * @return the return value of the method invokation or null if (n modifier | method have void retuen value | exception in method)
	   * @throws Exception
	   */
	  private Object invokeMethod(	Object[] methodParams,	String methodName,	String modifier,	String[] objSplit) throws Exception {
		  Object[] constructorParams=new Object[] {};
		  boolean invokeMethod=true;
		  if ("".equals(methodName)) {
			  invokeMethod=false;
			  constructorParams=methodParams;
		  }
		  if (!"".equals(objSplit[0])) {
			  Object o=t.dynamicObjects.get(objSplit[0]);
			  if (o==null || "n".equals(modifier)) {// create a new instance of the object.
				  	Object newObj=t.dynCompiler.getInstance(objSplit[1], constructorParams);
					t.dynamicObjects.put(objSplit[0], newObj);
				  	if (invokeMethod) {return t.dynCompiler.invoke(newObj,methodName, methodParams);}
			  } else {// just invoke the method
				  	if (invokeMethod) {return t.dynCompiler.invoke(o,methodName, methodParams);}
			  }
		  } else if (!"".equals(objSplit[1])) { //no variable name supplied just make a new one and execute the method on it once.
			  Object newObj=t.dynCompiler.getInstance(objSplit[1], constructorParams);
			  if (invokeMethod) {return t.dynCompiler.invoke(newObj, methodName, methodParams);}
		  }
		  return null;
	  }
//	///////// resolveExpr //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	  /** 
	   * this resloves subexpressions (delimited in expressions by {expr}) 
	   * be carefult with int & floats
	   * @param expr
	   * @return
	   */
	  public String resolveExpr(String expr) {// resolve a subExpression (delimited by {$...})
		  int start=expr.indexOf("{");
		  if (start==-1) {return expr;}
		  int end=expr.lastIndexOf("}");
		  if ( end==-1) {return expr;}
		  String resExpr=expr.substring(start+1,end);
		  if (expr.indexOf("{")>-1) {resExpr=resolveExpr(resExpr);}
		  Object result=getValueExpr(resExpr);
		  if (result==null) {			return expr;		}
		  String outExpr="";
		  if (start>0) {outExpr=expr.substring(0,start);}
		  outExpr+=result.toString();
		  if (end<expr.length()-1) {outExpr+=expr.substring(end+1,expr.length());}
		  return outExpr;
	  }
//	///////// resolve Deep //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// this resolves expression in the target data (only really applies to timeline parameters and event values) if a result is a vector 
//   then the content is at that point into the list. 
// TODO - detect cycles in resolution.
//	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	  	/**this resolves expressions in the target data (only really applies to timeline parameters and event values) if a result is a vector 
	  	 * then the content is at that point into the list. 
	  	 * @param object
	  	 * @return
	  	 */
	  	private Object resolveDeep (Object object) {
	  		if (object instanceof String) {
	  			boolean resolved=false;
	  			while (!resolved) {
		  			String textexpr=(String) object;
					if (textexpr.startsWith("$")) {object=getValueExpr(textexpr);}
		  			else {return object;}
		  			if (object instanceof Vector) {return resolveDeep ( (Vector )object );}
		  			else if (object instanceof String) {resolved=((String) object).startsWith("$"); }
		  			else {resolved=true;}
	  			}
	  		} else if (object instanceof Vector) {return resolveDeep ( (Vector) object );}
	  		return object;
	  	}
	  	
		private Vector resolveDeep ( Vector input ) {
			Vector output=new Vector();
	  		for (int i=0;i<input.size();i++) {
				Object o = resolveDeep(input.get(i)) ;
				if (o instanceof Vector) {output.addAll((Vector)o);}
				else {output.add(o);}
	  		}
	  		return output;
		}
		
		public void setThis(TimeLineObject to , Event e) {
			this.thisTimeLine=to;
			this.thisEvent=e;
		}
		public void clearThis() {
			this.thisTimeLine=null;
			this.thisEvent=null;
		}
}
