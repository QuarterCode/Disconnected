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

package com.quartercode.disconnected.world.comp.os;

import java.util.HashSet;
import java.util.Set;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.CollectionPropertyDefinition;
import com.quartercode.classmod.extra.FunctionDefinition;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionInvocation;
import com.quartercode.classmod.extra.Prioritized;
import com.quartercode.classmod.extra.PropertyDefinition;
import com.quartercode.classmod.extra.def.ObjectCollectionProperty;
import com.quartercode.classmod.extra.def.ObjectProperty;
import com.quartercode.classmod.util.FunctionDefinitionFactory;
import com.quartercode.disconnected.world.WorldChildFeatureHolder;
import com.quartercode.disconnected.world.comp.Computer;
import com.quartercode.disconnected.world.comp.Version;
import com.quartercode.disconnected.world.comp.Vulnerability;
import com.quartercode.disconnected.world.comp.file.FileSystem;
import com.quartercode.disconnected.world.comp.file.FileSystemModule;
import com.quartercode.disconnected.world.comp.net.NetworkModule;
import com.quartercode.disconnected.world.comp.net.Packet;
import com.quartercode.disconnected.world.comp.program.Process;
import com.quartercode.disconnected.world.comp.program.Process.ProcessState;
import com.quartercode.disconnected.world.comp.program.ProcessModule;
import com.quartercode.disconnected.world.comp.program.RootProcess;

/**
 * This class stores information about an operating system.
 * It is the core part of a running computer and manages all required modules (e.g. the {@link FileSystemModule}).
 * 
 * @see FileSystemModule
 */
public class OperatingSystem extends WorldChildFeatureHolder<Computer> {

    // ----- Properties -----

    /**
     * The name of the operating system.
     */
    public static final PropertyDefinition<String>                                      NAME;

    /**
     * The {@link Version} of the operating system.
     */
    public static final PropertyDefinition<Version>                                     VERSION;

    /**
     * The {@link Vulnerability}s the operating system has.
     */
    public static final CollectionPropertyDefinition<Vulnerability, Set<Vulnerability>> VULNERABILITIES;

    /**
     * The {@link FileSystemModule} for managing and accessing {@link FileSystem}s.
     */
    public static final PropertyDefinition<FileSystemModule>                            FS_MODULE;

    /**
     * The {@link ProcessModule} which manages the {@link RootProcess}.
     */
    public static final PropertyDefinition<ProcessModule>                               PROC_MODULE;

    /**
     * The {@link NetworkModule} which takes care of sending and receiving {@link Packet}s.
     */
    public static final PropertyDefinition<NetworkModule>                               NET_MODULE;

    static {

        NAME = ObjectProperty.createDefinition("name");
        VERSION = ObjectProperty.createDefinition("version");
        VULNERABILITIES = ObjectCollectionProperty.createDefinition("vulnerabilities", new HashSet<Vulnerability>(), true);

        FS_MODULE = ObjectProperty.createDefinition("fileSystemModule", new FileSystemModule(), true);
        FS_MODULE.addSetterExecutor("cancel", OperatingSystem.class, new FunctionExecutor<Void>() {

            @Override
            @Prioritized (Prioritized.LEVEL_6)
            public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) {

                return null;
            }

        });

        PROC_MODULE = ObjectProperty.createDefinition("processModule", new ProcessModule(), true);
        PROC_MODULE.addSetterExecutor("cancel", OperatingSystem.class, new FunctionExecutor<Void>() {

            @Override
            @Prioritized (Prioritized.LEVEL_6)
            public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) {

                return null;
            }

        });

        NET_MODULE = ObjectProperty.createDefinition("networkModule", new NetworkModule(), true);
        NET_MODULE.addSetterExecutor("cancel", OperatingSystem.class, new FunctionExecutor<Void>() {

            @Override
            @Prioritized (Prioritized.LEVEL_6)
            public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) {

                return null;
            }

        });

    }

    // ----- Functions -----

    /**
     * Returns whether the operating system is running or not.
     * The state is determined by the running state of the {@link RootProcess}.
     */
    public static final FunctionDefinition<Boolean>                                     IS_RUNNING;

    /**
     * Boots up (true) or shuts down (false) the operating system.
     * If the operating system already is in the correct state, nothing happens.
     * This method calls the {@link OSModule#SET_RUNNING} function on every used module.
     * Because of the fact that modules are not stored in a central collection, every module has its executor which invokes the module method.
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
     * <td>{@link Boolean}</td>
     * <td>running</td>
     * <td>True to boot up the operating system, false to shut it down.</td>
     * </tr>
     * </table>
     */
    public static final FunctionDefinition<Void>                                        SET_RUNNING;

    static {

        IS_RUNNING = FunctionDefinitionFactory.create("isRunning", OperatingSystem.class, new FunctionExecutor<Boolean>() {

            @Override
            public Boolean invoke(FunctionInvocation<Boolean> invocation, Object... arguments) {

                FeatureHolder holder = invocation.getHolder();
                boolean running = holder.get(PROC_MODULE).get().get(ProcessModule.ROOT_PROCESS).get().get(Process.STATE).get() != ProcessState.STOPPED;

                invocation.next(arguments);
                return running;
            }

        });
        SET_RUNNING = FunctionDefinitionFactory.create("setRunning", Boolean.class);

    }

    // ----- Foreign Content -----

    static {

        SET_RUNNING.addExecutor("fileSystemModule", OperatingSystem.class, new FunctionExecutor<Void>() {

            @Override
            public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) {

                invocation.getHolder().get(FS_MODULE).get().get(OSModule.SET_RUNNING).invoke(arguments);
                return invocation.next(arguments);
            }

        });
        SET_RUNNING.addExecutor("processModule", OperatingSystem.class, new FunctionExecutor<Void>() {

            @Override
            public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) {

                invocation.getHolder().get(PROC_MODULE).get().get(OSModule.SET_RUNNING).invoke(arguments);
                return invocation.next(arguments);
            }

        });

    }

    /**
     * Creates a new operating system.
     */
    public OperatingSystem() {

        setParentType(Computer.class);
    }

}