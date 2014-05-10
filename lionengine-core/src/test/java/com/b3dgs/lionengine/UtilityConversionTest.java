/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test utility conversion class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class UtilityConversionTest
{
    /**
     * Test the core class.
     * 
     * @throws NoSuchMethodException If error.
     * @throws IllegalAccessException If error.
     * @throws InstantiationException If error.
     * @throws InvocationTargetException If success.
     */
    @Test(expected = InvocationTargetException.class)
    public void testUtilityConversionClass() throws NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException
    {
        final Constructor<UtilConversion> utilityConversion = UtilConversion.class.getDeclaredConstructor();
        utilityConversion.setAccessible(true);
        final UtilConversion clazz = utilityConversion.newInstance();
        Assert.assertNotNull(clazz);
    }

    /**
     * Test the utility conversion short.
     */
    @Test
    public void testUtilityConversionShort()
    {
        final short s = 12345;
        Assert.assertEquals(s, UtilConversion.byteArrayToShort(UtilConversion.shortToByteArray(s)));
        Assert.assertEquals(s, UtilConversion.fromUnsignedShort(UtilConversion.toUnsignedShort(s)));
    }

    /**
     * Test the utility conversion int.
     */
    @Test
    public void testUtilityConversionInt()
    {
        final int i = 123456789;
        Assert.assertEquals(i, UtilConversion.byteArrayToInt(UtilConversion.intToByteArray(i)));
    }

    /**
     * Test the utility conversion byte.
     */
    @Test
    public void testUtilityConversionByte()
    {
        final byte b = 123;
        Assert.assertEquals(b, UtilConversion.fromUnsignedByte(UtilConversion.toUnsignedByte(b)));
    }

    /**
     * Test the utility conversion string.
     */
    @Test
    public void testUtilityConversionString()
    {
        final String title = UtilConversion.toTitleCase("title");
        Assert.assertEquals("Title", title);
    }
}
