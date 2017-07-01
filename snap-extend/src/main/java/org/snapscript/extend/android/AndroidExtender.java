package org.snapscript.extend.android;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

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
import org.snapscript.dx.stock.ProxyBuilder;
import org.snapscript.extend.ConstructorData;
import org.snapscript.extend.ConstructorResolver;
import org.snapscript.extend.InstanceConverter;
import org.snapscript.extend.ProxyInstance;

@Bug("Fix this")
public class AndroidExtender implements TypeExtender {
   
   private final Cache<Method, Invocation> invocations;
   private final AtomicReference<Class> reference;
   private final ObjectFunctionMatcher matcher;
   private final Type type;
   
   public AndroidExtender(ObjectFunctionMatcher matcher, Type type) {
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
         invocation = AndroidProxyResolver.getSuperCall(proxy, method);
         invocations.cache(method, invocation);
      }
      return invocation;
   }

   private Object getExtendedClass(final Scope scope, final Instance inst, Class typeToMock, Object... args) {
      // support concrete classes via dexmaker's ProxyBuilder
      InvocationHandler handler = getHandler(scope, inst, typeToMock);
      try {
         final Context c = scope.getModule().getContext();
         final Type t = c.getLoader().loadType(typeToMock);
          Class proxyClass = getProxyClass(typeToMock);
          //Object mock = unsafeAllocator.newInstance(proxyClass);
          ConstructorData data = ConstructorResolver.findConstructor(scope, t, args);
          Object mock = proxyClass.getDeclaredConstructor(data.getTypes()).newInstance(args);
          ProxyBuilder.setInvocationHandler(mock, handler);
          return mock;
      } catch (Exception e) {
          throw new IllegalStateException("Failed to mock " + typeToMock, e);
      }
   }
   
   private Class getProxyClass(Class typeToMock) throws Exception{
      Class proxyClass = reference.get();
      if(proxyClass == null) {
         proxyClass = ProxyBuilder.forClass(typeToMock)
                .implementing(Extension.class)
                .buildProxyClass();
         reference.set(proxyClass);
      }
      return proxyClass;
   }
   
   private InvocationHandler getHandler(final Scope scope, final Instance inst, Class typeToMock) {
      final Context c = scope.getModule().getContext();
      return new AndroidMethodHandler(inst, scope, matcher, c);
   }
}
