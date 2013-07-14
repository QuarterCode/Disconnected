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

package com.quartercode.disconnected.sim.comp;

import java.util.List;

/**
 * This class stores information about an operating system.
 * This also contains a list of all vulnerabilities this operating system has.
 * 
 * @see ComputerPart
 */
public class OperatingSystem extends ComputerPart {

    /**
     * Creates a new operating system and sets the name, the version and the vulnerabilities.
     * 
     * @param name The name the operating system has.
     * @param version The version of the operating system (hasn't to be a number).
     * @param vulnerabilities The vulnerabilities the operating system has.
     */
    public OperatingSystem(String name, String version, List<Vulnerability> vulnerabilities) {

        super(name, version, vulnerabilities);
    }

}
