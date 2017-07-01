package org.snapscript.extend;

import org.snapscript.core.Bug;
import org.snapscript.core.Context;
import org.snapscript.core.Reserved;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.bind.ObjectFunctionMatcher;
import org.snapscript.core.function.Function;
import org.snapscript.core.function.Signature;

@Bug("This does not cache the data")
public class ConstructorResolver {

   public static ConstructorData findConstructor(Scope scope, Type type, Object... args) {
      try {
         final Context c = scope.getModule().getContext();
         final ObjectFunctionMatcher matcher = new ObjectFunctionMatcher(c.getExtractor(), c.getStack());
         Function function = matcher.resolve(type, Reserved.TYPE_CONSTRUCTOR, args);
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
