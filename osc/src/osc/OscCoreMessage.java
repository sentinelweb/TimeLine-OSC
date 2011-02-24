// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OscCoreMessage.java

package osc;

import java.io.*;
import java.util.*;

public class OscCoreMessage
{

    public OscCoreMessage(String s)
    {
        name = s;
        types = new ArrayList();
        arguments = new ArrayList();
    }

    public void addArg(Character character, Object obj)
    {
        types.add(character);
        arguments.add(obj);
    }

    public void addBracket(Character character)
    {
        types.add(character);
    }

    private void alignStream(ByteArrayOutputStream bytearrayoutputstream)
        throws IOException
    {
        int i = 4 - bytearrayoutputstream.size() % 4;
        for(int j = 0; j < i; j++)
            bytearrayoutputstream.write(0);

    }

    public String checkString(String s)
    {
        int i = s.length();
        for(int j = 0; j < 4 - i % 4; j++)
            s = s + "\0";

        return s;
    }

    public ArrayList getArgs()
    {
        return arguments;
    }

    public byte[] getByteArray()
        throws IOException
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        dataoutputstream.writeBytes(name);
        alignStream(bytearrayoutputstream);
        dataoutputstream.writeByte(44);
        for(int i = 0; i < types.size(); i++)
        {
            char c = ((Character)types.get(i)).charValue();
            dataoutputstream.writeByte(c);
        }

        alignStream(bytearrayoutputstream);
        Iterator itTypes = types.iterator();
        Iterator itArgs = arguments.iterator();
        while(itTypes.hasNext()) 
        {
            char c1 = ((Character)itTypes.next()).charValue();
            switch(c1)
            {
            case 105: // 'i'
                dataoutputstream.writeInt(((Integer)itArgs.next()).intValue());
                break;

            case 102: // 'f'
                dataoutputstream.writeFloat(((Float)itArgs.next()).floatValue());
                break;

            case 104: // 'h'
                dataoutputstream.writeLong(((Long)itArgs.next()).longValue());
                break;

            case 100: // 'd'
                dataoutputstream.writeDouble(((Double)itArgs.next()).doubleValue());
                break;

            case 115: // 's'
                dataoutputstream.writeBytes(checkString((String)itArgs.next()));
                break;
            }
        }
        return bytearrayoutputstream.toByteArray();
    }

    public String getName()
    {
        if(types == null)
            return "Osc Message getName > ERROR Types not set";
        else
            return name;
    }

    public ArrayList getTypes()
    {
        return types;
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

    public void setTypesAndArgs(ArrayList arrTypes, ArrayList arrArgs)
    {
        types = arrTypes;
        arguments = arrArgs;
    }

    private ArrayList arguments;
    private String name;
    private ArrayList types;
}
