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

package com.quartercode.disconnected.server.world.comp.program;

import static com.quartercode.classmod.ClassmodFactory.create;
import org.apache.commons.lang3.reflect.TypeLiteral;
import com.quartercode.classmod.extra.conv.CFeatureHolder;
import com.quartercode.classmod.extra.func.FunctionDefinition;
import com.quartercode.disconnected.shared.util.XmlPersistent;

/**
 * A process state listener is notified after the {@link ProcessState} of a {@link Process} has changed.
 * Such listeners can be activated by adding them to {@link Process#STATE_LISTENERS}.
 */
@XmlPersistent
public interface ProcessStateListener extends CFeatureHolder {

    /**
     * Called after the {@link ProcessState} of the given {@link Process} has changed.
     * 
     * <table>
     * <tr>
     * <th>Index</th>
     * <th>Type</th>
     * <th>Parameter</th>
     * <th>Description</th>
     * </tr>
     * <tr>
     * <td>0</td>
     * <td>{@link Process}&lt;?&gt;</td>
     * <td>process</td>
     * <td>The process whose state has changed.</td>
     * </tr>
     * <tr>
     * <td>1</td>
     * <td>{@link ProcessState}</td>
     * <td>oldState</td>
     * <td>The old state of the process.</td>
     * </tr>
     * <tr>
     * <td>2</td>
     * <td>{@link ProcessState}</td>
     * <td>newState</td>
     * <td>The new state of the process.</td>
     * </tr>
     * </table>
     */
    public static final FunctionDefinition<Void> ON_STATE_CHANGE = create(new TypeLiteral<FunctionDefinition<Void>>() {}, "name", "onStateChange", "parameters", new Class[] { Process.class, ProcessState.class, ProcessState.class });

}
