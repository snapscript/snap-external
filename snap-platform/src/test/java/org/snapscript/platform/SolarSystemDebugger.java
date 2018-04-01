package org.snapscript.platform;

import java.io.File;
import java.util.concurrent.Executor;

import org.snapscript.common.store.FileStore;
import org.snapscript.common.store.Store;
import org.snapscript.common.thread.ThreadPool;
import org.snapscript.compile.Compiler;
import org.snapscript.compile.Executable;
import org.snapscript.compile.ResourceCompiler;
import org.snapscript.compile.StoreContext;
import org.snapscript.core.Context;

public class SolarSystemDebugger {

   public static void main(String[] list) throws Exception {
      Store store = new FileStore(
            new File("C:\\Work\\development\\snapscript\\snap-develop\\snap-studio\\work\\demo\\physics\\src"),
            new File("C:\\Work\\development\\snapscript\\snap-develop\\snap-studio\\work\\demo\\physics\\assets")
      );
      Executor executor = new ThreadPool(8);
      Context context = new StoreContext(store, executor);
      Compiler compiler = new ResourceCompiler(context);
      
      try {
         Executable executable = compiler.compile("/solarsystem/SolarSystem.snap");
         executable.execute();
      } catch(Exception e){
         e.printStackTrace();
      }
   }
}
