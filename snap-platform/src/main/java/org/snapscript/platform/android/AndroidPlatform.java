package org.snapscript.platform.android;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.snapscript.core.Any;
import org.snapscript.core.ContextClassLoader;
import org.snapscript.core.Type;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.function.Invocation;
import org.snapscript.core.platform.Platform;
import org.snapscript.platform.InvocationRouter;
import org.snapscript.platform.ThreadLocalHandler;
import org.snapscript.platform.generate.BridgeConstructorBuilder;
import org.snapscript.platform.generate.BridgeInstance;

public class AndroidPlatform implements Platform {

   private final ProxyBuilderGenerator generator;   
   private final BridgeConstructorBuilder builder;
   private final ProxyBuilderWrapper wrapper;
   private final InvocationHandler handler;
   private final InvocationRouter router;
   private final ClassLoader loader;
   private final ThreadLocal local;

   public AndroidPlatform(FunctionResolver resolver) {
      this.loader = new ContextClassLoader(Any.class);
      this.router = new InvocationRouter(this, resolver);
      this.local = new ThreadLocal<BridgeInstance>();
      this.handler = new ThreadLocalHandler(local, router);
      this.generator = new ProxyBuilderGenerator(handler, loader);
      this.builder = new BridgeConstructorBuilder(generator, resolver, local);
      this.wrapper = new ProxyBuilderWrapper(loader);
   }

   @Override
   public Invocation createSuperConstructor(Type real, Type base) {
      try {
         return builder.createInvocation(real, base);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create super for '" + base + "'", e);
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
         throw new IllegalStateException("Could not create adapter for '" + method + "'", e);
      }
   }
   
   @Override
   public Invocation createConstructor(Type real, Constructor constructor) {
      try {
         return wrapper.thisInvocation(constructor);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create adapter for '" + constructor + "'", e);
      }
   }
}