package org.snapscript.platform.shell;

import org.snapscript.core.Type;

public interface ShellBuilder {
   Object create(Type type, Class real);
}