package org.snapscript.platform.standard;

import static org.snapscript.core.ModifierType.CLASS;

import org.snapscript.core.Context;
import org.snapscript.core.function.Invocation;
import org.snapscript.core.platform.Platform;
import org.snapscript.core.platform.PlatformProvider;
import org.snapscript.core.scope.instance.Instance;

public class MockInstanceBuilder {
   
   public static void createInstance(Context context) throws Exception {
      PlatformProvider provider = new PlatformProvider(context.getExtractor(), context.getWrapper(), context.getStack());
      
      Platform builder = provider.create();
      Invocation invocation = builder.createSuperConstructor(context.getLoader().defineType("foo", "Foo", CLASS.mask), context.getLoader().loadType("javax.swing.JPanel"));
      Instance instance = (Instance)invocation.invoke(null, null);

      
      if(!instance.getBridge().getClass().getSuperclass().getName().equals("javax.swing.JPanel")){
         throw new RuntimeException(instance.getBridge().getClass().getName());
      }
   }
}
