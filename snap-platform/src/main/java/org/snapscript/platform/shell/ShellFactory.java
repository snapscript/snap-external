package org.snapscript.platform.shell;

import java.lang.reflect.Modifier;

import org.snapscript.common.Cache;
import org.snapscript.common.CopyOnWriteCache;
import org.snapscript.core.Type;
import org.snapscript.core.TypeCache;
import org.snapscript.core.function.ConstantInvocation;
import org.snapscript.core.function.Invocation;
import org.snapscript.platform.generate.ClassGenerator;

public class ShellFactory {
   
   private static Class[] TYPES = {
      InstanceBuilder.class,
      InterfaceBuilder.class,
      NativeBuilder.class,
      SerializationBuilder.class,
      ConstructorBuilder.class
   };

   private final Cache<Class, ShellBuilder> builders;
   private final TypeCache<Object> shells;
   private final ClassGenerator generator;
   
   public ShellFactory(ClassGenerator generator) {
      this.builders = new CopyOnWriteCache<Class, ShellBuilder>();
      this.shells = new TypeCache<Object>();
      this.generator = generator;
      
   }

   public Invocation createInvocation(Type type) {
      Object instance = shells.fetch(type);
      
      if(instance == null) {
         try {
            instance = createInstance(type);
            
            if(instance != null) {
               shells.cache(type, instance);
               return new ConstantInvocation(instance);
            }
         }catch(Exception e) {
            throw new IllegalStateException("Could not create instance of '" + type + "'", e);
         }
      }
      return new ConstantInvocation(instance);
   }
   
   private Object createInstance(Type type) throws Exception {
      int count = builders.size();
      Class real = type.getType();
      
      if(count < TYPES.length) {
         for(Class entry : TYPES) {
            Object instance = entry.newInstance();
            ShellBuilder builder = (ShellBuilder)instance;
           
            builders.cache(entry, builder);
         }
      }
      if(real != null) {
         int modifiers = real.getModifiers();
         
         if(Modifier.isAbstract(modifiers)) {
            real = generator.generate(type, real);
         }
      }
      for(Class entry : TYPES) {
         ShellBuilder builder = builders.fetch(entry);
         Object value = builder.create(type, real);
         
         if(value != null) {
            return value;
         }
      }  
      return null;
   }
}