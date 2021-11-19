package com.wxf.ant_man_plugin.utils

import com.wxf.ant_man_plugin.extensions.CLASS_SUFFIX
import com.wxf.ant_man_plugin.extensions.JAR_SUFFIX
import java.io.*
import java.util.*
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

object JarZipUtil {
    /**
     * 将该jar包解压到指定目录
     *
     * @param jarPath     jar包的绝对路径
     * @param destDirPath jar包解压后的保存路径
     * @return 返回该jar包中包含的所有class的完整路径
     */
    fun unZipJar(jarPath: String, destDirPath: String): List<String> {
        val list: MutableList<String> = ArrayList()
        if (jarPath.endsWith(JAR_SUFFIX)) {
            try {
                val jarFile = JarFile(jarPath)
                val jarEntrys = jarFile.entries()
                while (jarEntrys.hasMoreElements()) {
                    val jarEntry = jarEntrys.nextElement()
                    if (jarEntry.isDirectory) {
                        continue
                    }
                    val entryName = jarEntry.name
                    if (entryName.endsWith(CLASS_SUFFIX)) {
//                        val className = entryName.replace("\\", ".").replace("/", ".")
                        list.add(entryName)
                    }
                    val outFileName = "$destDirPath/$entryName"
                    val outFile = File(outFileName)
                    outFile.parentFile.mkdirs()
                    val inputStream = jarFile.getInputStream(jarEntry)
                    val fileOutputStream = FileOutputStream(outFile)
                    val tempBytes = ByteArray(1024)
                    var byteread: Int
                    while ((inputStream.read(tempBytes).also { byteread = it }) != -1) {
                        fileOutputStream.write(tempBytes, 0, byteread)
                    }
                    fileOutputStream.close()
                    inputStream.close()
                }
                jarFile.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return list
    }

    /**
     * 重新打包jar
     *
     * @param packagePath 将这个目录下的所有文件打包成jar
     * @param destPath    打包好的jar包的绝对路径
     */
    fun zipJar(packagePath: String, destPath: String?) {
        var outputStream: JarOutputStream? = null
        try {
            val file = File(packagePath)
            outputStream = JarOutputStream(FileOutputStream(destPath))
            if (file.isDirectory) {
                val fileTreeWalk = file.walk()
                fileTreeWalk.iterator().forEach { f ->
                    if (f.absolutePath.length <= packagePath.length) {
                        return@forEach
                    }
                    val entryName = f.absolutePath.substring(packagePath.length + 1)
                    outputStream.putNextEntry(ZipEntry(entryName))
                    if (!f.isDirectory) {
                        val inputStream: InputStream = FileInputStream(f)
                        val tempBytes = ByteArray(1024)
                        var byteread: Int
                        while ((inputStream.read(tempBytes).also { byteread = it }) != -1) {
                            outputStream.write(tempBytes, 0, byteread)
                        }
                        inputStream.close()
                    }
                }
            }
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}