package org.snapscript.extend.android;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.snapscript.core.Bug;
import org.snapscript.core.Context;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.define.Instance;
import org.snapscript.core.extend.Extension;
import org.snapscript.core.function.Invocation;
import org.snapscript.dx.stock.ProxyBuilder;
import org.snapscript.extend.AbstractExtender;
import org.snapscript.extend.ConstructorData;

@Bug("Fix this")
public class AndroidExtender extends AbstractExtender {
   
   public AndroidExtender(FunctionResolver matcher, Type type) {
      super(matcher, type);
   }
   
   @Override
   protected Invocation getSuperCall(Scope scope, Class proxy, Method method) {
      return AndroidProxyResolver.getSuperCall(method);
   }

   @Override
   protected Object getExtendedClass(final Scope scope, final Instance inst, Class typeToMock, Object... args) {
      // support concrete classes via dexmaker's ProxyBuilder
      InvocationHandler handler = getHandler(scope, inst, typeToMock);
      try {
         final Context c = scope.getModule().getContext();
         final Type t = c.getLoader().loadType(typeToMock);
          Class proxyClass = getProxyClass(typeToMock);
          //Object mock = unsafeAllocator.newInstance(proxyClass);
          ConstructorData data = resolver.findConstructor(scope, t, args);
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
