package org.snapscript.platform.android;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import org.snapscript.common.thread.ThreadPool;
import org.snapscript.core.function.Invocation;

public class ProxyMethodBuilder {
   
   private final ProxyAdapterGenerator generator;
   private final Executor executor;

   public ProxyMethodBuilder(ClassLoader loader) {
      this.generator = new ProxyAdapterGenerator(loader);
      this.executor = new ThreadPool(1);
   }
   
   public Invocation superInvocation(Method method) {
      return new ProxyBuilderInvocation(method);
   }
   
   public Invocation thisInvocation(Method method) {
      return new ProxyMethodInvocation(generator, method, executor);
   }
   
   public Invocation thisInvocation(Constructor constructor) {
      return new ProxyConstructorInvocation(generator, constructor, executor);
   }
}
