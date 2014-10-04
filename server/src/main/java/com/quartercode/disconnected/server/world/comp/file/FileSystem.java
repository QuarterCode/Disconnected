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

package com.quartercode.disconnected.server.world.comp.file;

import static com.quartercode.classmod.ClassmodFactory.create;
import org.apache.commons.lang3.reflect.TypeLiteral;
import com.quartercode.classmod.extra.FunctionDefinition;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionInvocation;
import com.quartercode.classmod.extra.PropertyDefinition;
import com.quartercode.classmod.extra.ValueFactory;
import com.quartercode.classmod.extra.storage.StandardStorage;
import com.quartercode.classmod.util.PropertyAccessorFactory;
import com.quartercode.disconnected.server.world.WorldFeatureHolder;
import com.quartercode.disconnected.server.world.comp.SizeUtil;
import com.quartercode.disconnected.server.world.comp.SizeUtil.DerivableSize;
import com.quartercode.disconnected.shared.util.PathUtils;

/**
 * This class represents a file system.
 * The system stores {@link File}s which can be accessed like regular file objects.
 * A file system can be virtual or physical.
 * 
 * @see File
 */
public class FileSystem extends WorldFeatureHolder implements DerivableSize {

    // ----- Properties -----

    /**
     * The size of the file system, given in bytes.
     */
    public static final PropertyDefinition<Long>          SIZE;

    /**
     * The {@link RootFile} every other {@link File} branches of somehow.
     */
    public static final PropertyDefinition<RootFile>      ROOT;

    static {

        SIZE = create(new TypeLiteral<PropertyDefinition<Long>>() {}, "name", "size", "storage", new StandardStorage<>());

        ROOT = create(new TypeLiteral<PropertyDefinition<RootFile>>() {}, "name", "root", "storage", new StandardStorage<>(), "initialValue", new ValueFactory<RootFile>() {

            @Override
            public RootFile get() {

                return new RootFile();
            }

        });

    }

    // ----- Functions -----

    /**
     * Returns the {@link File} which is stored under the given path.
     * A path is a collection of {@link File}s seperated by a separator.
     * This will look up the {@link File} using a local file system path.
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
     * <td>{@link String}</td>
     * <td>path</td>
     * <td>The path to search under.</td>
     * </tr>
     * </table>
     */
    public static final FunctionDefinition<File<?>>       GET_FILE;

    /**
     * Returns a {@link FileAddAction} for adding a file with the given parameters.
     * In order to actually add the file, the {@link FileAddAction#EXECUTE} method must be invoked.
     * Note that that method might throw exceptions if the given file cannot be added.<br>
     * <br>
     * The returned action adds the given {@link File} to the file system under the given path.
     * If the path does not exist, this method creates directories to match it.<br>
     * <br>
     * The name of the file to add is changed to match the path.
     * Furthermore, newly created directories have the same right settings as the file to add.
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
     * <td>{@link File}&lt;{@link ParentFile}&lt;?&gt;&gt;</td>
     * <td>file</td>
     * <td>The {@link File} to add to the file system.</td>
     * </tr>
     * <tr>
     * <td>1</td>
     * <td>{@link String}</td>
     * <td>path</td>
     * <td>The path for the new {@link File}. The name of the {@link File} will be changed to the last entry.</td>
     * </tr>
     * </table>
     * 
     * @see FileAddAction#EXECUTE
     */
    public static final FunctionDefinition<FileAddAction> CREATE_ADD_FILE;

    /**
     * Returns the total amount of bytes which are occupied by {@link File}s on the file system.
     */
    public static final FunctionDefinition<Long>          GET_FILLED;

    /**
     * Returns the total amount of bytes which are not occupied by {@link File}s on the file system.
     */
    public static final FunctionDefinition<Long>          GET_FREE;

    static {

        GET_FILE = create(new TypeLiteral<FunctionDefinition<File<?>>>() {}, "name", "getFile", "parameters", new Class[] { String.class });
        GET_FILE.addExecutor("default", FileSystem.class, new FunctionExecutor<File<?>>() {

            @Override
            public File<?> invoke(FunctionInvocation<File<?>> invocation, Object... arguments) {

                String path = PathUtils.normalize((String) arguments[0]);

                String[] parts = path.split(PathUtils.SEPARATOR);
                File<?> current = invocation.getHolder().get(ROOT).get();
                for (int index = 0; index < parts.length; index++) {
                    String part = parts[index];

                    if (!part.isEmpty()) {
                        if (current instanceof ParentFile) {
                            current = current.get(ParentFile.GET_CHILD_BY_NAME).invoke(part);

                            // Return null if the next file is not a parent file and not the last file (invalid path)
                            if (! (current instanceof ParentFile) && index != parts.length - 1) {
                                current = null;
                                break;
                            }
                            // Return null if the next file doesn't exist
                            else if (current == null) {
                                break;
                            }
                        }
                    }
                }

                invocation.next(arguments);
                return current;
            }

        });

        CREATE_ADD_FILE = create(new TypeLiteral<FunctionDefinition<FileAddAction>>() {}, "name", "createAddFile", "parameters", new Class[] { File.class, String.class });
        CREATE_ADD_FILE.addExecutor("default", FileSystem.class, new FunctionExecutor<FileAddAction>() {

            @Override
            @SuppressWarnings ("unchecked")
            public FileAddAction invoke(FunctionInvocation<FileAddAction> invocation, Object... arguments) {

                FileAddAction action = new FileAddAction();
                action.get(FileAddAction.FILE_SYSTEM).set((FileSystem) invocation.getHolder());
                action.get(FileAddAction.FILE).set((File<ParentFile<?>>) arguments[0]);
                action.get(FileAddAction.PATH).set((String) arguments[1]);

                invocation.next(arguments);
                return action;
            }

        });

        GET_FILLED = create(new TypeLiteral<FunctionDefinition<Long>>() {}, "name", "getFilled", "parameters", new Class[0]);
        GET_FILLED.addExecutor("default", FileSystem.class, new FunctionExecutor<Long>() {

            @Override
            public Long invoke(FunctionInvocation<Long> invocation, Object... arguments) {

                long filled = SizeUtil.getSize(invocation.getHolder().get(ROOT).get());
                invocation.next(arguments);
                return filled;
            }

        });

        GET_FREE = create(new TypeLiteral<FunctionDefinition<Long>>() {}, "name", "getFree", "parameters", new Class[0]);
        GET_FREE.addExecutor("default", FileSystem.class, new FunctionExecutor<Long>() {

            @Override
            public Long invoke(FunctionInvocation<Long> invocation, Object... arguments) {

                long free = invocation.getHolder().get(GET_SIZE).invoke() - invocation.getHolder().get(GET_FILLED).invoke();
                invocation.next(arguments);
                return free;
            }

        });

        GET_SIZE.addExecutor("fileSystemSize", FileSystem.class, PropertyAccessorFactory.createGet(SIZE));

    }

}