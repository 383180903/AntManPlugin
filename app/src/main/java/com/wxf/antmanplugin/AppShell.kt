package com.wxf.antmanplugin

import android.app.Application
import android.content.Context
import com.sensorsdata.analytics.android.sdk.SAConfigOptions
import com.sensorsdata.analytics.android.sdk.SensorsAnalyticsAutoTrackEventType
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI


/**
 *  Created by wuxiaofeng on 2021/9/15.
 */
class AppShell : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)


    }

    override fun onCreate() {
        super.onCreate()
        val saConfigOptions = SAConfigOptions("https://cnstat.lz310.com/sa?project=zhiya")
// 开启全埋点
// 开启全埋点
        saConfigOptions.setAutoTrackEventType(
            SensorsAnalyticsAutoTrackEventType.APP_CLICK or
                    SensorsAnalyticsAutoTrackEventType.APP_START or
                    SensorsAnalyticsAutoTrackEventType.APP_END or
                    SensorsAnalyticsAutoTrackEventType.APP_VIEW_SCREEN
        ) //开启 Log
            .enableLog(true)
            //开启 crash 采集
            .enableTrackAppCrash()
            .enableVisualizedAutoTrack(true)
            .enableVisualizedAutoTrackConfirmDialog(false)
            .enableJavaScriptBridge(false)//开启App打通H5
            .enableHeatMap(true)
        /**
         * 其他配置，如开启可视化全埋点
         */
// 需要在主线程初始化神策 SDK
        /**
         * 其他配置，如开启可视化全埋点
         */
// 需要在主线程初始化神策 SDK
        SensorsDataAPI.startWithConfigOptions(this, saConfigOptions)
    }
}