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

package com.quartercode.disconnected.sim.member.interest;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlIDREF;
import com.quartercode.disconnected.sim.Simulation;
import com.quartercode.disconnected.sim.comp.ComputerPart;
import com.quartercode.disconnected.sim.comp.Vulnerability;
import com.quartercode.disconnected.sim.member.Member;
import com.quartercode.disconnected.sim.member.MemberGroup;
import com.quartercode.disconnected.sim.run.action.Action;
import com.quartercode.disconnected.sim.run.action.AttackAction;
import com.quartercode.disconnected.sim.run.attack.Attack;
import com.quartercode.disconnected.sim.run.attack.Exploit;
import com.quartercode.disconnected.sim.run.attack.Payload;
import com.quartercode.disconnected.util.ProbabilityUtil;

/**
 * This is a simple destroy interest which has a computer as target.
 * The executor of the resulting action should destroy the target.
 * 
 * @see Interest
 * @see Target
 */
@XmlAccessorType (XmlAccessType.FIELD)
public class DestroyInterest extends Interest implements Target {

    @XmlIDREF
    private Member target;

    /**
     * Creates a new empty destroy interest object.
     * This is only recommended for direct field access (e.g. for serialization).
     */
    public DestroyInterest() {

    }

    /**
     * Creates a new destroy interest and sets the priority and the computer target.
     * 
     * @param priority The priority of the interest.
     * @param target The member target the interest has.
     */
    public DestroyInterest(float priority, Member target) {

        super(priority);

        this.target = target;
    }

    @Override
    public Member getTarget() {

        return target;
    }

    @Override
    public int getReputationChange(Simulation simulation, Member member, MemberGroup group) {

        int change = (int) (getPriority() * 30);

        if (group.getMembers().contains(member)) {
            if (!group.getInterests().contains(this)) {
                change /= 2;
            }
        } else {
            change = -change;
        }

        return change;
    }

    @Override
    public Action getAction(Simulation simulation, Member member) {

        // Calculate probability for executing the action
        int currentReputation = simulation.getGroup(member).getReputation(member).getValue();
        float probability = getPriority() * (getReputationChange(simulation, member, simulation.getGroup(member)) * 20F) / ( (currentReputation == 0 ? 1 : currentReputation) * 100);

        if (ProbabilityUtil.genPseudo(probability)) {
            // Collect all vulnerabilities
            List<Vulnerability> vulnerabilities = new ArrayList<Vulnerability>();
            for (ComputerPart part : member.getComputer().getParts()) {
                vulnerabilities.addAll(part.getVulnerabilities());
            }

            // Take the first avaiable vulnerability and quickly develop a new exploit
            if (vulnerabilities.size() > 0) {
                Exploit exploit = new Exploit(vulnerabilities.get(0));

                // Also develop a brand new payload which immediatly destroys the target computer
                List<String> scripts = new ArrayList<String>();
                scripts.add("simulation.removeMember(member)");
                scripts.add("simulation.getGroup(member).removeMember(member)");
                scripts.add("simulation.removeComputer(member.getComputer())");

                // Use the first avaiable operating system as execution environment
                Payload payload = new Payload(member.getComputer().getOperatingSystems().get(0), scripts);

                return new AttackAction(this, new Attack(target, exploit, payload));
            }
        }

        return null;
    }

}