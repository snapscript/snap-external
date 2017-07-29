package org.snapscript.bridge.standard;

import java.lang.reflect.Method;

import org.snapscript.bridge.BridgeHandler;
import org.snapscript.cglib.proxy.MethodInterceptor;
import org.snapscript.cglib.proxy.MethodProxy;
import org.snapscript.core.Scope;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.bridge.BridgeBuilder;
import org.snapscript.core.define.Instance;
import org.snapscript.core.function.Invocation;

public class StandardSupport {

   private final FunctionResolver resolver;
   private final BridgeBuilder builder;

   public StandardSupport(BridgeBuilder builder, FunctionResolver resolver) {
      this.resolver = resolver;
      this.builder = builder;
   }

   public Invocation getSuperCall(Scope scope, Class proxy, Method method) {
      return StandardProxyResolver.getSuperCall(proxy, method);
   }

   public MethodInterceptor getMethodHandler(Scope scope, Instance instance) {
      return new InvocationInterceptor(builder, resolver, instance, scope);
   }
   
   private static class InvocationInterceptor extends BridgeHandler implements MethodInterceptor  {
      
      public InvocationInterceptor(BridgeBuilder builder, FunctionResolver resolver, Instance instance, Scope scope) {
         super(builder, resolver, instance, scope);
      }

      @Override
      public Object intercept(Object object, Method method, Object[] list, MethodProxy proxy) throws Throwable {
         return invoke(object, method, list);
      }
   }
}
