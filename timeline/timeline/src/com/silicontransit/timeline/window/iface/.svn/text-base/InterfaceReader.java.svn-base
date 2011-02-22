package com.silicontransit.timeline.window.iface;

import java.io.File;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.silicontransit.timeline.model.Event;
import com.silicontransit.timeline.util.XMLUtil;

public class InterfaceReader {
	XMLUtil xu = new XMLUtil();
	static final String PARAM_TEXT_TITLE = "title";
	//private static final String PARAM_TEXT_LABEL = "label";
	static final String PARAM_TEXT_TOOLTIP= "tooltip";
	static final String PARAM_TEXT_WIDTH= "width";
	static final String PARAM_TEXT_VALUE = "value";
	static final String PARAM_TEXT_OUTPUT = "output";
	static final String PARAM_TEXT_INPUT = "input";
	static final String TAG_TEXT= "text";
	static final Object[][] PARAMS_TEXT= {
		 {PARAM_TEXT_TOOLTIP, String.class},
		 {PARAM_TEXT_WIDTH, Integer.class},
		 {PARAM_TEXT_TITLE, String.class},
		 {PARAM_TEXT_VALUE, Object.class},
		 {PARAM_TEXT_OUTPUT, String.class},
		 {PARAM_TEXT_INPUT, String.class}
	};
	
	static final String PARAM_FILE_TITLE = "title";
	//private static final String PARAM_FILE_LABEL = "label";
	static final String PARAM_FILE_TOOLTIP= "tooltip";
	static final String PARAM_FILE_WIDTH= "width";
	static final String PARAM_FILE_VALUE = "value";
	static final String PARAM_FILE_OUTPUT = "output";
	static final String PARAM_FILE_INPUT = "input";
	static final String TAG_FILE= "file";
	static final Object[][] PARAMS_FILE= {
		 {PARAM_FILE_TOOLTIP, String.class},
		 {PARAM_FILE_WIDTH, Integer.class},
		 {PARAM_FILE_TITLE, String.class},
		 {PARAM_FILE_VALUE, Object.class},
		 {PARAM_FILE_OUTPUT, String.class},
		 {PARAM_FILE_INPUT, String.class}
	};
	
	static final String PARAM_BUTTON_TOOLTIP= "tooltip";
	static final String PARAM_BUTTON_WIDTH= "width";
	static final String PARAM_BUTTON_TITLE = "title";
	static final String PARAM_BUTTON_VALUE = "value";
	static final String PARAM_BUTTON_OUTPUT = "output";
	static final String TAG_BUTTON= "button";
	static final Object[][] PARAMS_BUTTON = {
		 {PARAM_BUTTON_TOOLTIP, String.class},
		 {PARAM_BUTTON_WIDTH, Integer.class},
		 {PARAM_BUTTON_TITLE, String.class},
		 {PARAM_BUTTON_VALUE, Object.class},
		 {PARAM_BUTTON_OUTPUT, String.class}
	};
	static final String PARAM_SLIDER_TOOLTIP= "tooltip";
	static final String PARAM_SLIDER_WIDTH= "width";
	static final String PARAM_SLIDER_TITLE = "title";
	static final String PARAM_SLIDER_MAX = "max";
	static final String PARAM_SLIDER_MIN = "min";
	static final String PARAM_SLIDER_STEP = "step";
	static final String PARAM_SLIDER_VALUE = "value";
	static final String PARAM_SLIDER_INPUT = "input";
	static final String PARAM_SLIDER_OUTPUT = "output";
	static final String PARAM_SLIDER_LOG = "log";
	static final String TAG_SLIDER = "slider";
	static final Object[][] PARAMS_SLIDER = {
		 {PARAM_SLIDER_TOOLTIP, String.class},
		 {PARAM_SLIDER_WIDTH, Integer.class},
		 {PARAM_SLIDER_TITLE, String.class},
		 {PARAM_SLIDER_MAX, Integer.class},
		 {PARAM_SLIDER_MIN , Integer.class},
		 {PARAM_SLIDER_STEP, Float.class},
		 {PARAM_SLIDER_VALUE, Integer.class},
		 {PARAM_SLIDER_INPUT , String.class},
		 {PARAM_SLIDER_OUTPUT, String.class},
		 {PARAM_SLIDER_LOG, Boolean.class}
	};
	static final String PARAM_FRAME_TITLE = "title";
	static final String PARAM_ID = "id";
	
	public InterfaceReader() {
		
	}
	
	public void openFile(String fileName, InterfaceBuilder ib ) {
		HashMap<String, Object> docPres = new HashMap<String, Object>();
		File f = new File(fileName);
		Element xdata = null;
		if (f.exists()) {
			Document doc = xu.parseXmlFile(fileName,false);
			xdata = doc.getDocumentElement();
		}
		if (xdata==null) {ib.timeline.showMessage("Invalid interface file:"+fileName);}
		
		NodeList nodeList = xdata.getChildNodes();
		if (xdata.getAttribute(PARAM_FRAME_TITLE)!=null){
			ib.setTitle(xdata.getAttribute(PARAM_FRAME_TITLE));
		}
		for (int i=0;i<nodeList.getLength();i++) {
			
			if (nodeList.item(i).getNodeType()==Node.ELEMENT_NODE) {
				Element e= (Element)nodeList.item(i);
				try {
					if (e.getNodeName().equals(TAG_SLIDER))  {ib.addSlider(e.getAttribute(PARAM_ID), getConf(e,PARAMS_SLIDER));}
					if (e.getNodeName().equals(TAG_BUTTON))  {ib.addButton(e.getAttribute(PARAM_ID), getConf(e,PARAMS_BUTTON));}
					if (e.getNodeName().equals(TAG_FILE))  {ib.addFile(e.getAttribute(PARAM_ID), getConf(e,PARAMS_FILE));}
					if (e.getNodeName().equals(TAG_TEXT))  {ib.addText(e.getAttribute(PARAM_ID), getConf(e,PARAMS_TEXT));}
				} catch (NullPointerException n) {
					System.err.println("Invalid config for :"+e.getNodeName()+" >"+e.getAttribute(PARAM_ID));
				}
			}
		}
		ib.pack();
	}
	
	private HashMap getConf(Element e, Object[][] conf) {
		HashMap h = null;
		for (int i=0;i<conf.length;i++) {
			if (h==null) {h=new HashMap<String, Object>();}
			Event ev = new Event();
			Object o = Event.getValueFromStr(e.getAttribute((String)conf[i][0]));
			if (((Class) conf[i][1]).isInstance(o)) {
				h.put((String) conf[i][0], o);
			}
		}
		return h;
	}
}
