package io.github.wickedev.graphql.spring.data.r2dbc.query.redefine

import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.type.TypeDefinition
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy
import org.springframework.data.repository.core.RepositoryMetadata
import java.lang.reflect.Method
import java.lang.reflect.Modifier

fun redefineMethod(method: Method, metadata: RepositoryMetadata): Method {
    return if (method.name.startsWith("find")) {
        redefineFindMethod(method, metadata)
    } else {
        redefineCountExistsMethod(method, metadata)
    }
}


fun redefineInterfaceName(method: Method): String {
    return "${method.declaringClass.name}${method.name.toUpperCaseFirstLetter()}${method.parameters.size}"
}

fun makeType(
    interfaceName: String,
    methodName: String,
    parameters: List<TypeDefinition>,
    returnType: TypeDefinition,
): DynamicType.Unloaded<out Any>? {
    return ByteBuddy()
        .makeInterface()
        .name(interfaceName)
        .defineMethod(methodName, returnType, Modifier.PUBLIC)
        .withParameters(parameters)
        .withoutCode()
        .make()
}

fun loadType(
    unloadedType: DynamicType.Unloaded<out Any>
): Class<out Any>? {
    return unloadedType.load(
        unloadedType.javaClass.classLoader,
        ClassLoadingStrategy.Default.WRAPPER
    ).loaded
}

private fun String.toUpperCaseFirstLetter(): String {
    return if (isEmpty()) this else (this[0].uppercaseChar() + this.substring(1))
}