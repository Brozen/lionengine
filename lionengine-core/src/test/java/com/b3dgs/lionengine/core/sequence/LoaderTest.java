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
package com.b3dgs.lionengine.core.sequence;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.EngineMock;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.filter.FilterBilinear;
import com.b3dgs.lionengine.core.filter.FilterBlur;
import com.b3dgs.lionengine.core.filter.FilterHq2x;
import com.b3dgs.lionengine.core.filter.FilterHq3x;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Screen;
import com.b3dgs.lionengine.graphic.ScreenMock;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the loader class.
 */
public class LoaderTest
{
    /** Output. */
    static final Resolution OUTPUT = new Resolution(640, 480, 60);
    /** Config. */
    static final Config CONFIG = new Config(OUTPUT, 16, true);
    /** Icon. */
    private static Media icon;

    /**
     * Prepare the test.
     */
    @BeforeClass
    public static void prepareTest()
    {
        Medias.setLoadFromJar(LoaderTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        icon = Medias.create("image.png");
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Prepare test.
     */
    @Before
    public void before()
    {
        Engine.start(new EngineMock("LoaderTest", Version.DEFAULT));
    }

    /**
     * Terminate test.
     */
    @After
    public void after()
    {
        Engine.terminate();
    }

    /**
     * Test the loader with no config.
     */
    @Test(expected = LionEngineException.class)
    public void testNullConfig()
    {
        final Loader loader = new Loader();
        loader.start(null, SequenceSingleMock.class).await();
    }

    /**
     * Test the loader with no sequence.
     */
    @Test(expected = LionEngineException.class)
    public void testNullSequence()
    {
        final Loader loader = new Loader();
        loader.start(CONFIG, null).await();
    }

    /**
     * Test the loader with fail sequence.
     */
    @Test(expected = LionEngineException.class)
    public void testFailSequence()
    {
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        try
        {
            final Loader loader = new Loader();
            loader.start(CONFIG, SequenceFailMock.class).await();
        }
        finally
        {
            Verbose.info("****************************************************************************************");
        }
    }

    /**
     * Test the loader with fail next sequence.
     */
    @Test(expected = LionEngineException.class)
    public void testFailNextSequence()
    {
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        try
        {
            final Loader loader = new Loader();
            loader.start(CONFIG, SequenceNextFailMock.class).await();
        }
        finally
        {
            Verbose.info("****************************************************************************************");
        }
    }

    /**
     * Test the loader with malformed sequence.
     */
    @Test(expected = LionEngineException.class)
    public void testMalformedSequence()
    {
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        try
        {
            final Loader loader = new Loader();
            loader.start(CONFIG, SequenceMalformedMock.class).await();
        }
        finally
        {
            Verbose.info("****************************************************************************************");
        }
    }

    /**
     * Test the loader interrupted.
     * 
     * @throws Throwable If error.
     */
    @Test(timeout = SequenceInterruptMock.PAUSE_MILLI * 2L, expected = LionEngineException.class)
    public void testInterrupted() throws Throwable
    {
        final AtomicReference<Throwable> exception = new AtomicReference<Throwable>();
        final Semaphore semaphore = new Semaphore(0);

        final Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                final Loader loader = new Loader();
                final TaskFuture future = loader.start(CONFIG, SequenceInterruptMock.class);
                future.await();
            }
        };
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException(Thread t, Throwable throwable)
            {
                exception.set(throwable);
                semaphore.release();
            }
        });
        thread.start();
        UtilTests.pause(SequenceInterruptMock.PAUSE_MILLI / 2L);
        thread.interrupt();
        semaphore.acquire();
        throw exception.get();
    }

    /**
     * Test the loader interrupted unchecked exception.
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = SequenceInterruptMock.PAUSE_MILLI * 2L)
    public void testInterruptedUnchecked() throws InterruptedException
    {
        try
        {
            Graphics.setFactoryGraphic(new FactoryGraphicMock()
            {
                @Override
                public Screen createScreen(Config config)
                {
                    return null;
                }
            });

            final AtomicReference<Throwable> exception = new AtomicReference<Throwable>();
            final Semaphore semaphore = new Semaphore(0);
            final Thread thread = new Thread()
            {
                @Override
                public void run()
                {
                    final Loader loader = new Loader();
                    final TaskFuture future = loader.start(CONFIG, SequenceInterruptMock.class);
                    future.await();
                }
            };
            thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler()
            {
                @Override
                public void uncaughtException(Thread t, Throwable throwable)
                {
                    exception.set(throwable);
                    semaphore.release();
                }
            });
            Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
            thread.start();
            semaphore.acquire();
            Assert.assertTrue(exception.get().getCause() instanceof NullPointerException);
        }
        finally
        {
            Verbose.info("****************************************************************************************");
            Graphics.setFactoryGraphic(new FactoryGraphicMock());
        }
    }

    /**
     * Test the loader already started.
     */
    @Test(expected = LionEngineException.class)
    public void testStarted()
    {
        final Loader loader = new Loader();
        final TaskFuture future = loader.start(CONFIG, SequenceSingleMock.class);
        try
        {
            loader.start(CONFIG, SequenceSingleMock.class).await();
        }
        finally
        {
            future.await();
        }
    }

    /**
     * Test the loader engine started.
     */
    @Test
    public void testEngineStarted()
    {
        final Loader loader = new Loader();
        loader.start(CONFIG, SequenceSingleMock.class).await();
        Assert.assertTrue(Engine.isStarted());
    }

    /**
     * Test the loader with no icon in windowed mode.
     */
    @Test
    public void testNoIconWindowed()
    {
        final Config config = new Config(OUTPUT, 16, true, Medias.create("void"));
        final Loader loader = new Loader();
        loader.start(config, SequenceSingleMock.class).await();
    }

    /**
     * Test the loader with an icon in windowed mode.
     */
    @Test
    public void testIconWindowed()
    {
        final Config config = new Config(OUTPUT, 16, true, icon);
        final Loader loader = new Loader();
        loader.start(config, SequenceSingleMock.class).await();
    }

    /**
     * Test the loader with an icon in full screen mode.
     */
    @Test
    public void testIconFullScreen()
    {
        final Config config = new Config(OUTPUT, 16, false, icon);
        final Loader loader = new Loader();
        loader.start(config, SequenceSingleMock.class).await();
    }

    /**
     * Test the loader with a single sequence.
     */
    @Test
    public void testSequenceSingle()
    {
        final Loader loader = new Loader();
        loader.start(CONFIG, SequenceSingleMock.class).await();
    }

    /**
     * Test the loader with slow sequence.
     */
    @Test
    public void testSlowSequence()
    {
        final Loader loader = new Loader();
        loader.start(CONFIG, SequenceSlowMock.class).await();
    }

    /**
     * Test the loader with a sequence that have arguments.
     */
    @Test
    public void testSequenceArgument()
    {
        final Loader loader = new Loader();
        loader.start(CONFIG, SequenceArgumentsMock.class, new Object()).await();
    }

    /**
     * Test the loader with timed out screen.
     */
    @Test(expected = LionEngineException.class)
    public void testSequenceTimeout()
    {
        try
        {
            ScreenMock.setScreenWait(true);
            final Loader loader = new Loader();
            Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
            loader.start(CONFIG, SequenceSingleMock.class).await();
        }
        finally
        {
            Verbose.info("****************************************************************************************");
            ScreenMock.setScreenWait(false);
        }
    }

    /**
     * Test the loader interrupted.
     * 
     * @throws Throwable If error.
     */
    @Test(timeout = ScreenMock.READY_TIMEOUT * 2L, expected = LionEngineException.class)
    public void testScreenInterrupted() throws Throwable
    {
        try
        {
            ScreenMock.setScreenWait(true);
            final AtomicReference<Throwable> exception = new AtomicReference<Throwable>();
            final Semaphore semaphore = new Semaphore(0);
            final Thread thread = new Thread()
            {
                @Override
                public void run()
                {
                    final Screen screen = Graphics.createScreen(CONFIG);
                    screen.start();
                    screen.awaitReady();
                }
            };
            thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler()
            {
                @Override
                public void uncaughtException(Thread t, Throwable throwable)
                {
                    exception.set(throwable);
                    semaphore.release();
                }
            });
            thread.start();
            UtilTests.pause(ScreenMock.READY_TIMEOUT / 2L);
            thread.interrupt();
            semaphore.acquire();
            throw exception.get();
        }
        finally
        {
            ScreenMock.setScreenWait(false);
        }
    }

    /**
     * Test the loader with screen direct.
     */
    @Test
    public void testDirect()
    {
        final Resolution output = new Resolution(320, 240, 60);
        final Config config = new Config(output, 16, true);
        final Loader loader = new Loader();
        loader.start(config, SequenceSingleMock.class).await();
    }

    /**
     * Test the loader with sequence terminate engine.
     */
    @Test
    public void testEngineTerminate()
    {
        final Resolution output = new Resolution(320, 240, 60);
        final Config config = new Config(output, 16, true);
        final Loader loader = new Loader();
        loader.start(config, SequenceEngineTerminateMock.class).await();
    }

    /**
     * Test the loader with screen scaled.
     */
    @Test
    public void testScaled()
    {
        final Resolution output = new Resolution(640, 480, 0);
        final Config config = new Config(output, 16, true);
        final Loader loader = new Loader();
        loader.start(config, SequenceSingleMock.class).await();
    }

    /**
     * Test the loader with bilinear filter.
     */
    @Test
    public void testBilinear()
    {
        final Resolution output = new Resolution(640, 480, 0);
        final Config config = new Config(output, 16, true);
        final Loader loader = new Loader();
        loader.start(config, SequenceFilterMock.class, new FilterBilinear()).await();
    }

    /**
     * Test the loader with blur filter.
     */
    @Test
    public void testBlur()
    {
        final Resolution output = new Resolution(640, 480, 0);
        final FilterBlur blur = new FilterBlur();
        blur.setEdgeMode(FilterBlur.CLAMP_EDGES);
        blur.setAlpha(true);
        final Config config = new Config(output, 16, true);

        final Loader loader = new Loader();
        loader.start(config, SequenceFilterMock.class, blur).await();

        blur.setEdgeMode(FilterBlur.WRAP_EDGES);
        loader.start(config, SequenceFilterMock.class, blur).await();

        blur.setAlpha(false);
        loader.start(config, SequenceFilterMock.class, blur).await();

        blur.setAlpha(true);
        blur.setRadius(0.5f);
        loader.start(config, SequenceFilterMock.class, blur).await();
    }

    /**
     * Test the loader with a hq2x filter.
     */
    @Test
    public void testFilterHq2x()
    {
        final Resolution output = new Resolution(640, 480, 0);
        final Config config = new Config(output, 16, false);
        final Loader loader = new Loader();
        loader.start(config, SequenceFilterMock.class, new FilterHq2x()).await();
    }

    /**
     * Test the loader with a hq3x filter.
     */
    @Test
    public void testFilterHq3x()
    {
        final Resolution output = new Resolution(960, 720, 60);
        final Config config = new Config(output, 16, false);
        final Loader loader = new Loader();
        loader.start(config, SequenceFilterMock.class, new FilterHq3x()).await();
    }
}
