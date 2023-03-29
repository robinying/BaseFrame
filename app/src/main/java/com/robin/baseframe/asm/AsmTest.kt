package com.robin.baseframe.asm

import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import java.lang.reflect.Modifier

object AsmTest {

    private fun readArrayListByTreeApi() {
        // 1. 从类的全限定名、或字节数组、或二进制字节流中读取字节码
        val classReader = ClassReader(ArrayList::class.java.canonicalName)
        // 2. 以 ClassNode 形式表示字节码
        val classNode = ClassNode(Opcodes.ASM9)
        classReader.accept(classNode, ClassReader.SKIP_CODE)
        classNode.apply {
            println("name: $name\n")
            // 3. 读取属性
            fields.take(2).forEach {
                println("field: ${it.name} ${Modifier.toString(it.access)} ${it.desc} ${it.value}")
            }
            println()
            // 4. 读取方法
            methods.take(2).forEach {
                println("method: ${it.name} ${Modifier.toString(it.access)} ${it.desc}")
            }
        }
    }

}