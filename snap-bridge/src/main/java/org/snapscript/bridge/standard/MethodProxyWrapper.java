package org.snapscript.bridge.standard;

import java.lang.reflect.Method;

import org.snapscript.asm.Type;
import org.snapscript.cglib.core.Signature;
import org.snapscript.cglib.proxy.MethodProxy;
import org.snapscript.core.Scope;
import org.snapscript.core.function.Invocation;

public class MethodProxyWrapper {

   private final Type[] empty;

   public MethodProxyWrapper() {
      this.empty = new Type[] {};
   }

   public Invocation superInvocation(Scope scope, Class type, Method method) {
      String name = method.getName();
      Class returns = method.getReturnType();
      Class[] parameters = method.getParameterTypes();
      Signature signature = createSignature(name, returns, parameters);
      MethodProxy proxy = MethodProxy.find(type, signature);
      
      return new MethodProxyInvocation(proxy);
   }
   
   public Invocation thisInvocation(Scope scope, Method method) {
      return new MethodAdapterInvocation(method);
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
}