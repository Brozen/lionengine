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
package com.b3dgs.lionengine.drawable;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Verbose;

/**
 * Test the animated sprite class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class SpriteAnimatedAwtTest
        extends SpriteAnimatedTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Engine.start("SpriteAnimatedAwtTest", Version.create(1, 0, 0), Verbose.CRITICAL,
                UtilFile.getPath("src", "test", "resources", "drawable"));
        SpriteAnimatedTest.media = Core.MEDIA.create("image.png");
        SpriteAnimatedTest.g = Core.GRAPHIC.createImageBuffer(100, 100, Transparency.OPAQUE).createGraphic();
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Engine.terminate();
    }
}
