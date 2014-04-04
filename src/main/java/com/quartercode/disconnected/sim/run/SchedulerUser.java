
package com.quartercode.disconnected.sim.run;

import com.quartercode.classmod.base.FeatureDefinition;
import com.quartercode.classmod.base.FeatureHolder;
import com.quartercode.classmod.extra.ExecutorInvocationException;
import com.quartercode.classmod.extra.Function;
import com.quartercode.classmod.extra.FunctionDefinition;
import com.quartercode.classmod.extra.FunctionExecutor;
import com.quartercode.classmod.extra.FunctionInvocation;

/**
 * {@link FeatureHolder}s which implement this interface inherit a {@link Scheduler} that is automatically invoked by the tick simulator.
 * Actually, this class uses a {@link #TICK_UPDATE} function from {@link TickUpdatable} that delegates the call to the {@link Scheduler}.
 * 
 * @see Scheduler
 * @see #SCHEDULER
 * @see TickUpdatable
 */
public interface SchedulerUser extends FeatureHolder, TickUpdatable {

    /**
     * The {@link Scheduler} that can be used to execute schedule tasks later.
     * Scheduler users should use the scheduler instead of manually timing things with a {@link TickUpdatable}.
     */
    public static final FeatureDefinition<Scheduler> SCHEDULER   = Scheduler.createDefinition("scheduler");

    /**
     * The tick update {@link Function} is automatically invoked by the tick simulator on every tick.
     * In this case, it just calls the {@link Scheduler#update()} method on the defined {@link #SCHEDULER} feature.
     * The priority of the delegation executor is the default one.
     */
    public static final FunctionDefinition<Void>     TICK_UPDATE = Initializer.tickUpdateAddScheduleCall(TickUpdatable.TICK_UPDATE);

    static class Initializer {

        private static FunctionDefinition<Void> tickUpdateAddScheduleCall(FunctionDefinition<Void> definition) {

            definition.addExecutor("updateScheduler", SchedulerUser.class, new FunctionExecutor<Void>() {

                @Override
                public Void invoke(FunctionInvocation<Void> invocation, Object... arguments) throws ExecutorInvocationException {

                    invocation.getHolder().get(SchedulerUser.SCHEDULER).update();
                    return invocation.next(arguments);
                }
            });

            return definition;
        }

    }

}
