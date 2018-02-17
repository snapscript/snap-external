package org.snapscript.platform;

import org.snapscript.compile.Compiler;
import org.snapscript.compile.Executable;

import junit.framework.TestCase;

public class StaticCompileTest extends TestCase {

   private static final String SOURCE_1 = 
   "class Foo {\n"+
   "   var x: Integer;\n"+
   "   new(x: Integer){\n"+
   "      this.x = x;\n"+
   "   }\n"+
   "   func(a: String){\n"+
   "      println(a);\n"+
   "   }\n"+
   "}\n"+
   "var x: Foo = new Foo(1);\n"+
   "x.func('go');\n"+
   "x.func2('go');\n";
   
   public void testStaticCompile() throws Exception {
      Compiler compiler = ClassPathCompilerBuilder.createCompiler();
      System.err.println(SOURCE_1);
      Executable executable = compiler.compile(SOURCE_1);
      executable.execute();
   }
}
