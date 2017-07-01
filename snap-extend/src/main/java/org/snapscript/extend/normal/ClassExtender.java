package org.snapscript.extend.normal;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

import org.snapscript.cglib.proxy.Callback;
import org.snapscript.cglib.proxy.Enhancer;
import org.snapscript.cglib.proxy.Factory;
import org.snapscript.common.Cache;
import org.snapscript.common.CopyOnWriteCache;
import org.snapscript.core.Bug;
import org.snapscript.core.Context;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.bind.ObjectFunctionMatcher;
import org.snapscript.core.define.Instance;
import org.snapscript.core.extend.Extension;
import org.snapscript.core.extend.TypeExtender;
import org.snapscript.core.function.Invocation;
import org.snapscript.extend.ConstructorData;
import org.snapscript.extend.ConstructorResolver;
import org.snapscript.extend.InstanceConverter;
import org.snapscript.extend.ProxyInstance;

@Bug("Fix this")
public class ClassExtender implements TypeExtender {
   
   private final Cache<Method, Invocation> invocations;
   private final AtomicReference<Class> reference;
   private final ObjectFunctionMatcher matcher;
   private final Type type;
   
   public ClassExtender(ObjectFunctionMatcher matcher, Type type) {
      this.invocations = new CopyOnWriteCache<Method, Invocation>();
      this.reference = new AtomicReference<Class>();
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
         invocation = MethodProxyResolver.getSuperCall(proxy, method);
         invocations.cache(method, invocation);
      }
      return invocation;
   }

   private Object getExtendedClass(final Scope scope, final Instance inst, Class type, Object... args) {
      try {
         MethodHandler handler = getMethodHandler(scope, inst);
         Class proxyClass = getProxyClass(type);
         final Context context = scope.getModule().getContext();
         final Type t = context.getLoader().loadType(type);
         ConstructorData data = ConstructorResolver.findConstructor(scope, t, args);
         Factory mock = (Factory)proxyClass.getDeclaredConstructor(data.getTypes()).newInstance(args);
         mock.setCallbacks(new Callback[]{handler});
         return mock;
      } catch (Exception e) {
         throw new IllegalStateException("Failed to mock " + type, e);
     }
      
   }
   
   private Class getProxyClass(Class type){
      Class proxyClass = reference.get();
      if(proxyClass == null) {
         Enhancer enhancer = new Enhancer();
         enhancer.setSuperclass(type);
         enhancer.setInterfaces(new Class[]{Extension.class}); // ensure we can convert from object to Instance
         enhancer.setCallbackTypes(new Class[] { MethodHandler.class });
         proxyClass = enhancer.createClass();
         reference.set(proxyClass);
      }
      return proxyClass;
   }
   
   private MethodHandler getMethodHandler(final Scope scope, final Instance inst) {
      final Context c = scope.getModule().getContext();
      return new MethodHandler(inst, scope, matcher, c);
   }




}
