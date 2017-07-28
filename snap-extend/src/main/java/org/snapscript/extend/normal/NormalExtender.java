package org.snapscript.extend.normal;

import java.lang.reflect.Method;

import org.snapscript.cglib.proxy.Callback;
import org.snapscript.cglib.proxy.Factory;
import org.snapscript.common.Cache;
import org.snapscript.common.CopyOnWriteCache;
import org.snapscript.core.Bug;
import org.snapscript.core.Module;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.define.Instance;
import org.snapscript.core.function.Invocation;
import org.snapscript.core.generate.Extension;
import org.snapscript.core.generate.TypeExtender;
import org.snapscript.extend.InstanceConverter;
import org.snapscript.extend.ProxyInstance;
import org.snapscript.extend.proxy.ObjectBuilder;

@Bug("Fix this")
public class NormalExtender implements TypeExtender {
   
   private final Cache<Method, Invocation> invocations;
   private final EnhancerGenerator generator;
   private final FunctionResolver resolver;
   private final ObjectBuilder builder;
   private final Type type;

   public NormalExtender(FunctionResolver resolver, Type type) {
      this.invocations = new CopyOnWriteCache<Method, Invocation>();
      this.generator = new EnhancerGenerator(Extension.class);
      this.builder = new ObjectBuilder(generator, resolver);
      this.resolver = resolver;
      this.type = type;
   }

   @Override
   public Instance createInstance(Scope scope, Type real, Object... args) {
      Instance inst = getExtendedClass(scope, real, type, args);
      Object obj = inst.getObject();
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

   private Invocation getSuperCall(Scope scope, Class proxy, Method method) {
      return NormalProxyResolver.getSuperCall(proxy, method);
   }

   private Instance getExtendedClass(Scope scope, Type real, Type tt, Object... args) {
      Class typeToMock = tt.getType();
      try {
         Module module = scope.getModule();
         Factory mock = (Factory) builder.create(scope, typeToMock, args);
         Instance inst = new ProxyInstance(module, mock, type, real);
         MethodInterceptorHandler handler = getMethodHandler(scope, inst);
         mock.setCallbacks(new Callback[] { handler });
         return inst;
      } catch (Exception e) {
         throw new IllegalStateException("Failed to mock " + type, e);
      }

   }

   private MethodInterceptorHandler getMethodHandler(Scope scope, Instance instance) {
      return new MethodInterceptorHandler(resolver, this, instance, scope);
   }

}
