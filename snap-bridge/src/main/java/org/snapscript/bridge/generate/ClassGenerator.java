package org.snapscript.bridge.generate;

import org.snapscript.core.Scope;
import org.snapscript.core.Type;

public interface ClassGenerator {
   Class generate(Scope scope, Type type, Class base);
}