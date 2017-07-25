package org.snapscript.extend.proxy;

import static org.snapscript.core.Reserved.TYPE_CONSTRUCTOR;

import org.snapscript.core.Bug;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.function.Function;
import org.snapscript.core.function.Signature;

@Bug("This does not cache the data")
public class ConstructorResolver {
   
   private final FunctionResolver resolver;
   
   public ConstructorResolver(FunctionResolver resolver) {
      this.resolver = resolver;
   }

   public ConstructorArguments findConstructor(Scope scope, Type type, Object... args) {
      try {
         Function function = resolver.resolve(type, TYPE_CONSTRUCTOR, args);
         Signature signature = function.getSignature();
         Object[] list = signature.getConverter().convert(args);
         Class[] types = new Class[args.length];
         for (int i = 0; i < types.length; i++) {
            types[i] = signature.getParameters().get(i).getType().getType();
         }
         return new ConstructorArguments(types, list);
      } catch (Exception e) {
         throw new IllegalStateException("Error could not determine constructor data", e);
      }
   }
}
