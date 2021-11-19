package com.wxf.ant_man_plugin.extensions

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.BaseExtension
import javassist.ClassPool
import org.gradle.api.Project
import java.io.File

/**
 *  Created by wuxiaofeng on 2021/9/15.
 */

const val JAR_SUFFIX = ".jar"
const val CLASS_SUFFIX = ".class"


val Project.android: BaseExtension
    get() = extensions.getByType(BaseExtension::class.java)

/**
 * 从jarInput中获取输出
 */
fun TransformOutputProvider.jarOutput(jarInput: JarInput): File {
    return getContentLocation(jarInput.name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
}

/**
 * 方法安全执行保护器,debug状态直接抛出异常
 * @param error 执行发生异常时的回调
 * @param block 被保护执行的代码块
 */
inline fun <R> safe(noinline error: (Exception.() -> Unit)? = null, block: () -> R?): R? {
    try {
        return block()
    } catch (e: Exception) {
        if (error != null) {
            error(e)
        } else {
            println("错误:${e.localizedMessage}")
            throw e
        }
    }
    return null
}

fun String.print() {
    println("*************************************************")
    println(this)
}