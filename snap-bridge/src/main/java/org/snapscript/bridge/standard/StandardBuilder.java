package org.snapscript.bridge.standard;

import java.lang.reflect.Method;

import org.snapscript.bridge.proxy.InstanceBuilder;
import org.snapscript.cglib.proxy.Callback;
import org.snapscript.cglib.proxy.Factory;
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

@Bug("Fix this")
public class StandardBuilder implements BridgeBuilder {
   
   private final Cache<Method, Invocation> invocations;
   private final EnhancerGenerator generator;
   private final FunctionResolver resolver;
   private final InstanceBuilder builder;
   private final Type type;

   public StandardBuilder(FunctionResolver resolver, Type type) {
      this.invocations = new CopyOnWriteCache<Method, Invocation>();
      this.generator = new EnhancerGenerator(Bridge.class);
      this.builder = new InstanceBuilder(generator, resolver, type);
      this.resolver = resolver;
      this.type = type;
   }

   @Override
   public Instance createInstance(Scope scope, Type real, Object... args) {
      try {
         Instance inst = builder.createInstance(scope, real, args);
         Factory mock = (Factory) inst.getBridge();
         MethodInterceptorHandler handler = getMethodHandler(scope, inst);
         mock.setCallbacks(new Callback[] { handler });
         return inst;
      } catch (Exception e) {
         throw new IllegalStateException("Failed to mock " + type, e);
      }
   }

   @Override
   public Invocation createInvocation(Scope scope, Class proxy, Method method) {
      Invocation invocation = invocations.fetch(method);

      if (invocation == null) {
         invocation = getSuperCall(scope, proxy, method);
         invocations.cache(method, invocation);
      }
      return invocation;
   }

   private Invocation getSuperCall(Scope scope, Class proxy, Method method) {
      return StandardProxyResolver.getSuperCall(proxy, method);
   }

   private MethodInterceptorHandler getMethodHandler(Scope scope, Instance instance) {
      return new MethodInterceptorHandler(resolver, this, instance, scope);
   }
}
