package org.snapscript.cglib.proxy;

class D5 implements DI5 {
    public int vararg(String... strs) {
        return strs.length;
    }
}
