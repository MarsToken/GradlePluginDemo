package com.demo.plugin;

import android.app.Application;

import androidx.multidex.MultiDex;

import com.demo.hotfix.BrettFix;
import com.demo.hotfix2.PatchHotFix;

import java.io.File;

/**
 *
 * @author WangMaoBo
 * @since 2024/5/10
 */
public class MyApp extends Application {
    private static final String TAG = "MyApp";

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        BrettFix.installPatch(this, new File(this.getCacheDir().getAbsolutePath() + "/patch.jar"));
    }
}
