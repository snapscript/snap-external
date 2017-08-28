package org.snapscript.dx.stock;

// can be up to 30x faster than reflection
public interface ProxyAdapter {
   Object invoke(Object object, Object... list) throws Exception;
}
/*
 
 
        try {
            final Method method = android.graphics.Canvas.class.getDeclaredMethod("drawBitmap",
                    android.graphics.Bitmap.class,
                    android.graphics.Rect.class,
                    android.graphics.Rect.class,
                    android.graphics.Paint.class);
            final ProxyBuilder builder = ProxyBuilder.forClass(Object.class).parentClassLoader(AndroidActivity.class.getClassLoader());
            final Class accessorClass = builder.buildMethodAccessor(method);
            final ProxyAdapter accessor = (ProxyAdapter)accessorClass.newInstance();
            System.err.println(accessor);
        }catch(Exception e){
            e.printStackTrace();;
        }
        
        
       try {
         final List l = new ArrayList();
         l.add(0);
         l.add(1);
         l.add(2);
         l.add(3);
         l.add(4);
         l.add(5);
         final Map m = new HashMap();
         m.put("a", "A");
         m.put("b", "B");
         m.put("c", "C");
         m.put("d", "D");
         m.put("e", "E");
         m.put("f", "F");
         final Method method =List.class.getDeclaredMethod("get", int.class);
         final Method method2 =Math.class.getDeclaredMethod("max", int.class, int.class);
         final Method method3 =Map.class.getDeclaredMethod("get", Object.class);
         final Method method4 =Class.class.getDeclaredMethod("forName", String.class);
         final ProxyBuilder builder = ProxyBuilder.forClass(MethodAdapter.class).parentClassLoader(AndroidActivity.class.getClassLoader());
         final Class accessorClass = builder.buildMethodAccessor(method);
         final Class accessorClass2 = builder.buildMethodAccessor(method2);
         final Class accessorClass3 = builder.buildMethodAccessor(method3);
         final Class accessorClass4 = builder.buildMethodAccessor(method4);
         final MethodAdapter accessor = (MethodAdapter)accessorClass.newInstance();
         final MethodAdapter accessor2 = (MethodAdapter)accessorClass2.newInstance();
         final MethodAdapter accessor3 = (MethodAdapter)accessorClass3.newInstance();
         final MethodAdapter accessor4 = (MethodAdapter)accessorClass4.newInstance();
          timeIt("normal (1000) List.get(int)", new Runnable() {
            public void run(){
                for(int i = 0; i < 1000; i++) {
                    Object val = new Object[]{2};
                    l.get(2);
                }
            }
         });
         
         timeIt("reflect (1000) List.get(int)", new Runnable() {
            public void run(){
               try {
                   for(int i = 0; i < 1000; i++) {
                       method.invoke(l, 2);
                   }
               }catch(Exception e){
                  e.printStackTrace();
               }
            }
         });
          timeIt("accessor (1000) List.get(int)", new Runnable() {
              public void run(){
                  try {
                      for(int i = 0; i < 1000; i++) {
                          accessor.invoke(l, 2);
                      }
                  }catch(Exception e){
                      e.printStackTrace();
                  }
              }
          });

          System.err.println("------------------------------------------------------------");


         timeIt("normal (1000000) List.get(int)", new Runnable() {
             public void run() {
                 for(int i = 0; i < 1000000; i++) {
                     Object val = new Object[]{2};
                     l.get(2);
                 }
             }
         });
         
         timeIt("reflect (1000000) List.get(int)", new Runnable() {
            public void run(){
               try {
                   for(int i = 0; i < 1000000; i++) {
                       method.invoke(l, 2);
                   }
               }catch(Exception e){
                  e.printStackTrace();
               }
            }
         });
          timeIt("accessor (1000000) List.get(int)", new Runnable() {
              public void run() {
                  try {
                      for(int i = 0; i < 1000000; i++) {
                          accessor.invoke(l, 2);
                      }
                  } catch (Exception e) {
                      e.printStackTrace();
                  }
              }
          });

          System.err.println("------------------------------------------------------------");

          timeIt("normal (1000000) Math.max(int, int)", new Runnable() {
              public void run() {
                  for(int i = 0; i < 1000000; i++) {
                      Object val = new Object[]{i,i+1};
                      Math.max(i, i+1);
                  }
              }
          });

          timeIt("reflect (1000000) Math.max(int, int)", new Runnable() {
              public void run(){
                  try {
                      for(int i = 0; i < 1000000; i++) {
                          method2.invoke(null, i, i+1);
                      }
                  }catch(Exception e){
                      e.printStackTrace();
                  }
              }
          });
          timeIt("accessor (1000000) Math.max(int, int)", new Runnable() {
              public void run() {
                  try {
                      for(int i = 0; i < 1000000; i++) {
                          accessor2.invoke(null, i, i+1);
                      }
                  } catch (Exception e) {
                      e.printStackTrace();
                  }
              }
          });

          System.err.println("------------------------------------------------------------");

          timeIt("normal (1000000) Map.get(Object)", new Runnable() {
              public void run() {
                  for(int i = 0; i < 1000000; i++) {
                      Object val = new Object[]{"a"};
                      m.get("a");
                  }
              }
          });

          timeIt("reflect (1000000) Map.get(Object)", new Runnable() {
              public void run(){
                  try {
                      for(int i = 0; i < 1000000; i++) {
                          method3.invoke(m, "a");
                      }
                  }catch(Exception e){
                      e.printStackTrace();
                  }
              }
          });
          timeIt("accessor (1000000) Map.get(Object)", new Runnable() {
              public void run() {
                  try {
                      for(int i = 0; i < 1000000; i++) {
                          accessor3.invoke(m, "a");
                      }
                  } catch (Exception e) {
                      e.printStackTrace();
                  }
              }
          });

          System.err.println("------------------------------------------------------------");

          timeIt("normal (100000) Class.forName(String)", new Runnable() {
              public void run() {
                  for(int i = 0; i < 100000; i++) {
                      try {
                          Object val = new Object[]{"java.lang.String"};
                          Class.forName("java.lang.String");
                      }catch(Exception e){
                          e.printStackTrace();
                      }
                  }
              }
          });

          timeIt("reflect (100000) Class.forName(String)", new Runnable() {
              public void run(){
                  try {
                      for(int i = 0; i < 100000; i++) {
                          method4.invoke(null, "java.lang.String");
                      }
                  }catch(Exception e){
                      e.printStackTrace();
                  }
              }
          });
          timeIt("accessor (100000) Class.forName(String)", new Runnable() {
              public void run() {
                  try {
                      for(int i = 0; i < 100000; i++) {
                          accessor4.invoke(null, "java.lang.String");
                      }
                  } catch (Exception e) {
                      e.printStackTrace();
                  }
              }
          });

          System.err.println("------------------------------------------------------------");

      } catch(Exception e){
         e.printStackTrace();
      }
      */
