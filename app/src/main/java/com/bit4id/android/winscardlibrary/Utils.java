package com.bit4id.android.winscardlibrary;

/**
 * Created by Miguel Pazo (miguelpazo.com) on 02/01/2017.
 */

import java.util.Locale;
import java.util.Random;

public class Utils {
    public static Integer randInt(int min, int max) {
        Random rand = new Random();

        int randomNum = rand.nextInt(max - min + 1) + min;

        return Integer.valueOf(randomNum);
    }

    public static String toHexString(byte[] array) {
        String bufferString = "";
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                String hexChar = Integer.toHexString(array[i] & 0xFF);
                if (hexChar.length() == 1) {
                    hexChar = "0" + hexChar;
                }
                bufferString = bufferString + hexChar.toUpperCase(Locale.US) + " ";
            }
        }
        return bufferString;
    }

    private static boolean isHexNumber(byte value) {
        if (((value < 48) || (value > 57)) && ((value < 65) || (value > 70)) && ((value < 97) || (value > 102))) {
            return false;
        }
        return true;
    }

    public static boolean isHexNumber(String string) {
        if (string == null) {
            throw new NullPointerException("string was null");
        }
        boolean flag = true;
        for (int i = 0; i < string.length(); i++) {
            char cc = string.charAt(i);
            if (!isHexNumber((byte) cc)) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    private static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);

        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    public static byte[] hexString2Bytes(String string) {
        if (string == null) {
            throw new NullPointerException("string was null");
        }
        int len = string.length();
        if (len == 0) {
            return new byte[0];
        }
        if (len % 2 == 1) {
            throw new IllegalArgumentException("string length should be an even number");
        }
        byte[] ret = new byte[len / 2];
        byte[] tmp = string.getBytes();
        for (int i = 0; i < len; i += 2) {
            if ((!isHexNumber(tmp[i])) || (!isHexNumber(tmp[(i + 1)]))) {
                throw new NumberFormatException("string contained invalid value");
            }
            ret[(i / 2)] = uniteBytes(tmp[i], tmp[(i + 1)]);
        }
        return ret;
    }
}
