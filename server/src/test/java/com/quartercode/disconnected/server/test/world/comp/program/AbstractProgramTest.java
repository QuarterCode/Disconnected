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

package com.quartercode.disconnected.server.test.world.comp.program;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import com.quartercode.disconnected.server.DefaultServerData;
import com.quartercode.disconnected.server.world.World;
import com.quartercode.disconnected.server.world.comp.Computer;
import com.quartercode.disconnected.server.world.comp.Version;
import com.quartercode.disconnected.server.world.comp.file.ContentFile;
import com.quartercode.disconnected.server.world.comp.file.FileAddAction;
import com.quartercode.disconnected.server.world.comp.file.FileSystem;
import com.quartercode.disconnected.server.world.comp.file.FileSystemModule;
import com.quartercode.disconnected.server.world.comp.file.FileSystemModule.KnownFileSystem;
import com.quartercode.disconnected.server.world.comp.file.StringFileTypeMapper;
import com.quartercode.disconnected.server.world.comp.os.OperatingSystem;
import com.quartercode.disconnected.server.world.comp.program.ProcessModule;
import com.quartercode.disconnected.server.world.comp.program.Program;
import com.quartercode.disconnected.server.world.comp.program.ProgramCommonLocationMapper;
import com.quartercode.disconnected.server.world.comp.program.ProgramExecutor;
import com.quartercode.disconnected.server.world.gen.WorldGenerator;
import com.quartercode.eventbridge.EventBridgeFactory;
import com.quartercode.eventbridge.bridge.Bridge;
import com.quartercode.eventbridge.extra.extension.ReturnEventExtensionRequester;
import com.quartercode.eventbridge.extra.extension.ReturnEventExtensionReturner;

public abstract class AbstractProgramTest {

    @BeforeClass
    public static void setUpBeforeClass() {

        DefaultServerData.addDefaultStringFileTypeMappings();
        DefaultServerData.addDefaultProgramCommonLocationMappings();
    }

    @AfterClass
    public static void tearDownAfterClass() {

        StringFileTypeMapper.clearMappings();
        ProgramCommonLocationMapper.clearMappings();
    }

    @Rule
    public JUnitRuleMockery   context = new JUnitRuleMockery();

    protected final String    fileSystemMountpoint;

    protected Bridge          bridge;
    protected World           world;
    protected Computer        computer;
    protected OperatingSystem os;
    protected ProcessModule   processModule;
    protected FileSystem      fileSystem;

    protected AbstractProgramTest(String fileSystemMountpoint) {

        this.fileSystemMountpoint = fileSystemMountpoint;
    }

    @Before
    public void setUp() {

        bridge = EventBridgeFactory.create(Bridge.class);
        bridge.addModule(EventBridgeFactory.create(ReturnEventExtensionRequester.class));
        bridge.addModule(EventBridgeFactory.create(ReturnEventExtensionReturner.class));

        world = new World();
        world.injectBridge(bridge);

        computer = WorldGenerator.generateComputer(false);
        world.addCol(World.COMPUTERS, computer);

        os = computer.getObj(Computer.OS);
        os.invoke(OperatingSystem.SET_RUNNING, true);

        processModule = os.getObj(OperatingSystem.PROC_MODULE);

        FileSystemModule fsModule = os.getObj(OperatingSystem.FS_MODULE);
        for (KnownFileSystem knownFs : fsModule.getCol(FileSystemModule.KNOWN_FS)) {
            if (knownFs.getObj(KnownFileSystem.MOUNTPOINT).equals(fileSystemMountpoint)) {
                fileSystem = knownFs.getObj(KnownFileSystem.FILE_SYSTEM);
                break;
            }
        }
    }

    protected Program createProgram(Class<? extends ProgramExecutor> executorClass, int majorVersion, int minorVersion, int revisionVersion) {

        Program program = new Program();
        program.setObj(Program.EXECUTOR_CLASS, executorClass);

        Version version = new Version();
        version.setObj(Version.MAJOR, majorVersion);
        version.setObj(Version.MINOR, minorVersion);
        version.setObj(Version.REVISION, revisionVersion);
        program.setObj(Program.VERSION, version);

        return program;
    }

    protected ContentFile addProgram(FileSystem fileSystem, Program program, String path) {

        ContentFile file = new ContentFile();
        file.setObj(ContentFile.CONTENT, program);
        fileSystem.invoke(FileSystem.CREATE_ADD_FILE, file, path).invoke(FileAddAction.EXECUTE);
        return file;
    }

}
