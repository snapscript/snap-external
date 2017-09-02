package org.snapscript.platform.standard;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.snapscript.core.Type;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.function.Invocation;
import org.snapscript.core.platform.Platform;
import org.snapscript.platform.InvocationRouter;
import org.snapscript.platform.generate.BridgeConstructorBuilder;
import org.snapscript.platform.generate.BridgeInstance;

public class StandardPlatform implements Platform {
   
   private final MethodInterceptorHandler handler;
   private final EnhancerGenerator generator;
   private final BridgeConstructorBuilder builder;
   private final MethodProxyWrapper wrapper;
   private final InvocationRouter router;
   private final ThreadLocal local;

   public StandardPlatform(FunctionResolver resolver) {
      this.router = new InvocationRouter(this, resolver);
      this.local = new ThreadLocal<BridgeInstance>();
      this.handler = new MethodInterceptorHandler(local, router);
      this.generator = new EnhancerGenerator(handler);
      this.builder = new BridgeConstructorBuilder(generator, resolver, local);
      this.wrapper = new MethodProxyWrapper();
   }

   @Override
   public Invocation createSuperConstructor(Type real, Type base) {
      try {
         return builder.createInvocation(real, base);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create super for '" + real + "'", e);
      } 
   }

   @Override
   public Invocation createSuperMethod(Type real, Method method) {
      try {
         return wrapper.superInvocation(real, method);
      } catch (Exception e) {
         throw new IllegalStateException("Could not call super for '" + method + "'", e);
      }
   }

   @Override
   public Invocation createMethod(Type real, Method method) {
      try {
         return wrapper.thisInvocation(method);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create invocation for '" + method + "'", e);
      }
   }
   
   @Override
   public Invocation createConstructor(Type real, Constructor constructor) {
      try {
         return wrapper.thisInvocation(constructor);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create invocation for '" + constructor + "'", e);
      }
   }
}