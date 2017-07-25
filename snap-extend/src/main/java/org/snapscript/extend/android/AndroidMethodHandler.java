package org.snapscript.extend.android;

import java.lang.reflect.InvocationHandler;

import org.snapscript.core.Bug;
import org.snapscript.core.Scope;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.define.Instance;
import org.snapscript.core.generate.TypeExtender;
import org.snapscript.extend.AbstractHandler;

@Bug("this is a total mess")
public class AndroidMethodHandler extends AbstractHandler implements InvocationHandler  {

   public AndroidMethodHandler(FunctionResolver matcher, TypeExtender extender, Instance inst, Scope scope) {
      super(matcher, extender, inst, scope);
   }
}