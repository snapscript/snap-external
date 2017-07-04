package org.snapscript.extend.normal;

import java.lang.reflect.Method;

import org.snapscript.asm.Type;
import org.snapscript.cglib.core.Signature;
import org.snapscript.cglib.proxy.MethodProxy;
import org.snapscript.core.InternalStateException;
import org.snapscript.core.Result;
import org.snapscript.core.ResultType;
import org.snapscript.core.Scope;
import org.snapscript.core.function.Invocation;

public class NormalProxyResolver {

   public static Invocation getSuperCall(Class type, Method method) {
      String name = method.getName();
      Type returnType = Type.getType(method.getReturnType());
      Type[] argumentTypes = new Type[method.getParameterTypes().length];
      for (int i = 0; i < method.getParameterTypes().length; i++) {
         argumentTypes[i] = Type.getType(method.getParameterTypes()[i]);
      }
      Signature signature = new Signature(name, returnType, argumentTypes);
      MethodProxy proxy = MethodProxy.find(type, signature);
      return new SuperCallProxy(proxy);
   }
   

   // get the method proxy for the following call... eventually we will -> MethodCall --> SuperMethodCall & SuperMethodInvocation
   public static Invocation getSuperCall(Class type, String name, Class... types) {
      try {
         Method method = type.getDeclaredMethod(name, types);
         return getSuperCall(type, method);
      } catch (Exception e) {
         return null;
      }
   }
   
   private static class SuperCallProxy implements Invocation {
      
      private final MethodProxy proxy;
      
      public SuperCallProxy(MethodProxy proxy) {
         this.proxy = proxy;
      }

      @Override
      public Result invoke(Scope scope, Object value, Object... arguments) {
         try {
            Object result = proxy.invokeSuper(value, arguments);
            return ResultType.getNormal(result);
         }catch(Throwable e) {
            throw new InternalStateException("Could not invoke super", e);
         }
      }
   }
}
