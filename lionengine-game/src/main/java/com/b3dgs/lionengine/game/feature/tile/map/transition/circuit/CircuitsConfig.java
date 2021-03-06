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
package com.b3dgs.lionengine.game.feature.tile.map.transition.circuit;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.feature.tile.TileConfig;
import com.b3dgs.lionengine.game.feature.tile.TileRef;
import com.b3dgs.lionengine.io.Xml;

/**
 * Find all tiles circuits and extract them to an XML file.
 * It will check from an existing map all circuits.
 * 
 * @see CircuitType
 */
public final class CircuitsConfig
{
    /** Configuration file name. */
    public static final String FILENAME = "circuits.xml";
    /** Circuits node. */
    public static final String NODE_CIRCUITS = Constant.XML_PREFIX + "circuits";
    /** Circuit node. */
    public static final String NODE_CIRCUIT = Constant.XML_PREFIX + "circuit";
    /** Attribute circuit type. */
    public static final String ATTRIBUTE_CIRCUIT_TYPE = "type";
    /** Attribute group in. */
    public static final String ATTRIBUTE_GROUP_IN = "in";
    /** Attribute group out. */
    public static final String ATTRIBUTE_GROUP_OUT = "out";

    /**
     * Import all circuits from configuration.
     * 
     * @param circuitsConfig The circuits configuration.
     * @return The circuits imported.
     * @throws LionEngineException If unable to read data.
     */
    public static Map<Circuit, Collection<TileRef>> imports(Media circuitsConfig)
    {
        final Xml root = new Xml(circuitsConfig);
        final Collection<Xml> nodesCircuit = root.getChildren(NODE_CIRCUIT);
        final Map<Circuit, Collection<TileRef>> circuits;
        circuits = new HashMap<Circuit, Collection<TileRef>>();

        for (final Xml nodeCircuit : nodesCircuit)
        {
            final String groupIn = nodeCircuit.readString(ATTRIBUTE_GROUP_IN);
            final String groupOut = nodeCircuit.readString(ATTRIBUTE_GROUP_OUT);
            final String circuitType = nodeCircuit.readString(ATTRIBUTE_CIRCUIT_TYPE);
            final CircuitType type = CircuitType.from(circuitType);
            final Circuit circuit = new Circuit(type, groupIn, groupOut);

            final Collection<Xml> nodesTileRef = nodeCircuit.getChildren(TileConfig.NODE_TILE);
            final Collection<TileRef> tilesRef = importTiles(nodesTileRef);

            circuits.put(circuit, tilesRef);
        }
        return circuits;
    }

    /**
     * Export all circuits to an XML file.
     *
     * @param media The export output.
     * @param levels The level rips used.
     * @param sheetsConfig The sheets media.
     * @param groupsConfig The groups media.
     * @throws LionEngineException If error on export.
     */
    public static void exports(Media media, Collection<Media> levels, Media sheetsConfig, Media groupsConfig)
    {
        final CircuitsExtractor extractor = new CircuitsExtractorImpl();
        final Map<Circuit, Collection<TileRef>> circuits = extractor.getCircuits(levels, sheetsConfig, groupsConfig);
        exports(media, circuits);
    }

    /**
     * Export all circuits to an XML file.
     * 
     * @param media The export output.
     * @param circuits The circuits reference.
     * @throws LionEngineException If error on export.
     */
    public static void exports(Media media, Map<Circuit, Collection<TileRef>> circuits)
    {
        final Xml nodeCircuits = new Xml(NODE_CIRCUITS);

        for (final Map.Entry<Circuit, Collection<TileRef>> entry : circuits.entrySet())
        {
            final Circuit circuit = entry.getKey();
            final Xml nodeCircuit = nodeCircuits.createChild(NODE_CIRCUIT);
            nodeCircuit.writeString(ATTRIBUTE_CIRCUIT_TYPE, circuit.getType().name());
            nodeCircuit.writeString(ATTRIBUTE_GROUP_IN, circuit.getIn());
            nodeCircuit.writeString(ATTRIBUTE_GROUP_OUT, circuit.getOut());

            exportTiles(nodeCircuit, entry.getValue());
        }

        nodeCircuits.save(media);
    }

    /**
     * Import all tiles from their nodes.
     * 
     * @param nodesTileRef The tiles nodes.
     * @return The imported tiles ref.
     */
    private static Collection<TileRef> importTiles(Collection<Xml> nodesTileRef)
    {
        final Collection<TileRef> tilesRef = new HashSet<TileRef>(nodesTileRef.size());
        for (final Xml nodeTileRef : nodesTileRef)
        {
            final TileRef tileRef = TileConfig.create(nodeTileRef);
            tilesRef.add(tileRef);
        }
        return tilesRef;
    }

    /**
     * Export all tiles for the circuit.
     * 
     * @param nodeCircuit The circuit node.
     * @param tilesRef The circuit tiles ref.
     */
    private static void exportTiles(Xml nodeCircuit, Collection<TileRef> tilesRef)
    {
        for (final TileRef tileRef : tilesRef)
        {
            final Xml nodeTileRef = TileConfig.export(tileRef);
            nodeCircuit.add(nodeTileRef);
        }
    }

    /**
     * Disabled constructor.
     */
    private CircuitsConfig()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
