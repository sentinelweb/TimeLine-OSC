
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
import java.util.HashMap;


public interface RecompilationListener {

	public void recompiledEvent(DynCompilerObjectBean o) ;
	public void reLoadedEvent() ;
	
	//public void recompileStart(DynCompilerObjectBean o) ;
	
	//public void recompileStop(DynCompilerObjectBean o) ;
	
	public HashMap getDynamicObjects() ;
	
}
