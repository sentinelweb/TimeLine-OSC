package com.silicontransit.timeline.obj;
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
public class Standard {
	public Integer add(Integer a,Integer b) {
		return new Integer(a.intValue()+b.intValue());
		
	}
	public Float add(Float a,Float b) {
		return new Float(a.floatValue()+b.floatValue());
	}
	
	public Integer sub(Integer a,Integer b) {
		return new Integer(a.intValue()-b.intValue());
		
	}
	public Float sub(Float a,Float b) {
		return new Float(a.floatValue()-b.floatValue());
	}
	
	public Integer mul(Integer a,Integer b) {
		return new Integer(a.intValue()*b.intValue());
		
	}
	public Float mul(Float a,Float b) {
		return new Float(a.floatValue()*b.floatValue());
	}
	
	public Integer div(Integer a,Integer b) {
		return new Integer(a.intValue()/b.intValue());
		
	}
	public Float div(Float a,Float b) {
		return new Float(a.floatValue()/b.floatValue());
	}
	public Integer rnd(Integer a){
		return new Integer(Math.round((float)Math.random()*a.intValue()));
	}
	public Integer rnd(Integer a,Integer offset){
		return new Integer(offset.intValue()+Math.round((float)Math.random()*a.intValue()));
		
	}
	public Float rnd(Float a){
		return new Float((float)Math.random()*a.floatValue());
		
	}
	public Float rnd(Float a,Float offset){
		return new Float(offset.floatValue()+(float)Math.random()*a.floatValue());
		
	}
	public int rndW(String s){
		try {
			String[] weights=s.split(";"); 
			int[][] weights_pairs=new int[weights.length][2];
			int total=0;
			for (int i=0;i<weights.length;i++) {
				String[] pair=weights[i].split("-");
					weights_pairs[i][0]=Integer.parseInt(pair[0]);
					weights_pairs[i][1]=Integer.parseInt(pair[1]);
					total+=weights_pairs[i][1];
			}
			int sumNow=0;
			int rnd= (int) Math.floor( Math.random() * total );
			for (int i=0;i<weights_pairs.length;i++) {
				sumNow+=weights_pairs[i][1];
				if (sumNow>rnd) {return weights_pairs[i][0];	}
			}
		} catch (NumberFormatException nex){}
		catch (ArrayIndexOutOfBoundsException aiex){}
		return 0;
		
	}
}
