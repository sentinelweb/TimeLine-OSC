package oscbase;
// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OscSender.java

import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.ArrayList;
import osc.OscCoreMessage;
import osc.OscCorePacket;
import processing.core.PApplet;

public class OscSender
{

    public OscSender(String theAddr, int thePort)
    {
        socket = null;
        try {
        addr = InetAddress.getByName(theAddr);
        }catch(UnknownHostException e){
        	System.out.println("ERROR while initializing host " + e);
        }
        try {
        	socket = new DatagramSocket();
        }catch(SocketException e){
        	System.out.println("ERROR while creating socket " + e);
        }
        port = thePort;
        System.out.println("osc sender initialized.");
        return;
    }

    public OscSender(PApplet theParent, String theAddr, int thePort)
    {
        this(theAddr, thePort);
    }

    public OscSender(OscP5 theOscP5, String theAddr, int thePort)
    {
        this(theAddr, thePort);
        oscP5 = theOscP5;
    }

    public OscBundle newBundle()
    {
        return new OscBundle();
    }

    public OscMessage newMsg(String s)
    {
        return new OscMessage(s);
    }

    public OscCorePacket preparePacket(String msgName, ArrayList types, ArrayList args, InetAddress addr, int port)
    {
        OscCorePacket packet = new OscCorePacket(addr, port, 0);
        OscCoreMessage message = new OscCoreMessage(msgName);
        message.setTypesAndArgs(types, args);
        packet.addMessage(message);
        return packet;
    }

    public void sendBundle(long theTimetag, Object messages[])
    {
        if(messages.length > 0)
        {
            OscCorePacket packet = new OscCorePacket(theTimetag, addr, port, 1);
            for(int i = 0; i < messages.length; i++)
                packet.addMessage((OscCoreMessage)messages[i]);

            sendPacket(packet);
        }
    }

    public void sendMsg(MessageOUT msg, String addr, int port)
    {
        try {
        	InetAddress tmpAddr = InetAddress.getByName(addr);
        	sendPacket(preparePacket(msg.getMsgName(), msg.getTypes(), msg.getArgs(), tmpAddr, port));
        }catch (UnknownHostException e){
        	System.out.println("ERROR while sending packet " + e);
        }
        return;
    }

    public void sendMsg(MessageOUT msg)
    {
        sendPacket(preparePacket(msg.getMsgName(), msg.getTypes(), msg.getArgs(), addr, port));
    }

    public void sendMsg(String msgName, ArrayList types, ArrayList args)
    {
        sendPacket(preparePacket(msgName, types, args, addr, port));
    }

    public void sendPacket(OscCorePacket oscPacket)
    {
        try {
    	byte byteArray[] = oscPacket.getByteArray();
        
        DatagramPacket packet = new DatagramPacket(byteArray, byteArray.length, oscPacket.getAddress(), oscPacket.getPort());
        socket.send(packet);
        } catch (SocketException se){
        	System.out.println("ERROR > sendMsg() SocketException");
        } catch (IOException ioe){
        	System.out.println("ERROR > sendMsg() io exception");
        } catch (NullPointerException npe){
        	System.out.println("ERROR > sendMsg() NullPointerException");
        }
        return;
    }

    public void setPort(int thePort)
    {
        port = thePort;
    }

    public InetAddress addr;
    public String host;
    private OscP5 oscP5;
    public int port;
    private DatagramSocket socket;
}
