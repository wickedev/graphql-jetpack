package io.github.wickedev.graphql.spring.data.r2dbc.query.redefine

import graphql.schema.DataFetchingEnvironment
import net.bytebuddy.description.type.TypeDefinition
import net.bytebuddy.description.type.TypeDescription
import org.springframework.data.repository.core.RepositoryMetadata
import reactor.core.publisher.Flux
import java.lang.reflect.Method

fun redefineFindMethod(method: Method, metadata: RepositoryMetadata): Method {
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
    return "${method.name}In"
        .replace("find", "findDataLoader")
        .replace("And", "InAnd")
}

private fun redefineParameters(method: Method): List<TypeDescription.Generic> {
    return method.parameters.filter { p -> p.type != DataFetchingEnvironment::class.java }
        .map {
            TypeDescription.Generic.Builder
                .parameterizedType(Collection::class.java, it.type)
                .build()
        }
}

private fun redefineReturnType(method: Method, metadata: RepositoryMetadata): TypeDefinition {
    val returnObjectType = metadata.getReturnedDomainClass(method)
    return TypeDescription.Generic.Builder
        .parameterizedType(Flux::class.java, returnObjectType).build()
}

private fun parametersForGetMethod(method: Method): Array<Class<*>> {
    return method.parameters.filter { p -> p.type != DataFetchingEnvironment::class.java }
        .map { Collection::class.java }
        .toTypedArray()
}