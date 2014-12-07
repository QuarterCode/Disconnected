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

package com.quartercode.disconnected.shared.event.comp.program.control;

import com.quartercode.disconnected.shared.event.comp.program.SBPWorldProcessUserCommand;
import com.quartercode.disconnected.shared.world.comp.program.SBPWorldProcessUserId;
import com.quartercode.disconnected.shared.world.comp.program.WorldProcessId;

/**
 * This event is sent when a program is launched using a {@link WorldProcessLaunchCommand} on the computer of the receiving world process user.
 * It contains the {@link WorldProcessId} of the newly launched process, which is required for further communication.
 * 
 * @see WorldProcessLaunchCommand
 */
public class WorldProcessLaunchAcknowledgmentEvent extends SBPWorldProcessUserCommand {

    private static final long           serialVersionUID = 1474812214810071321L;

    private final SBPWorldProcessUserId worldProcessUserId;
    private final WorldProcessId        worldProcessId;

    /**
     * Creates a new world process launch acknowledgment event.
     * 
     * @param worldProcessUserId The {@link SBPWorldProcessUserId} that should receive the event.
     *        That world process user must have launched the new world process.
     * @param worldProcessId The {@link WorldProcessId} of the newly launched world process.
     */
    public WorldProcessLaunchAcknowledgmentEvent(SBPWorldProcessUserId worldProcessUserId, WorldProcessId worldProcessId) {

        this.worldProcessUserId = worldProcessUserId;
        this.worldProcessId = worldProcessId;
    }

    @Override
    public SBPWorldProcessUserId getWorldProcessUserId() {

        return worldProcessUserId;
    }

    /**
     * Returns the {@link WorldProcessId} of the newly launched world process.
     * This event was sent because that new process has been launched.
     * 
     * @return The process id of the new process.
     */
    public WorldProcessId getWorldProcessId() {

        return worldProcessId;
    }

}
