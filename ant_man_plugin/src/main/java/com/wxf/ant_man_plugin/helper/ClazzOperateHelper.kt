package com.wxf.ant_man_plugin.helper

import com.wxf.ant_man_plugin.extensions.JAR_SUFFIX
import com.wxf.ant_man_plugin.extensions.print
import com.wxf.ant_man_plugin.manager.AsmHookManager
import com.wxf.ant_man_plugin.utils.JarZipUtil
import org.apache.commons.io.FileUtils
import java.io.File

/**
 *  Created by wuxiaofeng on 2021/9/16.
 *  Desc:
 */
object ClazzOperateHelper {

    fun hookSensorTargetFile(jarPath: String, targetFileName: String) {
        if (jarPath.endsWith(JAR_SUFFIX)) {
            "need to fixed jar path : $jarPath".print()
            val jarFile = File(jarPath)
            //生成解压路径,按时间戳来识别每次编译，避免并行编译带来的问题
            val unZipDir = "${jarFile.parent}${File.separator}${System.currentTimeMillis()}_${
                jarFile.name.replace(
                    JAR_SUFFIX, ""
                )
            }"
            "unZipDir - $unZipDir".print()
            //解压jar包
            val classPathList: List<String> = JarZipUtil.unZipJar(jarPath, unZipDir)
            var isModifySuccess = false
            classPathList.forEach { classPath ->
                if (classPath.contains(targetFileName) && classPath.contains("$").not()) {
                    val targetFile = File("$unZipDir${File.separator}$classPath")
                    "found target file - ${targetFile.absolutePath}".print()
                    if (targetFile.exists()) {
                        try {
                            AsmHookManager.operateSensorClazz(targetFile)
                            isModifySuccess = true
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            "isModifySuccess : $isModifySuccess".print()
            //修改成功则删除原jar包
            if (isModifySuccess) {
                jarFile.delete()
                //将修改好的class重新压缩
                JarZipUtil.zipJar(unZipDir, jarPath)
            }
            //删除解压出来的多余文件
            FileUtils.deleteDirectory(File(unZipDir))
        }
    }
}