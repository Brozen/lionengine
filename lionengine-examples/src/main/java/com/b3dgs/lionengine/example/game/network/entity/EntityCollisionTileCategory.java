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
package com.b3dgs.lionengine.example.game.network.entity;

import java.util.Collection;

import com.b3dgs.lionengine.game.collision.CollisionFormula;
import com.b3dgs.lionengine.game.configurer.ConfigCollisionCategory;

/**
 * List of entity collision categories on tile.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
enum EntityCollisionTileCategory implements ConfigCollisionCategory
{
    /** Default ground center collision. */
    GROUND_CENTER(TileCollision.COLLISION_VERTICAL),
    /** Ground leg left. */
    LEG_LEFT(TileCollision.COLLISION_VERTICAL),
    /** Ground leg right. */
    LEG_RIGHT(TileCollision.COLLISION_VERTICAL),
    /** Horizontal knee left. */
    KNEE_LEFT(TileCollision.COLLISION_HORIZONTAL),
    /** Horizontal knee right. */
    KNEE_RIGHT(TileCollision.COLLISION_HORIZONTAL);

    /** The collisions list. */
    private final Collection<CollisionFormula> collisions;

    /**
     * Constructor.
     * 
     * @param collisions The collisions list.
     */
    private EntityCollisionTileCategory(Collection<CollisionFormula> collisions)
    {
        this.collisions = collisions;
    }

    @Override
    public Collection<CollisionFormula> getCollisions()
    {
        return collisions;
    }
}
