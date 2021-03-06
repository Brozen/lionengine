/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.util;

import java.nio.ByteBuffer;
import java.util.Locale;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;

/**
 * Conversion class utility.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class UtilConversion
{
    /**
     * Convert an integer to an array of byte.
     * 
     * @param value The integer value.
     * @return The byte array.
     */
    public static byte[] intToByteArray(int value)
    {
        return new byte[]
        {
            (byte) (value >>> Constant.BYTE_4), (byte) (value >>> Constant.BYTE_3), (byte) (value >>> Constant.BYTE_2),
            (byte) value
        };
    }

    /**
     * Convert a byte array to an integer.
     * 
     * @param bytes The byte array.
     * @return The integer value.
     */
    public static int byteArrayToInt(byte[] bytes)
    {
        return ByteBuffer.wrap(bytes).getInt();
    }

    /**
     * Convert a short to an array of byte.
     * 
     * @param value The short value.
     * @return The byte array.
     */
    public static byte[] shortToByteArray(short value)
    {
        return new byte[]
        {
            (byte) (value >>> Constant.BYTE_2), (byte) value
        };
    }

    /**
     * Convert a byte array to an integer.
     * 
     * @param bytes The byte array.
     * @return The integer value.
     */
    public static short byteArrayToShort(byte[] bytes)
    {
        return ByteBuffer.wrap(bytes).getShort();
    }

    /**
     * Return the java byte value [-128|127] from an unsigned byte [0|255].
     * 
     * @param value The unsigned byte value [0|255].
     * @return The java byte value [-128|127].
     */
    public static byte fromUnsignedByte(short value)
    {
        return (byte) (value - (Byte.MAX_VALUE + 1));
    }

    /**
     * Return the unsigned byte value [0|255] from the java byte value [-128|127].
     * 
     * @param value The java byte value [-128|127].
     * @return The unsigned byte value [0|255].
     */
    public static short toUnsignedByte(byte value)
    {
        return (short) (value - Byte.MIN_VALUE);
    }

    /**
     * Return the java short value [-32768|32767] from an unsigned short [0|65535].
     * 
     * @param value The unsigned short value [0|65535].
     * @return The java short value [-32768|32767].
     */
    public static short fromUnsignedShort(int value)
    {
        return (short) (value - (Short.MAX_VALUE + 1));
    }

    /**
     * Return the unsigned short value [0|65535] from the java short value [-32768|32767].
     * 
     * @param value The java short value [-32768|32767].
     * @return The unsigned short value [0|65535].
     */
    public static int toUnsignedShort(short value)
    {
        return value - Short.MIN_VALUE;
    }

    /**
     * Return the masked value by 0xFF.
     * 
     * @param value The input value.
     * @return The masked value.
     */
    public static int mask(int value)
    {
        return value & 0xFF;
    }

    /**
     * Convert boolean to integer representation.
     * 
     * @param value The boolean value.
     * @return <code>1</code> if <code>true</code>, <code>0</code> if <code>false</code>.
     */
    public static int boolToInt(boolean value)
    {
        if (value)
        {
            return 1;
        }
        return 0;
    }

    /**
     * Convert number to binary array representation.
     * 
     * @param number The number to convert.
     * @param length The binary length.
     * @return The boolean representation.
     */
    public static boolean[] toBinary(int number, int length)
    {
        final boolean[] binary = new boolean[length];
        for (int i = 0; i < length; i++)
        {
            binary[length - 1 - i] = (1 << i & number) != 0;
        }
        return binary;
    }

    /**
     * Convert binary array to number representation.
     * 
     * @param binary The binary to convert.
     * @return The number representation.
     */
    public static int fromBinary(boolean[] binary)
    {
        int number = 0;
        for (final boolean current : binary)
        {
            number = number << 1 | boolToInt(current);
        }
        return number;
    }

    /**
     * Invert binary array (apply a negation to each value).
     * 
     * @param binary The binary array.
     * @return The inverted binary array representation.
     */
    public static boolean[] invert(boolean[] binary)
    {
        final boolean[] inverted = new boolean[binary.length];
        for (int i = 0; i < inverted.length; i++)
        {
            inverted[i] = !binary[i];
        }
        return inverted;
    }

    /**
     * Convert a string to title case.
     * 
     * @param string The string to convert.
     * @return The string in title case.
     */
    public static String toTitleCase(String string)
    {
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < string.length(); i++)
        {
            final String next = string.substring(i, i + 1);
            if (i == 0)
            {
                result.append(next.toUpperCase(Locale.ENGLISH));
            }
            else
            {
                result.append(next.toLowerCase(Locale.ENGLISH));
            }
        }
        return result.toString();
    }

    /**
     * Convert a string to a pure title case for each word, replacing special characters by space.
     * 
     * @param string The string to convert.
     * @return The string in title case.
     */
    public static String toTitleCaseWord(String string)
    {
        final String[] words = string.replaceAll("\\W|_", Constant.SPACE).split(Constant.SPACE);
        final StringBuilder title = new StringBuilder();
        for (int i = 0; i < words.length; i++)
        {
            title.append(toTitleCase(words[i]));
            if (i < words.length - 1)
            {
                title.append(Constant.SPACE);
            }
        }
        return title.toString();
    }

    /**
     * Private constructor.
     */
    private UtilConversion()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
