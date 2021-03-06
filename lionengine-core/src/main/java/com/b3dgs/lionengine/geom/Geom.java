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
package com.b3dgs.lionengine.geom;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;

/**
 * Geometry factory.
 */
public final class Geom
{
    /**
     * Get the intersection point of two lines.
     * 
     * @param l1 The first line.
     * @param l2 The second line.
     * @return The intersection point.
     */
    public static Coord intersection(Line l1, Line l2)
    {
        final int x1 = (int) l1.getX1();
        final int x2 = (int) l1.getX2();
        final int y1 = (int) l1.getY1();
        final int y2 = (int) l1.getY2();

        final int x3 = (int) l2.getX1();
        final int x4 = (int) l2.getX2();
        final int y3 = (int) l2.getY1();
        final int y4 = (int) l2.getY2();

        final int d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (0 == d)
        {
            return new Coord(0.0, 0.0);
        }

        final int xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
        final int yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;

        return new Coord(xi, yi);
    }

    /**
     * Create a localizable.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @return The localizable.
     */
    public static Localizable createLocalizable(final double x, final double y)
    {
        return new Localizable()
        {
            @Override
            public double getX()
            {
                return x;
            }

            @Override
            public double getY()
            {
                return y;
            }
        };
    }

    /**
     * Private constructor.
     */
    private Geom()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
