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

public class SolarSystemDebugger {

   public static void main(String[] list) throws Exception {
      Store store = new FileStore(
            new File("C:\\Work\\development\\snapscript\\snap-develop\\snap-studio\\work\\demo\\physics\\src"),
            new File("C:\\Work\\development\\snapscript\\snap-develop\\snap-studio\\work\\demo\\physics\\assets")
      );
      Executor executor = new ThreadPool(8);
      Context context = new StoreContext(store, executor);
      Compiler compiler = new ResourceCompiler(context);
      
      //compile(store, executor);
      execute(compiler);
   }
   
   
   private static void compile(Store store, Executor executor) {
      try {
         for(int i = 0; i < 100; i++) {
            long start = System.currentTimeMillis();
            Context context = new StoreContext(store, executor);
            Compiler compiler = new ResourceCompiler(context);
            Model model = new EmptyModel();
            Executable executable = compiler.compile("/solarsystem/SolarSystem.snap");
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
         Executable executable = compiler.compile("/solarsystem/SolarSystem.snap");
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
