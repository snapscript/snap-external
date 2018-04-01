package org.snapscript.platform.android;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.snapscript.core.Type;
import org.snapscript.core.convert.ProxyWrapper;
import org.snapscript.core.function.Invocation;
import org.snapscript.core.function.search.FunctionResolver;
import org.snapscript.core.platform.Platform;
import org.snapscript.platform.InvocationRouter;
import org.snapscript.platform.ThreadLocalHandler;
import org.snapscript.platform.generate.BridgeConstructorBuilder;
import org.snapscript.platform.generate.BridgeInstance;

public class AndroidPlatform implements Platform {

   private final ProxyClassGenerator generator;   
   private final BridgeConstructorBuilder builder;
   private final ProxyInvocationResolver resolver;
   private final InvocationHandler handler;
   private final InvocationRouter router;
   private final ProxyClassLoader loader;
   private final ThreadLocal local;

   public AndroidPlatform(FunctionResolver resolver, ProxyWrapper wrapper) {
      this.router = new InvocationRouter(this, resolver);
      this.local = new ThreadLocal<BridgeInstance>();
      this.handler = new ThreadLocalHandler(local, router);
      this.loader = new ProxyClassLoader(handler);
      this.generator = new ProxyClassGenerator(loader);
      this.builder = new BridgeConstructorBuilder(generator, resolver, wrapper, local);
      this.resolver = new ProxyInvocationResolver(loader);
   }

   @Override
   public Invocation createSuperConstructor(Type real, Type base) {
      try {
         return builder.createSuperConstructor(real, base);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create super for '" + base + "'", e);
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
         throw new IllegalStateException("Could not create adapter for '" + method + "'", e);
      }
   }
   
   @Override
   public Invocation createConstructor(Type real, Constructor constructor) {
      try {
         return resolver.resolveConstructor(constructor);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create adapter for '" + constructor + "'", e);
      }
   }
}