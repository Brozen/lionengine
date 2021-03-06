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
package com.b3dgs.lionengine.example.game.collision;

import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.FeatureGet;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.feature.Displayable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.SpriteAnimated;

/**
 * Mario rendering implementation.
 */
class MarioRenderer extends FeatureModel implements Displayable
{
    private final SpriteAnimated surface;
    private final Viewer viewer;

    @FeatureGet private Collidable collidable;
    @FeatureGet private Transformable transformable;

    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param model The model reference.
     */
    public MarioRenderer(Services services, MarioModel model)
    {
        super();

        viewer = services.get(Viewer.class);
        surface = model.getSurface();
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        collidable.setCollisionVisibility(true);
    }

    @Override
    public void render(Graphic g)
    {
        surface.setLocation(viewer, transformable);
        surface.render(g);

        g.setColor(ColorRgba.GREEN);
        collidable.render(g);
    }
}
