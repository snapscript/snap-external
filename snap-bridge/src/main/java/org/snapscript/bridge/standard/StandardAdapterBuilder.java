package org.snapscript.bridge.standard;

import java.lang.reflect.Method;

import org.snapscript.asm.Type;
import org.snapscript.bridge.BridgeHandler;
import org.snapscript.cglib.core.Signature;
import org.snapscript.cglib.proxy.MethodInterceptor;
import org.snapscript.cglib.proxy.MethodProxy;
import org.snapscript.core.Context;
import org.snapscript.core.Module;
import org.snapscript.core.Scope;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.bridge.BridgeBuilder;
import org.snapscript.core.define.Instance;
import org.snapscript.core.function.Invocation;

public class StandardAdapterBuilder {

   private final FunctionResolver resolver;
   private final BridgeBuilder builder;
   private final Type[] empty;

   public StandardAdapterBuilder(BridgeBuilder builder, FunctionResolver resolver) {
      this.empty = new Type[] {};
      this.resolver = resolver;
      this.builder = builder;
   }

   public Invocation createInvocation(Scope scope, Class type, Method method) {
      String name = method.getName();
      Class returns = method.getReturnType();
      Class[] parameters = method.getParameterTypes();
      Signature signature = createSignature(name, returns, parameters);
      MethodProxy proxy = MethodProxy.find(type, signature);
      
      return new MethodProxyInvocation(proxy);
   }

   private Signature createSignature(String name, Class returns, Class[] parameters) {
      Type type = Type.getType(returns);
      
      if(parameters.length > 0) {
         Type[] types = new Type[parameters.length];
         
         for (int i = 0; i < parameters.length; i++) {
            types[i] = Type.getType(parameters[i]);
         }
         return new Signature(name, type, types);
      }
      return new Signature(name, type, empty);
   }

   public MethodInterceptor createInterceptor(Scope scope, Instance instance) {
      Module module = scope.getModule();
      Context context = module.getContext();
      
      return new InvocationInterceptor(builder, resolver, context, instance);
   }
   
   private static class InvocationInterceptor extends BridgeHandler implements MethodInterceptor  {
      
      public InvocationInterceptor(BridgeBuilder builder, FunctionResolver resolver, Context context, Instance instance) {
         super(builder, resolver, context, instance);
      }

      @Override
      public Object intercept(Object object, Method method, Object[] list, MethodProxy proxy) throws Throwable {
         return invoke(object, method, list);
      }
   }
}