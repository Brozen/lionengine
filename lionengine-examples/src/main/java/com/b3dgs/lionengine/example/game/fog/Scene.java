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
package com.b3dgs.lionengine.example.game.fog;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.core.awt.Mouse;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.map.FogOfWar;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.object.Factory;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.trait.fovable.Fovable;

/**
 * Game loop designed to handle our little world.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.core.minimal
 */
class Scene extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(320, 240, 60);

    /** Collection fog. */
    private final Collection<Fovable> fovables = new ArrayList<>();
    /** Fog of war layer. */
    private final FogOfWar fogOfWar = new FogOfWar();
    /** Services reference. */
    private final Services services = new Services();
    /** Game factory. */
    private final Factory factory = services.create(Factory.class);
    /** Camera reference. */
    private final Camera camera = services.create(Camera.class);
    /** Map reference. */
    private final MapTile map = services.create(MapTileGame.class);
    /** Keyboard reference. */
    private final Keyboard keyboard = getInputDevice(Keyboard.class);
    /** Mouse reference. */
    private final Mouse mouse = getInputDevice(Mouse.class);
    /** Peon reference. */
    private Peon peon;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    public Scene(Loader loader)
    {
        super(loader, NATIVE);
        setSystemCursorVisible(false);
        keyboard.addActionPressed(Keyboard.ESCAPE, () -> end());
    }

    @Override
    protected void load()
    {
        map.create(Medias.create("level.png"));
        map.setTileRenderer(fogOfWar);

        final SpriteTiled hide = Drawable.loadSpriteTiled(Medias.create("hide.png"), 16, 16);
        final SpriteTiled fog = Drawable.loadSpriteTiled(Medias.create("fog.png"), 16, 16);
        hide.load();
        hide.prepare();
        fog.load();
        fog.prepare();
        fogOfWar.setTilesheet(hide, fog);
        fogOfWar.setEnabled(true, true);
        fogOfWar.create(map, map);

        camera.setView(0, 0, getWidth(), getHeight());
        camera.setLimits(map);
        camera.setLocation(0, 0);

        peon = factory.create(Peon.MEDIA);
        fovables.add(peon.getTrait(Fovable.class));
    }

    @Override
    public void update(double extrp)
    {
        mouse.update(extrp);
        peon.update(extrp);
        fogOfWar.update(fovables);
    }

    @Override
    public void render(Graphic g)
    {
        map.render(g);
        peon.render(g);
    }

    @Override
    protected void onTerminate(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
