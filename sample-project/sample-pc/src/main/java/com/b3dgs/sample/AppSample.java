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
package com.b3dgs.sample;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.awt.EngineAwt;
import com.b3dgs.lionengine.core.sequence.Loader;

/**
 * Program starts here.
 */
public final class AppSample
{
    /** Application name. */
    public static final String NAME = "Sample Project";
    /** Application version. */
    public static final Version VERSION = Version.create(0, 1, 0);
    /** Resources directory. */
    public static final String RESOURCES_DIR = "resources";

    /**
     * Main function.
     * 
     * @param args The arguments (none).
     */
    public static void main(String[] args)
    {
        EngineAwt.start(NAME, VERSION, RESOURCES_DIR);

        final Resolution output = new Resolution(640, 480, 60);
        final Config config = new Config(output, 16, true);
        final Loader loader = new Loader();
        loader.start(config, Scene.class);
    }

    /**
     * Private constructor.
     */
    private AppSample()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
