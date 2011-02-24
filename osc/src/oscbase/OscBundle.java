package oscbase;
// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OscBundle.java

import java.util.ArrayList;
import osc.OscCoreMessage;

public class OscBundle
{

    public OscBundle()
    {
        messages = new ArrayList();
        timetag = 0L;
    }

    public void add(OscMessage msg)
    {
        OscCoreMessage message = new OscCoreMessage(msg.getMsgName());
        message.setTypesAndArgs(msg.getTypes(), msg.getArgs());
        messages.add(message);
    }

    public Object[] getMessages()
    {
        return messages.toArray();
    }

    public long getTimetag()
    {
        return timetag;
    }

    public void setTimetag(long theTimetag)
    {
        timetag = theTimetag;
    }

    ArrayList messages;
    long timetag;
}
