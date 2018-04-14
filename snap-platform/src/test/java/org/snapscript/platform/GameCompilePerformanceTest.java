package org.snapscript.platform;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;

import org.snapscript.common.store.FileStore;
import org.snapscript.common.store.Store;
import org.snapscript.common.thread.ThreadPool;
import org.snapscript.compile.Compiler;
import org.snapscript.compile.Executable;
import org.snapscript.compile.ResourceCompiler;
import org.snapscript.compile.StoreContext;
import org.snapscript.compile.verify.VerifyError;
import org.snapscript.compile.verify.VerifyException;
import org.snapscript.core.Context;
import org.snapscript.core.scope.EmptyModel;
import org.snapscript.core.scope.Model;

public class GameCompilePerformanceTest {

   public static void main(String[] list) throws Exception {
      Store store = new FileStore(
            new File("C:\\Work\\development\\snapscript\\snap-develop\\snap-studio\\work\\demo\\games\\src"),
            new File("C:\\Work\\development\\snapscript\\snap-develop\\snap-studio\\work\\demo\\games\\assets")
      );
//      Store store = new FileStore(
//            new File("C:\\Work\\development\\snapscript\\snap-develop\\snap-studio\\work\\games\\mario\\src"),
//            new File("C:\\Work\\development\\snapscript\\snap-develop\\snap-studio\\work\\games\\mario\\assets")
//      );
      Executor executor = new ThreadPool(8);
      
      for(int i = 0; i < 10000; i++) {
         execute(store, executor);
      }
   }
   
   private static void execute(Store store, Executor executor) throws Exception {
      long start = System.currentTimeMillis();
      
      try {
         Context context = new StoreContext(store, executor);
         Compiler compiler = new ResourceCompiler(context);
         Model model = new EmptyModel();         
         Executable executable = compiler.compile("/mario/MarioGame.snap");
         executable.execute(model, true);
      } catch(VerifyException e){
         List<VerifyError> errors = e.getErrors();
         
         for(VerifyError error : errors) {
            System.err.println(error);
         }
      } catch(Exception e){
         e.printStackTrace();
      } finally {
         long finish = System.currentTimeMillis();
         long duration = finish-start;
         System.err.println("Compile time was " + duration);
      }
   }
}
