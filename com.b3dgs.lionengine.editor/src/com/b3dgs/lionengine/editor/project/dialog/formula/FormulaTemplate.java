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
package com.b3dgs.lionengine.editor.project.dialog.formula;

import java.util.Locale;

import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.collision.CollisionConstraint;
import com.b3dgs.lionengine.game.collision.CollisionFormula;
import com.b3dgs.lionengine.game.collision.CollisionFunction;
import com.b3dgs.lionengine.game.collision.CollisionFunctionLinear;
import com.b3dgs.lionengine.game.collision.CollisionRange;
import com.b3dgs.lionengine.game.map.MapTile;

/**
 * List of formula template.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum FormulaTemplate
{
    /** No collision. */
    NONE(new CollisionRange(Axis.Y, 0, 0, 0, 0), new CollisionFunctionLinear(0.0, 0.0)),
    /** Top collision. */
    TOP(new CollisionRange(Axis.Y, 0, 1, 1, 1), new CollisionFunctionLinear(0.0, Double.NaN)),
    /** Bottom collision. */
    BOTTOM(new CollisionRange(Axis.Y, 0, 1, 0, 0), new CollisionFunctionLinear(0.0, 0.0)),
    /** Left collision. */
    LEFT(new CollisionRange(Axis.X, 0, 0, 0, 1), new CollisionFunctionLinear(0.0, 0.0)),
    /** Right collision. */
    RIGHT(new CollisionRange(Axis.X, 1, 1, 0, 1), new CollisionFunctionLinear(0.0, Double.NaN));

    /**
     * Get the replaced tile size value.
     * 
     * @param value The original value (0 or 1).
     * @param tileSize The tile size.
     * @return The replaced value (0 or tileSize).
     */
    private static int getReplacedValue(int value, int tileSize)
    {
        if (value == 0)
        {
            return value;
        }
        return tileSize - 1;
    }

    /**
     * Get the replaced tile size value.
     * 
     * @param value The original value (0 or {@link Double#NaN}).
     * @param tileSize The tile size.
     * @return The replaced value (0 or tileSize).
     */
    private static double getReplacedValue(double value, int tileSize)
    {
        if (Double.isNaN(value))
        {
            return tileSize - 1;
        }
        return value;
    }

    /** Associated formula. */
    private final CollisionFormula formula;

    /**
     * Create template.
     * 
     * @param range The collision range.
     * @param function The collision function.
     */
    private FormulaTemplate(CollisionRange range, CollisionFunction function)
    {
        formula = new CollisionFormula(name().toLowerCase(Locale.ENGLISH), range, function, new CollisionConstraint());
    }

    /**
     * Get the collision formula configured for the map.
     * 
     * @param map The map reference.
     * @return The collision formula.
     */
    public CollisionFormula getFormula(MapTile map)
    {
        final CollisionRange original = formula.getRange();

        final int minX = getReplacedValue(original.getMinX(), map.getTileWidth());
        final int maxX = getReplacedValue(original.getMaxX(), map.getTileWidth());
        final int minY = getReplacedValue(original.getMinY(), map.getTileHeight());
        final int maxY = getReplacedValue(original.getMaxY(), map.getTileHeight());
        final CollisionRange range = new CollisionRange(original.getOutput(), minX, maxX, minY, maxY);

        CollisionFunction function = formula.getFunction();
        if (function instanceof CollisionFunctionLinear)
        {
            final CollisionFunctionLinear linear = (CollisionFunctionLinear) function;
            final double b = getReplacedValue(linear.getB(), map.getTileWidth());
            function = new CollisionFunctionLinear(linear.getA(), b);
        }

        return new CollisionFormula(formula.getName(), range, function, formula.getConstraint());
    }
}
