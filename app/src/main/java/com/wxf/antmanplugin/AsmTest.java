package com.wxf.antmanplugin;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

/**
 * Created by wuxiaofeng on 2021/11/23.
 */
class AsmTest {
    public static String getViewId(View view) {
        int id = view.getId();
        if ((id & 0xFF000000) == 0 && (id & 0x00FFFFFF) != 0) {
            return null;
        }
        return "";
    }

    public static void showToast(Context context) {
        Toast.makeText(context, "11", Toast.LENGTH_SHORT).show();
    }
}
