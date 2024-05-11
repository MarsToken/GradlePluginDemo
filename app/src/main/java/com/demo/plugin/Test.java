package com.demo.plugin;

/**
 *
 * @author WangMaoBo
 * @since 2024/5/10
 */
public class Test {

    public String makeTest(boolean isDebug) {
        if (isDebug) {
            return "抛异常了!不，热修复了。";
        } else {
            return "异常已被修复!soso";
        }
    }
}
