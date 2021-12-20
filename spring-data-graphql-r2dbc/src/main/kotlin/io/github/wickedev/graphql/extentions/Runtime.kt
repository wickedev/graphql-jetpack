package io.github.wickedev.graphql.extentions

import io.github.wickedev.graphql.interfaces.Node
import io.github.wickedev.graphql.scalars.ID
import kotlin.reflect.KClass
import kotlin.reflect.full.findParameterByName
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.memberFunctions

interface NodeCopy {
    operator fun invoke(node: Node, id: ID): Node
}

val cache = mutableMapOf<KClass<*>, NodeCopy>()

@Suppress("UNCHECKED_CAST")
fun <T : Node> copyWithId(node: T, id: ID): T {
    if (!node::class.isData) {
        println(node)
        throw Error("copyWithId is only supported for data classes")
    }

    return cache.computeIfAbsent(node::class) {
        return@computeIfAbsent object : NodeCopy {
            val copy = node::class.memberFunctions.first { it.name == "copy" }
            val instanceParam = copy.instanceParameter!!
            val idParam = copy.findParameterByName("id")!!

            override fun invoke(node: Node, id: ID): Node {
                return copy.callBy(mapOf(instanceParam to node, idParam to id)) as Node
            }
        }
    }(node, id) as T
}

