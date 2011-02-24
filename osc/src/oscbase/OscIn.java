package oscbase;
// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OscIn.java

import java.util.*;

public class OscIn
{

    public OscIn()
    {
        addrPattern = "";
        typeString = "";
        structure = new ArrayList();
        data = new ArrayList();
        dataSize = 0;
    }

    public void addAddrPattern(String s)
    {
        addrPattern = s;
    }

    public boolean checkAddrPattern(String s)
    {
        return s.equals(addrPattern);
    }

    public boolean checkTypes(String s)
    {
        return s.equals(typeString);
    }

    public boolean checkTypetag(String s)
    {
        return s.equals(typeString);
    }

    public String getAddrPattern()
    {
        return addrPattern;
    }

    public byte[] getBlob(int thePos)
    {
        if(thePos < dataSize)
        {
            if(data.get(thePos) instanceof byte[])
                return (byte[])data.get(thePos);
        }
        return null;
    }

    public boolean getBoolean(int thePos)
    {
        if(thePos < dataSize)
        {
            if(data.get(thePos) instanceof Boolean)
                return ((Boolean)data.get(thePos)).booleanValue();
        }
        return false;
    }

    public char getChar(int thePos)
    {
        if(thePos < dataSize)
        {
            if(data.get(thePos) instanceof Character)
                return (char)((Integer)data.get(thePos)).intValue();
        }
        return '\0';
    }

    public Object[] getData()
    {
        return data.toArray();
    }

    public ArrayList getDataList()
    {
        return data;
    }

    public float getFloat(int thePos)
    {
        if(thePos < dataSize)
        {
            if(data.get(thePos) instanceof Float)
                return ((Float)data.get(thePos)).floatValue();
        }
        return (0.0F / 0.0F);
    }

    public int getInt(int thePos)
    {
        if(thePos < dataSize)
        {
            if(data.get(thePos) instanceof Integer)
                return ((Integer)data.get(thePos)).intValue();
        }
        return 0;
    }

    public int[] getMidi(int thePos)
    {
        if(thePos < dataSize)
        {
            if(data.get(thePos) instanceof byte[])
            {
                if(((byte[])data.get(thePos)).length == 4)
                {
                    int tInt[] = new int[4];
                    byte tByte[] = (byte[])data.get(thePos);
                    for(int i = 0; i < 4; i++)
                        tInt[i] = tByte[i];

                    return tInt;
                }
            }
        }
        return null;
    }

    public byte[] getMidiBytes(int thePos)
    {
        if(thePos < dataSize)
        {
            if(data.get(thePos) instanceof byte[])
            {
                if(((byte[])data.get(thePos)).length == 4)
                    return (byte[])data.get(thePos);
            }
        }
        return null;
    }

    public Vector getOscData()
    {
        Vector tVector = new Vector();
        for(int i = 0; i < data.size(); i++)
            tVector.add(data.get(i));

        return tVector;
    }

    public Vector getOscStructure()
    {
        Vector tVector = new Vector();
        for(int i = 0; i < structure.size(); i++)
            tVector.add(structure.get(i));

        return tVector;
    }

    public String getString(int thePos)
    {
        if(thePos < dataSize)
        {
            if(data.get(thePos) instanceof String)
                return ((String)data.get(thePos)).toString();
        }
        return null;
    }

    public Object[] getStructure()
    {
        return structure.toArray();
    }

    public String getStructureAsString()
    {
        return structure.toString();
    }

    public ArrayList getStructureList()
    {
        return structure;
    }

    public String getTypes()
    {
        return typeString;
    }

    public String getTypetag()
    {
        return typeString;
    }

    public void setData(ArrayList arr)
    {
        dataSize = arr.size();
        data = arr;
    }

    public void setStructure(ArrayList arr)
    {
        structure = arr;
    }

    public void setTypetag()
    {
        for(int i = 0; i < structure.size(); i++)
        {
            if(structure.size() > 0)
                typeString += ( (structure.get(i)));
        }

    }

    public String addrPattern;
    public ArrayList data;
    public int dataSize;
    public ArrayList structure;
    public String typeString;
}
