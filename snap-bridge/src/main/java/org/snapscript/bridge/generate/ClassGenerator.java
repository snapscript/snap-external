package org.snapscript.bridge.generate;

import org.snapscript.core.Scope;

public interface ClassGenerator {
   Class generate(Scope scope, Class type);
}