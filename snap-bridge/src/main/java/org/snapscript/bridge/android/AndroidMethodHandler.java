package org.snapscript.bridge.android;

import java.lang.reflect.InvocationHandler;

import org.snapscript.bridge.BridgeHandler;
import org.snapscript.core.Bug;
import org.snapscript.core.Scope;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.bridge.BridgeBuilder;
import org.snapscript.core.define.Instance;

@Bug("this is a total mess")
public class AndroidMethodHandler extends BridgeHandler implements InvocationHandler  {

   public AndroidMethodHandler(FunctionResolver matcher, BridgeBuilder extender, Instance inst, Scope scope) {
      super(matcher, extender, inst, scope);
   }
}