package org.snapscript.extend.android;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.snapscript.core.Bug;
import org.snapscript.core.Context;
import org.snapscript.core.Module;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.define.Instance;
import org.snapscript.core.extend.Extension;
import org.snapscript.core.function.Invocation;
import org.snapscript.dx.stock.ProxyBuilder;
import org.snapscript.extend.AbstractExtender;

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
   protected Object getExtendedClass(Scope scope, Instance inst, Type tt, Object... args) {
      Class typeToMock = tt.getType();
      InvocationHandler handler = getHandler(scope, inst);
      try {
         Object mock = getInstance(scope, typeToMock, args);
         ProxyBuilder.setInvocationHandler(mock, handler);
         return mock;
      } catch (Exception e) {
         throw new IllegalStateException("Failed to mock " + typeToMock, e);
      }
   }

   @Override
   protected Class getProxyClass(Class typeToMock) throws Exception {
      Class proxyClass = reference.get();
      if (proxyClass == null) {
         proxyClass = ProxyBuilder.forClass(typeToMock).implementing(Extension.class).buildProxyClass();
         reference.set(proxyClass);
      }
      return proxyClass;
   }

   private InvocationHandler getHandler(Scope scope, Instance instance) {
      Module module = scope.getModule();
      Context context = module.getContext();

      return new AndroidMethodHandler(matcher, instance, scope, context);
   }
}
