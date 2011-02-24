package oscbase;
// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OscP5.java

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import oscP5.OscError;
import oscP5.OscPlug;

public class OscP5
{

    public OscP5(Object theParent, String theHost, int theSendToPort, int theReceiveAtPort)
    {
        parent = null;
        host = null;
        isMethod = false;
        oscP5version = 12;
        parent = theParent;
        initialize(theHost, theSendToPort, theReceiveAtPort);
    }

    public OscP5(Object theParent, String theHost, int theSendToPort, int theReceiveAtPort, String theMethodName)
    {
        String tMethodName;
        parent = null;
        host = null;
        isMethod = false;
        oscP5version = 12;
        parent = theParent;
        parentClass = parent.getClass();
        tMethodName = theMethodName;
        try {
        if(tMethodName == null) 	return;
        Class tClass[] = {
            OscIn.class
        };
        method = parentClass.getDeclaredMethod(tMethodName, tClass);
        isMethod = true;
    
    }catch(SecurityException e){
        e.printStackTrace();
    }catch(NoSuchMethodException e){
        e.printStackTrace();
    	OscError.methodException(tMethodName);
    }
    initialize(theHost, theSendToPort, theReceiveAtPort);
    
    return;
    }

    public void call(OscIn oscIn)
    {
        Object t[] = {
            oscIn
        };
        try {
	        method.invoke(parent, t);
        }catch(IllegalArgumentException e){
	        e.printStackTrace();
        }catch(IllegalAccessException e){
	        e.printStackTrace();
        }catch(InvocationTargetException e){
	        e.printStackTrace();
        }
        return;
    }

    public boolean checkNet()
    {
        return orObj.checkNet();
    }

    public OscIn getMsg(int theInt)
    {
        return orObj.getMsg(theInt);
    }

    public int getPort()
    {
        return orObj.getPort();
    }

    public String getVersion()
    {
        String t = "------------------------\n";
        t = t + "OscP5 v." + oscP5version + "\n";
        t = t + "by andreas schlegel\n";
        t = t + "andi@sojamo.de\n";
        t = t + "------------------------\n";
        return t;
    }

    private void initialize(String theHost, int theSendToPort, int theReceiveAtPort)
    {
        host = theHost;
        sendToPort = theSendToPort;
        receiveAtPort = theReceiveAtPort;
        oscPlug = new OscPlug();
        if(sendToPort > 0 && receiveAtPort > 0 && parent != null)
        {
            System.out.println("OscP5 started ...");
            orObj = new OscReceiver(this, receiveAtPort);
            osObj = new OscSender(this, host, sendToPort);
        } else
        {
            System.out.println("ERROR > OscP5 failed to initialize.");
        }
    }

    public int msgSize()
    {
        return orObj.msgSize();
    }

    public OscBundle newBundle()
    {
        return osObj.newBundle();
    }

    public OscMessage newMsg(String s)
    {
        return osObj.newMsg(s);
    }

    public void receiverStopped()
    {
        orObj = null;
        orObj = new OscReceiver(this, receiveAtPort);
        System.out.println("restarting OscReceiver ...");
    }

    public void sendBundle(OscBundle theBundle)
    {
        osObj.sendBundle(theBundle.getTimetag(), theBundle.getMessages());
    }

    public void sendMsg(String s, Object o[], String addr, int port)
    {
        sendMsg(((MessageOUT) (new OscMessage(s, o))), addr, port);
    }

    public void sendMsg(String s, Object o[])
    {
        sendMsg(((MessageOUT) (new OscMessage(s, o))));
    }

    public void sendMsg(MessageOUT msg, String addr, int port)
    {
        osObj.sendMsg(msg, addr, port);
    }

    public void sendMsg(MessageOUT msg)
    {
        osObj.sendMsg(msg);
    }

    void sendMsg(String msgName, ArrayList types, ArrayList args)
    {
        osObj.sendMsg(msgName, types, args);
    }

    void setHost(String theHost)
    {
        host = theHost;
        osObj.host = theHost;
    }

    void setSendToPort(int thePort)
    {
        sendToPort = thePort;
        osObj.port = thePort;
    }

    int version()
    {
        return oscP5version;
    }

    private String host;
    public boolean isMethod;
    private Method method;
    private OscReceiver orObj;
    private OscSender osObj;
    private int oscP5version;
    private OscPlug oscPlug;
    Object parent;
    private Class parentClass;
    private int receiveAtPort;
    private int sendToPort;
}
