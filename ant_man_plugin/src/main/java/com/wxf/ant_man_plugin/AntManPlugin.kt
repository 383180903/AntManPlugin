package com.wxf.ant_man_plugin

import com.wxf.ant_man_plugin.configext.AntManConfigExtension
import com.wxf.ant_man_plugin.extensions.android
import org.gradle.api.Plugin
import org.gradle.api.Project


/**
 *  Created by wuxiaofeng on 2021/9/15.
 *  该插件命名为AntMan，出自漫威超级英雄之一的蚁人，受到皮姆粒子的影响，可无限缩小，自由出入各种场景，完成任务
 *  曾在复仇者大战X战警大电影中，协助钢铁侠完成反凤凰装甲
 *  我们的插件也是类似，需要穿梭于各种.class，完成bug修复，问题定位
 */
class AntManPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        println("********************************")
        println("***** Ant-Man, let's go... *****")
        println("********************************")

        val config = project.extensions.create("antManConfig", AntManConfigExtension::class.java)
        println("********************************")
        if (config.isNeedOpenPlugin()) {
            if (config.isHookSensor == true) {
                println("***** hook sensor mission start *****")
                project.android.registerTransform(AntManTransform(project))
            }
        } else {
            println("***** no mission *****")
        }
        println("********************************")
    }
}