package com.example.lesson20.repositories

import android.util.Log
import com.example.lesson20.Tester
import com.example.lesson20.javaAttribute.TesterAttribute
import com.example.lesson20.javaAttribute.TesterMethod
import java.lang.reflect.Method

class ReflectionRepository {
    companion object {
        private const val NAME_PUBLIC_METHOD = "doPublic"
        private const val NAME_PRIVATE_METHOD = "doPrivate"
        private const val NAME_PROTECTED_METHOD = "doProtected"

        private const val TAG_CONSTRUCTOR = "TAG_CONSTRUCTOR"
        private const val TAG_ATTRIBUTES = "TAG_ATTRIBUTES"
        private const val TAG_METHODS = "TAG_METHODS"
    }

    fun reflectionMethods() {
        val tester = Tester::class.java

        callMethodDoPublic(tester)

        callMethodDoProtected(tester)

        callMethodDoPrivate(tester)

        infoAboutConstructor(tester)

        infoAboutAttributes(tester)

        infoAboutMethods(tester)
    }

    private fun callMethodDoPublic(tester: Class<Tester>) {
        val methodDoPublic: Method? = tester.methods.find { method ->
            NAME_PUBLIC_METHOD == method.name
        }

        val objTester = Tester("method doPublic")
        methodDoPublic?.invoke(objTester)
    }

    private fun callMethodDoProtected(tester: Class<Tester>) {
        val methodDoProtected: Method? = tester.getDeclaredMethod(NAME_PROTECTED_METHOD)
        methodDoProtected?.isAccessible = true

        val objTester2 = Tester("method doProtected")
        methodDoProtected?.invoke(objTester2)
    }

    private fun callMethodDoPrivate(tester: Class<Tester>) {
        val methodDoPrivate: Method? = tester.getDeclaredMethod(NAME_PRIVATE_METHOD)
        methodDoPrivate?.isAccessible = true

        val objTester3 = Tester("method doPrivate")
        methodDoPrivate?.invoke(objTester3)
    }

    private fun infoAboutConstructor(tester: Class<Tester>) {
        val constructors = tester.constructors

        Log.e(TAG_CONSTRUCTOR, "---Info about constructors---")
        constructors.forEach { constructor ->
            val paramTypes = constructor.parameterTypes

            paramTypes.forEach { paramType ->
                Log.e(TAG_CONSTRUCTOR, "Param: ${paramType.name}")
            }
        }
    }

    private fun infoAboutAttributes(tester: Class<Tester>) {
        val fields = tester.declaredFields

        Log.e(TAG_ATTRIBUTES, "---Info about attributes---")
        fields.forEach { field ->
            Log.e(TAG_ATTRIBUTES, "Name: ${field.name}")
            Log.e(TAG_ATTRIBUTES, "Type: ${field.type.name}")

            if (field.isAnnotationPresent(TesterAttribute::class.java)) {
                field.annotations.forEach { annotation ->
                    Log.e(TAG_ATTRIBUTES, "Annotation: $annotation")
                }
            }
        }
    }

    private fun infoAboutMethods(tester: Class<Tester>) {
        val methods = tester.declaredMethods

        Log.e(TAG_METHODS, "---Info about methods---")
        methods.forEach { method ->
            Log.e(TAG_METHODS, "Name: ${method.name}")

            if (method.isAnnotationPresent(TesterMethod::class.java)) {
                method.annotations.forEach { annotation ->
                    Log.e(TAG_METHODS, "Annotation: $annotation")
                }
            }
        }
    }
}