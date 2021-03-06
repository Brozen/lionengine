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
package com.b3dgs.lionengine.game.feature.producible;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.SizeConfig;
import com.b3dgs.lionengine.io.Xml;

/**
 * Represents the producible data from a configurer compatible with {@link SizeConfig}.
 */
public final class ProducibleConfig
{
    /** Producible root node. */
    public static final String NODE_PRODUCIBLE = Constant.XML_PREFIX + "producible";
    /** Production steps attribute name. */
    public static final String ATT_STEPS = "steps";

    /**
     * Create the producible data from configurer.
     *
     * @param configurer The configurer reference.
     * @return The producible data.
     * @throws LionEngineException If unable to read node.
     */
    public static ProducibleConfig imports(Configurer configurer)
    {
        return imports(configurer.getRoot());
    }

    /**
     * Create the producible data from node.
     *
     * @param root The root reference.
     * @return The producible data.
     * @throws LionEngineException If unable to read node.
     */
    public static ProducibleConfig imports(Xml root)
    {
        final Xml node = root.getChild(NODE_PRODUCIBLE);
        final SizeConfig size = SizeConfig.imports(root);
        final int time = node.readInteger(ATT_STEPS);

        return new ProducibleConfig(time, size.getWidth(), size.getHeight());
    }

    /**
     * Export the producible node from config.
     *
     * @param config The config reference.
     * @return The producible node.
     * @throws LionEngineException If unable to write node.
     */
    public static Xml exports(ProducibleConfig config)
    {
        final Xml node = new Xml(NODE_PRODUCIBLE);
        node.writeInteger(ATT_STEPS, config.getSteps());

        return node;
    }

    /** Production steps number. */
    private final int steps;
    /** Production width. */
    private final int width;
    /** Production height. */
    private final int height;

    /**
     * Create producible from configuration.
     *
     * @param steps The production steps number.
     * @param width The production width.
     * @param height The production height.
     */
    public ProducibleConfig(int steps, int width, int height)
    {
        this.steps = steps;
        this.width = width;
        this.height = height;
    }

    /**
     * Get the production width.
     *
     * @return The production width.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Get the production height.
     *
     * @return The production height.
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Get the production steps number.
     *
     * @return The production steps number.
     */
    public int getSteps()
    {
        return steps;
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + height;
        result = prime * result + steps;
        result = prime * result + width;
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null || object.getClass() != getClass())
        {
            return false;
        }
        final ProducibleConfig other = (ProducibleConfig) object;
        return other.getWidth() == getWidth() && other.getHeight() == getHeight() && other.getSteps() == getSteps();
    }

    @Override
    public String toString()
    {
        return new StringBuilder().append(getClass().getSimpleName())
                                  .append(" [steps=")
                                  .append(steps)
                                  .append(", width=")
                                  .append(width)
                                  .append(", height=")
                                  .append(height)
                                  .append("]")
                                  .toString();
    }
}
