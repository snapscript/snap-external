package org.snapscript.bridge.standard;

import static org.snapscript.core.Category.CLASS;

import org.snapscript.core.Context;
import org.snapscript.core.Result;
import org.snapscript.core.bridge.BridgeBuilder;
import org.snapscript.core.bridge.PlatformBridgeProvider;
import org.snapscript.core.define.Instance;
import org.snapscript.core.function.Invocation;

public class MockInstanceBuilder {
   
   public static void createInstance(Context context) throws Exception {
      PlatformBridgeProvider provider = new PlatformBridgeProvider(context.getExtractor());
      
      BridgeBuilder builder = provider.create();
      Invocation invocation = builder.superConstructor(context.getLoader().defineType("foo", "Foo", CLASS), context.getLoader().resolveType("javax.swing.JPanel"));
      Result result = invocation.invoke(null, null);
      Instance instance = result.getValue();
      
      if(!instance.getBridge().getClass().getSuperclass().getName().equals("javax.swing.JPanel")){
         throw new RuntimeException(instance.getBridge().getClass().getName());
      }
   }
}
