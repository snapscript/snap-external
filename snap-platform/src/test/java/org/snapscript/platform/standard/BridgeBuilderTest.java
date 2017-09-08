package org.snapscript.platform.standard;

import static org.snapscript.core.Category.CLASS;
import junit.framework.TestCase;

import org.snapscript.common.store.ClassPathStore;
import org.snapscript.compile.StoreContext;
import org.snapscript.core.Context;
import org.snapscript.core.Type;
import org.snapscript.core.define.Instance;
import org.snapscript.core.function.Invocation;
import org.snapscript.core.platform.CachePlatformProvider;
import org.snapscript.core.platform.Platform;

public class BridgeBuilderTest extends TestCase {
   
   public void testBridgeBuilder() throws Exception {
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      
      createInstance(context);
      MockInstanceBuilder.createInstance(context);
   }
   
   public void createInstance(Context context) throws Exception {
      CachePlatformProvider provider = new CachePlatformProvider(context.getExtractor());
      
      Type type = context.getLoader().defineType("foo", "Foo", CLASS);
      Type panel = context.getLoader().resolveType("javax.swing.JPanel");
      
      type.getTypes().add(panel);

      Platform builder = provider.create();
      Invocation invocation = builder.createSuperConstructor(type, panel);
      Instance instance = (Instance)invocation.invoke(null, null);
      
      assertEquals(instance.getBridge().getClass().getSuperclass().getName(), "javax.swing.JPanel");
   }

}
