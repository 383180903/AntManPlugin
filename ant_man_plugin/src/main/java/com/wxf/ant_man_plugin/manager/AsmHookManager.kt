package com.wxf.ant_man_plugin.manager

import com.wxf.ant_man_plugin.asm_visitor.clazz_visitor.SensorClazzVisitor
import com.wxf.ant_man_plugin.extensions.print
import jdk.internal.org.objectweb.asm.ClassReader
import jdk.internal.org.objectweb.asm.ClassVisitor
import jdk.internal.org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileOutputStream

/**
 *  Created by wuxiaofeng on 2021/11/19.
 *  通过ASM修改字节码文件
 */
object AsmHookManager {

    @JvmStatic
    fun operateSensorClazz(clazzFile: File) {
        val clazzReader = ClassReader(clazzFile.readBytes())
        val clazzWriter = ClassWriter(clazzReader, ClassWriter.COMPUTE_MAXS)
        val clazzVisitor = SensorClazzVisitor(clazzWriter = clazzWriter)
        clazzReader.accept(clazzVisitor, ClassReader.EXPAND_FRAMES)
        //覆盖原来的class文件
        val code = clazzWriter.toByteArray()
        val fos =
            FileOutputStream(clazzFile.parentFile.absolutePath + File.separator + clazzFile.name)
        fos.write(code)
        fos.close()
        "${clazzFile.name} modify success".print()
    }
}