package org.snapscript.extend;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

import org.snapscript.common.Cache;
import org.snapscript.common.CopyOnWriteCache;
import org.snapscript.core.Context;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.define.Instance;
import org.snapscript.core.extend.TypeExtender;
import org.snapscript.core.function.Invocation;
import org.snapscript.extend.normal.NormalProxyResolver;

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
      Class clazz = type.getType();
      Context c = scope.getModule().getContext();
      final Type t = c.getLoader().loadType(clazz);
      Instance inst = new ProxyInstance(scope, t, real);
      Object obj = getExtendedClass(scope, inst, clazz, args);
      InstanceConverter.convert(t, inst, obj);
      return inst;
   }
   

   @Override
   public Invocation createSuper(Scope scope, Class proxy, Method method) {
      Invocation invocation = invocations.fetch(method);
      if(invocation == null){
         invocation = NormalProxyResolver.getSuperCall(proxy, method);
         invocations.cache(method, invocation);
      }
      return invocation;
   }
   
   
   protected abstract Object getExtendedClass(Scope scope, Instance inst, Class type, Object... args);
   protected abstract Invocation getSuperCall(Scope scope, Class proxy, Method method);
}
