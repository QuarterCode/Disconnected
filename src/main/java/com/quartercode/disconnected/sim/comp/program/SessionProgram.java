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

package com.quartercode.disconnected.sim.comp.program;

import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlIDREF;
import com.quartercode.disconnected.sim.comp.Version;
import com.quartercode.disconnected.sim.comp.Vulnerability;
import com.quartercode.disconnected.sim.comp.os.User;
import com.quartercode.disconnected.util.InfoString;

/**
 * This class represents program which opens a session.
 * For opening an actual session, you just have to create a process out of this with the argument "user".
 * 
 * @see Program
 * @see Session
 */
public abstract class SessionProgram extends Program {

    private boolean serializable;

    /**
     * Creates a new empty session program.
     * This is only recommended for direct field access (e.g. for serialization).
     */
    protected SessionProgram() {

    }

    /**
     * Creates a new session program and only sets the serializable attribute.
     * This can be used by subclasses with default constructors.
     * 
     * @param serializable True if session instances are serializable.
     */
    protected SessionProgram(boolean serializable) {

        this.serializable = serializable;
    }

    /**
     * Creates a new session program and sets the name, the version, the vulnerabilities and if session instances are serializable.
     * 
     * @param name The name the session program has.
     * @param version The current version the session program has.
     * @param vulnerabilities The vulnerabilities the session program has.
     * @param serializable True if session instances are serializable.
     */
    protected SessionProgram(String name, Version version, List<Vulnerability> vulnerabilities, boolean serializable) {

        super(name, version, vulnerabilities);

        this.serializable = serializable;
    }

    @Override
    protected void addParameters() {

        addParameter("user", User.class);
    }

    /**
     * Returns true if instances of this session program are serializable, false if not.
     * Sessions which are not serializable must be closed before the simulation can be serialized.
     * 
     * @return True if instances of this session program are serializable, false if not.
     */
    public boolean isSerializable() {

        return serializable;
    }

    @Override
    protected ProgramExecutor createExecutorInstance(Process host, Map<String, Object> arguments) {

        return openSession(host, (User) arguments.get("user"));
    }

    /**
     * Creates and returns a new session instance of the implementing session program.
     * 
     * @param host The host process of the session process.
     * @param user The user the new session will run under.
     * @return The new open session instance.
     */
    protected abstract Session openSession(Process host, User user);

    /**
     * A session is an instance of a session program.
     * Sessions are used for letting users interact with a system.
     * Such a session is simply a process, every child of this process can use the rights provided by the parent session.
     * 
     * @see SessionProgram
     */
    public static abstract class Session extends ProgramExecutor implements InfoString {

        @XmlIDREF
        private final User user;

        /**
         * Creates a new session instance and sets the parent process and the user the session is running under.
         * 
         * @param host The parent process of the session instance.
         * @param user The user the session is running under.
         */
        protected Session(Process host, User user) {

            super(host);

            this.user = user;
        }

        /**
         * Returns the user the session is running under.
         * Every child process of this session can use the rights provided by this session.
         * 
         * @return The user the session is running under.
         */
        public User getUser() {

            return user;
        }

        /**
         * Closes the session.
         * You need to close sessions after usage, so the operating system can free the resources.
         * Sessions which are not serializable must also be closed before the simulation can be serialized.
         */
        protected abstract void close();

        @Override
        public final void update() {

            // Do nothing
        }

        @Override
        public String toInfoString() {

            return user.getName() + "@" + getHost().getHost().getId();
        }

    }

}
