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
package com.b3dgs.lionengine.core.awt;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.graphic.ScreenListener;
import com.b3dgs.lionengine.io.awt.Keyboard;
import com.b3dgs.lionengine.io.awt.Mouse;

/**
 * Full screen implementation.
 * 
 * @see Keyboard
 * @see Mouse
 */
final class ScreenFullAwt extends ScreenAwt
{
    /** Error message unsupported full screen. */
    private static final String ERROR_UNSUPPORTED_FULLSCREEN = "Unsupported resolution: ";
    /** Unable to switch to full screen. */
    private static final String ERROR_SWITCH = "Unable to switch to full screen mode !";

    /**
     * Format resolution to string.
     * 
     * @param resolution The resolution reference.
     * @param depth The depth reference.
     * @return The formatted string.
     */
    private static String formatResolution(Resolution resolution, int depth)
    {
        return new StringBuilder().append(String.valueOf(resolution.getWidth()))
                                  .append(Constant.STAR)
                                  .append(String.valueOf(resolution.getHeight()))
                                  .append(Constant.STAR)
                                  .append(depth)
                                  .append(Constant.SPACE)
                                  .append(Constant.AT)
                                  .append(String.valueOf(resolution.getRate()))
                                  .append(Constant.UNIT_RATE)
                                  .toString();
    }

    /** Graphics device reference. */
    private final GraphicsDevice dev;
    /** Graphic configuration reference. */
    private final GraphicsConfiguration conf;
    /** Frame reference. */
    private final JFrame frame;

    /**
     * Internal constructor.
     * 
     * @param config The config reference.
     * @throws LionEngineException If renderer is <code>null</code> or no available display.
     */
    ScreenFullAwt(Config config)
    {
        super(config);
        final GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        dev = env.getDefaultScreenDevice();
        conf = dev.getDefaultConfiguration();
        frame = initMainFrame();
    }

    /**
     * Called when screen is disposed.
     */
    void onDisposed()
    {
        for (final ScreenListener listener : listeners)
        {
            listener.notifyClosed();
        }
    }

    /**
     * Initialize the main frame.
     * 
     * @return The created main frame.
     * @throws LionEngineException If the engine has not been started.
     */
    private JFrame initMainFrame()
    {
        final String title = new StringBuilder().append(Engine.getProgramName())
                                                .append(Constant.SPACE)
                                                .append(Engine.getProgramVersion())
                                                .toString();
        final JFrame frame = new JFrame(title, conf);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent event)
            {
                onDisposed();
            }
        });
        frame.setResizable(false);
        frame.setUndecorated(true);
        frame.setIgnoreRepaint(true);

        return frame;
    }

    /**
     * Prepare fullscreen mode.
     * 
     * @param output The output resolution
     * @param depth The bit depth color.
     * @throws LionEngineException If unsupported resolution.
     */
    private void initFullscreen(Resolution output, int depth)
    {
        final java.awt.Window window = new java.awt.Window(frame, conf);
        window.setBackground(Color.BLACK);
        window.setIgnoreRepaint(true);
        window.setPreferredSize(new Dimension(output.getWidth(), output.getHeight()));
        dev.setFullScreenWindow(window);

        final DisplayMode disp = isSupported(new DisplayMode(output.getWidth(),
                                                             output.getHeight(),
                                                             depth,
                                                             output.getRate()));
        if (disp == null)
        {
            throw new LionEngineException(ScreenFullAwt.ERROR_UNSUPPORTED_FULLSCREEN,
                                          formatResolution(output, depth),
                                          getSupportedResolutions());
        }
        if (!dev.isDisplayChangeSupported())
        {
            throw new LionEngineException(ScreenFullAwt.ERROR_SWITCH);
        }
        dev.setDisplayMode(disp);
        window.validate();

        // Create buffer
        try
        {
            window.createBufferStrategy(2, conf.getBufferCapabilities());
        }
        catch (final AWTException exception)
        {
            Verbose.exception(exception);
            window.createBufferStrategy(1);
        }
        buf = window.getBufferStrategy();

        // Set input listeners
        componentForKeyboard = frame;
        componentForMouse = window;
        componentForCursor = window;
        frame.validate();
    }

    /**
     * Get the supported resolution information.
     * 
     * @return The supported resolutions.
     */
    private String getSupportedResolutions()
    {
        final StringBuilder builder = new StringBuilder(Constant.HUNDRED);
        int i = 0;
        for (final DisplayMode display : dev.getDisplayModes())
        {
            final StringBuilder widthSpace = new StringBuilder();
            final int width = display.getWidth();
            if (width < Constant.THOUSAND)
            {
                widthSpace.append(Constant.SPACE);
            }
            final StringBuilder heightSpace = new StringBuilder();
            final int height = display.getHeight();
            if (height < Constant.THOUSAND)
            {
                heightSpace.append(Constant.NEW_LINE);
            }
            final StringBuilder freqSpace = new StringBuilder();
            final int freq = display.getRefreshRate();
            if (freq < Constant.HUNDRED)
            {
                freqSpace.append(Constant.SPACE);
            }
            builder.append("Supported display mode:")
                   .append(Constant.NEW_LINE)
                   .append('[')
                   .append(widthSpace)
                   .append(width)
                   .append(Constant.STAR)
                   .append(heightSpace)
                   .append(height)
                   .append(Constant.STAR)
                   .append(display.getBitDepth())
                   .append(Constant.SPACE)
                   .append(Constant.AT)
                   .append(freqSpace)
                   .append(freq)
                   .append(Constant.UNIT_RATE)
                   .append(']')
                   .append(Constant.SPACE);
            i++;
            final int linesPerDisplay = 5;
            if (i % linesPerDisplay == 0)
            {
                builder.append(Constant.NEW_LINE);
            }
        }
        return builder.toString();
    }

    /**
     * Check if the display mode is supported.
     * 
     * @param display The display mode to check.
     * @return Supported display, <code>null</code> else.
     */
    private DisplayMode isSupported(DisplayMode display)
    {
        final DisplayMode[] supported = dev.getDisplayModes();
        for (final DisplayMode current : supported)
        {
            final boolean multiDepth = current.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI && display.equals(current);
            if (multiDepth
                || current.getBitDepth() == DisplayMode.BIT_DEPTH_MULTI
                   && display.getWidth() == current.getWidth()
                   && display.getHeight() == current.getHeight())
            {
                return current;
            }
        }
        return null;
    }

    /*
     * Screen
     */

    @Override
    public void start()
    {
        super.start();
        frame.validate();
        frame.setEnabled(true);
        frame.setVisible(true);
    }

    @Override
    public void dispose()
    {
        super.dispose();
        buf.dispose();
        frame.dispose();
    }

    @Override
    public void requestFocus()
    {
        frame.requestFocus();
        super.requestFocus();
    }

    @Override
    public void setIcon(String filename)
    {
        final ImageIcon icon = new ImageIcon(filename);
        frame.setIconImage(icon.getImage());
    }

    @Override
    protected void setResolution(Resolution output)
    {
        initFullscreen(output, config.getDepth());
        super.setResolution(output);
    }
}
