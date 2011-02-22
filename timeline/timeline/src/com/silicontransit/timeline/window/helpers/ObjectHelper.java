/*
 * Created on 05-Nov-2007
 *
 */
package com.silicontransit.timeline.window.helpers;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import com.silicontransit.timeline.TimeLine;
import com.silicontransit.timeline.util.SwingStyler;

/**
 * @author munror
 *
 */
public class ObjectHelper extends InputHelper {
	protected static final SelBean[] objOpts = {
		new SelBean("m","Method"),
		new SelBean("o","Object"),
		new SelBean("c","Constructor"),
		new SelBean("f","Field"),
		new SelBean("p","Parent")
	};
	
	private JTextField searchObjTxt;
	private JPanel searchObjTxtPanel;
	
	private JTextField searchPkgTxt;
	private JPanel searchPkgTxtPanel;
	
	private JTextField searchMemTxt;
	private JPanel searchMemTxtPanel;
	
	private JComboBox memberTypeSelector;
	private JPanel memberTypeSelectorPanel;
	
	private JComboBox packageSelector;
	private JPanel packageSelectorPanel;
	
	private JComboBox objSelector;
	private JPanel objSelectorPanel;
	
	private JComboBox memberSelector;
	private JPanel memberSelectorPanel;
	
	private static Vector globalCL=new Vector() ;
	private static boolean globalsLoading = false;
	String classpathSep;
	JProgressBar loadBar ;
	String importStr = "";
	JTextComponent helpFor = null;
	
	ClassLoader classLoader  ;
	String additionalCP ="" ;
	
	public ObjectHelper(int type, TimeLine t, boolean insert) {
		super(type, t, insert);
		classLoader = ClassLoader.getSystemClassLoader();
		classpathSep=";";
		String ver=System.getProperty("java.version");
		if (ver.indexOf("1.5")==0 && "/".equals(File.separator)) {classpathSep=":";}
		
	}

	

	public void buildComponents() {
		
		// loading bar.
		loadBar = new JProgressBar(0,100) ;
		SwingStyler.setStyleCommon(loadBar);
		loadBar.setToolTipText("% objects loaded - click to reload");
		loadBar.setPreferredSize(new Dimension(50,20));
		loadBar.setStringPainted(true);
		loadBar.setForeground(new Color(128,128,128));
		loadBar.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {	
				indexClasses();
			}
			public void mousePressed(MouseEvent e) {	}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
		buttonPanel.add( loadBar );
		
		// build fields
		searchPkgTxt =  getTextField(TEXT_TYPE_NORMAL);
		searchPkgTxt.setPreferredSize(new Dimension(250,25));
		searchPkgTxtPanel = addField("Search Package", searchPkgTxt );
		packageSelector = getPackageSelector(null);
		packageSelectorPanel = addField("Package", packageSelector );
		packageSelector.setPreferredSize(new Dimension(250,25));
		searchObjTxt =  getTextField(TEXT_TYPE_NORMAL);
		searchObjTxt.setPreferredSize(new Dimension(250,25));
		searchObjTxtPanel = addField("Search Object", searchObjTxt);
		objSelector = getObjectSelector();
		objSelector.setPreferredSize(new Dimension(250,25));
		objSelectorPanel = addField("Object", objSelector);
		memberTypeSelector = getSelector(objOpts);
		memberTypeSelectorPanel = addField("Member type", memberTypeSelector);
		memberTypeSelector.setPreferredSize(new Dimension(250,25));
		searchMemTxt =  getTextField(TEXT_TYPE_NORMAL);
		searchMemTxt.setPreferredSize(new Dimension(250,25));
		searchMemTxtPanel = addField("Search Member", searchMemTxt);
		memberSelector = getMemberSelector();
		memberSelectorPanel = addField("Member", memberSelector);
		memberSelector.setPreferredSize(new Dimension(250,25));
		// buttons
		JButton wrapButon =new SwingStyler.ImgButton("( . . . )");
		wrapButon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {wrapBracket();}
		});
		wrapButon.setPreferredSize(new Dimension(50,15));
		buttonPanel.add( wrapButon );
		JButton insertButon = new SwingStyler.ImgButton("Rep Sel");
		insertButon.setToolTipText("Replace Selection");
		insertButon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {replaceSelectionInInput();}
		});
		insertButon.setPreferredSize(new Dimension(50,15));
		buttonPanel.add( insertButon );
		// frame properties
		thisFrame.setTitle("Object Helper");
		thisFrame.setPreferredSize(new Dimension(400, 330));
	}
	
	private void indexClasses() {
		// class indexer
		LoaderThread lt= new LoaderThread();
		lt.start();
	}
	private class LoaderThread extends Thread {
			/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			if (!globalsLoading) {
				globalsLoading = true;
				refreshClassList();
				globalsLoading=false;
			}
		}

	}
	public String getInput() {
		setObjectSelectorByPackage("", "");
		return input.getText();
	}

	public void setInput(String inputText) {
		
		importStr="";
		this.input.setText(inputText);
	}

	public void updateSelection(EventObject ae) {
		if (ae.getSource() == packageSelector) {
			clearObjects(objSelector);
			clearObjects(memberSelector);
			String packageName = getSelectedValue(packageSelector);
			setObjectSelectorByPackage(packageName,"");
		} else if ( ae.getSource() == searchPkgTxt) {
			// route up & down to package selector.
			KeyEvent ke= (KeyEvent)ae;
			if (ke.getKeyCode()==KeyEvent.VK_UP || ke.getKeyCode()==KeyEvent.VK_DOWN) {
				packageSelector.requestFocus();	return;
			}
			// gets around some ArrayIndexOutOfBounds in swing.
			if (( (ke.getKeyCode()==0)) && "".equals(searchPkgTxt.getText())) {return;}//ke.getKeyCode()==KeyEvent.VK_BACK_SPACE ||
			String objText=searchPkgTxt.getText();
			clearObjects( packageSelector );
			clearObjects( memberSelector );
			clearObjects( objSelector );
			String packageName = searchPkgTxt.getText();
			setPackages( packageName, packageSelector );
			packageSelector.showPopup();
		} else if (ae.getSource() == objSelector) {
			String memText=searchMemTxt.getText();
			Class c = (Class) getSelectedObject(objSelector);
			clearObjects(memberSelector);
			String type = getSelectedValue(memberTypeSelector);
			if (c!=null && !"o".equals(type)) {
				setMemberSelector( c, type,memText);
				importStr = c.getName();
			} else if (c!=null && "o".equals(type)) {
				if ("".equals(input.getText())) {input.setText(c.getSimpleName());}
				importStr = c.getName();
			}
		} else if ( ae.getSource() == searchObjTxt) {
			// route up & down to package selector.
			KeyEvent ke= (KeyEvent)ae;
			if (ke.getKeyCode()==KeyEvent.VK_UP || ke.getKeyCode()==KeyEvent.VK_DOWN) {
				objSelector.requestFocus();return;
			}
			if (( (ke.getKeyCode()==0)) && "".equals(searchObjTxt.getText())) {return;}//ke.getKeyCode()==KeyEvent.VK_BACK_SPACE ||
			 // text typed.
			String objText=searchObjTxt.getText();
			clearObjects( objSelector );
			clearObjects( memberSelector );
			String packageName = getSelectedValue( packageSelector );
			setObjectSelectorByPackage( packageName, objText );
			objSelector.showPopup();
		} else if (ae.getSource() == memberSelector) {
			String val= getSelectedValue(memberSelector);
			if ("".equals(input.getText())) {input.setText(val);	}
		} else if ( ae.getSource() == searchMemTxt) {
			// route up & down to package selector.
			KeyEvent ke= (KeyEvent)ae;
			if (ke.getKeyCode()==KeyEvent.VK_UP || ke.getKeyCode()==KeyEvent.VK_DOWN) {
				memberSelector.requestFocus();return;
			}
			// gets around some ArrayIndexOutOfBounds in swing.
			if (((ke.getKeyCode()==0)) && "".equals(searchMemTxt.getText())) {return;}//ke.getKeyCode()==KeyEvent.VK_BACK_SPACE || 
			String memText=searchMemTxt.getText();
			String type = getSelectedValue(memberTypeSelector);
			clearObjects( memberSelector );
			Class c = (Class) getSelectedObject(objSelector);
			if (c!=null) {
				importStr = c.getName();
				setMemberSelector( c, type,memText);
			}
			memberSelector.showPopup();
		} else if (ae.getSource() == memberTypeSelector) {
			String memText=searchMemTxt.getText();
			Class c = (Class) getSelectedObject(objSelector);
			clearObjects(memberSelector);
			String type = getSelectedValue(memberTypeSelector);
			if (c!=null) {
				importStr = c.getName();
				setMemberSelector( c, type,memText);
			}
		} 
	}
	
	private class SelectionSorter implements java.util.Comparator{
		public int compare(Object arg0, Object arg1) {
			SelBean sb1=(SelBean)arg0;
			SelBean sb2=(SelBean)arg1;
			return sb1.getValue().compareTo(sb2.getValue());
		}	
	}
	
	private void setObjectSelectorByPackage(String packageName,String text) {
		SwingUtilities.invokeLater(new ObjectSelectorUpdater(packageName, text));
	}
	class ObjectSelectorUpdater implements Runnable {
		String packageName="";
		String text="";
		public ObjectSelectorUpdater(String packageName,String text) {
			this.packageName=packageName;
			this.text=text;
		}
		public void run() {
			setObjectSelectorByPackageRun( packageName, text) ;
		}
	}
	
	private void setObjectSelectorByPackageRun(String packageName,String text) {
		Vector v = new Vector();
		if (packageName==null) {packageName="";}
		if (text==null) {text="";}
		//System.out.println("classlis-st:"+ObjectHelper.globalCL.hashCode());
		for (Iterator iter = ObjectHelper.globalCL.iterator(); iter.hasNext();) {
			Class element = (Class) iter.next();
			if ("".equals(packageName) || element.getPackage()==null || (element.getPackage().getName().equals(packageName) ) ) {
				if (  "".equals(text) || element.getSimpleName().toLowerCase().indexOf(text.toLowerCase())>-1) {
					v.add( new SelBean( element.getSimpleName() , makeClassDesc(element),element));
				}
			}
		}
		Collections.sort(v, new SelectionSorter());
		objSelector.addItem( new SelBean( "",""));
		for (Iterator iter = v.iterator(); iter.hasNext();) {		objSelector.addItem( iter.next());	}
	}
	
	public JComboBox getObjectSelector() {
		JComboBox objSelector = new JComboBox(); 
		objSelector.addItem( new SelBean( "",""));
		SwingStyler.setStyleInput(objSelector);
		objSelector.addActionListener(selectedObjectHandler);
		return objSelector;
	}
	
	private void refreshClassList() {
		//System.out.println("classlist-r:"+ObjectHelper.globalCL.hashCode());
		ObjectHelper.globalCL.clear();
		//System.out.println("classlist-rc:"+ObjectHelper.globalCL.hashCode());
		String classPath = System.getProperty("java.class.path") + classpathSep + System.getProperty("sun.boot.class.path")+classpathSep+additionalCP;
		String split=classpathSep;if (split.equals(":")) {split="\\:";}
		String[] classPathEntryList = classPath.split(split);
		for (int i=0; i < classPathEntryList.length; i++) {
			//System.out.println(classPathEntryList[i]);
			loadBar.setValue((int)Math.round(i*100.0/classPathEntryList.length));
			if (classPathEntryList[i].endsWith("jar") || classPathEntryList[i].endsWith("zip")) {
				try {
					ZipFile jarFile= new ZipFile(classPathEntryList[i]);
					StringBuffer loaded=new StringBuffer();
					Enumeration ents = jarFile.entries();
					for (; ents.hasMoreElements(); ) {
						ZipEntry ent = (ZipEntry) ents.nextElement() ;
						if (ent.getName().endsWith(".class") && ent.getName().indexOf("$")==-1) { // don't index inner classes - messy and takes loads off perms memory.
							String className=ent.getName().substring(0,ent.getName().length()-6).replaceAll("/",".");
							addClass(className);
							loaded.append("*");
						}
					}
					System.out.println(classPathEntryList[i]+":"+loaded.length()+" classes");
				} catch (IOException e) {
					System.out.println(e.getMessage()+":"+classPathEntryList[i]);
				} 
			} else {
				getList( new File(classPathEntryList[i]),  classPathEntryList[i].length());
			}
		}
		loadBar.setValue(100);
		return;
	}
	
	private String makeClassDesc(Class clazz)  {
		if (clazz!=null ) {
			try {
				String text = "["+(clazz.isInterface()?"I":"C") +"]";
				if (clazz.getSuperclass()!=null) {
					text+=" - "+clazz.getSuperclass().getSimpleName();
				}
				if (clazz.getPackage()!=null) {
					text+=" - " + clazz.getPackage().getName();
				}
				return text;
			} catch(InternalError interr) {
				System.out.println("interr:"+interr.getMessage()+":"+clazz.toString());
			}
		}
		return "";
	}
	
	private void addClass(String className)  {
		try {
			if (ObjectHelper.class.getName().equals(className)) {
				System.err.println("Dont load youself.");
				return;
			}
			if (className.indexOf("com.rob")==0) {System.out.println(className);}
			Class c = Class.forName(className, false, this.classLoader);
			if (className.indexOf("com.rob")==0) {System.out.println(c.getSimpleName());}
			c.getSimpleName();
			if (c!=null ) {		ObjectHelper.globalCL.add(c);	}
			//makeClassDesc(className);
		} catch (ClassNotFoundException e1) {
			System.out.println("Cant find:"+className);
		} catch(NoClassDefFoundError ncderr) {
			System.out.println("ncdf:"+className);
		} catch(UnsatisfiedLinkError uslerr) {
			System.out.println("uslferr:"+className);
		} catch(InternalError uslerr) {
			System.out.println("interr:"+className);
		} catch(OutOfMemoryError uslerr) {//can add: -XX:MaxPermSize=200M       to get around this.
			System.out.println("omerr:"+className+" attempt recover");
			System.gc();
			System.out.println("omerr:"+className);
		}
	}
	
	private void  getList(File dir,int truncate) {
		File[] fileList = dir.listFiles();
		if (fileList!=null) {
			for (int i=0;i<fileList.length;i++) {
				if (fileList[i].isDirectory()) {
					//v.add(fileList[i].getAbsolutePath().replaceAll("\\"+File.separator ,"/" ).substring(truncate));
					getList(fileList[i],truncate);
				} else if (fileList[i].getAbsolutePath().endsWith(".class")) {
					String filePath=fileList[i].getAbsolutePath();
					String className=filePath.substring(truncate+1,filePath.length()-6).replaceAll("\\"+File.separator ,"." );
					addClass(className);
				}
			}
		}
	}
	
	public JComboBox  getPackageSelector(String txt) {
		JComboBox packSel = new JComboBox(); 
		setPackages(txt, packSel);
		SwingStyler.setStyleInput(packSel);
		packSel.addActionListener(selectedObjectHandler);
		return packSel;
	}

	private void setPackages(String txt, JComboBox packSel) {
		ArrayList objList= getPackages();
		for (Iterator iter = objList.iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			if (txt==null || "".equals(txt) || element.indexOf(txt)>-1) {
				packSel.addItem( new SelBean(element,"") );
			}
		}
	}
	
	private ArrayList getPackages() {
		ArrayList v = new ArrayList();
		v.add("");
		Package[] packs= (new CustCL(classLoader)).getPackages();
		for (int i = 0; i < packs.length; i++) {
			v.add(packs[i].getName());
		}	
		Collections.sort(v);
		return v;
	}
	
	public class CustCL extends ClassLoader {
		public CustCL() {super();}
		public CustCL(ClassLoader parent) {super(parent);}
		public Package[] getPackages() {	return super.getPackages();}
	}
	
	public JComboBox  getMemberSelector() {
		JComboBox memSel = new JComboBox(); 
		SwingStyler.setStyleInput(memSel);
		memSel.addActionListener(selectedObjectHandler);
		//memSel.setEditable(true);
		clearObjects(memSel);
		return memSel;
	}
	
	private void setMemberSelector(Class c,String type,String text) {
		memberSelector.addItem(new SelBean("",""));
		Vector list = new Vector();
		SelBean selected = null;
		if (text==null) {text="";}
		if ("m".equals(type)) {
			Method[] methods = c.getMethods();
			for (int i=0;i<methods.length;i++) {
				if ("".equals(text) || methods[i].getName().indexOf(text)>-1 ) {
					String sig=methods[i].getName()+"(";
					Class[] sigClass = methods[i].getParameterTypes();
					for ( int j=0; j < sigClass.length; j++ ) {
						sig+=sigClass[j].getSimpleName() + (( j < sigClass.length-1)?",":"");
					}
					sig+=")";
					String modifier = "";
					if (methods[i].getModifiers()==Method.PUBLIC) {modifier+="public ";}
					if (methods[i].getModifiers()==Method.DECLARED) {modifier+="declared ";}
					list.add(new SelBean(sig, methods[i].getReturnType().getName()+"- ["+modifier+"]" , methods[i]));
				}
			}
		} else if ("c".equals(type)) {
			Constructor[] constructors = c.getConstructors();
			for (int i=0;i<constructors.length;i++) {
				if ("".equals(text) || constructors[i].getName().indexOf(text)>-1 ) {
					String sig=constructors[i].getName()+"(";
					Class[] sigClass = constructors[i].getParameterTypes();
					for ( int j=0; j < sigClass.length; j++ ) {
						sig+=sigClass[j].getSimpleName() + (( j < sigClass.length-1)?",":"");
					}
					sig+=")";
					String modifier = "";
					if (constructors[i].getModifiers()==Method.PUBLIC) {modifier+="public ";}
					if (constructors[i].getModifiers()==Method.DECLARED) {modifier+="declared ";}
					list.add(new SelBean(sig,"- ["+modifier+"]" , constructors[i]));
				}
			}
		} else if ("f".equals(type)) {
			Field[] fields = c.getFields();
			for (int i=0;i<fields.length;i++) {
				if ("".equals(text) || fields[i].getName().indexOf(text)>-1 ) {
					String modifier = "";
					if (fields[i].getModifiers()==Method.PUBLIC) {modifier+="public ";}
					if (fields[i].getModifiers()==Method.DECLARED) {modifier+="declared ";}
					list.add(new SelBean(fields[i].getName(),fields[i].getClass()+"- ["+modifier+"]" , fields[i]));
				}
			}
		} else if ("p".equals(type)) {
			SelBean selBean = new SelBean(c.getSuperclass().getSimpleName(),"",c.getSuperclass() );
			list.add(selBean);
			selected=selBean;
		} else if ("o".equals(type)) {
			SelBean selBean = new SelBean(c.getSimpleName(),"" ,c);
			list.add(selBean);
			selected=selBean;
		}
		
		Collections.sort(list,new SelectionSorter());
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			memberSelector.addItem((SelBean) iterator.next()); 
		}
		if (selected!=null) {
			memberSelector.setSelectedItem(selected);
		}
	}
	
	private void wrapBracket() {
		int start = input.getSelectionStart();
		int end = input.getSelectionEnd();
		input.setText(
			input.getText().substring(0,start)+"("+input.getText().substring(start,end)+")"+input.getText().substring(end)
		);
	}
	
	private void replaceSelectionInInput() {
		int start = input.getSelectionStart();
		int end = input.getSelectionEnd();
		String val = getSelectedValue(memberSelector);
		if (!"".equals(val)) {
			input.setText(
				input.getText().substring(0,start)+val+input.getText().substring(end)
			);
		}
	}

	public static void main(String[] args) {
		ObjectHelper obj = new ObjectHelper(TYPE_OBJECT, null,true);
		obj.setVisible(true);
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader loader) {
		classLoader = loader;
	}

	public String getImportStr() {
		return importStr;
	}

	public JTextComponent getHelpFor() {
		return helpFor;
	}

	public void setHelpFor(JTextComponent helpFor) {
		this.helpFor = helpFor;
	}

	public String getAdditionalCP() {
		return additionalCP;
	}

	public void setAdditionalCP(String additionalCP) {
		this.additionalCP = additionalCP;
		indexClasses();
	}



	public void setVisible(boolean vis) {
		if (ObjectHelper.globalCL.size()==0) {
			indexClasses();
		}
		super.setVisible(vis);
	}

}
