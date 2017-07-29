package org.snapscript.extend.standard;

import java.lang.reflect.Method;

import org.snapscript.cglib.proxy.MethodInterceptor;
import org.snapscript.cglib.proxy.MethodProxy;
import org.snapscript.core.Bug;
import org.snapscript.core.Scope;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.bridge.BridgeBuilder;
import org.snapscript.core.define.Instance;
import org.snapscript.extend.AbstractHandler;

@Bug("this is a total mess")
public class MethodInterceptorHandler extends AbstractHandler implements MethodInterceptor  {
   
   public MethodInterceptorHandler(FunctionResolver matcher, BridgeBuilder extender, Instance instance, Scope scope) {
      super(matcher, extender, instance, scope);
   }

   @Override
   public Object intercept(Object object, Method method, Object[] list, MethodProxy proxy) throws Throwable {
      return invoke(object, method, list);
   }
}