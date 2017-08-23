package org.snapscript.bridge.android;

import java.lang.reflect.Method;

import org.snapscript.core.Scope;
import org.snapscript.core.function.Invocation;

public class ProxyBuilderWrapper {

   public ProxyBuilderWrapper() {
      super();
   }

   public Invocation createInvocation(Scope scope, Class proxy, Method method) {
      return new ProxyBuilderInvocation(method);
   }
}