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
package com.b3dgs.lionengine.example.game.effect;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.sequence.Sequence;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.feature.ComponentDisplayable;
import com.b3dgs.lionengine.game.feature.ComponentRefreshable;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.io.awt.Keyboard;
import com.b3dgs.lionengine.io.awt.Mouse;

/**
 * Scene implementation.
 * 
 * @see com.b3dgs.lionengine.example.core.minimal
 */
class Scene extends Sequence
{
    private final Services services = new Services();
    private final Factory factory = services.create(Factory.class);
    private final Camera camera = services.create(Camera.class);
    private final Handler handler = services.create(Handler.class);
    private final Mouse mouse = getInputDevice(Mouse.class);

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Scene(Context context)
    {
        super(context, new Resolution(320, 240, 60));

        handler.addComponent(new ComponentRefreshable());
        handler.addComponent(new ComponentDisplayable());

        getInputDevice(Keyboard.class).addActionPressed(Keyboard.ESCAPE, () -> end());
    }

    @Override
    public void load()
    {
        camera.setView(0, 0, getWidth(), getHeight(), getHeight());
    }

    @Override
    public void update(double extrp)
    {
        mouse.update(extrp);
        if (mouse.hasClicked(Mouse.LEFT))
        {
            final Effect effect = factory.create(Effect.EXPLODE);
            effect.start(mouse);
            handler.add(effect);
        }
        handler.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());
        handler.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
