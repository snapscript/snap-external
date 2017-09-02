package org.snapscript.platform.android;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.snapscript.core.Bug;
import org.snapscript.core.InternalStateException;
import org.snapscript.core.Result;
import org.snapscript.core.ResultType;
import org.snapscript.core.Scope;
import org.snapscript.core.function.Invocation;
import org.snapscript.dx.stock.ProxyAdapter;

public class ProxyMethodInvocation implements Invocation {
   
   private volatile MethodExchanger exchanger;
   private volatile ProxyAdapter reference;
   private volatile Executor executor;
   
   public ProxyMethodInvocation(ProxyAdapterGenerator generator, Method method, Executor executor) {
      this.exchanger = new MethodExchanger(generator, method);
      this.reference = new MethodAdapter(method);
      this.executor = executor;
   }

   @Override
   public Result invoke(Scope scope, Object value, Object... arguments) {
      try {
         Object result = reference.invoke(value, arguments);
         return ResultType.getNormal(result);
      }catch(Throwable e) {
         throw new InternalStateException("Could not invoke super", e);
      }
   }
   
   private class MethodAdapter implements ProxyAdapter {

      private final AtomicLong counter;
      private final Method method;
      
      public MethodAdapter(Method method) {
         this(method, 10);
      }
      
      public MethodAdapter(Method method, int threshold) {
         this.counter = new AtomicLong(threshold);
         this.method = method;
      }
      
      @Override
      public Object invoke(Object object, Object... list) throws Exception {
         long count = counter.getAndDecrement();
         
         if(count == 0) {
            executor.execute(exchanger); // generate proxy adapter
         }
         return method.invoke(object, list);
      }   
   }
   
   private class MethodExchanger implements Runnable {
      
      private final ProxyAdapterGenerator generator;
      private final ProxyClassFilter filter;
      private final Method method;
      
      public MethodExchanger(ProxyAdapterGenerator generator, Method method) {
         this.filter = new ProxyClassFilter();
         this.generator = generator;
         this.method = method;
      }

      @Override
      public void run() {
         if(filter.accept(method)) { // in a private dex class loader
            reference = generator.generate(method);
         }
      }
   }
}