/*
 * This file is part of Disconnected.
 * Copyright (c) 2013 QuarterCode <http://quartercode.com/>
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

import static com.quartercode.classmod.factory.ClassmodFactory.factory;
import com.quartercode.classmod.extra.prop.PropertyDefinition;
import com.quartercode.classmod.extra.storage.StandardStorage;
import com.quartercode.classmod.factory.PropertyDefinitionFactory;
import com.quartercode.disconnected.server.registry.VulnSource.Action;
import com.quartercode.disconnected.server.world.util.WorldFeatureHolder;

/**
 * Vulnerability actions describe what an attacked computer part (e.g. a program) should do.
 * For example, a buffer overflow {@link Vuln vulnerability} could provide actions to crash a program or to execute a payload (e.g. for opening a remote session).
 * On the other hand, an SQL injection vulnerability could be used to access a database.<br>
 * <br>
 * This class stores actions which are assigned to a generated vulnerability.
 * It is independent from the original {@link Action} configuration class.
 * However, it only stores the {@link #ATTACK_WEIGHT} value because the {@code vulnerabilityProbability} is not required anymore after a vulnerability has been generated.<br>
 * <br>
 * When a new {@link Attack} (it might also be an exploit) is "generated" from a vulnerability, the attack weights are used to determine which available action is
 * used as {@link Attack#PREFERRED_ACTION} for the attack.
 * Actions with higher attack weights are more likely to be chosen than actions with lower attack weights.
 * For example, a {@code "crash"} action would usually have a higher attack weight than an {@code "executePayload"} action because it is easier
 * to write an exploit that crashes a program than to write an exploit that executes a binary payload.<br>
 * <br>
 * See {@link Action} for more general information on vulnerability actions.
 * 
 * @see Vuln
 * @see Attack
 * @see Action
 */
public class VulnAction extends WorldFeatureHolder {

    // ----- Properties -----

    /**
     * The name of the vulnerability action.
     * For example, this might be {@code "crash"} or {@code "executePayload"}.
     */
    public static final PropertyDefinition<String>  NAME;

    /**
     * The weight that is used to determine which available action is used for an actual {@link Attack} (e.g. an exploit) when it is "generated" from a {@link Vuln vulnerability}.
     * Actions with higher attack weights are more likely to be chosen than actions with lower attack weights.
     * For example, a {@code "crash"} action would usually have a higher attack weight than an {@code "executePayload"} action because it is easier to
     * write an exploit that crashes a program than to write an exploit that executes a binary payload.
     */
    public static final PropertyDefinition<Integer> ATTACK_WEIGHT;

    static {

        NAME = factory(PropertyDefinitionFactory.class).create("name", new StandardStorage<>());
        ATTACK_WEIGHT = factory(PropertyDefinitionFactory.class).create("attackWeight", new StandardStorage<>());

    }

}
