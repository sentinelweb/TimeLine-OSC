
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
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * @author munror
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class XMLUtil {
	public boolean isElement(Node node,String name) {
		return (node.getNodeType()==Node.ELEMENT_NODE) && name.equals(((Element)node).getNodeName());	
	}
	
	public String getText(Element el) {
			String output="";
			for (int i=0; i < el.getChildNodes().getLength() ; i++) {
				Node n=el.getChildNodes().item(i);
				if (n.getNodeType()==Node.TEXT_NODE) {
					Text t=(Text)n;
					output+=t.getData();
				}
			}
			return output;
		
		}
	
		public  void writeXmlFile(Document doc, String filename) {
			try {
				// Prepare the DOM document for writing
				Source source = new DOMSource(doc);

				// Prepare the output file
				File file = new File(filename);
				Result result = new StreamResult(file);

				// Write the DOM document to the file
				Transformer xformer = TransformerFactory.newInstance().newTransformer();
				xformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
				xformer.setOutputProperty(OutputKeys.INDENT, "yes");
				xformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
				xformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				xformer.transform(source, result);
			} catch (TransformerConfigurationException e) {
				System.out.println("TransformerConfigurationException:"+e.getMessage());
			} catch (TransformerException e) {
				System.out.println("TransformerException:"+e.getMessage());
				e.printStackTrace();
				if (e.getCause()!=null) e.getCause().printStackTrace();
			}
		}

		public  Document parseXmlFile(String filename, boolean validating) {
			try {
				// Create a builder factory
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setValidating(validating);

				// Create the builder and parse the file
				Document doc = factory.newDocumentBuilder().parse(new File(filename));
				return doc;
			} catch (SAXException e) {
				System.out.println(e.getClass().getName()+""+e.getMessage());
			} catch (ParserConfigurationException e) {
				System.out.println(e.getClass().getName()+""+e.getMessage());
			} catch (IOException e) {
				System.out.println(e.getClass().getName()+""+e.getMessage());
			}
			return null;
		}
		
		public  Document createDomDocument() {
			try {
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = builder.newDocument();
				return doc;
			} catch (ParserConfigurationException e) {
			}
			return null;
		}
}
