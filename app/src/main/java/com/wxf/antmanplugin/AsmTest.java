package com.wxf.antmanplugin;

import android.view.View;

/**
 * Created by wuxiaofeng on 2021/11/23.
 */
class AsmTest {
    public static String getViewId(View view) {
        int id = view.getId();
        if ((id & 0xFF000000) == 0 && (id & 0x00FFFFFF) != 0){
            return null;
        }
        return "";
    }
}
