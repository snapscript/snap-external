package org.snapscript.extend;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

import org.snapscript.common.Cache;
import org.snapscript.common.CopyOnWriteCache;
import org.snapscript.core.Context;
import org.snapscript.core.Module;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.TypeLoader;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.define.Instance;
import org.snapscript.core.extend.TypeExtender;
import org.snapscript.core.function.Invocation;

public abstract class AbstractExtender implements TypeExtender {

   protected final Cache<Method, Invocation> invocations;
   protected final AtomicReference<Class> reference;
   protected final ConstructorResolver resolver;
   protected final FunctionResolver matcher;
   protected final Type type;

   protected AbstractExtender(FunctionResolver matcher, Type type) {
      this.invocations = new CopyOnWriteCache<Method, Invocation>();
      this.reference = new AtomicReference<Class>();
      this.resolver = new ConstructorResolver(matcher);
      this.matcher = matcher;
      this.type = type;
   }

   @Override
   public Instance createInstance(Scope scope, Type real, Object... args) {
      Instance inst = new ProxyInstance(scope, type, real);
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

   protected Object getInstance(Scope scope, Class typeToMock, Object... args) throws Exception {
      Class proxyClass = getProxyClass(typeToMock);
      Module module = scope.getModule();
      Context context = module.getContext();
      TypeLoader loader = context.getLoader();
      Type type = loader.loadType(typeToMock);
      ConstructorData data = resolver.findConstructor(scope, type, args);
      Class[] types = data.getTypes();
      
      return proxyClass.getDeclaredConstructor(types).newInstance(args);
   }

   protected abstract Class getProxyClass(Class type) throws Exception;
   protected abstract Object getExtendedClass(Scope scope, Instance inst, Type type, Object... args);
   protected abstract Invocation getSuperCall(Scope scope, Class proxy, Method method);
}
