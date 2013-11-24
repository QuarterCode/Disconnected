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

package com.quartercode.disconnected.test.mocl.extra.def;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.quartercode.disconnected.mocl.extra.def.TickFeature;
import com.quartercode.disconnected.mocl.extra.def.TickFeature.UpdateTask;

@RunWith (Parameterized.class)
public class TickFeatureTest {

    @Parameters
    public static Collection<Object[]> data() {

        List<Object[]> data = new ArrayList<Object[]>();

        data.add(new Object[] { 2, 0, 3, 1 });
        data.add(new Object[] { 4, 0, 3, 0 });

        data.add(new Object[] { 0, 3, 10, 3 });
        data.add(new Object[] { 0, 5, 10, 2 });
        data.add(new Object[] { 0, 10, 10, 1 });
        data.add(new Object[] { 0, 11, 10, 0 });

        return data;
    }

    private TickFeature tickFeature;

    private final int   delay;
    private final int   period;
    private final int   ticks;
    private final int   invokations;

    public TickFeatureTest(int delay, int period, int ticks, int invokations) {

        this.delay = delay;
        this.period = period;
        this.ticks = ticks;
        this.invokations = invokations;
    }

    @Before
    public void setUp() {

        tickFeature = new TickFeature("testTickFeature", null);
    }

    @Test
    public void testUpdate() {

        final AtomicInteger actualInvokations = new AtomicInteger();
        tickFeature.add(new UpdateTask(new Runnable() {

            @Override
            public void run() {

                actualInvokations.incrementAndGet();
            }

        }, delay, period));

        for (int tick = 0; tick < ticks; tick++) {
            tickFeature.update();
        }

        Assert.assertEquals("Update task invokations", invokations, actualInvokations.get());
    }

}
