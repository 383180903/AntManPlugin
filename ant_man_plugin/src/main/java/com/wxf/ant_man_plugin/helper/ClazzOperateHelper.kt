package com.wxf.ant_man_plugin.helper

import com.wxf.ant_man_plugin.extensions.CLASS_SUFFIX
import com.wxf.ant_man_plugin.extensions.JAR_SUFFIX
import com.wxf.ant_man_plugin.extensions.print
import com.wxf.ant_man_plugin.utils.JarZipUtil
import javassist.ClassPool
import org.apache.commons.io.FileUtils
import java.io.File

/**
 *  Created by wuxiaofeng on 2021/9/16.
 *  Desc:
 */
object ClazzOperateHelper {

    @JvmStatic
    fun modifyTargetFileFromJar(
        jarPath: String,
        classPool: ClassPool,
        targetClassName: String,
        targetMethodName: String,
        targetMethodBody: String = "",
        targetMethodBefore: String = "",
        targetMethodAfter: String = ""
    ) {
        if (jarPath.endsWith(JAR_SUFFIX)) {
            "need to fixed jar path : $jarPath".print()
            val jarFile = File(jarPath)
            //生成解压路径,按时间戳来识别每次编译，避免并行编译带来的问题
            val unZipDir = "${jarFile.parent}${File.separator}${System.currentTimeMillis()}_${jarFile.name.replace(
                JAR_SUFFIX, "")}"
            "unZipDir - $unZipDir".print()
            //解压jar包
            val classNameList: List<String> = JarZipUtil.unZipJar(jarPath, unZipDir)
            var isModifySuccess = false
            classNameList.forEach { className ->
                //找到想要编辑的类的类名,并且剔除匿名内部类
                if (className.contains(targetClassName) && className.contains("$").not()) {
                    "found target className - $className".print()
                    val newClassName =
                        className.substring(0, className.length - CLASS_SUFFIX.length)
                    val ctClass = classPool[newClassName]
                    //如果该类已经被冻结，需要解冻才能编辑
                    if (ctClass.isFrozen) {
                        ctClass.defrost()
                    }
                    try {
                        //找到要替换的方法
                        val ctMethod = ctClass.getDeclaredMethod(targetMethodName)
                        if (targetMethodBody.isEmpty()) {
                            if (targetMethodBefore.isEmpty().not()) {
                                ctMethod.insertBefore(targetMethodBefore)
                            }
                            if (targetMethodAfter.isEmpty().not()) {
                                ctMethod.insertAfter(targetMethodAfter)
                            }
                        } else {
                            ctMethod.setBody(targetMethodBody)
                        }
                        ctClass.writeFile(unZipDir)
                        isModifySuccess = true
                        "$className has been hooked".print()
                    } catch (e: Exception) {
                        "hook $className failed - ${e.message}".print()
                        e.printStackTrace()
                        isModifySuccess = false
                    } finally {
                        ctClass.detach()
                    }
                }
            }
            "isModifySuccess : $isModifySuccess".print()
            //修改成功则删除原jar包
            if (isModifySuccess){
                jarFile.delete()
                //将修改好的class重新压缩
                JarZipUtil.zipJar(unZipDir, jarPath)
            }
            //删除解压出来的多余文件
            FileUtils.deleteDirectory(File(unZipDir))
        }
    }
}