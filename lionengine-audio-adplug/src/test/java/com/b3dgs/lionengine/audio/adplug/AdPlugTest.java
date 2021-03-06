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
package com.b3dgs.lionengine.audio.adplug;

import java.lang.reflect.Field;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Architecture;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.OperatingSystem;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.audio.AudioFormat;
import com.b3dgs.lionengine.audio.AudioVoidFormat;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.util.UtilEnum;
import com.b3dgs.lionengine.util.UtilReflection;

/**
 * Test the AdPlug player.
 */
public class AdPlugTest
{
    /** Binding. */
    private static AdPlug adplug;
    /** Media music. */
    private static Media music;

    /**
     * Prepare tests.
     */
    @BeforeClass
    public static void prepare()
    {
        try
        {
            final AudioFormat<?> format = AdPlugFormat.getFailsafe();
            AudioFactory.addFormat(format);
            Medias.setLoadFromJar(AdPlugTest.class);
            music = Medias.create("music.lds");
            adplug = AudioFactory.loadAudio(music, AdPlug.class);
            format.close();
        }
        catch (final LionEngineException exception)
        {
            final String message = exception.getMessage();
            Assert.assertTrue(message,
                              message.contains(AdPlugFormat.ERROR_LOAD_LIBRARY)
                                       || message.contains(AudioFactory.ERROR_FORMAT));
            final boolean skip = message.contains(AdPlugFormat.ERROR_LOAD_LIBRARY)
                                 || message.contains(AudioFactory.ERROR_FORMAT);
            Assume.assumeFalse("AdPlug not supported on test machine - Test skipped", skip);
        }
    }

    /**
     * Clean up tests.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
    }

    /**
     * Test with <code>null</code> argument.
     */
    @Test(expected = LionEngineException.class)
    public void testNullArgument()
    {
        Assert.assertNotNull(AudioFactory.loadAudio(null, AdPlug.class));
    }

    /**
     * Test with missing library.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testMissingLibrary() throws Exception
    {
        final Field field = AdPlugFormat.class.getDeclaredField("LIBRARY_NAME");
        final String back = UtilReflection.getField(AdPlugFormat.class, "LIBRARY_NAME");
        try
        {
            UtilEnum.setStaticFinal(field, "void");
            Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
            Assert.assertEquals(AudioVoidFormat.class, AdPlugFormat.getFailsafe().getClass());
            Verbose.info("****************************************************************************************");
        }
        finally
        {
            UtilEnum.setStaticFinal(field, back);
        }
    }

    /**
     * Test with other architecture linkage error.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testArch() throws Exception
    {
        final Field field = Architecture.class.getDeclaredField("ARCHI");
        final Architecture back = UtilReflection.getField(Architecture.class, "ARCHI");
        final Architecture x64 = Architecture.X64;
        try
        {
            UtilEnum.setStaticFinal(field, Architecture.getArchitecture() == x64 ? Architecture.X86 : x64);
            Assert.assertNull(new AdPlugFormat());
        }
        finally
        {
            UtilEnum.setStaticFinal(field, back);
        }
    }

    /**
     * Test with other OS linkage error.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testOs() throws Exception
    {
        final Field field = OperatingSystem.class.getDeclaredField("OS");
        final OperatingSystem back = UtilReflection.getField(OperatingSystem.class, "OS");
        final OperatingSystem unix = OperatingSystem.UNIX;
        try
        {
            UtilEnum.setStaticFinal(field,
                                    OperatingSystem.getOperatingSystem() == unix ? OperatingSystem.WINDOWS : unix);
            Assert.assertNull(new AdPlugFormat());
        }
        finally
        {
            UtilEnum.setStaticFinal(field, back);
        }
    }

    /**
     * Test with negative volume.
     */
    @Test(expected = LionEngineException.class)
    public void testNegativeVolume()
    {
        adplug.setVolume(-1);
        Assert.fail();
    }

    /**
     * Test with out of range volume.
     */
    @Test(expected = LionEngineException.class)
    public void testOutOfRangeVolume()
    {
        adplug.setVolume(101);
        Assert.fail();
    }

    /**
     * Test AdPlug sequence.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testAdPlug() throws InterruptedException
    {
        adplug.setVolume(40);
        adplug.play();
        Thread.sleep(500);
        adplug.pause();
        Thread.sleep(500);
        adplug.setVolume(60);
        adplug.resume();
        Thread.sleep(500);
        adplug.stop();
    }

    /**
     * Test AdPlug stress.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testStress() throws InterruptedException
    {
        adplug.play();
        adplug.stop();
        adplug.play();
        Thread.sleep(Constant.HUNDRED);
        adplug.stop();
        adplug.play();
        adplug.pause();
        adplug.resume();
        for (int i = 0; i < 5; i++)
        {
            adplug.play();
            Thread.sleep(Constant.HUNDRED);
        }
        Thread.sleep(250);
        adplug.stop();
        adplug.play();
        adplug.stop();
    }
}
