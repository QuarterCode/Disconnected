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

package com.quartercode.disconnected.world.comp.hardware;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.reflect.TypeUtils;
import com.quartercode.classmod.extra.CollectionPropertyDefinition;
import com.quartercode.classmod.extra.FunctionDefinition;
import com.quartercode.classmod.extra.PropertyDefinition;
import com.quartercode.classmod.extra.def.ObjectCollectionProperty;
import com.quartercode.classmod.extra.def.ObjectProperty;
import com.quartercode.classmod.extra.def.ReferenceProperty;
import com.quartercode.classmod.util.CollectionPropertyAccessorFactory;
import com.quartercode.classmod.util.CollectionPropertyAccessorFactory.CriteriumMatcher;
import com.quartercode.classmod.util.FunctionDefinitionFactory;
import com.quartercode.disconnected.world.WorldChildFeatureHolder;

/**
 * This class stores information about a mainboard.
 * A mainboard has {@link MainboardSlot}s that house different pieces of {@link Hardware}.
 * 
 * @see Hardware
 */
public class Mainboard extends Hardware {

    // ----- Properties -----

    /**
     * The {@link MainboardSlot}s the mainboard offers.
     * The slots could have a content on them, you have to check before you set the content to a new one.
     */
    public static final CollectionPropertyDefinition<MainboardSlot, List<MainboardSlot>> SLOTS;

    static {

        SLOTS = ObjectCollectionProperty.createDefinition("slots", new ArrayList<MainboardSlot>(), true);

    }

    // ----- Functions -----

    /**
     * Returns the {@link MainboardSlot}s the mainboard offers which have the given content type.
     * 
     * <table>
     * <tr>
     * <th>Index</th>
     * <th>Type</th>
     * <th>Parameter</th>
     * <th>Description</th>
     * </tr>
     * <tr>
     * <td>0</td>
     * <td>{@link Class}&lt;? extends {@link Hardware}&gt;</td>
     * <td>type</td>
     * <td>The content type to use for the selection.</td>
     * </tr>
     * </table>
     */
    public static final FunctionDefinition<List<MainboardSlot>>                          GET_SLOTS_BY_CONTENT_TYPE;

    static {

        GET_SLOTS_BY_CONTENT_TYPE = FunctionDefinitionFactory.create("getSlotsByContentType", Mainboard.class, CollectionPropertyAccessorFactory.createGet(SLOTS, new CriteriumMatcher<MainboardSlot>() {

            @Override
            public boolean matches(MainboardSlot element, Object... arguments) {

                return TypeUtils.isInstance(element.get(MainboardSlot.TYPE).get(), (Class<?>) arguments[0]);
            }

        }), Class.class);

    }

    /**
     * Creates a new mainboard.
     */
    public Mainboard() {

    }

    /**
     * This class represents a mainboard slot which can have a {@link Hardware} part as content.
     * The {@link Hardware} type a slot can accept is defined using the type class.
     * A mainboard slot is only used by the mainboard class.
     * 
     * @see Mainboard
     * @see Hardware
     */
    public static class MainboardSlot extends WorldChildFeatureHolder<Mainboard> {

        // ----- Properties -----

        /**
         * The {@link Hardware} type the mainboard slot accepts.
         */
        public static final PropertyDefinition<Class<? extends Hardware>> TYPE;

        /**
         * The {@link Hardware} part which currently uses the mainboard slot.
         */
        public static final PropertyDefinition<Hardware>                  CONTENT;

        static {

            TYPE = ObjectProperty.createDefinition("name");
            CONTENT = ReferenceProperty.createDefinition("contentt");

        }

        /**
         * Creates a new mainboard slot.
         */
        public MainboardSlot() {

            setParentType(Mainboard.class);
        }

    }

    /**
     * This annotation marks {@link Hardware} types which are compatible with a {@link MainboardSlot} and need a {@link MainboardSlot} to function.
     */
    @Target (ElementType.TYPE)
    @Retention (RetentionPolicy.RUNTIME)
    public static @interface NeedsMainboardSlot {

    }

}