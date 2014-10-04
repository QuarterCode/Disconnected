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

package com.quartercode.disconnected.server.world.comp.hardware;

import static com.quartercode.classmod.ClassmodFactory.create;
import org.apache.commons.lang3.reflect.TypeLiteral;
import com.quartercode.classmod.extra.PropertyDefinition;
import com.quartercode.classmod.extra.ValueFactory;
import com.quartercode.classmod.extra.storage.StandardStorage;
import com.quartercode.disconnected.server.world.comp.Computer;
import com.quartercode.disconnected.server.world.comp.file.File;
import com.quartercode.disconnected.server.world.comp.file.FileSystem;
import com.quartercode.disconnected.server.world.comp.hardware.Mainboard.NeedsMainboardSlot;

/**
 * This class represents a hard drive of a {@link Computer}.
 * A hard drive only has it's size stored (given in bytes).
 * The hard drive stores a {@link FileSystem} which stores {@link File}s that can be accessed like regular files.
 * 
 * @see Hardware
 * @see FileSystem
 */
@NeedsMainboardSlot
public class HardDrive extends Hardware {

    // ----- Properties -----

    /**
     * The {@link FileSystem} the hard drive contains.
     * It is constructed automatically after creation.
     */
    public static final PropertyDefinition<FileSystem> FILE_SYSTEM;

    static {

        FILE_SYSTEM = create(new TypeLiteral<PropertyDefinition<FileSystem>>() {}, "name", "fileSystem", "storage", new StandardStorage<>(), "initialValue", new ValueFactory<FileSystem>() {

            @Override
            public FileSystem get() {

                return new FileSystem();
            }

        });

    }

}