package org.snapscript.extend.normal;

import java.lang.reflect.Method;

import org.snapscript.cglib.proxy.Callback;
import org.snapscript.cglib.proxy.Enhancer;
import org.snapscript.cglib.proxy.Factory;
import org.snapscript.core.Bug;
import org.snapscript.core.Context;
import org.snapscript.core.Module;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.define.Instance;
import org.snapscript.core.extend.Extension;
import org.snapscript.core.function.Invocation;
import org.snapscript.extend.AbstractExtender;

@Bug("Fix this")
public class NormalExtender extends AbstractExtender {

   public NormalExtender(FunctionResolver matcher, Type type) {
      super(matcher, type);
   }

   @Override
   protected Invocation getSuperCall(Scope scope, Class proxy, Method method) {
      return NormalProxyResolver.getSuperCall(proxy, method);
   }

   @Override
   protected Object getExtendedClass(Scope scope, Instance inst, Type tt, Object... args) {
      Class type = tt.getType();
      try {
         NormalHandler handler = getMethodHandler(scope, inst);
         Factory mock = (Factory) getInstance(scope, type, args);
         mock.setCallbacks(new Callback[] { handler });
         return mock;
      } catch (Exception e) {
         throw new IllegalStateException("Failed to mock " + type, e);
      }

   }

   @Override
   protected Class getProxyClass(Class type) {
      Class proxyClass = reference.get();
      if (proxyClass == null) {
         Enhancer enhancer = new Enhancer();
         enhancer.setSuperclass(type);
         enhancer.setInterfaces(new Class[] { Extension.class }); // ensure we can convert from object to Instance
         enhancer.setCallbackTypes(new Class[] { NormalHandler.class });
         proxyClass = enhancer.createClass();
         reference.set(proxyClass);
      }
      return proxyClass;
   }

   private NormalHandler getMethodHandler(Scope scope, Instance instance) {
      Module module = scope.getModule();
      Context context = module.getContext();

      return new NormalHandler(matcher, instance, scope, context);
   }

}
