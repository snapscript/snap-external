package org.snapscript.platform.android;

import java.lang.reflect.Constructor;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.snapscript.core.InternalStateException;
import org.snapscript.core.Scope;
import org.snapscript.core.function.Invocation;
import org.snapscript.dx.stock.ProxyAdapter;

public class ProxyConstructorInvocation implements Invocation {
   
   private volatile ConstructorExchanger exchanger;
   private volatile ProxyAdapter reference;
   private volatile Constructor constructor;
   private volatile Executor executor;
   
   public ProxyConstructorInvocation(ProxyAdapterBuilder generator, Constructor constructor, Executor executor) {
      this.exchanger = new ConstructorExchanger(generator, constructor);
      this.reference = new ConstructorAdapter(constructor);
      this.constructor = constructor;
      this.executor = executor;
   }

   @Override
   public Object invoke(Scope scope, Object value, Object... arguments) {
      try {
         return reference.invoke(value, arguments);
      }catch(Throwable e) {
         throw new InternalStateException("Could not invoke " + constructor, e);
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
            ProxyAdapter adapter = generator.generate(constructor);
         
            if(adapter != null) {
               reference = adapter;
            }
         }
      }
   }
}