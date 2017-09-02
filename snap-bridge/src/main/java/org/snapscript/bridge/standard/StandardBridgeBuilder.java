package org.snapscript.bridge.standard;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import org.snapscript.bridge.InvocationRouter;
import org.snapscript.bridge.generate.BridgeInstance;
import org.snapscript.bridge.generate.BridgeInstanceBuilder;
import org.snapscript.core.Type;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.bridge.BridgeBuilder;
import org.snapscript.core.function.Invocation;

public class StandardBridgeBuilder implements BridgeBuilder {
   
   private final MethodInterceptorHandler handler;
   private final EnhancerGenerator generator;
   private final BridgeInstanceBuilder builder;
   private final MethodProxyWrapper wrapper;
   private final InvocationRouter router;
   private final ThreadLocal local;

   public StandardBridgeBuilder(FunctionResolver resolver, Executor executor) {
      this.router = new InvocationRouter(this, resolver);
      this.local = new ThreadLocal<BridgeInstance>();
      this.handler = new MethodInterceptorHandler(local, router);
      this.generator = new EnhancerGenerator(handler);
      this.builder = new BridgeInstanceBuilder(generator, resolver, local);
      this.wrapper = new MethodProxyWrapper();
   }

   @Override
   public Invocation superConstructor(Type real, Type base) {
      try {
         return builder.createInvocation(real, base);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create super for '" + real + "'", e);
      } 
   }

   @Override
   public Invocation superMethod(Type real, Method method) {
      try {
         return wrapper.superInvocation(real, method);
      } catch (Exception e) {
         throw new IllegalStateException("Could not call super for '" + method + "'", e);
      }
   }

   @Override
   public Invocation thisMethod(Type real, Method method) {
      try {
         return wrapper.thisInvocation(method);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create invocation for '" + method + "'", e);
      }
   }
   
   @Override
   public Invocation thisConstructor(Type real, Constructor constructor) {
      try {
         return wrapper.thisInvocation(constructor);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create invocation for '" + constructor + "'", e);
      }
   }
}