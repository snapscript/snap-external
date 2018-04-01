package org.snapscript.platform.standard;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.snapscript.core.type.Type;
import org.snapscript.core.convert.proxy.ProxyWrapper;
import org.snapscript.core.function.Invocation;
import org.snapscript.core.function.search.FunctionResolver;
import org.snapscript.core.platform.Platform;
import org.snapscript.platform.InvocationRouter;
import org.snapscript.platform.generate.BridgeConstructorBuilder;
import org.snapscript.platform.generate.BridgeInstance;

public class StandardPlatform implements Platform {
   
   private final MethodInvocationResolver resolver;
   private final MethodInterceptorHandler handler;
   private final BridgeConstructorBuilder builder;
   private final EnhancerGenerator generator;
   private final InvocationRouter router;
   private final ThreadLocal local;

   public StandardPlatform(FunctionResolver resolver, ProxyWrapper wrapper) {
      this.router = new InvocationRouter(this, resolver);
      this.local = new ThreadLocal<BridgeInstance>();
      this.handler = new MethodInterceptorHandler(local, router);
      this.generator = new EnhancerGenerator(handler);
      this.builder = new BridgeConstructorBuilder(generator, resolver, wrapper, local);
      this.resolver = new MethodInvocationResolver();
   }

   @Override
   public Invocation createSuperConstructor(Type real, Type base) {
      try {
         return builder.createSuperConstructor(real, base);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create super for '" + real + "'", e);
      } 
   }

   @Override
   public Invocation createSuperMethod(Type real, Method method) {
      try {
         return resolver.resolveSuperMethod(real, method);
      } catch (Exception e) {
         throw new IllegalStateException("Could not call super for '" + method + "'", e);
      }
   }

   @Override
   public Invocation createMethod(Type real, Method method) {
      try {
         return resolver.resolveMethod(method);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create invocation for '" + method + "'", e);
      }
   }
   
   @Override
   public Invocation createConstructor(Type real, Constructor constructor) {
      try {
         return resolver.resolveConstructor(constructor);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create invocation for '" + constructor + "'", e);
      }
   }
}