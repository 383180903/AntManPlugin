package com.wxf.ant_man_plugin.helper

import javassist.ClassPath
import javassist.ClassPool

object ClassPathHelper {

    // 存储添加的{@link javassist.ClassPath}的容器
    private val sClassPaths = HashMap<ClassPool, MutableSet<ClassPath>>()

    /**
     * 添加路径到ClassPool
     * @param classPool
     * @param pathName
     * @return
     */
    fun appendClassPath(classPool: ClassPool, pathName: String) {
        val path = classPool.appendClassPath(pathName)
        addPath(classPool, path)
    }

    /**
     * 插入路径到ClassPool
     * @param classPool
     * @param pathName
     */
    fun insertClassPath(classPool: ClassPool, pathName: String) {
        val path = classPool.insertClassPath(pathName)
        addPath(classPool, path)
    }

    private fun addPath(classPool: ClassPool, path: ClassPath) {
        val paths = sClassPaths[classPool] ?: HashSet<ClassPath>().apply {
            sClassPaths[classPool] = this
        }
        paths.add(path)
    }

    /**
     * 清空添加到ClassPool的路径
     * @param classPool
     * @return
     */
    fun removeClassPath(classPool: ClassPool) {
        val paths = sClassPaths.remove(classPool)
        paths?.forEach {
            classPool.removeClassPath(it)
        }
    }

}