package org.snapscript.platform.generate;

import org.snapscript.core.type.Type;

public interface ClassGenerator {
   Class generate(Type type, Class base);
}