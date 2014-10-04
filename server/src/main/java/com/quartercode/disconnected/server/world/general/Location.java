/*
 * This file is part of Disconnected.
 * Copyright (c) 2013 QuarterCode <http://www.quartercode.com/>
 *
 * Disconnected is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Disconnected is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Disconnected. If not, see <http://www.gnu.org/licenses/>.
 */

package com.quartercode.disconnected.server.world.general;

import static com.quartercode.classmod.ClassmodFactory.create;
import org.apache.commons.lang3.reflect.TypeLiteral;
import com.quartercode.classmod.extra.PropertyDefinition;
import com.quartercode.classmod.extra.storage.StandardStorage;
import com.quartercode.disconnected.server.world.WorldFeatureHolder;

/**
 * This class stores a location using x and y relative coordinates.
 */
public class Location extends WorldFeatureHolder {

    // ----- Properties -----

    /**
     * The relative relative x coordinate of the location.
     */
    public static final PropertyDefinition<Float> X;

    /**
     * The relative y coordinate of the location.
     */
    public static final PropertyDefinition<Float> Y;

    static {

        X = create(new TypeLiteral<PropertyDefinition<Float>>() {}, "name", "x", "storage", new StandardStorage<>());
        Y = create(new TypeLiteral<PropertyDefinition<Float>>() {}, "name", "y", "storage", new StandardStorage<>());

    }

}