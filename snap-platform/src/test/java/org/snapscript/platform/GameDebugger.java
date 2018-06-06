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

public class GameDebugger {

   public static void main(String[] list) throws Exception {
      File[] roots = new File[] {
            new File("C:\\Work\\development\\snapscript\\snap-develop\\snap-studio\\work\\demo\\games\\src"),
            new File("C:\\Work\\development\\snapscript\\snap-develop\\snap-studio\\work\\demo\\games\\assets")    
      };
//    File[] roots = new File[] {
//    new File("C:\\Work\\development\\snapscript\\snap-develop\\snap-studio\\work\\games\\mario\\src"),
//    new File("C:\\Work\\development\\snapscript\\snap-develop\\snap-studio\\work\\games\\mario\\assets")
//    };
      Store store = new FileStore(roots);
      Executor executor = new ThreadPool(8);
      Context context = new StoreContext(store, executor);
      Compiler compiler = new ResourceCompiler(context);

      compile(roots, executor);
      execute(compiler);
   }
   
   private static void compile(File[] roots, Executor executor) {
      try {
         for(int i = 0; i < 1000; i++) {
            Store store = new FileStore(roots);
            long start = System.currentTimeMillis();
            Context context = new StoreContext(store, executor);
            Compiler compiler = new ResourceCompiler(context);
            Model model = new EmptyModel();
            Executable executable = compiler.compile("/mario/MarioGame.snap");
            executable.execute(model, true);
            long finish = System.currentTimeMillis();
            System.out.println("COMPILE: " +(finish-start));
         }
      } catch(VerifyException e){
         List<VerifyError> errors = e.getErrors();
         
         for(VerifyError error : errors) {
            System.err.println(error);
         }
      } catch(Exception e){
         e.printStackTrace();
      }
   }

   private static void execute(Compiler compiler) {
      try {
         Executable executable = compiler.compile("/mario/MarioGame.snap");
         executable.execute();
      } catch(VerifyException e){
         List<VerifyError> errors = e.getErrors();
         
         for(VerifyError error : errors) {
            System.err.println(error);
         }
      } catch(Exception e){
         e.printStackTrace();
      }
   }
}
