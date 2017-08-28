package org.snapscript.bridge.android;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import org.snapscript.core.Scope;
import org.snapscript.core.function.Invocation;

public class ProxyBuilderWrapper {
   
   private final ProxyAdapterGenerator generator;
   private final Executor executor;

   public ProxyBuilderWrapper(ClassLoader loader, Executor executor) {
      this.generator = new ProxyAdapterGenerator(loader);
      this.executor = executor;
   }

   public Invocation superInvocation(Scope scope, Class proxy, Method method) {
      return new ProxyBuilderInvocation(method);
   }
   
   public Invocation thisInvocation(Scope scope, Method method) {
      return new ProxyMethodInvocation(generator, method, executor);
   }
   
   public Invocation thisInvocation(Scope scope, Constructor constructor) {
      return new ProxyConstructorInvocation(generator, constructor, executor);
   }
}