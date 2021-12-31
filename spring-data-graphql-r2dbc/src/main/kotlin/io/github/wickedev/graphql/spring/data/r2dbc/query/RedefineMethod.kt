package io.github.wickedev.graphql.spring.data.r2dbc.query

import graphql.schema.DataFetchingEnvironment
import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.type.TypeDefinition
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy
import org.springframework.data.repository.core.RepositoryMetadata
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.lang.reflect.Method
import java.lang.reflect.Modifier

fun redefineMethod(method: Method, metadata: RepositoryMetadata): Method {
    return if (method.name.startsWith("find")) {
        redefineFindMethod(method, metadata)
    } else {
        redefineCountExistsMethod(method, metadata)
    }
}

private fun redefineFindMethod(method: Method, metadata: RepositoryMetadata): Method {
    val interfaceName = redefineInterfaceName(method)
    val methodName = redefineFindMethodName(method)
    val parameters = redefineFindParameters(method)
    val returnType = redefineFindReturnType(method, metadata)

    val unloadedType = makeType(interfaceName, methodName, parameters, returnType)
        ?: throw Error("unloaded type is null")

    val type = loadType(unloadedType)
        ?: throw Error("loaded type is null")

    return type.getMethod(methodName, *findParametersForGetMethod(method))
}

private fun redefineCountExistsMethod(method: Method, metadata: RepositoryMetadata): Method {
    val interfaceName = redefineInterfaceName(method)
    val methodName = redefineCountExistsMethodName(method)
    val parameters = redefineCountExistsParameters(method)
    val returnType = redefineCountExistsReturnType(method, metadata)

    val unloadedType = makeType(interfaceName, methodName, parameters, returnType)
        ?: throw Error("unloaded type is null")

    val type = loadType(unloadedType)
        ?: throw Error("loaded type is null")

    return type.getMethod(methodName, *countExistsParametersForGetMethod(method))
}

private fun redefineInterfaceName(method: Method): String {
    return "${method.declaringClass.name}${method.name.toUpperCaseFirstLetter()}${method.parameters.size}"
}

private fun redefineFindMethodName(method: Method): String {
    return "${method.name}In"
        .replace("find", "findDataLoader")
        .replace("And", "InAnd")
}

private fun redefineCountExistsMethodName(method: Method): String {
    return method.name
        .replace("count", "countDataLoader")
        .replace("exists", "existsDataLoader")
}

private fun redefineCountExistsParameters(method: Method): List<TypeDescription> {
    return method.parameters.filter { p -> p.type != DataFetchingEnvironment::class.java }
        .map { TypeDescription.ForLoadedType.of(it.type) }
}

private fun redefineCountExistsReturnType(method: Method, metadata: RepositoryMetadata): TypeDefinition {
    val returnObjectType = metadata.getReturnedDomainClass(method)
    return TypeDescription.Generic.Builder
        .parameterizedType(Mono::class.java, returnObjectType).build()
}

private fun countExistsParametersForGetMethod(method: Method): Array<Class<*>> {
    return method.parameters.filter { p -> p.type != DataFetchingEnvironment::class.java }
        .map { it.type }
        .toTypedArray()
}

private fun redefineFindParameters(method: Method): List<TypeDescription.Generic> {
    return method.parameters.filter { p -> p.type != DataFetchingEnvironment::class.java }
        .map {
            TypeDescription.Generic.Builder
                .parameterizedType(Collection::class.java, it.type)
                .build()
        }
}

private fun redefineFindReturnType(method: Method, metadata: RepositoryMetadata): TypeDefinition {
    val returnObjectType = metadata.getReturnedDomainClass(method)
    return TypeDescription.Generic.Builder
        .parameterizedType(Flux::class.java, returnObjectType).build()
}

private fun findParametersForGetMethod(method: Method): Array<Class<*>> {
    return method.parameters.filter { p -> p.type != DataFetchingEnvironment::class.java }
        .map { Collection::class.java }
        .toTypedArray()
}

private fun makeType(
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

private fun loadType(
    unloadedType: DynamicType.Unloaded<out Any>
): Class<out Any>? {
    return unloadedType.load(
        unloadedType.javaClass.classLoader,
        ClassLoadingStrategy.Default.WRAPPER
    ).loaded
}

private fun String.toUpperCaseFirstLetter(): String {
    return if (isEmpty()) this else (this[0].toUpperCase() + this.substring(1))
}