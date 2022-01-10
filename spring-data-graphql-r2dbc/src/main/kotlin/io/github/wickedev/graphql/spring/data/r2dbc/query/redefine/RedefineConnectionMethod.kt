package io.github.wickedev.graphql.spring.data.r2dbc.query.redefine

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.graphql.types.ConnectionParam
import net.bytebuddy.description.type.TypeDefinition
import net.bytebuddy.description.type.TypeDescription
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.core.RepositoryMetadata
import reactor.core.publisher.Flux
import java.lang.reflect.Method

fun redefineConnectionMethod(method: Method, metadata: RepositoryMetadata): Method {
    val interfaceName = redefineInterfaceName(method)
    val methodName = redefineMethodName(method)
    val parameters = redefineParameters(method)
    val returnType = redefineReturnType(metadata)

    val unloadedType = makeType(interfaceName, methodName, parameters, returnType)
        ?: throw Error("unloaded type is null")

    val type = loadType(unloadedType)
        ?: throw Error("loaded type is null")

    return type.getMethod(methodName, *parametersForGetMethod(method))
}

private fun redefineMethodName(method: Method): String {
    return method.name
        .replace("connection", "findAll")
}

private fun redefineParameters(method: Method): List<TypeDescription.Generic> {
    return method.parameters.filter { p ->
        p.type != DataFetchingEnvironment::class.java
                && !ConnectionParam::class.java.isAssignableFrom(p.type)
    }
        .map {
            TypeDescription.Generic.Builder
                .rawType(it.type)
                .build()
        } + listOf(
        TypeDescription.Generic.Builder
            .rawType(Pageable::class.java)
            .build()
    )
}

private fun redefineReturnType(metadata: RepositoryMetadata): TypeDefinition {
    val returnObjectType = metadata.domainType
    return TypeDescription.Generic.Builder
        .parameterizedType(Flux::class.java, returnObjectType).build()
}

private fun parametersForGetMethod(method: Method): Array<Class<*>> {
    return (method.parameters.filter { p ->
        p.type != DataFetchingEnvironment::class.java
                && !ConnectionParam::class.java.isAssignableFrom(p.type)
    }
        .map { it.type } + listOf(Pageable::class.java))
        .toTypedArray()
}