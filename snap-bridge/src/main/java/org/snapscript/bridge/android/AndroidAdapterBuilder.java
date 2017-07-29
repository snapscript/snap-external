package org.snapscript.bridge.android;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.snapscript.bridge.BridgeHandler;
import org.snapscript.core.Scope;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.bridge.BridgeBuilder;
import org.snapscript.core.define.Instance;
import org.snapscript.core.function.Invocation;

public class AndroidAdapterBuilder {

   private final FunctionResolver resolver;
   private final BridgeBuilder builder;

   public AndroidAdapterBuilder(BridgeBuilder builder, FunctionResolver resolver) {
      this.resolver = resolver;
      this.builder = builder;
   }

   public Invocation createInvocation(Scope scope, Class proxy, Method method) {
      return new ProxyBuilderInvocation(method);
   }

   public InvocationHandler createHandler(Scope scope, Instance instance) {
      return new InvocationInterceptor(builder, resolver, instance);
   }
   
   private static class InvocationInterceptor extends BridgeHandler implements InvocationHandler  {

      public InvocationInterceptor(BridgeBuilder builder, FunctionResolver resolver, Instance instance) {
         super(builder, resolver, instance);
      }
   }
}
