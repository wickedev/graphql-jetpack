package io.github.wickedev.graphql.spring.data.r2dbc.query.redefine

import graphql.schema.DataFetchingEnvironment
import net.bytebuddy.description.type.TypeDefinition
import net.bytebuddy.description.type.TypeDescription
import org.springframework.data.repository.core.RepositoryMetadata
import reactor.core.publisher.Mono
import java.lang.reflect.Method


fun redefineCountExistsMethod(method: Method, metadata: RepositoryMetadata): Method {
    val interfaceName = redefineInterfaceName(method)
    val methodName = redefineMethodName(method)
    val parameters = redefineParameters(method)
    val returnType = redefineReturnType(method, metadata)

    val unloadedType = makeType(interfaceName, methodName, parameters, returnType)
        ?: throw Error("unloaded type is null")

    val type = loadType(unloadedType)
        ?: throw Error("loaded type is null")

    return type.getMethod(methodName, *parametersForGetMethod(method))
}

private fun redefineMethodName(method: Method): String {
    return method.name
        .replace("count", "countDataLoader")
        .replace("exists", "existsDataLoader")
}

private fun redefineParameters(method: Method): List<TypeDescription> {
    return method.parameters.filter { p -> p.type != DataFetchingEnvironment::class.java }
        .map { TypeDescription.ForLoadedType.of(it.type) }
}

private fun redefineReturnType(method: Method, metadata: RepositoryMetadata): TypeDefinition {
    val returnObjectType = metadata.getReturnedDomainClass(method)
    return TypeDescription.Generic.Builder
        .parameterizedType(Mono::class.java, returnObjectType).build()
}

private fun parametersForGetMethod(method: Method): Array<Class<*>> {
    return method.parameters.filter { p -> p.type != DataFetchingEnvironment::class.java }
        .map { it.type }
        .toTypedArray()
}
