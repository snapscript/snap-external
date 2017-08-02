package org.snapscript.bridge;

import org.snapscript.core.Context;
import org.snapscript.core.ContextModule;
import org.snapscript.core.EmptyModel;
import org.snapscript.core.Model;
import org.snapscript.core.ModelScope;
import org.snapscript.core.Module;
import org.snapscript.core.Path;
import org.snapscript.core.Scope;
import org.snapscript.core.bridge.BridgeBuilder;
import org.snapscript.core.bridge.PlatformBridgeProvider;
import org.snapscript.core.define.Instance;

public class THING {
   public static void createInstance(Context context) {
      Model model = new EmptyModel();
      Path path = new Path("/foo");
      Module module = new ContextModule(context, path, "foo");
      Scope scope = new ModelScope(model, module);
      
      PlatformBridgeProvider provider = new PlatformBridgeProvider(
            context.getExtractor(), 
            context.getStack());
      
      BridgeBuilder builder = provider.create(context.getLoader().resolveType("javax.swing.JPanel"));
      Instance instance = builder.superInstance(scope, context.getLoader().defineType("foo", "Foo"));
      
      if(!instance.getBridge().getClass().getSuperclass().getName().equals("javax.swing.JPanel")){
         throw new RuntimeException(instance.getBridge().getClass().getName());
      }
   }
}
