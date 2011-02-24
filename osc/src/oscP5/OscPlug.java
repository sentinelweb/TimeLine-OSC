// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OscPlug.java

package oscP5;


// Referenced classes of package oscP5:
//            OscError

public class OscPlug
{

    public OscPlug()
    {
    }

    public void add(Object theParent, String theMethodName, String theArgs)
    {
        Class parentClass;
        String tMethodName;
        parentClass = theParent.getClass();
        tMethodName = theMethodName;
        try{
        	if(tMethodName == null)
            return;
        Class tClass[] = new Class[5];
        tClass[0] = int[].class;
        java.lang.reflect.Method method = parentClass.getDeclaredMethod(tMethodName, tClass);

	    }catch(SecurityException e){
	        e.printStackTrace();
	    }catch(NoSuchMethodException e){
	        e.printStackTrace();
	    }
        OscError.methodException(tMethodName);
        return;
    }
}
