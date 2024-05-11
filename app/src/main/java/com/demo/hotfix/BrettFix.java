package com.demo.hotfix;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brett.li on 2022/7/31.
 */
public class BrettFix {
    private static final String TAG = "BrettFix";

    public static void installPatch(Context application, File patch) {
        List<File> patchs = new ArrayList<>();
        if (patch.exists()) {
            patchs.add(patch);
        }
        //1.获取程序的PathClassLoader对象
        ClassLoader classLoader = application.getClassLoader();

        //替换成我们自己的classloader
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                classLoader = ClassLoaderInjector.inject(application, classLoader, patchs);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
//            return;
        }
        //2.反射获取PathClassLoader父类BaseDexClassLoader的pathList对象
        try {
            Field pathListField = ShareReflectUtil.findField(classLoader, "pathList");
            Object pathList = pathListField.get(classLoader);
            Log.e("tag", pathList.getClass().getName());
            //3.反射获取pathList的dexElements对象(oldElement)
            Field dexElementsField = ShareReflectUtil.findField(pathList, "dexElements");
            Object[] oldElements = (Object[]) dexElementsField.get(pathList);
            //4、把补丁包变成Element数组：patchElement（反射执行makePathElements）
            Object[] patchElements = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Method makePathElements = ShareReflectUtil.findMethod(pathList, "makePathElements",
                    List.class, File.class,
                    List.class);
                ArrayList<IOException> ioExceptions = new ArrayList<>();
                patchElements = (Object[])
                    makePathElements.invoke(pathList, patchs, application.getCacheDir(), ioExceptions);

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Method makePathElements = ShareReflectUtil.findMethod(pathList, "makeDexElements",
                    ArrayList.class, File.class, ArrayList.class);
                ArrayList<IOException> ioExceptions = new ArrayList<>();
                patchElements = (Object[])
                    makePathElements.invoke(pathList, patchs, application.getCacheDir(), ioExceptions);
            }

            //5、合并patchElement+oldElement = newElement （Array.newInstance）
            //创建一个新数组，大小 oldElements+patchElements
//                int[].class.getComponentType() ==int.class
            Object[] newElements = (Object[]) Array.newInstance(oldElements.getClass().getComponentType(),
                oldElements.length + patchElements.length);

            System.arraycopy(patchElements, 0, newElements, 0, patchElements.length);
            System.arraycopy(oldElements, 0, newElements, patchElements.length, oldElements.length);
            //6、反射把oldElement赋值成newElement
            dexElementsField.set(pathList, newElements);
            Log.e(TAG, "========");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}

