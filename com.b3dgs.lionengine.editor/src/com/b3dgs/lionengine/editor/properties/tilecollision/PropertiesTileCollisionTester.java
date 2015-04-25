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
package com.b3dgs.lionengine.editor.properties.tilecollision;

import org.eclipse.core.expressions.PropertyTester;

import com.b3dgs.lionengine.editor.properties.PropertiesModel;
import com.b3dgs.lionengine.game.configurer.ConfigCollisionFormula;

/**
 * Test the properties node existence.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class PropertiesTileCollisionTester
        extends PropertyTester
{
    /** Can add formula. */
    private static final String PROPERTY_FORMULA_ADD = "addFormula";
    /** Can remove formula. */
    private static final String PROPERTY_FORMULA_REMOVE = "removeFormula";
    /** Can edit formula. */
    private static final String PROPERTY_FORMULA_EDIT = "editFormula";

    /*
     * PropertyTester
     */

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        final PropertiesModel model = PropertiesModel.INSTANCE;
        if (!model.isEmpty())
        {
            final Object data = model.getSelectedData();
            if (PROPERTY_FORMULA_ADD.equals(property))
            {
                return ConfigCollisionFormula.FORMULAS.equals(data);
            }
            else if (PROPERTY_FORMULA_REMOVE.equals(property))
            {
                return ConfigCollisionFormula.FORMULA.equals(data);
            }
            else if (PROPERTY_FORMULA_EDIT.equals(property))
            {
                return ConfigCollisionFormula.FORMULA.equals(data);
            }
        }
        return false;
    }
}
