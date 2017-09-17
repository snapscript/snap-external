package org.snapscript.platform.standard;

import static org.snapscript.core.Category.CLASS;

import org.snapscript.core.Context;
import org.snapscript.core.define.Instance;
import org.snapscript.core.function.Invocation;
import org.snapscript.core.platform.CachePlatformProvider;
import org.snapscript.core.platform.Platform;

public class MockInstanceBuilder {
   
   public static void createInstance(Context context) throws Exception {
      CachePlatformProvider provider = new CachePlatformProvider(context.getExtractor(), context.getStack());
      
      Platform builder = provider.create();
      Invocation invocation = builder.createSuperConstructor(context.getLoader().defineType("foo", "Foo", CLASS), context.getLoader().resolveType("javax.swing.JPanel"));
      Instance instance = (Instance)invocation.invoke(null, null);

      
      if(!instance.getBridge().getClass().getSuperclass().getName().equals("javax.swing.JPanel")){
         throw new RuntimeException(instance.getBridge().getClass().getName());
      }
   }
}
