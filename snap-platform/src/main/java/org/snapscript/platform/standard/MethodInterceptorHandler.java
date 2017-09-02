package org.snapscript.platform.standard;

import java.lang.reflect.Method;

import org.snapscript.cglib.proxy.MethodInterceptor;
import org.snapscript.cglib.proxy.MethodProxy;
import org.snapscript.platform.InvocationRouter;
import org.snapscript.platform.ThreadLocalHandler;
import org.snapscript.platform.generate.BridgeInstance;

public class MethodInterceptorHandler extends ThreadLocalHandler implements MethodInterceptor  {
   
   public MethodInterceptorHandler(ThreadLocal<BridgeInstance> local, InvocationRouter handler) {
      super(local, handler);
   }

   @Override
   public Object intercept(Object object, Method method, Object[] list, MethodProxy proxy) throws Throwable {
      return invoke(object, method, list);
   }
}