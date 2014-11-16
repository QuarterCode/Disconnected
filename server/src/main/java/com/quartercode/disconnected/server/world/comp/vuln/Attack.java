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

package com.quartercode.disconnected.server.world.comp.vuln;

import static com.quartercode.classmod.ClassmodFactory.create;
import org.apache.commons.lang3.reflect.TypeLiteral;
import com.quartercode.classmod.extra.PropertyDefinition;
import com.quartercode.classmod.extra.storage.ReferenceStorage;
import com.quartercode.classmod.extra.storage.StandardStorage;
import com.quartercode.disconnected.server.world.util.WorldFeatureHolder;

/**
 * An attack is targeted at a specific {@link Vulnerability} of a computer part (e.g. a program).
 * It tries to execute a defined {@link #PREFERRED_ACTION}.
 * That action describes what the attacked computer part should do when the attack arrives.
 * For example, there could be {@code "crash"} and {@code "executePayload"} actions for a buffer overflow vulnerability.<br>
 * <br>
 * Note that the preferred action is really "preferred" and might not always be executed.
 * Depending on the environment, something else might happen.
 * For example, the action might be unsafe and sometimes causes the attacked program to crash instead of executing the payload.
 * 
 * @see Vulnerability
 * @see VulnerabilityAction
 * @see VulnerabilityAction#ATTACK_WEIGHT
 */
public class Attack extends WorldFeatureHolder {

    // ----- Properties -----

    /**
     * The {@link Vulnerability} the attack is using to attack a computer part.
     * This is only used to check that the attacked computer part actually has the assumed vulnerability.
     */
    public static final PropertyDefinition<Vulnerability> VULNERABILITY;

    /**
     * Returns the action which should be executed once the attack arrives at the target.
     * See {@link Attack} for more information on the purpose of actions and why the field starts with "preferred".
     */
    public static final PropertyDefinition<String>        PREFERRED_ACTION;

    static {

        VULNERABILITY = create(new TypeLiteral<PropertyDefinition<Vulnerability>>() {}, "name", "vulnerability", "storage", new ReferenceStorage<>());
        PREFERRED_ACTION = create(new TypeLiteral<PropertyDefinition<String>>() {}, "name", "preferredAction", "storage", new StandardStorage<>());

    }

}
