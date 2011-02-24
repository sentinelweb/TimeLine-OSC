// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OscError.java

package oscP5;

import java.io.PrintStream;

public class OscError
{

    public OscError()
    {
    }

    public static void methodException(String theMethodName)
    {
        String t = "--------------------------------------\n";
        t = t + "ERROR ERROR ERROR\n";
        t = t + "Method " + theMethodName + "() doesn't exists ";
        t = t + "in your processing code.\n";
        t = t + "add the following method to your code\n";
        t = t + "void " + theMethodName + "() {\n";
        t = t + "//your code here.\n";
        t = t + "}\n";
        t = t + "ERROR ERROR ERROR\n";
        t = t + "--------------------------------------\n";
        System.out.println(t);
    }
}
