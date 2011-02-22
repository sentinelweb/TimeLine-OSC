package com.silicontransit.timeline;
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


 
public class TimeLineApp  {
	//static 
	public static void main(String[] args) {
		TimeLine timeLineApplet = new TimeLine();
		for (int i=0;i<args.length;i++) {
			System.out.println("arg:"+i+":"+args[i]);
		}
		String configArg=null;
		String mapArg=null;
		if (args.length>0) {
			configArg=args[0];  // the full path
			if (args.length>1) {
				mapArg=args[1]; // the full path
			}
		} 
		
		File pwd=new File(System.getProperty("user.dir"));
		File home=new File(System.getProperty("user.home"));
		File rc=new File(home,".timelinerc");
		if (!rc.exists()) {	rc.mkdir();	}
		
		timeLineApplet.configDirectoryPath=rc.getAbsolutePath();
		
		File homeConfig = new File(rc,"config.xml");
		if (homeConfig.exists() && configArg==null) {
			configArg=homeConfig.getAbsolutePath();
		}
		
		File pwdConfig = new File(pwd,"config.xml");
		if (pwdConfig.exists() && configArg==null) {
			configArg=pwdConfig.getAbsolutePath();
		}
		
		if (configArg!=null) {
			timeLineApplet.configFilePath = configArg;
		}
		
		if (mapArg!=null) {
			timeLineApplet.mapFilePath = mapArg;
		} else {
			timeLineApplet.mapFilePath = timeLineApplet.configDirectoryPath+File.separator+"ControlMaps.xml";
		}
		
		System.out.println("home:"+System.getProperty("user.home"));
		System.out.println("pwd:"+System.getProperty("user.dir"));
		System.out.println("map:"+timeLineApplet.mapFilePath);
	    timeLineApplet.init();
	}
}
