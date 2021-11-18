package com.wxf.ant_man_plugin.manager

import com.android.utils.FileUtils
import java.io.File

/**
 *  Created by wuxiaofeng on 2021/9/17.
 *  用来记录jar包的输出记录，避免重复编译带来的类定义冲突问题
 */
object JarOutputManager {
    var jarOutputMap: HashMap<String, String> = HashMap()

    fun saveJarOutput(jarName: String, outputPath: String) {
        jarOutputMap[jarName] = outputPath
    }

    fun removeJarOutput(jarName: String) {
        FileUtils.deleteIfExists(File(jarOutputMap[jarName] ?: ""))
        jarOutputMap.remove(jarName)
    }

    fun checkJarExists(jarName: String): Boolean {
        return jarOutputMap.containsKey(jarName) && File(jarOutputMap[jarName] ?: "").exists()
    }

    fun clearAll() {
        jarOutputMap.clear()
    }
}