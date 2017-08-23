package org.snapscript.bridge;

import org.snapscript.compile.Compiler;
import org.snapscript.compile.Executable;

import junit.framework.TestCase;

public class SuperConstructorCallsBaseMethodTest extends TestCase {
   
   private static final String SOURCE =
   "import org.snapscript.bridge.SuperConstructorCallsBaseMethodTest.SomeSuperCaller;\n"+
   "import org.snapscript.bridge.SuperConstructorCallsBaseMethodTest.SomeResult;\n"+
   "\n"+
   "class SomeBase extends SomeSuperCaller with Runnable {\n"+
   "   new(a,b) : super(a, b){\n"+
   "   }\n"+
   "\n"+
   "   public override getSomething(): SomeResult{\n"+
   "      return new SomeResult(this);\n"+
   "   }\n"+
   "}\n"+
   "var s = new SomeBase(1, 2);\n"+
   "s.dump();\n";
   
   public abstract static class SomeSuperCaller {
      
      protected final SomeResult result;
      protected final int x;
      protected final int y;
      
      protected SomeSuperCaller(int x, int y) {
         this.x = x;
         this.y = y;
         this.result = getSomething();
      }
      
      public void dump() {
         result.doIt();
      }
      
      public abstract SomeResult getSomething();
   }
   
   public static class SomeResult {
      
      private final SomeSuperCaller caller;
      
      public SomeResult(SomeSuperCaller caller) {
         this.caller = caller;
      }
      
      public void doIt(){
         System.err.println("x="+caller.x + " y="+caller.y);
      }
   }

   public void testSuperCallToBaseMethod() throws Exception {
      Compiler compiler = ClassPathCompilerBuilder.createCompiler();
      System.err.println(SOURCE);
      Executable executable = compiler.compile(SOURCE);
      executable.execute();
   }
}
