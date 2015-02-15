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
package com.b3dgs.lionengine.editor.collision;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.b3dgs.lionengine.editor.InputValidator;
import com.b3dgs.lionengine.editor.ObjectList;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.UtilSwt;
import com.b3dgs.lionengine.editor.palette.PaletteView;
import com.b3dgs.lionengine.editor.world.WorldViewModel;
import com.b3dgs.lionengine.editor.world.WorldViewPart;
import com.b3dgs.lionengine.game.map.CollisionFunction;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.Tile;

/**
 * Represents the tile collision view, where the tile collision can be edited.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class TileCollisionView
        implements PaletteView
{
    /** View ID. */
    public static final String ID = "tile-collision";
    /** Default new function name. */
    private static final String DEFAULT_FUNCTION_NAME = "function";

    /** Part service. */
    final EPartService partService;
    /** Selected tile. */
    Tile tile;
    /** Scroll composite. */
    private ScrolledComposite scrolledComposite;
    /** Formulas content. */
    private Composite content;
    /** Single height. */
    private int singleHeight;
    /** Tool bar. */
    private ToolBar toolbar;
    /** Add formula item. */
    private ToolItem addFormula;
    /** Save collisions item. */
    private ToolItem saveCollisions;
    /** Formula list. */
    private Collection<TileCollisionComposite> formulas;
    /** Parent reference. */
    private Composite parent;

    /**
     * Create the collision view.
     * 
     * @param partService The part service reference.
     */
    public TileCollisionView(EPartService partService)
    {
        this.partService = partService;
    }

    /**
     * Set the selected tile.
     * 
     * @param tile The selected tile.
     */
    public void setSelectedTile(Tile tile)
    {
        this.tile = tile;
        for (final TileCollisionComposite formula : formulas)
        {
            formula.dispose();
        }
        formulas.clear();
        if (tile != null)
        {
            for (final CollisionFunction function : tile.getCollision().getCollisionFunctions())
            {
                final TileCollisionComposite tileCollisionComposite = createFormula();
                tileCollisionComposite.setSelectedFunction(function);
            }
        }
        addFormula.setEnabled(tile != null);
    }

    /**
     * Remove an existing formula from the list.
     * 
     * @param formula The formula to remove.
     */
    public void removeFormula(TileCollisionComposite formula)
    {
        final MapTile<?> map = WorldViewModel.INSTANCE.getMap();
        map.removeCollisionFunction(formula.getCollisionFunction());
        map.createCollisionDraw();

        formulas.remove(formula);
        formula.dispose();
        parent.layout(true, true);
        updateWorldView();
    }

    /**
     * Set the enabled state.
     * 
     * @param enabled <code>true</code> if enabled, <code>false</code> else.
     */
    public void setEnabled(boolean enabled)
    {
        toolbar.setEnabled(enabled);
        content.setEnabled(enabled);
    }

    /**
     * Set the save button enabled.
     * 
     * @param enabled <code>true</code> if enabled, <code>false</code> else.
     */
    public void setSaveEnabled(boolean enabled)
    {
        saveCollisions.setEnabled(enabled);
    }

    /**
     * Update the world view part.
     */
    public void updateWorldView()
    {
        final WorldViewPart part = UtilEclipse.getPart(partService, WorldViewPart.ID, WorldViewPart.class);
        part.update();
    }

    /**
     * Create a blank formula.
     * 
     * @return The formula instance.
     */
    TileCollisionComposite createFormula()
    {
        final TileCollisionComposite tileCollisionComposite = new TileCollisionComposite(this, content);
        formulas.add(tileCollisionComposite);
        singleHeight = tileCollisionComposite.getMinHeight();
        scrolledComposite.setMinSize(content.computeSize(tileCollisionComposite.getMinWidth(),
                tileCollisionComposite.getMinHeight()));
        scrolledComposite.setMinHeight((singleHeight + 5) * formulas.size());
        parent.layout(true, true);

        return tileCollisionComposite;
    }

    /**
     * Create the tool bar.
     * 
     * @param parent The composite parent.
     */
    private void createToolBar(final Composite parent)
    {
        toolbar = new ToolBar(parent, SWT.FLAT | SWT.HORIZONTAL | SWT.RIGHT);
        toolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));

        addFormula = new ToolItem(toolbar, SWT.PUSH);
        addFormula.setText(Messages.TileCollision_AddFormula);
        addFormula.setImage(ObjectList.ICON_ADD);
        addFormula.setEnabled(false);
        addFormula.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final InputDialog inputDialog = new InputDialog(parent.getShell(),
                        Messages.TileCollision_AddFunction_Title, Messages.TileCollision_AddFunction_Text,
                        TileCollisionView.DEFAULT_FUNCTION_NAME, new InputValidator(InputValidator.NAME_MATCH,
                                com.b3dgs.lionengine.editor.Messages.InputValidator_Error_Name));
                if (inputDialog.open() == Window.OK)
                {
                    final TileCollisionComposite tileCollisionComposite = createFormula();
                    final CollisionFunction function = new CollisionFunction();
                    function.setName(inputDialog.getValue());
                    tileCollisionComposite.setSelectedFunction(function);
                    final MapTile<?> map = WorldViewModel.INSTANCE.getMap();
                    map.assignCollisionFunction(tile.getCollision(), tileCollisionComposite.getCollisionFunction());
                    updateWorldView();
                }
            }
        });

        saveCollisions = new ToolItem(toolbar, SWT.PUSH);
        saveCollisions.setText(Messages.TileCollision_Save);
        saveCollisions.setImage(ObjectList.ICON_SAVE);
        saveCollisions.setEnabled(false);
        saveCollisions.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final MapTile<?> map = WorldViewModel.INSTANCE.getMap();
                map.saveCollisions();
                map.createCollisionDraw();
                setSaveEnabled(false);
            }
        });
    }

    /*
     * PaletteView
     */

    @Override
    public void create(Composite parent)
    {
        this.parent = parent;
        formulas = new ArrayList<>(1);

        final GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 1;
        layout.marginWidth = 1;
        layout.verticalSpacing = 1;
        parent.setLayout(layout);

        createToolBar(parent);

        scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        scrolledComposite.setLayout(new GridLayout(1, false));
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        UtilSwt.installMouseWheelScroll(scrolledComposite);

        content = new Composite(scrolledComposite, SWT.NONE);
        content.setLayout(new GridLayout(1, false));

        scrolledComposite.setContent(content);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setAlwaysShowScrollBars(true);
    }

    @Override
    public String getId()
    {
        return TileCollisionView.ID;
    }
}
