package com.wxf.ant_man_plugin.configext

/**
 *  Created by wuxiaofeng on 2021/11/4.
 *  插件配置
 */
open class AntManConfigExtension {

    @JvmField
    var isHookSensor:Boolean? = true

    fun setIsHookSensor(isHookSensor: Boolean?) {
        this.isHookSensor = isHookSensor
    }

    fun getIsHookSensor(): Boolean? {
        return isHookSensor
    }

    /**
     * 任意一项功能开启，都会让插件启动
     */
    fun isNeedOpenPlugin():Boolean = isHookSensor?:false
}