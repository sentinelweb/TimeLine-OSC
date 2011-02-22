
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
import java.io.File;


public class DynCompilerObjectBean {
	private String name;
	private String imports;
	private String superClass;
	private String code;
	private File file;
	private Class generatedClass;
	public DynCompilerObjectBean(){}
	public DynCompilerObjectBean(String name, String imports,
			String superClass, String code) {
		super();
		this.name = name;
		this.imports = imports;
		this.superClass = superClass;
		this.code = code;
	}
	public String getCode() {		return code;	}
	public String getImports() {		return imports;	}
	public String getName() {		return name;	}
	public String getSuperClass() {		return superClass;	}
	public void setCode(String string) {		code = string;	}
	public void setImports(String string) {		imports = string;	}
	public void setName(String string) {		name = string;	}
	public void setSuperClass(String string) {		superClass = string;	}
	public String toString(){return this.name;}
	public File getFile() {		return file;	}
	public void setFile(File file) {		this.file = file;	}
	public Class getGeneratedClass() {		return generatedClass;	}
	public void setGeneratedClass(Class class1) {		generatedClass = class1;	}

}
