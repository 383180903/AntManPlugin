package com.wxf.ant_man_plugin.asm_visitor.method_visitor

import jdk.internal.org.objectweb.asm.Label
import jdk.internal.org.objectweb.asm.MethodVisitor
import jdk.internal.org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.INVOKEVIRTUAL


/**
 *  Created by wuxiaofeng on 2021/11/19.
 *  修改神策方法
 */
class SensorMethodVisitor(api: Int, mv: MethodVisitor) :
    MethodVisitor(api, mv) {

    override fun visitCode() {
        val label0 = Label()
        mv.visitLabel(label0)
        mv.visitLineNumber(12, label0)
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(INVOKEVIRTUAL, "android/view/View", "getId", "()I", false)
        mv.visitVarInsn(Opcodes.ISTORE, 1)
        val label1 = Label()
        mv.visitLabel(label1)
        mv.visitLineNumber(13, label1)
        mv.visitVarInsn(Opcodes.ILOAD, 1)
        mv.visitLdcInsn(-16777216)
        mv.visitInsn(Opcodes.IAND)
        val label2 = Label()
        mv.visitJumpInsn(Opcodes.IFNE, label2)
        mv.visitVarInsn(Opcodes.ILOAD, 1)
        mv.visitLdcInsn(16777215)
        mv.visitInsn(Opcodes.IAND)
        mv.visitJumpInsn(Opcodes.IFEQ, label2)
        val label3 = Label()
        mv.visitLabel(label3)
        mv.visitLineNumber(14, label3)
        mv.visitInsn(Opcodes.ACONST_NULL)
        mv.visitInsn(Opcodes.ARETURN)
        mv.visitLabel(label2)
        super.visitCode()
    }
}