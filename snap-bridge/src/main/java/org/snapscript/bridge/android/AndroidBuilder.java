package org.snapscript.bridge.android;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.snapscript.bridge.proxy.InstanceBuilder;
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
import org.snapscript.dx.stock.ProxyBuilder;

@Bug("Fix this")
public class AndroidBuilder implements BridgeBuilder {

   private final Cache<Method, Invocation> invocations;
   private final ProxyBuilderGenerator generator;
   private final FunctionResolver resolver;
   private final InstanceBuilder builder;
   private final Type type;

   public AndroidBuilder(FunctionResolver resolver, Type type) {
      this.invocations = new CopyOnWriteCache<Method, Invocation>();
      this.generator = new ProxyBuilderGenerator(Bridge.class);
      this.builder = new InstanceBuilder(generator, resolver, type);
      this.resolver = resolver;
      this.type = type;
   }

   @Override
   public Instance createInstance(Scope scope, Type real, Object... args) {
      try {
         Instance inst = builder.createInstance(scope, real, args);
         Object mock = inst.getBridge();
         InvocationHandler handler = getHandler(scope, inst);
         ProxyBuilder.setInvocationHandler(mock, handler);

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
      return new ProxyBuilderInvocation(method);
   }

   private InvocationHandler getHandler(Scope scope, Instance instance) {
      return new AndroidMethodHandler(resolver, this, instance, scope);
   }
}
