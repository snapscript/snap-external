package org.snapscript.extend.android;

import java.lang.reflect.Method;

import org.snapscript.core.InternalStateException;
import org.snapscript.core.Result;
import org.snapscript.core.ResultType;
import org.snapscript.core.Scope;
import org.snapscript.core.function.Invocation;
import org.snapscript.dx.stock.ProxyBuilder;

public class AndroidProxyResolver {

   public static Invocation getSuperCall(Class type, Method method) {
      return new SuperCallProxy(method);
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
      
      private final Method method;
      
      public SuperCallProxy(Method method) {
         this.method = method;
      }

      @Override
      public Result invoke(Scope scope, Object value, Object... arguments) {
         try {
            Object result = ProxyBuilder.callSuper(value, method, arguments);
            return ResultType.getNormal(result);
         }catch(Throwable e) {
            throw new InternalStateException("Could not invoke super", e);
         }
      }
   }
}