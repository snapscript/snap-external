package org.snapscript.bridge.android;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.snapscript.bridge.BridgeInstance;
import org.snapscript.bridge.InstanceConverter;
import org.snapscript.bridge.proxy.ObjectBuilder;
import org.snapscript.common.Cache;
import org.snapscript.common.CopyOnWriteCache;
import org.snapscript.core.Bug;
import org.snapscript.core.Module;
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
   private final InstanceConverter converter;
   private final FunctionResolver resolver;
   private final ObjectBuilder builder;
   private final Type type;

   public AndroidBuilder(FunctionResolver resolver, Type type) {
      this.invocations = new CopyOnWriteCache<Method, Invocation>();
      this.generator = new ProxyBuilderGenerator(Bridge.class);
      this.builder = new ObjectBuilder(generator, resolver);
      this.converter = new InstanceConverter(type);
      this.resolver = resolver;
      this.type = type;
   }

   @Override
   public Instance createInstance(Scope scope, Type real, Object... args) {
      Instance inst = getExtendedClass(scope, real, type, args);
      converter.convert(inst);
      return inst;
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

   private Instance getExtendedClass(Scope scope, Type real, Type tt, Object... args) {
      Class typeToMock = tt.getType();
      try {
         Module module = scope.getModule();
         Object mock = builder.create(scope, typeToMock, args);
         Instance inst = new BridgeInstance(module, mock, type, real);
         InvocationHandler handler = getHandler(scope, inst);
         ProxyBuilder.setInvocationHandler(mock, handler);
         return inst;
      } catch (Exception e) {
         throw new IllegalStateException("Failed to mock " + typeToMock, e);
      }
   }

   private Invocation getSuperCall(Scope scope, Class proxy, Method method) {
      return new ProxyBuilderInvocation(method);
   }

   private InvocationHandler getHandler(Scope scope, Instance instance) {
      return new AndroidMethodHandler(resolver, this, instance, scope);
   }
}
