package org.snapscript.platform.generate;

import static org.snapscript.core.Reserved.TYPE_CONSTRUCTOR;

import java.util.List;

import org.snapscript.core.constraint.Constraint;
import org.snapscript.core.function.ArgumentConverter;
import org.snapscript.core.function.Function;
import org.snapscript.core.function.Parameter;
import org.snapscript.core.function.Signature;
import org.snapscript.core.function.index.FunctionIndexer;
import org.snapscript.core.function.index.FunctionPointer;
import org.snapscript.core.scope.Scope;
import org.snapscript.core.type.Type;

public class ConstructorResolver {
   
   private final FunctionIndexer indexer;
   private final Class[] empty;
   
   public ConstructorResolver(FunctionIndexer indexer) {
      this.empty = new Class[]{};
      this.indexer = indexer;
   }

   public ConstructorArguments resolve(Scope scope, Type type, Object... args) {
      try {
         FunctionPointer call = indexer.index(type, TYPE_CONSTRUCTOR, args);
         Function function = call.getFunction();
         Signature signature = function.getSignature();
         ArgumentConverter converter = signature.getConverter();
         List<Parameter> parameters = signature.getParameters();
         Object[] list = converter.convert(args);
         
         if(list.length > 0) {
            Class[] types = new Class[list.length];
            
            for (int i = 0; i < types.length; i++) {
               Parameter parameter = parameters.get(i);
               Constraint constraint = parameter.getConstraint();
               Type require = constraint.getType(scope);
               Class real = require.getType();
               
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