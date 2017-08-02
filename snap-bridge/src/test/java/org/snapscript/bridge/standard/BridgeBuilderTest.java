package org.snapscript.bridge.standard;

import junit.framework.TestCase;

import org.snapscript.bridge.THING;
import org.snapscript.common.store.ClassPathStore;
import org.snapscript.compile.StoreContext;
import org.snapscript.core.Context;
import org.snapscript.core.ContextModule;
import org.snapscript.core.EmptyModel;
import org.snapscript.core.Model;
import org.snapscript.core.ModelScope;
import org.snapscript.core.Module;
import org.snapscript.core.Path;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.bridge.BridgeBuilder;
import org.snapscript.core.bridge.PlatformBridgeProvider;
import org.snapscript.core.define.Instance;

public class BridgeBuilderTest extends TestCase {
   
   public void testBridgeBuilder() throws Exception {
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      
      createInstance(context);
      THING.createInstance(context);
   }
   
   public void createInstance(Context context) {
      Model model = new EmptyModel();
      Path path = new Path("/foo");
      Module module = new ContextModule(context, path, "foo");
      Scope scope = new ModelScope(model, module);
      
      PlatformBridgeProvider provider = new PlatformBridgeProvider(
            context.getExtractor(), 
            context.getStack());
      
      Type type = context.getLoader().defineType("foo", "Foo");

      BridgeBuilder builder = provider.create(context.getLoader().resolveType("javax.swing.JPanel"));
      Instance instance = builder.superInstance(scope, type);
      
      assertEquals(instance.getBridge().getClass().getSuperclass().getName(), "javax.swing.JPanel");
   }

}
