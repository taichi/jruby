/*
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 1.0
 * GNU General Public License version 2
 * GNU Lesser General Public License version 2.1
 */
package org.jruby.truffle.nodes.yield;

import com.oracle.truffle.api.*;
import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;
import org.jruby.truffle.nodes.*;
import org.jruby.truffle.runtime.*;
import org.jruby.truffle.runtime.control.RaiseException;
import org.jruby.truffle.runtime.core.*;
import org.jruby.truffle.runtime.core.RubyArray;

/**
 * Yield to the current block.
 */
public class YieldNode extends RubyNode {

    @Children protected final RubyNode[] arguments;
    @Child protected YieldDispatchHeadNode dispatch;
    private final boolean unsplat;

    public YieldNode(RubyContext context, SourceSection sourceSection, RubyNode[] arguments, boolean unsplat) {
        super(context, sourceSection);
        this.arguments = arguments;
        dispatch = new YieldDispatchHeadNode(getContext());
        this.unsplat = unsplat;
    }

    @ExplodeLoop
    @Override
    public final Object execute(VirtualFrame frame) {
        Object[] argumentsObjects = new Object[arguments.length];

        for (int i = 0; i < arguments.length; i++) {
            argumentsObjects[i] = arguments[i].execute(frame);
        }

        final RubyProc block = RubyArguments.getBlock(frame.getArguments());

        if (block == null) {
            CompilerDirectives.transferToInterpreter();
            throw new RaiseException(getContext().getCoreLibrary().noBlockToYieldTo());
        }

        if (unsplat) {
            notDesignedForCompilation();

            // TOOD(CS): what is the error behaviour here?
            assert argumentsObjects.length == 1;
            assert argumentsObjects[0] instanceof RubyArray;
            argumentsObjects = ((RubyArray) argumentsObjects[0]).slowToArray();
        }

        return dispatch.dispatch(frame, block, argumentsObjects);
    }

    @Override
    public Object isDefined(VirtualFrame frame) {
        notDesignedForCompilation();

        if (RubyArguments.getBlock(frame.getArguments()) == null) {
            return NilPlaceholder.INSTANCE;
        } else {
            return getContext().makeString("yield");
        }
    }

}
