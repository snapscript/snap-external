package org.snapscript.extend;

import static org.snapscript.core.Reserved.TYPE_CONSTRUCTOR;

import org.snapscript.core.Bug;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.function.Function;
import org.snapscript.core.function.Signature;

@Bug("This does not cache the data")
public class ConstructorResolver {
   
   private final FunctionResolver matcher;
   
   public ConstructorResolver(FunctionResolver matcher) {
      this.matcher = matcher;
   }

   public ConstructorData findConstructor(Scope scope, Type type, Object... args) {
      try {
         Function function = matcher.resolve(type, TYPE_CONSTRUCTOR, args);
         Signature signature = function.getSignature();
         Object[] list = signature.getConverter().convert(args);
         Class[] types = new Class[args.length];
         for (int i = 0; i < types.length; i++) {
            types[i] = signature.getParameters().get(i).getType().getType();
         }
         return new ConstructorData(types, list);
      } catch (Exception e) {
         throw new IllegalStateException("Error could not determine constructor data", e);
      }
   }
}
