package org.snapscript.platform;

import java.io.File;

import junit.framework.TestCase;

import org.snapscript.common.store.FileStore;
import org.snapscript.common.store.Store;
import org.snapscript.compile.Compiler;
import org.snapscript.compile.Executable;
import org.snapscript.compile.StoreContext;
import org.snapscript.compile.StringCompiler;
import org.snapscript.core.Context;

// this test can be disabled
public class TestSuiteTest extends TestCase {
   
   private static final String LOCATION_OF_TEST_SUITE = "../../snap-develop/snap-studio/work/test";
   
   private static final String PATTERN = ".*ExtendClassTest.*";
   private static final String SOURCE = 
   "import test.TestCaseRunner;\n"+   
   "const runner = new TestCaseRunner('%s');\n"+
   //"assert runner.runTests(name -> name.matches(`" + PATTERN + "`));\n";
   "assert runner.runTests();\n";
         
   public void testSuite() throws Exception {
      File file = new File(LOCATION_OF_TEST_SUITE);
      Store store = new FileStore(file);
      Context context = new StoreContext(store, null);
      assertTrue("Test suite directory not found: " +file.getCanonicalPath(), file.exists());
      Compiler compiler = new StringCompiler(context);
      String source = String.format(SOURCE, file.getCanonicalPath().replace(""+File.separatorChar,"\\"+File.separatorChar));
      System.err.println(source);
      Executable executable = compiler.compile(source);
      executable.execute();
   }
}
