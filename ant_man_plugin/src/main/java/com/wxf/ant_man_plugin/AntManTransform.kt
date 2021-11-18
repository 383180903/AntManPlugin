package com.wxf.ant_man_plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.wxf.ant_man_plugin.extensions.*
import com.wxf.ant_man_plugin.helper.ClassPathHelper
import com.wxf.ant_man_plugin.helper.ClazzOperateHelper
import com.wxf.ant_man_plugin.manager.JarOutputManager
import javassist.ClassPool
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
        const val TARGET_METHOD = "getViewId"
        const val INSERT_BEFORE = "int id = $1.getId();\n" +
                "        if ((id & 0xFF000000) == 0 && (id & 0x00FFFFFF) != 0){\n" +
                "            return null;\n" +
                "        }"
    }

    private val pool: ClassPool by lazy {
        ClassPool(true).apply {
            addPathProject(project)
        }
    }

    override fun getName(): String = AntManTransform::class.java.simpleName

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> =
        TransformManager.CONTENT_CLASS

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> =
        TransformManager.SCOPE_FULL_PROJECT

    override fun isIncremental(): Boolean = true

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        println("*******************************************")
        println("********* AntManTransform start ***********")
        println("*******************************************")
        val startTime = System.currentTimeMillis()

        if (transformInvocation?.isIncremental?.not() == true) {
            println("******** not incremental ************")
            transformInvocation.outputProvider?.deleteAll()
            JarOutputManager.clearAll()
        }

        project.android.bootClasspath.forEach {
            pool.appendClassPath(it.absolutePath)
        }

        transformInvocation?.inputs?.forEach {
            //遍历jar包
            println("***********************************")
            println("*******    foreach jar    ********")
            println("***********************************")
            it.jarInputs.forEach { jarInput ->
                pool.addPathJarInput(jarInput)
                var jarName = jarInput.file.name
                if (!JarOutputManager.checkJarExists(jarName) || jarInput.status == Status.CHANGED) {
                    "$jarName need copy to output area".print()
                    "$jarName state:${jarInput.status}".print()
                    if (JarOutputManager.checkJarExists(jarName) && jarInput.status == Status.CHANGED){
                        //如果文件输出路径有一样名字的jar，现在出现更新，则必须把旧jar删除掉
                        safe {
                            "$jarName need delete overdue jar".print()
                            JarOutputManager.removeJarOutput(jarName)
                        }
                    }
                    val dest: File = if (jarName.contains(TARGET_JAR_NAME)) {
                        val jarPath = jarInput.file.absolutePath
                        ClazzOperateHelper.modifyTargetFileFromJar(
                            jarPath = jarPath,
                            classPool = pool,
                            targetClassName = TARGET_FILE,
                            targetMethodName = TARGET_METHOD,
                            targetMethodBefore = INSERT_BEFORE
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
            println("****************************************")
            println("*******    foreach directory    ********")
            println("****************************************")
            it.directoryInputs.forEach { dirInput ->
                pool.addPathDirInput(dirInput)
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
        ClassPathHelper.removeClassPath(pool)
        val cost = System.currentTimeMillis() - startTime
        println("*******************************************")
        println("********* AntManTransform finish **********")
        println("********* total cost: ${cost}ms  **********")
        println("*******************************************")
    }
}