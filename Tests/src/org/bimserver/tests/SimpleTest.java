package org.bimserver.tests;

/******************************************************************************
 * Copyright (C) 2011  BIMserver.org
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************************/

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bimserver.LocalDevPluginLoader;
import org.bimserver.models.ifc2x3.IfcBuildingStorey;
import org.bimserver.models.ifc2x3.IfcDoor;
import org.bimserver.models.ifc2x3.IfcProduct;
import org.bimserver.models.ifc2x3.IfcRelContainedInSpatialStructure;
import org.bimserver.plugins.PluginManager;
import org.bimserver.plugins.deserializers.DeserializerPlugin;
import org.bimserver.plugins.deserializers.EmfDeserializer;
import org.bimserver.plugins.serializers.IfcModelInterface;

import com.sun.xml.internal.ws.encoding.soap.DeserializationException;

public class SimpleTest {
	public static void main(String[] args) {
		new SimpleTest().start();
	}

	private void start() {
		try {
			PluginManager pluginManager = LocalDevPluginLoader.createPluginManager();
			DeserializerPlugin deserializerPlugin = pluginManager.getFirstDeserializer("ifc", true);
			EmfDeserializer deserializer = deserializerPlugin.createDeserializer();
			deserializer.init(pluginManager.requireSchemaDefinition());
			IfcModelInterface model = deserializer.read(TestFile.HAUS_SOURCE_FILE.getFile(), true);
            List<IfcBuildingStorey> stories = model.getAll(IfcBuildingStorey.class);
            Map<Float, IfcBuildingStorey> orderedStories = new TreeMap<Float, IfcBuildingStorey>();
            for (IfcBuildingStorey storey : stories) {
                orderedStories.put(storey.getElevation(), storey);
            }
            if (orderedStories.size() > 1) {
                IfcBuildingStorey firstFloor = stories.get(1);
                for (IfcRelContainedInSpatialStructure rel : firstFloor.getContainsElements()) {
                	for (IfcProduct product : rel.getRelatedElements()) {
                		if (product instanceof IfcDoor) {
                			IfcDoor ifcDoor = (IfcDoor)product;
                            if (ifcDoor.getOverallHeight() > 2) {
                                System.out.println(ifcDoor.getName() + " " + ifcDoor.getOverallHeight());
                            }
                		}
                	}
                }
            }
		} catch (DeserializationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}