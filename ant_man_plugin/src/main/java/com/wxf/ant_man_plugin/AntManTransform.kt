package com.wxf.ant_man_plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.wxf.ant_man_plugin.extensions.*
import com.wxf.ant_man_plugin.helper.ClazzOperateHelper
import com.wxf.ant_man_plugin.manager.JarOutputManager
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project
import java.io.File

/**
 *  Created by wuxiaofeng on 2021/9/15.
 */
class AntManTransform constructor(var project: Project) : Transform() {

    /**
     * 先写死需要hook的类以及方法，后续会改成可配置方案，支持批量修改
     */
    companion object {
        const val TARGET_JAR_NAME = "SensorsAnalyticsSDK"
        const val TARGET_FILE = "AopUtil"
    }

    override fun getName(): String = AntManTransform::class.java.simpleName

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> =
        TransformManager.CONTENT_CLASS

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> =
        TransformManager.SCOPE_FULL_PROJECT

    override fun isIncremental(): Boolean = true

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        "*******************************************".print()
        "********* AntManTransform start ***********".print()
        "*******************************************".print()
        val startTime = System.currentTimeMillis()

        if (transformInvocation?.isIncremental?.not() == true) {
            "******** not incremental ************".print()
            transformInvocation.outputProvider?.deleteAll()
            JarOutputManager.clearAll()
        }

        transformInvocation?.inputs?.forEach {
            //遍历jar包
            "***********************************".print()
            "*******    foreach jar    ********".print()
            "***********************************".print()
            it.jarInputs.forEach { jarInput ->
                var jarName = jarInput.name
                if (!JarOutputManager.checkJarExists(jarName) || jarInput.status == Status.CHANGED) {
                    "$jarName need copy to output area".print()
                    "$jarName state:${jarInput.status}".print()
                    if (JarOutputManager.checkJarExists(jarName) && jarInput.status == Status.CHANGED) {
                        //如果文件输出路径有一样名字的jar，现在出现更新，则必须把旧jar删除掉
                        safe {
                            "$jarName need delete overdue jar".print()
                            JarOutputManager.removeJarOutput(jarName)
                        }
                    }
                    val dest: File = if (jarName.contains(TARGET_JAR_NAME)) {
                        val jarPath = jarInput.file.absolutePath
                        ClazzOperateHelper.hookSensorTargetFile(
                            jarPath = jarPath,
                            targetFileName = TARGET_FILE
                        )
                        val md5Name = DigestUtils.md5Hex(jarInput.file.absolutePath)
                        if (jarName.endsWith(CLASS_SUFFIX)) {
                            jarName = jarName.substring(0, (jarName?.length ?: 0) - 4)
                        }
                        transformInvocation.outputProvider.getContentLocation(
                            jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR
                        )
                    } else {
                        transformInvocation.outputProvider.jarOutput(jarInput)
                    }
                    safe {
                        FileUtils.copyFile(jarInput.file, dest)
                        JarOutputManager.saveJarOutput(jarName, dest.absolutePath)
                        "$jarName has copied to output area:${dest.absolutePath}".print()
                    }
                }
            }
            //遍历文件夹
            "****************************************".print()
            "*******    foreach directory    ********".print()
            "****************************************".print()
            it.directoryInputs.forEach { dirInput ->
                // 获取output目录
                val dest = transformInvocation.outputProvider.getContentLocation(
                    dirInput.name,
                    dirInput.contentTypes,
                    dirInput.scopes,
                    Format.DIRECTORY
                )
                // 将input的目录复制到output指定目录
                safe {
                    FileUtils.copyDirectory(dirInput.file, dest)
                }
            }
        }
        val cost = System.currentTimeMillis() - startTime
        "*******************************************".print()
        "********* AntManTransform finish **********".print()
        "********* total cost: ${cost}ms  **********".print()
        "*******************************************".print()
    }
}