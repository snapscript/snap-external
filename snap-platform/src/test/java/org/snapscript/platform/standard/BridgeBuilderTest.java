package org.snapscript.platform.standard;

import static org.snapscript.core.ModifierType.CLASS;
import junit.framework.TestCase;

import org.snapscript.common.store.ClassPathStore;
import org.snapscript.compile.StoreContext;
import org.snapscript.core.Context;
import org.snapscript.core.constraint.Constraint;
import org.snapscript.core.function.Invocation;
import org.snapscript.core.platform.Platform;
import org.snapscript.core.platform.PlatformProvider;
import org.snapscript.core.scope.instance.Instance;
import org.snapscript.core.type.Type;

public class BridgeBuilderTest extends TestCase {
   
   public void testBridgeBuilder() throws Exception {
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      
      createInstance(context);
      MockInstanceBuilder.createInstance(context);
   }
   
   public void createInstance(Context context) throws Exception {
      PlatformProvider provider = new PlatformProvider(context.getExtractor(), context.getWrapper(), context.getStack());
      
      Type type = context.getLoader().defineType("foo", "Foo", CLASS.mask);
      Type panel = context.getLoader().loadType("javax.swing.JPanel");
      
      type.getTypes().add(Constraint.getConstraint(panel));

      Platform builder = provider.create();
      Invocation invocation = builder.createSuperConstructor(type, panel);
      Instance instance = (Instance)invocation.invoke(null, null);
      
      assertEquals(instance.getBridge().getClass().getSuperclass().getName(), "javax.swing.JPanel");
   }

}
