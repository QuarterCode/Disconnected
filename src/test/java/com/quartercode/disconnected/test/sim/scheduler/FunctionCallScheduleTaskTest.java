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

package com.quartercode.disconnected.test.sim.scheduler;

import static com.quartercode.classmod.ClassmodFactory.create;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.reflect.TypeLiteral;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.quartercode.classmod.base.FeatureDefinition;
import com.quartercode.classmod.base.def.DefaultFeatureHolder;
import com.quartercode.classmod.extra.FunctionDefinition;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionInvocation;
import com.quartercode.classmod.util.FeatureDefinitionReference;
import com.quartercode.disconnected.sim.scheduler.FunctionCallScheduleTask;
import com.quartercode.disconnected.sim.scheduler.Scheduler;

@RunWith (Parameterized.class)
public class FunctionCallScheduleTaskTest {

    private static boolean executedTestFunction;

    @Parameters
    public static Collection<Object[]> data() {

        List<Object[]> data = new ArrayList<>();

        data.add(new Object[] { 1 });
        data.add(new Object[] { 5 });
        data.add(new Object[] { 100 });

        return data;
    }

    private final int         delay;

    private TestFeatureHolder schedulerHolder;
    private Scheduler         scheduler;

    public FunctionCallScheduleTaskTest(int delay) {

        this.delay = delay;
    }

    @Before
    public void setUp() {

        executedTestFunction = false;

        schedulerHolder = new TestFeatureHolder();
        scheduler = schedulerHolder.get(TestFeatureHolder.SCHEDULER);
    }

    @Test
    public void testSchedule() {

        scheduler.schedule(new FunctionCallScheduleTask(new FeatureDefinitionReference<FunctionDefinition<?>>(TestFeatureHolder.class, "TEST_FUNCTION")), delay);

        for (int update = 0; update < delay; update++) {
            scheduler.update();
        }

        Assert.assertTrue("Function call schedule task with delay of " + delay + " called function after " + (delay + 1) + " updates", executedTestFunction);
    }

    @Test
    public void testScheduleWithPersistence() throws JAXBException {

        scheduler.schedule(new FunctionCallScheduleTask(new FeatureDefinitionReference<FunctionDefinition<?>>(TestFeatureHolder.class, "TEST_FUNCTION")), delay);

        JAXBContext context = JAXBContext.newInstance(Scheduler.class, FunctionCallScheduleTask.class, TestFeatureHolder.class);
        StringWriter serialized = new StringWriter();
        context.createMarshaller().marshal(schedulerHolder, serialized);
        TestFeatureHolder schedulerHolderCopy = (TestFeatureHolder) context.createUnmarshaller().unmarshal(new StringReader(serialized.toString()));
        Scheduler copy = schedulerHolderCopy.get(TestFeatureHolder.SCHEDULER);

        for (int update = 0; update < delay; update++) {
            copy.update();
        }

        Assert.assertTrue("Function call schedule task with delay of " + delay + " called function after " + (delay + 1) + " updates", executedTestFunction);
    }

    @XmlRootElement
    private static class TestFeatureHolder extends DefaultFeatureHolder {

        public static final FeatureDefinition<Scheduler> SCHEDULER = Scheduler.createDefinition("scheduler");

        public static final FunctionDefinition<String>   TEST_FUNCTION;

        static {

            TEST_FUNCTION = create(new TypeLiteral<FunctionDefinition<String>>() {}, "name", "testFunction", "parameters", new Class[0]);
            TEST_FUNCTION.addExecutor("default", TestFeatureHolder.class, new FunctionExecutor<String>() {

                @Override
                public String invoke(FunctionInvocation<String> invocation, Object... arguments) {

                    executedTestFunction = true;
                    return invocation.next(arguments);
                }

            });

        }

    }

}
