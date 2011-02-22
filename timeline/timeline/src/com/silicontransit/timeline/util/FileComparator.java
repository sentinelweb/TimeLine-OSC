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
import java.util.Comparator;

public class FileComparator implements Comparator {
	public int compare(Object o1, Object o2) {
		String s1=((File)o1).getName();
		String s2=((File)o2).getName();
	    return s1.toLowerCase().compareTo(s2.toLowerCase());
	}
}
