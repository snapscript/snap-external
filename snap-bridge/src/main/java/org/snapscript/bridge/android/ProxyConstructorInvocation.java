package org.snapscript.bridge.android;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.snapscript.core.Bug;
import org.snapscript.core.InternalStateException;
import org.snapscript.core.Result;
import org.snapscript.core.ResultType;
import org.snapscript.core.Scope;
import org.snapscript.core.function.Invocation;
import org.snapscript.dx.stock.ProxyAdapter;

public class ProxyConstructorInvocation implements Invocation {
   
   private volatile ProxyExchanger exchanger;
   private volatile ProxyAdapter reference;
   private volatile Executor executor;
   
   public ProxyConstructorInvocation(ProxyAdapterGenerator generator, Constructor constructor, Executor executor) {
      this.exchanger = new ProxyExchanger(generator, constructor);
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
   
   @Bug("clean up debug")
   private class ProxyExchanger implements Runnable {
      
      private final ProxyAdapterGenerator generator;
      private final Constructor constructor;
      
      public ProxyExchanger(ProxyAdapterGenerator generator, Constructor constructor) {
         this.constructor = constructor;
         this.generator = generator;
      }

      @Override
      public void run() {
         System.out.println("############################# generating: "+constructor);
         try {
            int modifiers = constructor.getModifiers();
            
            if(Modifier.isPublic(modifiers)) {
               reference = generator.generate(constructor);
            } else {
               System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> IGNORE AS ITS NOT PUBLIC: "+constructor);
            }
         }catch(Exception e){
            e.printStackTrace();
         }finally {
            System.out.println("############################# finished generating: "+constructor+ " as "+reference);
         }
      }
   }
}