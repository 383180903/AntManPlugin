package com.wxf.ant_man_plugin.asm_visitor.method_visitor

import jdk.internal.org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 *  Created by wuxiaofeng on 2021/12/14.
 *  Desc:
 */
class ToastMethodVisitor(api: Int, mv: MethodVisitor) :
    MethodVisitor(api, mv) {


    /**
     * 访问方法的指令。 方法指令是调用方法的指令。
     *
     * @param opcode 要访问的类型指令的操作码。可以是INVOKEVIRTUAL，INVOKESPECIAL，INVOKESTATIC或INVOKEINTERFACE。
     * @param owner 方法的所有者类的内部名称 (see {@link
     *     Type#getInternalName()}).
     * @param name 方法名
     * @param descriptor the method's descriptor (see {@link Type}).
     * @param isInterface if the method's owner class is an interface.
     */
    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        if (Opcodes.INVOKESTATIC == opcode) {
            println("opcode:$opcode ,owner:$owner ,name:$name")
            if (owner?.contains("com/wxf/antmanplugin/TestToast") == true) {
                super.visitMethodInsn(
                    opcode,
                    "com/wxf/antmanplugin/ToastUtils",
                    name,
                    descriptor,
                    false
                )
                return
            }
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
    }
}