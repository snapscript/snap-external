package org.snapscript.bridge.standard;

import java.lang.reflect.Method;

import org.snapscript.bridge.generate.BridgeInstanceBuilder;
import org.snapscript.cglib.proxy.Callback;
import org.snapscript.cglib.proxy.Factory;
import org.snapscript.cglib.proxy.MethodInterceptor;
import org.snapscript.common.Cache;
import org.snapscript.common.CopyOnWriteCache;
import org.snapscript.core.Bug;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.bridge.Bridge;
import org.snapscript.core.bridge.BridgeBuilder;
import org.snapscript.core.define.Instance;
import org.snapscript.core.function.Invocation;

public class StandardBridgeBuilder implements BridgeBuilder {
   
   private final Cache<Method, Invocation> invocations;
   private final EnhancerGenerator generator;
   private final BridgeInstanceBuilder builder;
   private final StandardAdapterBuilder support;
   private final Type type;

   public StandardBridgeBuilder(FunctionResolver resolver, Type type) {
      this.invocations = new CopyOnWriteCache<Method, Invocation>();
      this.generator = new EnhancerGenerator(Bridge.class);
      this.builder = new BridgeInstanceBuilder(generator, resolver, type);
      this.support = new StandardAdapterBuilder(this, resolver);
      this.type = type;
   }

   @Override
   public Instance superInstance(Scope scope, Type real, Object... list) {
      try {
         Instance instance = builder.createInstance(scope, real, list);
         MethodInterceptor handler = support.createInterceptor(scope, instance);
         Factory factory = (Factory) instance.getBridge();
         
         factory.setCallbacks(new Callback[] { handler });
         
         return instance;
      } catch (Exception e) {
         throw new IllegalStateException("Could not create super for '" + type + "'", e);
      }
   }

   @Override
   public Invocation superInvocation(Scope scope, Class proxy, Method method) {
      try {
         Invocation invocation = invocations.fetch(method);
   
         if (invocation == null) {
            invocation = support.createInvocation(scope, proxy, method);
            invocations.cache(method, invocation);
         }
         return invocation;
      } catch (Exception e) {
         throw new IllegalStateException("Could not call super for '" + method + "'", e);
      }
   }
}
