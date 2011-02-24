// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OscCorePacket.java

package osc;

import java.io.*;
import java.net.InetAddress;
import java.util.*;

// Referenced classes of package osc:
//            OscCoreMessage

public class OscCorePacket
{

    public OscCorePacket(long theTime, InetAddress ia, int thePort, int theType)
    {
        this();
        time = theTime;
        address = ia;
        port = thePort;
        type = theType;
    }

    public OscCorePacket(InetAddress ia, int thePort, int theType)
    {
        this();
        address = ia;
        port = thePort;
        type = theType;
    }

    public OscCorePacket()
    {
        msgName = "/error";
        time = 0L;
        messages = new ArrayList();
    }

    public void addMessage(OscCoreMessage oscmessage)
    {
        messages.add(oscmessage);
    }

    private void alignStream(ByteArrayOutputStream bytearrayoutputstream)
        throws IOException
    {
        int i = 4 - bytearrayoutputstream.size() % 4;
        for(int j = 0; j < i; j++)
            bytearrayoutputstream.write(0);

    }

    public ArrayList extractPacket()
    {
        ArrayList arr = new ArrayList(messages.size());
        boolean flag = true;
        for(Iterator it = messages.iterator(); it.hasNext();)
        {
            OscCoreMessage oscmessage = (OscCoreMessage)it.next();
            ArrayList tArr = new ArrayList();
            tArr.add(oscmessage.getName());
            tArr.add(oscmessage.getTypes());
            tArr.add(oscmessage.getArgs());
            arr.add(tArr);
            if(flag)
                flag = false;
        }

        return arr;
    }

    public InetAddress getAddress()
    {
        return address;
    }

    public byte[] getByteArray()
        throws IOException
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        boolean flag = false;
        if(type == 1)
        {
            bytearrayoutputstream.write("#bundle".getBytes());
            bytearrayoutputstream.write(0);
            boolean flag1 = true;
            dataoutputstream.writeLong(time);
        }
        byte abyte0[];
        for(Iterator it = messages.iterator(); it.hasNext(); bytearrayoutputstream.write(abyte0))
        {
            OscCoreMessage oscmessage = (OscCoreMessage)it.next();
            abyte0 = oscmessage.getByteArray();
            if(type == 1)
                dataoutputstream.writeInt(abyte0.length);
        }

        return bytearrayoutputstream.toByteArray();
    }

    public int getPort()
    {
        return port;
    }

    public static void printBytes(byte abyte0[])
    {
        for(int i = 0; i < abyte0.length; i++)
        {
            System.out.print(abyte0[i] + " (" + (char)abyte0[i] + ")  ");
            if((i + 1) % 4 == 0)
                System.out.print("\n");
        }

    }

    public void setAddress(InetAddress inetaddress)
    {
        address = inetaddress;
    }

    public void setPort(int i)
    {
        port = i;
    }

    public void setTime(long l)
    {
        time = l;
    }

    public InetAddress address;
    private ArrayList messages;
    public String msgName;
    public int port;
    private long time;
    private int type;
}
