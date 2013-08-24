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

package com.quartercode.disconnected.sim.comp.hardware;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import com.quartercode.disconnected.sim.comp.Computer;
import com.quartercode.disconnected.sim.comp.Hardware;
import com.quartercode.disconnected.sim.comp.Version;
import com.quartercode.disconnected.sim.comp.Vulnerability;
import com.quartercode.disconnected.sim.comp.hardware.HardDrive.File.FileType;

/**
 * This class represents a hard drive of a computer.
 * A hard drive only has it's size stored (given in bytes).
 * The hard drive has a letter (e.g. "C") and stores files which can be accessed like regular files.
 * 
 * @see Hardware
 * @see File
 */
@XmlAccessorType (XmlAccessType.FIELD)
public class HardDrive extends Hardware {

    private static final long serialVersionUID = 1L;

    private long              size;

    private char              letter;
    private File              rootFile;

    /**
     * Creates a new empty hard drive.
     * This is only recommended for direct field access (e.g. for serialization).
     */
    public HardDrive() {

    }

    /**
     * Creates a new hard drive and sets the computer, the name, the version, the vulnerabilities and the size.
     * 
     * @param computer The computer this part is built in.
     * @param name The name the hard drive has.
     * @param version The current version the hard drive has.
     * @param vulnerabilities The vulnerabilities the hard drive has.
     * @param size The size of the hard drive module, given in bytes.
     */
    public HardDrive(Computer computer, String name, Version version, List<Vulnerability> vulnerabilities, long size) {

        super(computer, name, version, vulnerabilities);

        this.size = size;

        rootFile = new File(this);
    }

    /**
     * Returns the size of the hard drive module, given in bytes.
     * 
     * @return The size of the hard drive module, given in bytes.
     */
    public long getSize() {

        return size;
    }

    /**
     * Returns the letter the computer uses to recognize the hard drive.
     * 
     * @return The letter the computer uses to recognize the hard drive.
     */
    public char getLetter() {

        return letter;
    }

    /**
     * Setts the letter the computer uses to recognize the hard drive to a new one.
     * 
     * @param letter The new letter the computer uses to recognize the hard drive.
     */
    public void setLetter(char letter) {

        this.letter = letter;
    }

    /**
     * Returns the root file which every other file path branches of.
     * 
     * @return The root file which every other file path branches of.
     */
    public File getRootFile() {

        return rootFile;
    }

    /**
     * Returns the file which is stored on the hard drive under the given path.
     * A path is a collection of files seperated by a seperator.
     * This will look up the file using a local drive path.
     * 
     * @param path The path to look in for the file.
     * @return The file which is stored on the hard drive under the given path.
     */
    public File getFile(String path) {

        String[] parts = path.split(File.SEPERATOR);

        File current = rootFile;
        for (String part : parts) {
            if (!part.isEmpty()) {
                current = current.getChildFile(part);
                if (current == null) {
                    break;
                }
            }
        }

        return current;
    }

    /**
     * Creates a new file using the given path and type on this hard drive and returns it.
     * If the file already exists, the existing file will be returned.
     * A path is a collection of files seperated by a seperator.
     * This will get the file location using a local drive path.
     * 
     * @param path The path the new file will be located under.
     * @param type The file type the new file should has.
     * @return The new file (or the existing one, if the file already exists).
     */
    public File addFile(String path, FileType type) {

        String[] parts = path.split(File.SEPERATOR);
        return addFile(new File(this, parts[parts.length - 1], type), path);
    }

    private File addFile(File file, String path) {

        String[] parts = path.split(File.SEPERATOR);

        File current = rootFile;
        for (int counter = 0; counter < parts.length; counter++) {
            String part = parts[counter];
            if (!part.isEmpty()) {
                if (current.getChildFile(part) == null) {
                    if (counter == parts.length - 1) {
                        current.addChildFile(file);
                        file.setName(part);
                    } else {
                        current.addChildFile(new File(this, part, FileType.DIRECTORY));
                    }
                }
                current = current.getChildFile(part);
            }
        }

        return current;
    }

    /**
     * Returns the total amount of bytes which are occupied by files.
     * 
     * @return The total amount of bytes which are occupied by files.
     */
    public long getFilled() {

        return getFilled(rootFile);
    }

    private long getFilled(File file) {

        long filled = file.getSize();
        if (file.getChildFiles() != null) {
            for (File childFile : file.getChildFiles()) {
                filled += getFilled(childFile);
            }
        }
        return filled;
    }

    /**
     * Returns the total amount of bytes which are not occupied by any files.
     * 
     * @return The total amount of bytes which are not occupied by any files.
     */
    public long getFree() {

        return size - getFilled();
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + letter;
        result = prime * result + (rootFile == null ? 0 : rootFile.hashCode());
        result = prime * result + (int) (size ^ size >>> 32);
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        HardDrive other = (HardDrive) obj;
        if (letter != other.letter) {
            return false;
        }
        if (rootFile == null) {
            if (other.rootFile != null) {
                return false;
            }
        } else if (!rootFile.equals(other.rootFile)) {
            return false;
        }
        if (size != other.size) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {

        return getClass().getName() + " [size=" + size + ", letter=" + letter + ", rootFile=" + rootFile + ", getName()=" + getName() + ", getVersion()=" + getVersion() + ", getVulnerabilities()=" + getVulnerabilities() + "]";
    }

    /**
     * This class represents a file on a hard drive.
     * Every file knows his path and has a content string. Every directory has a list of child files.
     * 
     * @see HardDrive
     */
    @XmlAccessorType (XmlAccessType.FIELD)
    public static class File implements Serializable {

        /**
         * The file type represents if a file is a content file or a directory.
         */
        public static enum FileType {

            FILE, DIRECTORY;
        }

        /**
         * The seperator which seperates different files in a path string.
         */
        public static String      SEPERATOR        = "/";

        private static final long serialVersionUID = 1L;

        @XmlIDREF
        @XmlAttribute
        private HardDrive         host;
        @XmlAttribute
        private String            name;
        @XmlAttribute
        private FileType          type;
        private String            content;
        @XmlElement (name = "child")
        private final List<File>  childs           = new ArrayList<HardDrive.File>();

        /**
         * Creates a new empty file.
         * This is only recommended for direct field access (e.g. for serialization).
         */
        public File() {

        }

        private File(HardDrive host) {

            this.host = host;
            name = "root";
            type = FileType.DIRECTORY;
        }

        private File(HardDrive host, String name, FileType type) {

            this.host = host;
            this.name = name;
            this.type = type;
        }

        /**
         * Returns the hard drive which hosts this file.
         * 
         * @return The hard drive which hosts this file.
         */
        public HardDrive getHost() {

            return host;
        }

        /**
         * Returns the name the file has.
         * 
         * @return The name the file has.
         */
        public String getName() {

            return name;
        }

        private void setName(String name) {

            this.name = name;
        }

        /**
         * Returns the global path the file has.
         * A path is a collection of files seperated by a seperator.
         * The global path also contains the drive letter and can be used on the os level.
         * 
         * @return The path the file has.
         */
        public String getGlobalPath() {

            return host.getLetter() + ":" + getLocalPath();
        }

        /**
         * Returns the local the path the file has.
         * A path is a collection of files seperated by a seperator.
         * The local path can be used on the hardware level to look up a file on a given hard drive.
         * 
         * @return The path the file has.
         */
        public String getLocalPath() {

            List<File> path = new ArrayList<File>();
            host.getRootFile().generatePathSections(this, path);

            String pathString = "";
            for (File pathEntry : path) {
                pathString += SEPERATOR + pathEntry.getName();
            }

            return pathString.isEmpty() ? null : pathString;
        }

        private boolean generatePathSections(File target, List<File> path) {

            for (File child : childs) {
                path.add(child);
                if (target.equals(child) || child.generatePathSections(target, path)) {
                    return true;
                } else {
                    path.remove(child);
                }
            }

            return false;
        }

        /**
         * Returns the type the file has.
         * The type sets if a file is a content one or a directory.
         * 
         * @return The type the file has.
         */
        public FileType getType() {

            return type;
        }

        /**
         * Returns the content the file has (if this file is a content one).
         * If this file isn't a content one, this will return null.
         * 
         * @return The content the file has (if this file is a content one).
         */
        public String getContent() {

            return type == FileType.FILE ? content == null ? "" : content : null;
        }

        /**
         * Changes the content to new one (if this file is a content one).
         * This throws an OutOfSpaceException if there isn't enough space on the host drive for the new content.
         * 
         * @param content The new content to write into the file.
         * @throws OutOfSpaceException If there isn't enough space on the host drive for the new content.
         */
        public void setContent(String content) {

            if (type == FileType.FILE) {
                String oldContent = this.content;
                this.content = content == null ? "" : content;

                if (host.getFilled() > host.getFree()) {
                    long size = getSize();
                    this.content = oldContent;
                    throw new OutOfSpaceException(host, size);
                }
            }
        }

        /**
         * Returns the size this file has in bytes (if this file is a content one).
         * Directories don't have a size.
         * 
         * @return The size this file has in bytes (if this file is a content one).
         */
        public long getSize() {

            return type == FileType.FILE ? (content == null ? "" : content).length() : 0;
        }

        /**
         * Returns the child files the directory contains (if this file is a directory).
         * If this file isn't a directory, this will return null.
         * 
         * @return The child files the directory contains (if this file is a directory).
         */
        public List<File> getChildFiles() {

            return type == FileType.DIRECTORY ? Collections.unmodifiableList(childs) : null;
        }

        /**
         * Looks up the child file with the given name (if this file is a directory).
         * If this file isn't a directory, this will return null.
         * 
         * @param name The name to look for.
         * @return The child file with the given name (if this file is a directory).
         */
        public File getChildFile(String name) {

            if (childs != null) {
                for (File child : childs) {
                    if (child.getName().equals(name)) {
                        return child;
                    }
                }
            }

            return null;
        }

        private void addChildFile(File file) {

            if (!childs.contains(file)) {
                if (file.getSize() > host.getFree()) {
                    throw new OutOfSpaceException(host, file.getSize());
                } else {
                    childs.add(file);
                }
            }
        }

        private void removeChildFile(File file) {

            childs.remove(file);
        }

        /**
         * Returns the parent directory which contains this file.
         * 
         * @return The parent directory which contains this file.
         */
        public File getParent() {

            String path = getLocalPath();
            return host.getFile(path.substring(0, path.lastIndexOf(SEPERATOR)));
        }

        /**
         * Moves the file to a new location under the given path.
         * After the movement, the file can be used like before.
         * This throws an OutOfSpaceException if there isn't enough space on the new host drive for the file.
         * 
         * @param path The new location for the file.
         * @throws OutOfSpaceException If there isn't enough space on the new host drive for the file.
         */
        public void move(String path) {

            remove();

            if (path.contains(":")) {
                host = host.getComputer().getOperatingSystem().getHardDrive(path);
                host.addFile(this, path.split(":")[1]);
            } else {
                host.addFile(this, path);
            }
        }

        /**
         * Changes the name of the file to a new one.
         * After the renaming, the file can be used like before.
         * 
         * @param name The new name for the file.
         */
        public void rename(String name) {

            move(getParent().getLocalPath() + SEPERATOR + name);
        }

        /**
         * Removes this file from the hard drive.
         * If this file is a directory, all child files will also be removed.
         */
        public void remove() {

            getParent().removeChildFile(this);
        }

        @Override
        public int hashCode() {

            final int prime = 31;
            int result = 1;
            result = prime * result + (childs == null ? 0 : childs.hashCode());
            result = prime * result + (content == null ? 0 : content.hashCode());
            result = prime * result + (name == null ? 0 : name.hashCode());
            result = prime * result + (type == null ? 0 : type.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {

            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            File other = (File) obj;
            if (childs == null) {
                if (other.childs != null) {
                    return false;
                }
            } else if (!childs.equals(other.childs)) {
                return false;
            }
            if (content == null) {
                if (other.content != null) {
                    return false;
                }
            } else if (!content.equals(other.content)) {
                return false;
            }
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!name.equals(other.name)) {
                return false;
            }
            if (type != other.type) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {

            return getClass().getName() + " [name=" + name + ", type=" + type + ", content=" + content + ", childs=" + childs + "]";
        }

    }

    /**
     * This runtime exception occures if there is not enough space on a hard drive for handling some new bytes (e.g. from a file).
     */
    public static class OutOfSpaceException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        private final HardDrive   host;
        private final long        size;

        /**
         * Creates a new out of space exception and sets the host which should have handled the new bytes and the amount of new bytes.
         * 
         * @param host The hard drive host which should have handled the new bytes,
         * @param size The amount of new bytes.
         */
        public OutOfSpaceException(HardDrive host, long size) {

            super("Out of space on " + host.getLetter() + ": " + host.getFilled() + "b/" + host.getSize() + "b filled, can't handle " + size + "b");
            this.host = host;
            this.size = size;
        }

        /**
         * Returns the hard drive host which should have handled the new bytes,
         * 
         * @return The hard drive host which should have handled the new bytes,
         */
        public HardDrive getHost() {

            return host;
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

}
