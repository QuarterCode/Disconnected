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

package com.quartercode.disconnected;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import com.quartercode.disconnected.graphics.GraphicsManager;
import com.quartercode.disconnected.graphics.session.DesktopState;
import com.quartercode.disconnected.profile.ProfileManager;
import com.quartercode.disconnected.sim.Simulation;
import com.quartercode.disconnected.sim.comp.Computer;
import com.quartercode.disconnected.sim.comp.hardware.CPU;
import com.quartercode.disconnected.sim.comp.hardware.HardDrive;
import com.quartercode.disconnected.sim.comp.hardware.Mainboard;
import com.quartercode.disconnected.sim.comp.hardware.NetworkInterface;
import com.quartercode.disconnected.sim.comp.hardware.RAM;
import com.quartercode.disconnected.sim.comp.program.ExploitProgram;
import com.quartercode.disconnected.sim.comp.program.KernelProgram;
import com.quartercode.disconnected.sim.comp.program.SystemViewerProgram;
import com.quartercode.disconnected.sim.comp.session.DesktopSessionProgram;
import com.quartercode.disconnected.sim.member.ai.PlayerController;
import com.quartercode.disconnected.sim.member.ai.UserController;
import com.quartercode.disconnected.sim.member.interest.DestroyInterest;
import com.quartercode.disconnected.sim.run.TickAction;
import com.quartercode.disconnected.sim.run.TickSimulator;
import com.quartercode.disconnected.sim.run.TickTimer;
import com.quartercode.disconnected.sim.run.Ticker;
import com.quartercode.disconnected.sim.run.util.SimulationGenerator;
import com.quartercode.disconnected.util.LogExceptionHandler;
import com.quartercode.disconnected.util.Registry;
import com.quartercode.disconnected.util.ResourceStore;

/**
 * The main class which initalizes the whole game.
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    /**
     * The main method which initalizes the whole game.
     * 
     * @param args The command line arguments.
     */
    public static void main(String[] args) {

        // Logging configuration
        try {
            LogManager.getLogManager().readConfiguration(Main.class.getResourceAsStream("/config/logging.properties"));
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Can't load logging configuration", e);
            return;
        }

        // Default exception handler if the vm throws an exception to the entry point of thread (e.g. main() or run())
        Thread.setDefaultUncaughtExceptionHandler(new LogExceptionHandler());

        // Print information about the software
        LOGGER.info("Version " + Disconnected.getVersion());

        // Initalize & fill registry
        LOGGER.info("Initalizing & filling class registry");
        Disconnected.setRegistry(new Registry());
        fillRegistry(Disconnected.getRegistry());

        // Initalize & fill resource store
        try {
            LOGGER.info("Initalizing & filling resource store");
            Disconnected.setRS(new ResourceStore());
            fillResourceStore(Disconnected.getRS());
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Can't fill resource store", e);
            return;
        }

        // Initalize profile manager and load stored profiles (TODO: Add code for loading).
        LOGGER.info("Initalizing profile manager");
        Disconnected.setProfileManager(new ProfileManager());

        // Initalize graphics manager and start it
        LOGGER.info("Initalizing & starting graphics manager");
        Disconnected.setGraphicsManager(new GraphicsManager());
        Disconnected.getGraphicsManager().setRunning(true);

        // Initalize ticker
        LOGGER.info("Initalizing ticker");
        List<TickAction> tickActions = new ArrayList<TickAction>();
        tickActions.add(new TickTimer());
        tickActions.add(new TickSimulator());
        Disconnected.setTicker(new Ticker(tickActions.toArray(new TickAction[tickActions.size()])));

        // DEBUG: Generate and set new simulation
        LOGGER.info("DEBUG-ACTION: Generating new simulation");
        Simulation simulation = SimulationGenerator.generateSimulation(10, 2);
        for (Computer computer : simulation.getComputers()) {
            computer.getOperatingSystem().setRunning(true);
        }
        Disconnected.setSimulation(simulation);
        // DEBUG: Start "game" with current simulation
        LOGGER.info("DEBUG-ACTION: Starting test-game with current simulation");
        Disconnected.getTicker().setRunning(true);
        Disconnected.getGraphicsManager().setState(new DesktopState(simulation));
    }

    /**
     * Fills the given registry with the default values which are needed for running vanilla disconnected.
     * 
     * @param registry The registry to fill.
     */
    public static void fillRegistry(Registry registry) {

        // Hardware
        registry.registerClass(Mainboard.class);
        registry.registerClass(CPU.class);
        registry.registerClass(RAM.class);
        registry.registerClass(HardDrive.class);
        registry.registerClass(NetworkInterface.class);

        // Programs
        registry.registerClass(KernelProgram.class);

        registry.registerClass(DesktopSessionProgram.class);

        registry.registerClass(SystemViewerProgram.class);
        registry.registerClass(ExploitProgram.class);

        // AI Controllers
        registry.registerClass(PlayerController.class);
        registry.registerClass(UserController.class);

        // Interests
        registry.registerClass(DestroyInterest.class);

        // Themes
        registry.registerTheme(Main.class.getResource("/ui/default/default.xml"));
        registry.registerTheme(Main.class.getResource("/ui/shell/shell.xml"));
        registry.registerTheme(Main.class.getResource("/ui/desktop/desktop.xml"));
    }

    /**
     * Fills the given resource store with the default resource objects which are needed for running vanilla disconnected.
     * 
     * @param resourceStore The resource store to fill.
     * @throws IOException Something goes wrong while reading from a jar file or resource.
     */
    public static void fillResourceStore(ResourceStore resourceStore) throws IOException {

        resourceStore.loadFromClasspath("/data");
    }

    private Main() {

    }

}
