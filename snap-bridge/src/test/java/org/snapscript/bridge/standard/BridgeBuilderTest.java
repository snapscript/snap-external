package org.snapscript.bridge.standard;

import static org.snapscript.core.Category.CLASS;
import junit.framework.TestCase;

import org.snapscript.common.store.ClassPathStore;
import org.snapscript.compile.StoreContext;
import org.snapscript.core.Context;
import org.snapscript.core.Result;
import org.snapscript.core.Type;
import org.snapscript.core.bridge.BridgeBuilder;
import org.snapscript.core.bridge.PlatformBridgeProvider;
import org.snapscript.core.define.Instance;
import org.snapscript.core.function.Invocation;

public class BridgeBuilderTest extends TestCase {
   
   public void testBridgeBuilder() throws Exception {
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      
      createInstance(context);
      MockInstanceBuilder.createInstance(context);
   }
   
   public void createInstance(Context context) throws Exception {
      PlatformBridgeProvider provider = new PlatformBridgeProvider(context.getExtractor());
      
      Type type = context.getLoader().defineType("foo", "Foo", CLASS);
      Type panel = context.getLoader().resolveType("javax.swing.JPanel");
      
      type.getTypes().add(panel);

      BridgeBuilder builder = provider.create();
      Invocation invocation = builder.superConstructor(type, panel);
      Result result = invocation.invoke(null, null);
      Instance instance = result.getValue();
      
      assertEquals(instance.getBridge().getClass().getSuperclass().getName(), "javax.swing.JPanel");
   }

}
