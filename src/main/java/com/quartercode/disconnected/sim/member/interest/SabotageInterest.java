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
import com.quartercode.disconnected.sim.comp.OperatingSystem;
import com.quartercode.disconnected.sim.member.Member;
import com.quartercode.disconnected.sim.member.MemberGroup;
import com.quartercode.disconnected.sim.run.action.Action;
import com.quartercode.disconnected.sim.run.action.AttackAction;
import com.quartercode.disconnected.sim.run.attack.Exploit;
import com.quartercode.disconnected.sim.run.attack.Payload;

/**
 * This is a simple sabotage interest which has a computer as target.
 * 
 * @see Interest
 * @see Target
 */
@XmlAccessorType (XmlAccessType.FIELD)
public class SabotageInterest extends Interest implements Target {

    @XmlIDREF
    private Member target;

    /**
     * Creates a new empty sabotage interest object.
     * This is only recommended for direct field access (e.g. for serialization).
     */
    public SabotageInterest() {

    }

    /**
     * Creates a new sabotage interest and sets the priority and the computer target.
     * 
     * @param priority The priority of the interest.
     * @param target The member target the interest has.
     */
    public SabotageInterest(int priority, Member target) {

        super(priority);

        this.target = target;
    }

    @Override
    public Member getTarget() {

        return target;
    }

    @Override
    public int getReputationChange(Simulation simulation, Member member, MemberGroup group) {

        int change = getPriority() * 3;

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

        // TODO: Implement real calculation (instead of dev placeholder for simple tests)

        // Only look for vulnerabilities on the OS; no other ones added yet
        for (OperatingSystem operatingSystem : member.getComputer().getOperatingSystems()) {
            if (operatingSystem.getVulnerabilities().size() > 0) {
                // Take the first avaiable vulnerability and fastly develop a new exploit which let the user gain the highest possible right level
                Exploit exploit = new Exploit(operatingSystem.getVulnerabilities().get(0), operatingSystem.getRightLevels().get(operatingSystem.getRightLevels().size() - 1));

                // Also develop a brand new payload which immediatly destroys the target computer
                List<String> scripts = new ArrayList<String>();
                scripts.add("simulation.getGroup(member).removeMember(member)");
                scripts.add("simulation.removeMember(member)");
                scripts.add("simulation.removeComputer(member.getComputer())");
                Payload payload = new Payload(operatingSystem, scripts);

                return new AttackAction(target, exploit, payload);
            }
        }

        return null;
    }

}
