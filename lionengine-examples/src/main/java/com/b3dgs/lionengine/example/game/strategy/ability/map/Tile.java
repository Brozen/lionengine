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
package com.b3dgs.lionengine.example.game.strategy.ability.map;

import com.b3dgs.lionengine.example.game.strategy.ability.ResourceType;
import com.b3dgs.lionengine.game.collision.CollisionFormula;
import com.b3dgs.lionengine.game.strategy.map.TileStrategy;

/**
 * Tile implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Tile
        extends TileStrategy<ResourceType>
{
    /**
     * Constructor.
     * 
     * @param width The tile width.
     * @param height The tile height.
     * @param pattern The tile pattern.
     * @param number The tile number.
     * @param collision The tile collision.
     */
    public Tile(int width, int height, Integer pattern, int number, CollisionFormula collision)
    {
        super(width, height, pattern, number, collision);
    }

    /*
     * TileStrategy
     */

    @Override
    public ResourceType checkResourceType()
    {
        switch (getCollision().getGroup())
        {
            case TREE:
                return ResourceType.WOOD;
            default:
                return ResourceType.NONE;
        }
    }

    @Override
    public boolean checkBlocking()
    {
        return TileCollisionGroup.GROUND != getCollision().getGroup();
    }

    @Override
    public boolean hasResources()
    {
        return getResourceType() != ResourceType.NONE;
    }

    @Override
    public TileCollision getCollision()
    {
        return (TileCollision) super.getCollision();
    }
}
