package com.demo.hotfix;

import dalvik.system.PathClassLoader;

public class BrettClassLoader extends PathClassLoader {
    public BrettClassLoader(String dexPath, String librarySearchPath, ClassLoader parent) {
        super(dexPath, librarySearchPath, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }
}


