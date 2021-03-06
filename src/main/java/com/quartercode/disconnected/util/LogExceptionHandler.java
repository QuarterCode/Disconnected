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

package com.quartercode.disconnected.util;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class catches all uncaught throwables in all threads and logs them.
 */
public class LogExceptionHandler implements UncaughtExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(LogExceptionHandler.class.getName());

    /**
     * Returns the used logger for logging the exceptions.
     * 
     * @return The used logger for logging the exceptions.
     */
    public static Logger getLogger() {

        return LOGGER;
    }

    /**
     * Creates a new default uncaught exception handler.
     */
    public LogExceptionHandler() {

    }

    @Override
    public void uncaughtException(Thread thread, Throwable t) {

        LOGGER.log(Level.SEVERE, "Uncaught exception in thread \"" + thread.getName() + "\" (id " + thread.getId() + ")", t);
    }

}
