// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OscCoreSocket.java

package osc;

import java.io.IOException;
import java.io.PrintStream;
import java.net.*;

// Referenced classes of package osc:
//            OscCorePacket

public class OscCoreSocket extends DatagramSocket
{

    public OscCoreSocket()
        throws SocketException
    {
    }

    public void send(OscCorePacket oscpacket)
        throws IOException
    {
        byte abyte0[] = oscpacket.getByteArray();
        System.out.println("OscSocket about to send this packet:");
        OscCorePacket.printBytes(abyte0);
        DatagramPacket datagrampacket = new DatagramPacket(abyte0, abyte0.length, oscpacket.getAddress(), oscpacket.getPort());
        super.send(datagrampacket);
    }
}
