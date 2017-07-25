package org.snapscript.extend.android;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.snapscript.common.Cache;
import org.snapscript.common.CopyOnWriteCache;
import org.snapscript.core.Bug;
import org.snapscript.core.Context;
import org.snapscript.core.Module;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.define.Instance;
import org.snapscript.core.function.Invocation;
import org.snapscript.core.generate.Extension;
import org.snapscript.core.generate.TypeExtender;
import org.snapscript.dx.stock.ProxyBuilder;
import org.snapscript.extend.InstanceConverter;
import org.snapscript.extend.ProxyInstance;
import org.snapscript.extend.proxy.ObjectBuilder;

@Bug("Fix this")
public class AndroidExtender implements TypeExtender {
   
   private final Cache<Method, Invocation> invocations;
   private final ProxyBuilderGenerator generator;
   private final FunctionResolver resolver;
   private final ObjectBuilder builder;
   private final Type type;

   public AndroidExtender(FunctionResolver resolver, Type type) {
      this.invocations = new CopyOnWriteCache<Method, Invocation>();
      this.generator = new ProxyBuilderGenerator(Extension.class);
      this.builder = new ObjectBuilder(generator, resolver);
      this.resolver = resolver;
      this.type = type;
   }

   @Override
   public Instance createInstance(Scope scope, Type real, Object... args) {
      Module module = scope.getModule();
      Instance inst = new ProxyInstance(module, scope, type, real);
      Object obj = getExtendedClass(scope, inst, type, args);
      InstanceConverter.convert(inst, obj, type);
      return inst;
   }

   @Override
   public Invocation createSuper(Scope scope, Class proxy, Method method) {
      Invocation invocation = invocations.fetch(method);

      if (invocation == null) {
         invocation = getSuperCall(scope, proxy, method);
         invocations.cache(method, invocation);
      }
      return invocation;
   }

   private Object getExtendedClass(Scope scope, Instance inst, Type tt, Object... args) {
      Class typeToMock = tt.getType();
      InvocationHandler handler = getHandler(scope, inst);
      try {
         Object mock = builder.create(inst, typeToMock, args);
         ProxyBuilder.setInvocationHandler(mock, handler);
         return mock;
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
