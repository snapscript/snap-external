package org.snapscript.platform.android;

import java.lang.reflect.Constructor;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.snapscript.core.InternalStateException;
import org.snapscript.core.Result;
import org.snapscript.core.ResultType;
import org.snapscript.core.Scope;
import org.snapscript.core.function.Invocation;
import org.snapscript.dx.stock.ProxyAdapter;

public class ProxyConstructorInvocation implements Invocation {
   
   private volatile ConstructorExchanger exchanger;
   private volatile ProxyAdapter reference;
   private volatile Executor executor;
   
   public ProxyConstructorInvocation(ProxyAdapterBuilder generator, Constructor constructor, Executor executor) {
      this.exchanger = new ConstructorExchanger(generator, constructor);
      this.reference = new ConstructorAdapter(constructor);
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
   
   private class ConstructorAdapter implements ProxyAdapter {

      private final Constructor constructor;
      private final AtomicLong counter;
      
      public ConstructorAdapter(Constructor constructor) {
         this(constructor, 10);
      }
      
      public ConstructorAdapter(Constructor constructor, int threshold) {
         this.counter = new AtomicLong(threshold);
         this.constructor = constructor;
      }
      
      @Override
      public Object invoke(Object object, Object... list) throws Exception {
         long count = counter.getAndDecrement();
         
         if(count == 0) {
            executor.execute(exchanger); // generate proxy adapter
         }
         return constructor.newInstance(list);
      }   
   }
   
   private class ConstructorExchanger implements Runnable {
      
      private final ProxyAdapterBuilder generator;
      private final InternalClassFilter filter;
      private final Constructor constructor;
      
      public ConstructorExchanger(ProxyAdapterBuilder generator, Constructor constructor) {
         this.filter = new InternalClassFilter();
         this.constructor = constructor;
         this.generator = generator;
      }

      @Override
      public void run() {
         if(filter.accept(constructor)) { // in a private dex class loader
            reference = generator.generate(constructor);
         }
      }
   }
}