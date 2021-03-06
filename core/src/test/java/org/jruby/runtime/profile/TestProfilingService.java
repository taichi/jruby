package org.jruby.runtime.profile;

import org.jruby.Ruby;
import org.jruby.internal.runtime.methods.DynamicMethod;
import org.jruby.runtime.ThreadContext;

/**
 * @author Andre Kullmann
 */
public class TestProfilingService implements ProfilingService {

    @Override
    public ProfileCollection newProfileCollection(ThreadContext context) {
        return null;
    }

    @Override
    public MethodEnhancer newMethodEnhancer(Ruby runtime) {
        return new MethodEnhancer() {
            @Override
            public DynamicMethod enhance(String name, DynamicMethod delegate) {
                return delegate;
            }
        };
    }

    @Override
    public ProfileReporter newProfileReporter(ThreadContext context) {
        return null;
    }
}
