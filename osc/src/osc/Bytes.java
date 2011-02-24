// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Bytes.java

package osc;


public class Bytes
{

    public Bytes()
    {
    }

    public static byte[] append(byte abyte0[], byte abyte1[], byte abyte2[])
    {
        byte abyte3[] = new byte[abyte0.length + abyte1.length + abyte2.length];
        System.arraycopy(abyte0, 0, abyte3, 0, abyte0.length);
        System.arraycopy(abyte1, 0, abyte3, abyte0.length, abyte1.length);
        System.arraycopy(abyte2, 0, abyte3, abyte0.length + abyte1.length, abyte2.length);
        return abyte3;
    }

    public static byte[] append(byte abyte0[], byte abyte1[])
    {
        byte abyte2[] = new byte[abyte0.length + abyte1.length];
        System.arraycopy(abyte0, 0, abyte2, 0, abyte0.length);
        System.arraycopy(abyte1, 0, abyte2, abyte0.length, abyte1.length);
        return abyte2;
    }

    public static boolean areEqual(byte abyte0[], byte abyte1[])
    {
        int i = abyte0.length;
        if(i != abyte1.length)
            return false;
        for(int j = 0; j < i; j++)
            if(abyte0[j] != abyte1[j])
                return false;

        return true;
    }

    public static byte[] copy(byte abyte0[], int i, int j)
    {
        byte abyte1[] = new byte[j];
        System.arraycopy(abyte0, i, abyte1, 0, j);
        return abyte1;
    }

    public static byte[] copy(byte abyte0[], int i)
    {
        return copy(abyte0, i, abyte0.length - i);
    }

    public static void merge(byte abyte0[], byte abyte1[], int i, int j)
    {
        System.arraycopy(abyte0, 0, abyte1, i, j);
    }

    public static void merge(byte abyte0[], byte abyte1[])
    {
        System.arraycopy(abyte0, 0, abyte1, 0, abyte0.length);
    }

    public static void merge(byte abyte0[], byte abyte1[], int i)
    {
        System.arraycopy(abyte0, 0, abyte1, i, abyte0.length);
    }

    public static void merge(byte abyte0[], byte abyte1[], int i, int j, int k)
    {
        System.arraycopy(abyte0, i, abyte1, j, k);
    }

    public static byte[] toBytes(long l, byte abyte0[])
    {
        abyte0[7] = (byte)(int)l;
        l >>>= 8;
        abyte0[6] = (byte)(int)l;
        l >>>= 8;
        abyte0[5] = (byte)(int)l;
        l >>>= 8;
        abyte0[4] = (byte)(int)l;
        l >>>= 8;
        abyte0[3] = (byte)(int)l;
        l >>>= 8;
        abyte0[2] = (byte)(int)l;
        l >>>= 8;
        abyte0[1] = (byte)(int)l;
        l >>>= 8;
        abyte0[0] = (byte)(int)l;
        return abyte0;
    }

    public static byte[] toBytes(long l)
    {
        return toBytes(l, new byte[8]);
    }

    public static byte[] toBytes(int i, byte abyte0[])
    {
        abyte0[3] = (byte)i;
        i >>>= 8;
        abyte0[2] = (byte)i;
        i >>>= 8;
        abyte0[1] = (byte)i;
        i >>>= 8;
        abyte0[0] = (byte)i;
        return abyte0;
    }

    public static byte[] toBytes(int i)
    {
        return toBytes(i, new byte[4]);
    }

    public static double toDouble(byte abyte0[])
    {
        long l = toLong(abyte0);
        return Double.longBitsToDouble(l);
    }

    public static float toFloat(byte abyte0[])
    {
        int i = toInt(abyte0);
        return Float.intBitsToFloat(i);
    }

    public static int toInt(byte abyte0[])
    {
        return (abyte0[3] & 0xff) + ((abyte0[2] & 0xff) << 8) + ((abyte0[1] & 0xff) << 16) + ((abyte0[0] & 0xff) << 24);
    }

    public static long toLong(byte abyte0[])
    {
        return ((long)abyte0[7] & 255L) + (((long)abyte0[6] & 255L) << 8) + (((long)abyte0[5] & 255L) << 16) + (((long)abyte0[4] & 255L) << 24) + (((long)abyte0[3] & 255L) << 32) + (((long)abyte0[2] & 255L) << 40) + (((long)abyte0[1] & 255L) << 48) + (((long)abyte0[0] & 255L) << 56);
    }

    public static String toString(byte abyte0[])
    {
        return toString(abyte0, 0, abyte0.length);
    }

    public static String toString(byte abyte0[], int i, int j)
    {
        char ac[] = new char[j * 2];
        int k = i;
        int l = 0;
        for(; k < i + j; k++)
        {
            byte byte0 = abyte0[k];
            ac[l++] = hexDigits[byte0 >>> 4 & 0xf];
            ac[l++] = hexDigits[byte0 & 0xf];
        }

        return new String(ac);
    }

    private static final char hexDigits[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
        'A', 'B', 'C', 'D', 'E', 'F'
    };

}
