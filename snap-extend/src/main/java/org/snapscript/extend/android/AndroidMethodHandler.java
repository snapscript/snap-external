package org.snapscript.extend.android;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.snapscript.core.Bug;
import org.snapscript.core.Context;
import org.snapscript.core.Scope;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.define.Instance;
import org.snapscript.core.function.Invocation;
import org.snapscript.extend.AbstractHandler;

@Bug("this is a total mess")
public class AndroidMethodHandler extends AbstractHandler implements InvocationHandler  {

   public AndroidMethodHandler(Instance inst, Scope scope, FunctionResolver matcher, Context c) {
      super(inst, scope, matcher, c);
   }

   @Override
   protected Invocation getSuperCall(Class type, Method method) {
      return AndroidProxyResolver.getSuperCall(method);
   }
}