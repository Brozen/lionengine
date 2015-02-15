/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.test.game;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.FactoryGraphicProvider;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.game.Cursor;

/**
 * Test the cursor class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class CursorTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        FactoryGraphicProvider.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        FactoryGraphicProvider.setFactoryGraphic(null);
    }

    /**
     * Test the cursor class.
     */
    @Test
    public void testCursor()
    {
        final Graphic g = new GraphicMock();
        final MouseMock mouse = new MouseMock();

        try
        {
            final Cursor cursor = new Cursor(mouse, null);
            Assert.assertNotNull(cursor);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        final Cursor cursor = new Cursor(mouse, new MediaMock("cursor.png"));
        cursor.setArea(0, 0, 320, 240);
        cursor.setSensibility(1.0, 2.0);
        cursor.setSurfaceId(0);

        cursor.update(1.0);
        cursor.setSyncMode(true);
        Assert.assertTrue(cursor.isSynchronized());
        cursor.update(1.0);
        cursor.setSyncMode(false);
        Assert.assertTrue(!cursor.isSynchronized());
        cursor.update(1.0);
        cursor.setSyncMode(true);
        cursor.update(1.0);

        cursor.setLocation(10, 20);
        Assert.assertEquals(10, cursor.getLocationX());
        Assert.assertEquals(20, cursor.getLocationY());
        Assert.assertEquals(1.0, cursor.getSensibilityHorizontal(), 0.000001);
        Assert.assertEquals(2.0, cursor.getSensibilityVertical(), 0.000001);
        cursor.setRenderingOffset(0, 0);
        Assert.assertEquals(0, cursor.getSurfaceId());
        Assert.assertEquals(0, cursor.getClick());
        cursor.render(g);
    }
}
