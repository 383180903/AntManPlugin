package com.wxf.ant_man_plugin.asm_visitor.clazz_visitor

import com.wxf.ant_man_plugin.asm_visitor.method_visitor.SensorMethodVisitor
import com.wxf.ant_man_plugin.asm_visitor.method_visitor.ToastMethodVisitor
import com.wxf.ant_man_plugin.extensions.print
import jdk.internal.org.objectweb.asm.ClassVisitor
import jdk.internal.org.objectweb.asm.ClassWriter
import jdk.internal.org.objectweb.asm.MethodVisitor
import jdk.internal.org.objectweb.asm.Opcodes

/**
 *  Created by wuxiaofeng on 2021/12/14.
 *  Desc:
 */
class ToastClazzVisitor(api: Int = Opcodes.ASM5, clazzWriter: ClassWriter) : ClassVisitor(api, clazzWriter) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions)
        return ToastMethodVisitor(api, methodVisitor)
    }
}