
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
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.silicontransit.timeline.util.*;


public class DynCompilerLoadSave {

	public void load(String fileName,DynCompiler dynCompiler) {
		try {
			System.out.println("file:"+fileName);
			XMLUtil xu=new XMLUtil();
			Document d=xu.parseXmlFile(fileName,false);
			
			if (d!=null) {
				Element objectsElement=d.getDocumentElement();
				readLoadElement(dynCompiler, objectsElement);
			}
		} catch (Exception e) {
			System.out.println(e.getClass().getName()+""+e.getMessage());
		}  
	}
	public void clearObjects (DynCompiler dynCompiler) {
		try {
			while (dynCompiler.objectSelector.getItemCount()>1) {
				dynCompiler.objectSelector.removeItemAt(0);
			}
		} catch (Exception e) {}
		if (dynCompiler.objectSelector.getItemCount()==0) {// to stop np in swing class
			dynCompiler.objectSelector.addItem(new DynCompilerObjectBean());
		}
	}
	
	public void readLoadElement(DynCompiler dynCompiler,	Element objectsElement) {
		clearObjects ( dynCompiler) ;// leaves an empty object in the to satisfy bug is selbox code.
		XMLUtil xu=new XMLUtil();
		//Element objectsElement = (Element)rootElement.getElementsByTagName("objects").item(0);
		NodeList children=objectsElement.getChildNodes();
		for (int i=0;i<children.getLength();i++) {
			if (children.item(i).getNodeType()==Node.ELEMENT_NODE) {
				Element e=(Element)children.item(i);
				if (e.getTagName().equals("jars")) {
					dynCompiler.jars.setText(	xu.getText(e)	);
				} else if (e.getTagName().equals("object")) {
					DynCompilerObjectBean dcob=new DynCompilerObjectBean();
					dcob.setImports(xu.getText((Element)e.getElementsByTagName("imports").item(0)));
					dcob.setName(e.getAttribute("name"));
					dcob.setSuperClass(e.getAttribute("super"));
					if (((Element)e.getElementsByTagName("code").item(0)).getChildNodes().getLength()>0) {
						String code=((CDATASection)((Element)e.getElementsByTagName("code").item(0)).getChildNodes().item(0)).getData();
						dcob.setCode(code);
					}
					dynCompiler.objectSelector.addItem(dcob);
				}
			}
		}
		dynCompiler.objectSelector.removeItemAt(0); // remove empty item
	}
	
	public void save(String fileName,DynCompiler dynCompiler) {
		XMLUtil xu=new XMLUtil();
		Document d= xu.createDomDocument();
		d.appendChild(writeSaveElement(dynCompiler, d));
		xu.writeXmlFile(d, fileName);
	}

	public Element writeSaveElement(DynCompiler dynCompiler, Document d) {
		Element objectsElement = d.createElement("objects");
		String jarsTextStr=dynCompiler.jars.getText();
		if (jarsTextStr!=null && !"".equals(jarsTextStr)) {
			Element jarsElement = d.createElement("jars");
			Text jarsText=d.createTextNode(dynCompiler.jars.getText());	
			jarsElement.appendChild(jarsText);
			objectsElement.appendChild(d.createTextNode("\n"));
			objectsElement.appendChild(jarsElement);
			objectsElement.appendChild(d.createTextNode("\n"));
		}
	
		for (int i=0;i<dynCompiler.objectSelector.getItemCount();i++) {
			//don't add if code section is null.note this cab be if obj hasn't been built.
			DynCompilerObjectBean obj=(DynCompilerObjectBean)dynCompiler.objectSelector.getItemAt(i);
			if (obj.getCode()==null) {continue;}
			Element objectElement = d.createElement("object");
			objectElement.setAttribute("name",obj.getName());
			objectElement.setAttribute("super",obj.getSuperClass());
			objectElement.appendChild(d.createTextNode("\n"));
			Element importsElement = d.createElement("imports");
			Text importsText=d.createTextNode(obj.getImports());	
			importsElement.appendChild(importsText);
			objectElement.appendChild(importsElement);
			objectElement.appendChild(d.createTextNode("\n"));
			Element codeElement = d.createElement("code");
			objectsElement.appendChild(d.createTextNode("\n"));
			if (obj.getCode()!=null) {
				String code=new String(obj.getCode());
				CDATASection codeText=d.createCDATASection(code);	
				codeElement.appendChild(codeText);
				objectElement.appendChild(codeElement);
			}
			objectElement.appendChild(d.createTextNode("\n"));
			objectsElement.appendChild(objectElement);
			objectsElement.appendChild(d.createTextNode("\n"));
		}
		return objectsElement;
	}
	
}
