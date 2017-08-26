package org.snapscript.bridge;

import org.snapscript.compile.Compiler;
import org.snapscript.compile.Executable;

import junit.framework.TestCase;

public class ReferenceVariableInSubclassTest extends TestCase {
   
   private static final String SOURCE =
   "import org.snapscript.bridge.ReferenceVariableInSubclassTest.Screen;\n"+
   "import org.snapscript.bridge.ReferenceVariableInSubclassTest.Game;\n"+
   "\n" +
   "class TetrisGame extends Game {\n"+
   "   public new(): super(1,2){}\n"+
   "   public getScreen(): Screen {\n"+
   "      return new TetrisScreen(this);\n"+
   "   }\n"+
   "   public showScore(){\n"+
   "      println(screen.score);\n"+
   "   }\n"+
   "}\n"+
   "class TetrisScreen extends Screen{\n"+
   "   private var score: Integer;\n"+
   "   public new(game) :super(game){\n"+
   "      this.score = 11;\n"+
   "   }\n"+
   "}\n"+
   "var g = new TetrisGame();\n"+
   "g.showScore();\n"+
   "assert g.screen.score == 11;";

   public abstract static class Screen {
      protected Game game;
      public Screen(Game game) {
         this.game = game;
      }
      public void show(){}
   }
   public abstract static class Game {
      protected Screen screen;
      protected int x;
      protected int y;
      public Game(int x, int y){
         this.screen = getScreen();
         this.x=x;
         this.y=y;
      }
      public abstract Screen getScreen();
   }
   
   public void testSuperCallToBaseMethod() throws Exception {
      Compiler compiler = ClassPathCompilerBuilder.createCompiler();
      System.err.println(SOURCE);
      Executable executable = compiler.compile(SOURCE);
      executable.execute();
   }
}
