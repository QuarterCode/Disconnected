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

package com.quartercode.disconnected.sim.comp.file;

/**
 * This runtime exception occures if there is not enough space on a file system for storing some new bytes (e.g. from a file).
 * 
 * @see FileSystem
 */
public class OutOfSpaceException extends RuntimeException {

    private static final long serialVersionUID = -905196120194774390L;

    private final FileSystem  fileSystem;
    private final long        size;

    /**
     * Creates a new out of space exception and sets the file system which should have stored the new bytes and the amount of new bytes.
     * 
     * @param fileSystem The file system which should have stored the new bytes.
     * @param size The amount of new bytes.
     */
    public OutOfSpaceException(FileSystem fileSystem, long size) {

        super("Out of space on file system for computer " + fileSystem.getHost().getId() + ": " + fileSystem.getFilled() + "b/" + fileSystem.getSize() + "b filled, can't store " + size + "b");
        this.fileSystem = fileSystem;
        this.size = size;
    }

    /**
     * Returns the file system which should have stored the new bytes.
     * 
     * @return The file system which should have stored the new bytes.
     */
    public FileSystem getFileSystem() {

        return fileSystem;
    }

    /**
     * Returns the amount of new bytes.
     * 
     * @return The amount of new bytes.
     */
    public long getSize() {

        return size;
    }

}
