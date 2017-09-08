package org.snapscript.platform.standard;

import org.snapscript.cglib.core.Signature;
import org.snapscript.cglib.proxy.MethodProxy;
import org.snapscript.common.Cache;
import org.snapscript.common.CopyOnWriteCache;
import org.snapscript.core.InternalStateException;
import org.snapscript.core.Scope;
import org.snapscript.core.function.Invocation;

public class MethodProxySuperInvocation implements Invocation {
   
   private final Cache<Class, MethodProxy> cache;
   private final Signature signature;
   
   public MethodProxySuperInvocation(Signature signature) {
      this.cache = new CopyOnWriteCache<Class, MethodProxy>();
      this.signature = signature;
   }

   @Override
   public Object invoke(Scope scope, Object value, Object... arguments) {
      try {
         Class type = value.getClass();
         MethodProxy proxy = cache.fetch(type);
         
         if(proxy == null) {
            proxy = MethodProxy.find(type, signature);
            cache.cache(type, proxy);
         }
         return proxy.invokeSuper(value, arguments);
      }catch(Throwable e) {
         throw new InternalStateException("Could not invoke super", e);
      }
   }
}