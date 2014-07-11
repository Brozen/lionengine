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
package com.b3dgs.lionengine.core;

import java.awt.IllegalComponentStateException;

import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Transparency;

/**
 * Applet screen implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class ScreenAppletAwt
        extends ScreenAwt
{
    /** Applet reference. */
    private final AppletAwt applet;
    /** Image buffer reference. */
    private ImageBuffer buffer;
    /** Graphic buffer reference. */
    private Graphic gbuf;

    /**
     * Constructor.
     * 
     * @param renderer The renderer reference.
     */
    ScreenAppletAwt(Renderer renderer)
    {
        super(renderer);
        applet = config.getApplet(AppletAwt.class);
    }

    /**
     * Prepare applet.
     * 
     * @param output The output resolution
     */
    private void initApplet(Resolution output)
    {
        buffer = Core.GRAPHIC.createImageBuffer(output.getWidth(), output.getHeight(), Transparency.OPAQUE);
        gbuf = buffer.createGraphic();
        graphics.setGraphic(gbuf);
        componentForKeyboard = applet;
        componentForMouse = applet;
        componentForCursor = applet;
        applet.validate();
    }

    /*
     * Screen
     */

    @Override
    public void update()
    {
        applet.getGraphics().drawImage(UtilityImage.getBuffer(buffer), 0, 0, null);
        graphics.setGraphic(gbuf);
    }

    @Override
    public void dispose()
    {
        super.dispose();
        applet.destroy();
    }

    @Override
    public void requestFocus()
    {
        applet.requestFocus();
        applet.validate();
        super.requestFocus();
    }

    @Override
    public void setIcon(String filename)
    {
        // Nothing to do
    }

    @Override
    public int getX()
    {
        try
        {
            return (int) applet.getLocationOnScreen().getX();
        }
        catch (final IllegalComponentStateException exception)
        {
            return 0;
        }
    }

    @Override
    public int getY()
    {
        try
        {
            return (int) applet.getLocationOnScreen().getY();
        }
        catch (final IllegalComponentStateException exception)
        {
            return 0;
        }
    }

    @Override
    protected void setResolution(Resolution output)
    {
        initApplet(output);
        super.setResolution(output);
    }
}
