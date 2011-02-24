package oscbase;
// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MessageOUT.java

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Vector;
import osc.Bytes;

public abstract class MessageOUT
{

    public MessageOUT(String s, Object o[])
    {
        msgName = "";
        types = new ArrayList();
        args = new ArrayList();
        msgName = s;
        add(o);
    }

    public MessageOUT(String s)
    {
        msgName = "";
        types = new ArrayList();
        args = new ArrayList();
        msgName = s;
    }

    public void add(Vector vector)
    {
        types.add(TYPE_O);
        for(int i = 0; i < vector.size(); i++)
        {
            if(vector.elementAt(i) instanceof String)
                add((String)vector.elementAt(i));
            else
            if(vector.elementAt(i) instanceof Integer)
                add((Integer)vector.elementAt(i));
            else
            if(vector.elementAt(i) instanceof Float)
                add((Float)vector.elementAt(i));
            else
            if(vector.elementAt(i) instanceof Boolean)
                add((Boolean)vector.elementAt(i));
            else
            if(vector.elementAt(i) instanceof byte[])
                add((byte[])vector.elementAt(i));
            else
            if(vector.elementAt(i) instanceof Character)
                add((Character)vector.elementAt(i));
            else
            if(vector.elementAt(i) instanceof Vector)
                add((Vector)vector.elementAt(i));
            else
                System.out.println("NOT DEFINED OBJECT");
        }

        types.add(TYPE_C);
    }

    private void add(Object o[], boolean isArray)
    {
        if(isArray)
            types.add(TYPE_O);
        for(int i = 0; i < o.length; i++)
        {
            if(o[i] instanceof String)
                add((String)o[i]);
            else
            if(o[i] instanceof Integer)
                add((Integer)o[i]);
            else
            if(o[i] instanceof Float)
                add((Float)o[i]);
            else
            if(o[i] instanceof Boolean)
                add((Boolean)o[i]);
            else
            if(o[i] instanceof byte[])
                add((byte[])o[i]);
            else
            if(o[i] instanceof Character)
                add((Character)o[i]);
            else
            if(o[i] instanceof Object[])
                add((Object[])o[i], true);
            else
                System.out.println("NOT DEFINED OBJECT");
        }

        if(isArray)
            types.add(TYPE_C);
    }

    public void add(Object o[])
    {
        add(o, false);
    }

    public void add(String as[], boolean isArray)
    {
        if(isArray)
            types.add(TYPE_O);
        add(as);
        if(isArray)
            types.add(TYPE_C);
    }

    public void add(float af[], boolean isArray)
    {
        if(isArray)
            types.add(TYPE_O);
        add(af);
        if(isArray)
            types.add(TYPE_C);
    }

    public void add(int iArr[], boolean isArray)
    {
        if(isArray)
            types.add(TYPE_O);
        add(iArr);
        if(isArray)
            types.add(TYPE_O);
    }

    public void add(String as[])
    {
        for(int i = 0; i < as.length; i++)
            add(as[i]);

    }

    public void add(float af[])
    {
        for(int i = 0; i < af.length; i++)
            add(af[i]);

    }

    public void add(int iArr[])
    {
        for(int i = 0; i < iArr.length; i++)
            add(iArr[i]);

    }

    public void add(int channel, int status, int value1, int value2)
    {
        types.add(TYPE_M);
        byte tByte[] = new byte[4];
        tByte[0] = (byte)channel;
        tByte[1] = (byte)status;
        tByte[2] = (byte)value1;
        tByte[3] = (byte)value2;
        args.add(tByte);
    }

    public void add(byte b[])
    {
        types.add(TYPE_B);
        args.add(makeBlob(b));
    }

    public void add(char c)
    {
        types.add(TYPE_c);
        args.add(new Character(c));
    }

    public void add(Character c)
    {
        types.add(TYPE_c);
        args.add(c);
    }

    public void add(Float float1)
    {
        types.add(TYPE_F);
        args.add(float1);
    }

    public void add(Integer integer)
    {
        types.add(TYPE_I);
        args.add(integer);
    }

    public void add(Boolean flag)
    {
        if(flag.booleanValue())
            types.add(TYPE_t);
        else
            types.add(TYPE_f);
    }

    public void add(boolean flag)
    {
        if(flag)
            types.add(TYPE_t);
        else
            types.add(TYPE_f);
    }

    public void add(double d)
    {
        types.add(TYPE_D);
        args.add(new Double(d));
    }

    public void add(float f)
    {
        types.add(TYPE_F);
        args.add(new Float(f));
    }

    public void add(String s)
    {
        types.add(TYPE_S);
        args.add(s);
    }

    public void add(int i)
    {
        types.add(TYPE_I);
        args.add(new Integer(i));
    }

    public void add()
    {
        types.add(TYPE_N);
    }

    public void addMidi(int channel, int status, int value1, int value2)
    {
        add(channel, status, value1, value2);
    }

    public ArrayList getArgs()
    {
        return args;
    }

    public String getMsgName()
    {
        return msgName;
    }

    public ArrayList getTypes()
    {
        return types;
    }

    private byte[] makeBlob(byte b[])
    {
        int tLength = b.length;
        int t = tLength % 4;
        byte tByte[] = Bytes.append(Bytes.toBytes(tLength), b);
        if(t != 0)
            tByte = Bytes.append(tByte, new byte[4 - t]);
        return tByte;
    }

    public void printMessage()
    {
        System.out.println("OUT --- " + msgName + "  " + types + "   " + args);
    }

    public static final boolean IS_ARRAY = true;
    private static final Character TYPE_B = new Character('b');
    private static final Character TYPE_C = new Character(']');
    private static final Character TYPE_D = new Character('d');
    private static final Character TYPE_F = new Character('f');
    private static final Character TYPE_I = new Character('i');
    private static final Character TYPE_M = new Character('m');
    private static final Character TYPE_N = new Character('N');
    private static final Character TYPE_O = new Character('[');
    private static final Character TYPE_S = new Character('s');
    private static final Character TYPE_c = new Character('c');
    private static final Character TYPE_f = new Character('F');
    private static final Character TYPE_t = new Character('T');
    private ArrayList args;
    private String msgName;
    private ArrayList types;

}
