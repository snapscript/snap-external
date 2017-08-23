package org.snapscript.bridge.standard;

import java.lang.reflect.Method;

import org.snapscript.bridge.InvocationRouter;
import org.snapscript.bridge.ThreadLocalHandler;
import org.snapscript.bridge.generate.BridgeInstance;
import org.snapscript.cglib.proxy.MethodInterceptor;
import org.snapscript.cglib.proxy.MethodProxy;

public class MethodInterceptorHandler extends ThreadLocalHandler implements MethodInterceptor  {
   
   public MethodInterceptorHandler(ThreadLocal<BridgeInstance> local, InvocationRouter handler) {
      super(local, handler);
   }

   @Override
   public Object intercept(Object object, Method method, Object[] list, MethodProxy proxy) throws Throwable {
      return invoke(object, method, list);
   }
}