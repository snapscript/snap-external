package org.snapscript.bridge.proxy;

import org.snapscript.core.Scope;

public interface ClassGenerator {
   Class generate(Scope scope, Class type);
}
