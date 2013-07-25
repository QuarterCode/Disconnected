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

package com.quartercode.disconnected.test.sim.member;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.quartercode.disconnected.sim.member.Member;

public class MemberTest {

    private Member member;

    @Before
    public void setUp() {

        member = new Member("member");
        member.addInterest(new EmptyInterest(0.1F));
        member.addInterest(new EmptyInterest(0.2F));
    }

    @Test
    public void testGetName() {

        Assert.assertEquals("member", member.getName());
    }

    @Test
    public void testGetInterests() {

        Assert.assertEquals("Interest count", 2, member.getInterests().size());
    }

    @Test
    public void testAddInterest() {

        member.addInterest(new EmptyInterest(0.3F));
        Assert.assertEquals("Interest count", 3, member.getInterests().size());
    }

    @Test
    public void testRemoveInterest() {

        member.removeInterest(member.getInterests().get(0));
        Assert.assertEquals("Member count", 1, member.getInterests().size());
    }

}
