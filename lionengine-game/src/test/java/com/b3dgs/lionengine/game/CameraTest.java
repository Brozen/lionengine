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
package com.b3dgs.lionengine.game;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Surface;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the camera class.
 */
public class CameraTest
{
    private final Camera camera = new Camera();
    private final Surface surface = new Surface()
    {
        @Override
        public int getWidth()
        {
            return 10;
        }

        @Override
        public int getHeight()
        {
            return 10;
        }
    };

    /**
     * Test the camera default values.
     */
    @Test
    public void testCameraDefault()
    {
        Assert.assertEquals(0, camera.getViewX());
        Assert.assertEquals(0, camera.getViewY());
        Assert.assertEquals(Integer.MAX_VALUE, camera.getWidth());
        Assert.assertEquals(Integer.MAX_VALUE, camera.getHeight());
        Assert.assertEquals(0, camera.getScreenHeight());
        Assert.assertEquals(0.0, camera.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, camera.getY(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, camera.getMovementHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, camera.getMovementVertical(), UtilTests.PRECISION);
    }

    /**
     * Test the camera teleport.
     */
    @Test
    public void testCameraTeleport()
    {
        camera.setLimits(surface);
        camera.teleport(1.0, 2.0);

        Assert.assertEquals(0.0, camera.getMovementHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, camera.getMovementVertical(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, camera.getX(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, camera.getY(), UtilTests.PRECISION);
    }

    /**
     * Test the camera movement.
     */
    @Test
    public void testCameraMovement()
    {
        camera.moveLocation(1.0, 2.0, 3.0);

        Assert.assertEquals(2.0, camera.getMovementHorizontal(), UtilTests.PRECISION);
        Assert.assertEquals(3.0, camera.getMovementVertical(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, camera.getX(), UtilTests.PRECISION);
        Assert.assertEquals(3.0, camera.getY(), UtilTests.PRECISION);
    }

    /**
     * Test the camera viewpoint.
     */
    @Test
    public void testCameraViewpoint()
    {
        camera.setView(0, 0, 16, 32, 32);

        Assert.assertEquals(0.0, camera.getViewpointX(0.0), UtilTests.PRECISION);
        Assert.assertEquals(32.0, camera.getViewpointY(0.0), UtilTests.PRECISION);

        camera.setLocation(1.0, 2.0);

        Assert.assertEquals(-1.0, camera.getViewpointX(0.0), UtilTests.PRECISION);
        Assert.assertEquals(34.0, camera.getViewpointY(0.0), UtilTests.PRECISION);
    }

    /**
     * Test the camera view.
     */
    @Test
    public void testCameraView()
    {
        camera.setView(1, 2, 3, 4, 4);

        Assert.assertEquals(1, camera.getViewX());
        Assert.assertEquals(2, camera.getViewY());
        Assert.assertEquals(3, camera.getWidth());
        Assert.assertEquals(4, camera.getHeight());
    }

    /**
     * Test the camera viewable.
     */
    @Test
    public void testIsViewable()
    {
        camera.setView(0, 0, 2, 2, 2);

        final Cursor cursor = new Cursor();
        cursor.setArea(-2, -2, 4, 4);
        cursor.setLocation(0, 0);
        cursor.update(1.0);

        Assert.assertTrue(camera.isViewable((Localizable) cursor, 0, 0));
        Assert.assertFalse(camera.isViewable((Localizable) cursor, 0, -1));
        Assert.assertFalse(camera.isViewable((Localizable) cursor, -1, 0));
        Assert.assertFalse(camera.isViewable((Localizable) cursor, -1, -1));

        cursor.setLocation(3, 3);
        cursor.update(1.0);

        Assert.assertFalse(camera.isViewable((Localizable) cursor, 0, 0));
        Assert.assertFalse(camera.isViewable((Localizable) cursor, 0, 1));
        Assert.assertFalse(camera.isViewable((Localizable) cursor, 1, 0));
        Assert.assertTrue(camera.isViewable((Localizable) cursor, 1, 1));

        Assert.assertTrue(camera.isViewable(cursor, 0, 0));
        Assert.assertTrue(camera.isViewable(cursor, 1, 3));

        cursor.setLocation(-2, -2);
        cursor.update(1.0);

        Assert.assertFalse(camera.isViewable(cursor, 0, 0));
        Assert.assertFalse(camera.isViewable(cursor, 0, -1));
        Assert.assertFalse(camera.isViewable(cursor, -1, 0));
        Assert.assertTrue(camera.isViewable(cursor, 1, 1));

        cursor.setLocation(0, -2);
        cursor.update(1.0);

        Assert.assertFalse(camera.isViewable(cursor, 0, 0));
        Assert.assertFalse(camera.isViewable(cursor, -1, 0));
        Assert.assertFalse(camera.isViewable(cursor, 0, -1));
        Assert.assertTrue(camera.isViewable(cursor, 1, 1));

        cursor.setLocation(-2, 0);
        cursor.update(1.0);

        Assert.assertFalse(camera.isViewable(cursor, 0, 0));
        Assert.assertFalse(camera.isViewable(cursor, 0, -1));
        Assert.assertFalse(camera.isViewable(cursor, -1, 0));
        Assert.assertTrue(camera.isViewable(cursor, 1, 1));

        cursor.setLocation(-2, 2);
        cursor.update(1.0);

        Assert.assertFalse(camera.isViewable(cursor, 0, 0));
        Assert.assertFalse(camera.isViewable(cursor, -1, 0));
        Assert.assertFalse(camera.isViewable(cursor, 0, -1));
        Assert.assertTrue(camera.isViewable(cursor, 1, 1));

        cursor.setLocation(2, -2);
        cursor.update(1.0);

        Assert.assertFalse(camera.isViewable(cursor, 0, 0));
        Assert.assertTrue(camera.isViewable(cursor, 1, 1));
    }

    /**
     * Test the camera interval.
     */
    @Test
    public void testInterval()
    {
        final Transformable transformable = new TransformableModel();
        transformable.teleport(1.0, 2.0);

        camera.setView(0, 0, 1, 1, 1);
        camera.setLimits(surface);
        camera.resetInterval(transformable);

        Assert.assertEquals(1.0, camera.getX(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, camera.getY(), UtilTests.PRECISION);

        camera.setIntervals(1, 1);
        camera.moveLocation(1.0, 3.0, 3.0);

        Assert.assertEquals(4.0, camera.getX(), UtilTests.PRECISION);
        Assert.assertEquals(5.0, camera.getY(), UtilTests.PRECISION);

        camera.moveLocation(1.0, -2.0, -2.0);

        Assert.assertEquals(2.0, camera.getX(), UtilTests.PRECISION);
        Assert.assertEquals(3.0, camera.getY(), UtilTests.PRECISION);
    }

    /**
     * Test the camera interval.
     */
    @Test
    public void testIntervalLimit()
    {
        camera.setView(0, 0, 0, 0, 0);
        camera.setLimits(surface);

        // Limit right
        camera.moveLocation(1.0, 5.0, 0.0);

        Assert.assertEquals(5.0, camera.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, camera.getY(), UtilTests.PRECISION);

        camera.moveLocation(1.0, 20.0, 0.0);

        Assert.assertEquals(10.0, camera.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, camera.getY(), UtilTests.PRECISION);

        // Limit left
        camera.moveLocation(1.0, -5.0, 0.0);

        Assert.assertEquals(5.0, camera.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, camera.getY(), UtilTests.PRECISION);

        camera.moveLocation(1.0, -20.0, 0.0);

        Assert.assertEquals(0.0, camera.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, camera.getY(), UtilTests.PRECISION);

        // Limit top
        camera.moveLocation(1.0, 0.0, 5.0);

        Assert.assertEquals(0.0, camera.getX(), UtilTests.PRECISION);
        Assert.assertEquals(5.0, camera.getY(), UtilTests.PRECISION);

        camera.moveLocation(1.0, 0.0, 20.0);

        Assert.assertEquals(0.0, camera.getX(), UtilTests.PRECISION);
        Assert.assertEquals(10.0, camera.getY(), UtilTests.PRECISION);

        // Limit bottom
        camera.moveLocation(1.0, 0.0, -5.0);

        Assert.assertEquals(0.0, camera.getX(), UtilTests.PRECISION);
        Assert.assertEquals(5.0, camera.getY(), UtilTests.PRECISION);

        camera.moveLocation(1.0, 0.0, -20.0);

        Assert.assertEquals(0.0, camera.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, camera.getY(), UtilTests.PRECISION);
    }

    /**
     * Test the camera interval.
     */
    @Test
    public void testIntervalNoLimit()
    {
        // Limit right
        camera.moveLocation(1.0, 20.0, 0.0);

        Assert.assertEquals(20.0, camera.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, camera.getY(), UtilTests.PRECISION);

        // Limit left
        camera.moveLocation(1.0, -20.0, 0.0);

        Assert.assertEquals(0.0, camera.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, camera.getY(), UtilTests.PRECISION);

        // Limit top
        camera.moveLocation(1.0, 0.0, 20.0);

        Assert.assertEquals(0.0, camera.getX(), UtilTests.PRECISION);
        Assert.assertEquals(20.0, camera.getY(), UtilTests.PRECISION);

        // Limit bottom
        camera.moveLocation(1.0, 0.0, -20.0);

        Assert.assertEquals(0.0, camera.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, camera.getY(), UtilTests.PRECISION);
    }
}
