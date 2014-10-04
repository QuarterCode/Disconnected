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

package com.quartercode.disconnected.client.graphics;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.quartercode.disconnected.shared.util.ApplicationInfo;
import com.quartercode.disconnected.shared.util.ExitUtil;
import de.matthiasmann.twl.Container;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;
import de.matthiasmann.twl.utils.PNGDecoder;

/**
 * This thread executes the OpenGL update which keeps the lwjgl display alive.
 * 
 * @see GraphicsService
 */
public class GraphicsThread extends Thread {

    private static final Logger   LOGGER   = LoggerFactory.getLogger(GraphicsThread.class);

    private final List<URL>       themes;

    private LWJGLRenderer         renderer;
    private GUI                   gui;
    private ThemeManager          theme;
    private Container             root;

    private GraphicsState         newState;
    private final Queue<Runnable> toInvoke = new LinkedList<>();
    private boolean               exit;

    /**
     * Creates a new graphics update thread which loads the given twl themes.
     * 
     * @param themes The {@link URL}s of the themes that should be loaded.
     */
    public GraphicsThread(List<URL> themes) {

        super("graphis");

        this.themes = themes;
    }

    /**
     * Changes the current {@link GraphicsState} to the given one.
     * The change is applied on the next tick.
     * 
     * @param newState The new {@link GraphicsState} to use.
     */
    protected void changeState(GraphicsState newState) {

        this.newState = newState;
    }

    /**
     * Invokes the given {@link Runnable} in the graphics update thread.
     * 
     * @param runnable The runnable to invoke in the graphics update thread.
     */
    protected void invoke(Runnable runnable) {

        toInvoke.offer(runnable);
    }

    /**
     * Tells the graphics update thread to exit after the current loop cycle.
     * This method has to be used instead of {@link #interrupt()} because the interrupt system is blocked by LWJGL.
     */
    protected void exit() {

        exit = true;
    }

    @Override
    public void run() {

        try {
            initialize();
            startLoop();
        } catch (LWJGLException e) {
            LOGGER.error("Error while creating lwjgl display", e);
        } catch (IOException e) {
            LOGGER.error("Error while loading files", e);
        } finally {
            end();
        }
    }

    private void initialize() throws LWJGLException, IOException {

        createDisplay();
        createRoot();
        createRenderer();
        loadTheme();
        gui.applyTheme(theme);
    }

    private void createDisplay() throws LWJGLException, IOException {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Display.setDisplayMode(new DisplayMode((int) (screenSize.width * 0.75F), (int) (screenSize.height * 0.75F)));
        Display.setTitle("Disconnected " + ApplicationInfo.VERSION);
        Display.setIcon(loadIcons());
        Display.setVSyncEnabled(true);
        Display.setResizable(true);
        Display.create();
    }

    private ByteBuffer[] loadIcons() throws IOException {

        ByteBuffer[] icons = new ByteBuffer[4];
        icons[0] = loadImage(getClass().getResource("/icons/icon16.png"));
        icons[1] = loadImage(getClass().getResource("/icons/icon32.png"));
        icons[2] = loadImage(getClass().getResource("/icons/icon64.png"));
        icons[3] = loadImage(getClass().getResource("/icons/icon128.png"));
        return icons;
    }

    private ByteBuffer loadImage(URL url) throws IOException {

        try (InputStream inputStream = url.openStream()) {
            PNGDecoder decoder = new PNGDecoder(inputStream);
            ByteBuffer buffer = ByteBuffer.allocateDirect(decoder.getWidth() * decoder.getHeight() * 4);
            decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            buffer.flip();
            return buffer;
        }
    }

    private void createRoot() {

        root = new Container();
        root.setTheme("");
    }

    private void createRenderer() throws LWJGLException {

        renderer = new LWJGLRenderer();
        renderer.setUseSWMouseCursors(true);

        gui = new GUI(root, renderer);
        gui.setSize();

        renderer.syncViewportSize();
    }

    private void loadTheme() throws LWJGLException, IOException {

        File themeFile = File.createTempFile(ApplicationInfo.TITLE + "-theme", ".xml");

        try (PrintWriter themeFileWriter = new PrintWriter(themeFile)) {
            themeFileWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            themeFileWriter.println("<!DOCTYPE themes PUBLIC \"-//www.matthiasmann.de//TWL-Theme//EN\"");
            themeFileWriter.println("\"http://hg.l33tlabs.org/twl/raw-file/tip/src/de/matthiasmann/twl/theme/theme.dtd\">");
            themeFileWriter.println("<themes>");
            for (URL themeURL : themes) {
                themeFileWriter.println("<include filename=\"" + themeURL + "\"/>");
            }
            themeFileWriter.println("</themes>");
            themeFileWriter.flush();
            theme = ThemeManager.createThemeManager(themeFile.toURI().toURL(), renderer);
        } catch (IOException e) {
            throw new IOException("Error while creating temporary theme file", e);
        } finally {
            themeFile.delete();
        }
    }

    private void startLoop() {

        GraphicsState lastState = null;
        while (!Display.isCloseRequested() && !exit) {
            if (newState != null) {
                if (lastState != null) {
                    root.removeChild(lastState);
                }
                lastState = newState;
                newState = null;
                if (lastState != null) {
                    root.add(lastState);
                }
            }

            while (!toInvoke.isEmpty()) {
                toInvoke.poll().run();
            }

            if (Display.wasResized()) {
                GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
                renderer.setViewport(0, 0, Display.getWidth(), Display.getHeight());
            }

            gui.update();
            Display.sync(60);
            Display.update();

            // Reduce lag on input devices
            GL11.glGetError();
            Display.processMessages();
            Mouse.poll();
            Keyboard.poll();
        }
    }

    private void end() {

        if (gui != null) {
            gui.destroy();
        }
        if (theme != null) {
            theme.destroy();
        }
        Display.destroy();

        // Try to exit (the exit method blocks the call if it was already called)
        ExitUtil.exit();
    }

}