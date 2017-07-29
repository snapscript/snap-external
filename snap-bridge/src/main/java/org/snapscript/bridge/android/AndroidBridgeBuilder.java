package org.snapscript.bridge.android;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.snapscript.bridge.BridgeHandler;
import org.snapscript.bridge.proxy.BridgeInstanceBuilder;
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

public class AndroidBridgeBuilder implements BridgeBuilder {

   private final Cache<Method, Invocation> invocations;
   private final ProxyBuilderGenerator generator;
   private final AndroidAdapterBuilder support;
   private final BridgeInstanceBuilder builder;
   private final Type type;

   public AndroidBridgeBuilder(FunctionResolver resolver, Type type) {
      this.invocations = new CopyOnWriteCache<Method, Invocation>();
      this.generator = new ProxyBuilderGenerator(Bridge.class);
      this.builder = new BridgeInstanceBuilder(generator, resolver, type);
      this.support = new AndroidAdapterBuilder(this, resolver);
      this.type = type;
   }

   @Override
   public Instance superInstance(Scope scope, Type real, Object... list) {
      try {
         Instance instance = builder.createInstance(scope, real, list);
         InvocationHandler handler = support.createHandler(scope, instance);
         Object bridge = instance.getBridge();
         
         ProxyBuilder.setInvocationHandler(bridge, handler);

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
