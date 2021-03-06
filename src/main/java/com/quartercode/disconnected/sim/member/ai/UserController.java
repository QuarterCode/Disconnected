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

package com.quartercode.disconnected.sim.member.ai;

import java.util.ArrayList;
import com.quartercode.disconnected.sim.Simulation;
import com.quartercode.disconnected.sim.member.Member;
import com.quartercode.disconnected.sim.member.MemberGroup;
import com.quartercode.disconnected.sim.member.action.Action;
import com.quartercode.disconnected.sim.member.interest.DestroyInterest;
import com.quartercode.disconnected.sim.member.interest.HasTarget;
import com.quartercode.disconnected.sim.member.interest.Interest;
import com.quartercode.disconnected.util.ProbabilityUtil;

/**
 * The user controller simulates a typical computer user.
 * An ai controller executes tick updates for a member type.
 * 
 * @see AIController
 */
public class UserController extends AIController {

    /**
     * Creates a new empty user controller.
     * This is only recommended for direct field access (e.g. for serialization).
     */
    protected UserController() {

    }

    /**
     * Creates a new user controller and sets the member which should be simulated.
     * 
     * @param member The member which should be simulated using this controller.
     */
    public UserController(Member member) {

        super(member);
    }

    @Override
    public void update(Simulation simulation) {

        // Generate member interests against members of other groups
        if (ProbabilityUtil.genPseudo(simulation.RANDOM.nextFloat() / 100F, simulation.RANDOM)) {
            if (getMember().getBrainData(Interest.class).size() < 5) {
                MemberGroup group = simulation.getGroup(getMember());
                targetLoop:
                for (Member target : simulation.getMembers()) {
                    if (!simulation.getGroup(target).equals(group)) {
                        for (Interest interest : getMember().getBrainData(Interest.class)) {
                            if (interest instanceof HasTarget && ((HasTarget) interest).getTarget().equals(target)) {
                                continue targetLoop;
                            }
                        }

                        if (ProbabilityUtil.genPseudo(simulation.RANDOM.nextFloat() + -group.getReputation(target).getValue() / 100F, simulation.RANDOM)) {
                            float priority = simulation.RANDOM.nextFloat() - group.getReputation(target).getValue() / 40F;
                            if (priority > 1) {
                                priority = 1;
                            }
                            getMember().addBrainData(new DestroyInterest(priority, target));
                            break;
                        }
                    }
                }
            }
        }

        // Execute member interests
        for (Interest interest : new ArrayList<Interest>(getMember().getBrainData(Interest.class))) {
            if (interest instanceof HasTarget && !simulation.getMembers().contains( ((HasTarget) interest).getTarget())) {
                continue;
            } else {
                Action action = interest.getAction(simulation, getMember());
                if (action != null) {
                    if (action.execute(simulation, getMember())) {
                        getMember().removeBrainData(interest);
                    }

                    break;
                }
            }
        }
    }

}
