package org.snapscript.platform.generate;

import org.snapscript.core.Type;

public interface ClassGenerator {
   Class generate(Type type, Class base);
}