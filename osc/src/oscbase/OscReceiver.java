package oscbase;
// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OscReceiver.java

import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import osc.*;
import processing.core.PApplet;

public class OscReceiver extends Thread
{

    public OscReceiver(PApplet theParent, int thePort)
    {
        this(thePort);
    }

    public OscReceiver(int thePort)
    {
        firstNameFlag = true;
        thrThis = null;
        oscP5 = null;
        netFlag = false;
        byteSize = 1024;
        port = thePort;
        start();
    }

    public OscReceiver(OscP5 theOscP5, int thePort)
    {
        this(thePort);
        oscP5 = theOscP5;
    }

    boolean checkNet()
    {
        if(netFlag)
        {
            netFlag = false;
            return true;
        } else
        {
            return false;
        }
    }

    public ArrayList[] getHeuristicallyTypeGuessedArgs(byte block[])
    {
        System.out.println(" getHeuristicallyTypeGuessedArgs() Bad OSC packet: No type tags");
        return new ArrayList[2];
    }

    public MessageINcontainer getMsg(int theInt)
    {
        return messageINcontainer[theInt];
    }

    int getPort()
    {
        return port;
    }

    public ArrayList getStringAndData(byte block[], int stringLength)
    {
        ArrayList arr = new ArrayList();
        if(stringLength % 4 != 0)
        {
            System.out.println(" getStringAndData() printNameAndArgs: bad boundary");
            return arr;
        }
        int i;
        for(i = 0; block[i] != 0; i++)
        {
            if(i >= stringLength)
            {
                System.out.println(" getStringAndData() printNameAndArgs: Unreasonably long string");
                return arr;
            }
        }

        arr.add(new String(Bytes.copy(block, 0, i)));
        for(i++; i % 4 != 0; i++)
        {
            if(i >= stringLength)
            {
                System.out.println(" getStringAndData() printNameAndArgs: Unreasonably long string");
                return arr;
            }
            if(block[i] != 0)
            {
                System.out.println(" getStringAndData() printNameAndArgs: Incorrectly padded string.");
                return arr;
            }
        }

        arr.add(new Integer(i));
        arr.add(Bytes.copy(block, i));
        return arr;
    }

    public ArrayList[] getTypeTaggedArgs(byte block[])
    {
        ArrayList typeArr = new ArrayList();
        ArrayList argArr = new ArrayList();
        int p = 0;
        ArrayList typesAndArgs = getStringAndData(block, block.length);
        if(typesAndArgs.size() > 1)
        {
            byte args[] = (byte[])typesAndArgs.get(typesAndArgs.size() - 1);
            for(int thisType = 1; block[thisType] != 0; thisType++)
            {
                switch(block[thisType])
                {
                case 91: // '['
                    typeArr.add(new Character('['));
                    break;

                case 93: // ']'
                    typeArr.add(new Character(']'));
                    break;

                case 98: // 'b'
                    typeArr.add(new Character('b'));
                    int tLen = (new Integer(Bytes.toInt(Bytes.copy(args, p, 4)))).intValue();
                    p += 4;
                    byte tByte[] = Bytes.copy(args, p, tLen);
                    argArr.add(tByte);
                    p += tLen % 4 == 0 ? tLen : tLen + (4 - tLen % 4);
                    break;

                case 109: // 'm'
                    typeArr.add(new Character('m'));
                    argArr.add(Bytes.copy(args, p, 4));
                    p += 4;
                    break;

                case 99: // 'c'
                    typeArr.add(new Character('c'));
                    argArr.add(new Character((char)Bytes.toInt(Bytes.copy(args, p, 4))));
                    p += 4;
                    break;

                case 105: // 'i'
                case 114: // 'r'
                    typeArr.add(new Character('i'));
                    argArr.add(new Integer(Bytes.toInt(Bytes.copy(args, p, 4))));
                    p += 4;
                    break;

                case 102: // 'f'
                    typeArr.add(new Character('f'));
                    argArr.add(new Float(Bytes.toFloat(Bytes.copy(args, p, 4))));
                    p += 4;
                    break;

                case 104: // 'h'
                case 116: // 't'
                    typeArr.add(new Character('h'));
                    argArr.add(new Long(Bytes.toLong(Bytes.copy(args, p, 8))));
                    p += 8;
                    break;

                case 100: // 'd'
                    typeArr.add(new Character('d'));
                    argArr.add(new Double(Bytes.toDouble(Bytes.copy(args, p, 8))));
                    p += 8;
                    break;

                case 83: // 'S'
                case 115: // 's'
                    typeArr.add(new Character('s'));
                    byte remaining[] = Bytes.copy(args, p);
                    ArrayList arr = getStringAndData(remaining, remaining.length);
                    if(arr.size() > 1)
                    {
                        argArr.add((String)arr.get(0));
                        p += ((Integer)arr.get(1)).intValue();
                    }
                    break;

                case 84: // 'T'
                    typeArr.add(new Character('T'));
                    argArr.add(new Boolean(true));
                    break;

                case 70: // 'F'
                    typeArr.add(new Character('F'));
                    argArr.add(new Boolean(false));
                    break;

                case 78: // 'N'
                    typeArr.add(new Character('N'));
                    argArr.add(null);
                    break;

                case 73: // 'I'
                    typeArr.add(new Character('I'));
                    break;

                case 71: // 'G'
                case 72: // 'H'
                case 74: // 'J'
                case 75: // 'K'
                case 76: // 'L'
                case 77: // 'M'
                case 79: // 'O'
                case 80: // 'P'
                case 81: // 'Q'
                case 82: // 'R'
                case 85: // 'U'
                case 86: // 'V'
                case 87: // 'W'
                case 88: // 'X'
                case 89: // 'Y'
                case 90: // 'Z'
                case 92: // '\\'
                case 94: // '^'
                case 95: // '_'
                case 96: // '`'
                case 97: // 'a'
                case 101: // 'e'
                case 103: // 'g'
                case 106: // 'j'
                case 107: // 'k'
                case 108: // 'l'
                case 110: // 'n'
                case 111: // 'o'
                case 112: // 'p'
                case 113: // 'q'
                default:
                    System.out.println(" getTypeTaggedArgs() [Unrecognized type tag " + block[thisType] + "]");
                    break;
                }
            }

        }
        ArrayList returnValue[] = new ArrayList[2];
        returnValue[0] = typeArr;
        returnValue[1] = argArr;
        return returnValue;
    }

    public ArrayList[] getTypesAndArgs(byte block[])
    {
        int n = block.length;
        ArrayList arr[] = new ArrayList[2];
        if(n != 0)
        {
            if(block[0] == 44)
            {
                if(block[1] != 44)
                    arr = getTypeTaggedArgs(block);
                else
                    arr = getHeuristicallyTypeGuessedArgs(block);
            } else
            {
                arr = getHeuristicallyTypeGuessedArgs(block);
            }
        }
        return arr;
    }

    public void killServer()
    {
        oscSocket.close();
        thrThis = null;
        System.out.println("ERROR > OscReceiver ... stopped");
        if(oscP5 != null)
            oscP5.receiverStopped();
    }

    int msgSize()
    {
        return messageINcontainer.length;
    }

    public boolean parseOscPacket(byte datagram[], int n, OscCorePacket packet)
    {
        boolean returnFlag = true;
        if(n % 4 != 0)
        {
            System.out.println(" parseOscPacket() SynthControl packet size (" + n + ") not a multiple of 4 bytes, dropped it.");
            returnFlag = false;
            return false;
        }
        String dataString = new String(datagram);
        if(n >= 8 && dataString.startsWith("#bundle"))
        {
            if(n < 16)
            {
                System.out.println(" parseOscPacket() Bundle message too small (" + n + " bytes) for time tag, dropped it.");
                returnFlag = false;
                return false;
            }
            Long time = new Long(Bytes.toLong(Bytes.copy(datagram, 8, 8)));
            packet.setTime(time.longValue());
            int size;
            for(int i = 16; i < n; i += 4 + size)
            {
                size = Bytes.toInt(Bytes.copy(datagram, i, i + 4));
                if(size % 4 != 0)
                {
                    System.out.println(" parseOscPacket() Bad size count" + size + "in bundle (not a multiple of 4)");
                    returnFlag = false;
                    return false;
                }
                if(size + i + 4 > n)
                {
                    System.out.println(" parseOscPacket() Bad size count" + size + "in bundle" + "(only" + (n - i - 4) + "bytes left in entire bundle)");
                    returnFlag = false;
                    return false;
                }
                byte remaining[] = Bytes.copy(datagram, i + 4);
                if(parseOscPacket(remaining, size, packet))
                    returnFlag = true;
                else
                    returnFlag = false;
            }

        } else
        {
            ArrayList nameAndData = getStringAndData(datagram, n);
            if(nameAndData.size() > 1)
            {
                String name = (String)nameAndData.get(0);
                if(firstNameFlag)
                {
                    packet.msgName = name;
                    firstNameFlag = false;
                }
                OscCoreMessage message = new OscCoreMessage(name);
                byte data[] = (byte[])nameAndData.get(nameAndData.size() - 1);
                ArrayList typesAndArgs[] = getTypesAndArgs(data);
                message.setTypesAndArgs(typesAndArgs[0], typesAndArgs[1]);
                packet.addMessage(message);
            } else
            {
                returnFlag = false;
            }
        }
        return returnFlag;
    }

    public void run()
    {
        while(Thread.currentThread() == thrThis) 
        {
            try
            {
                oscSocket = new DatagramSocket(port);
                System.out.println("osc receiver started on port: " + port);
                do
                {
                    byte datagram[] = new byte[byteSize];
                    DatagramPacket packet = new DatagramPacket(datagram, datagram.length);
                    oscSocket.receive(packet);
                    OscCorePacket oscp = new OscCorePacket();
                    oscp.address = packet.getAddress();
                    oscp.port = packet.getPort();
                    firstNameFlag = true;
                    if(parseOscPacket(datagram, packet.getLength(), oscp))
                    {
                        ArrayList tArr = oscp.extractPacket();
                        int tmpSize = tArr.size();
                        if(tmpSize > 0)
                        {
                            messageINcontainer = new MessageINcontainer[tmpSize];
                            for(int i = 0; i < tmpSize; i++)
                            {
                                ArrayList msgArr = (ArrayList)tArr.get(i);
                                messageINcontainer[i] = new MessageINcontainer();
                                messageINcontainer[i].addAddrPattern((String)msgArr.get(0));
                                messageINcontainer[i].setStructure((ArrayList)msgArr.get(1));
                                messageINcontainer[i].setData((ArrayList)msgArr.get(2));
                                messageINcontainer[i].setTypetag();
                                netFlag = true;
                                if(oscP5 != null)
                                {
                                    if(oscP5.isMethod)
                                        oscP5.call(messageINcontainer[i]);
                                }
                            }

                        }
                    }
                } while(true);
            }
            catch(IOException ioe)
            {
                System.out.println("ERROR > OscReceiver ...Stopping OscP5 receiver");
            }
            finally
            {
                killServer();
            }
        }
    }

    public void start()
    {
        if(thrThis == null)
        {
            thrThis = new Thread(this);
            thrThis.start();
        }
    }

    private int byteSize;
    private boolean firstNameFlag;
    public MessageINcontainer messageINcontainer[];
    boolean netFlag;
    private OscP5 oscP5;
    private DatagramSocket oscSocket;
    private int port;
    private Thread thrThis;
}
