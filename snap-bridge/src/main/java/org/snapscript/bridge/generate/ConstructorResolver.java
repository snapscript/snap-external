package org.snapscript.bridge.generate;

import static org.snapscript.core.Reserved.TYPE_CONSTRUCTOR;

import java.util.List;

import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.function.ArgumentConverter;
import org.snapscript.core.function.Function;
import org.snapscript.core.function.Parameter;
import org.snapscript.core.function.Signature;

public class ConstructorResolver {
   
   private final FunctionResolver resolver;
   private final Class[] empty;
   
   public ConstructorResolver(FunctionResolver resolver) {
      this.empty = new Class[]{};
      this.resolver = resolver;
   }

   public ConstructorArguments findConstructor(Scope scope, Type type, Object... args) {
      try {
         Function function = resolver.resolve(type, TYPE_CONSTRUCTOR, args);
         Signature signature = function.getSignature();
         ArgumentConverter converter = signature.getConverter();
         List<Parameter> parameters = signature.getParameters();
         Object[] list = converter.convert(args);
         
         if(list.length > 0) {
            Class[] types = new Class[list.length];
            
            for (int i = 0; i < types.length; i++) {
               Parameter parameter = parameters.get(i);
               Type constraint = parameter.getType();
               Class real = constraint.getType();
               
               types[i] = real;
            }
            return new ConstructorArguments(types, list);
         }
         return new ConstructorArguments(empty, list);
      } catch (Exception e) {
         throw new IllegalStateException("Could not match constructor for '" + type + "'", e);
      }
   }
}