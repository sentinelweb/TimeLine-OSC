/*
 * Created on 05-Oct-2007
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
/**
 * @author munror
 */
public class SelBean {
	
	private String description="";
	private String value="";
	private Object object=null;
	
	public SelBean(String value, String description) {this.description=description; this.value=value;}
	public SelBean(String value, String description, Object object) {this.description=description; this.value=value; this.object=object;}
	
	public String getDescription() {		return description;	}
	public String getValue() {		return value;	}
	public void setDescription(String string) {		description = string;	}
	public void setValue(String string) {		value = string;	}
	public String toString() {		return " ("+value+")"+description;	}
	public Object getObject() {		return object;	}
	public void setObject(Object object) {		this.object = object;	}
}
