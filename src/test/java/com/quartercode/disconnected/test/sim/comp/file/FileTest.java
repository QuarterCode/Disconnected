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

package com.quartercode.disconnected.test.sim.comp.file;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.quartercode.disconnected.sim.comp.Computer;
import com.quartercode.disconnected.sim.comp.Version;
import com.quartercode.disconnected.sim.comp.file.File;
import com.quartercode.disconnected.sim.comp.file.File.FileType;
import com.quartercode.disconnected.sim.comp.file.FileSystem;
import com.quartercode.disconnected.sim.comp.hardware.HardDrive;
import com.quartercode.disconnected.sim.comp.os.OperatingSystem;
import com.quartercode.disconnected.util.size.ByteUnit;

public class FileTest {

    private FileSystem fileSystem;
    private File       testFile;

    @Before
    public void setUp() {

        Computer computer = new Computer("0");

        OperatingSystem operatingSystem = new OperatingSystem(computer, "OperatingSystem", new Version(1, 0, 0), null);
        computer.setOperatingSystem(operatingSystem);

        HardDrive hardDrive = new HardDrive(computer, "HardDrive", new Version(1, 0, 0), null, ByteUnit.BYTE.convert(1, ByteUnit.TERABYTE));
        fileSystem = hardDrive.getFileSystem();
        computer.addHardware(hardDrive);
        operatingSystem.getFileSystemManager().mount(fileSystem, 'C');

        testFile = fileSystem.addFile("/test1/test2/test.txt", FileType.FILE);
        testFile.setContent("Test-Content");
    }

    @Test
    public void testGetLocalPath() {

        Assert.assertEquals("Local path is correct", "/test1/test2/test.txt", testFile.getLocalPath());
    }

    @Test
    public void testMove() {

        testFile.move("/test1/test3/test.txt");

        Assert.assertEquals("Moved file exists", testFile, fileSystem.getFile("/test1/test3/test.txt"));
        Assert.assertEquals("Moved file has correct path", "/test1/test3/test.txt", testFile.getLocalPath());
        Assert.assertEquals("Moved file has correct content", "Test-Content", testFile.getContent());
    }

    @Test
    public void testRename() {

        testFile.rename("test2.txt");

        Assert.assertEquals("Renamed file exists", testFile, fileSystem.getFile("/test1/test2/test2.txt"));
        Assert.assertEquals("Renamed file has correct path", "/test1/test2/test2.txt", testFile.getLocalPath());
        Assert.assertEquals("Renamed file has correct content", "Test-Content", testFile.getContent());
    }

    @Test
    public void testRemove() {

        testFile.remove();

        Assert.assertEquals("Removed file no longer exists", null, fileSystem.getFile("/test1/test2/test1.txt"));
        Assert.assertEquals("Renamed file has null path", null, testFile.getLocalPath());
    }

}
