package io.github.wickedev

import io.github.wickedev.graphql.annotations.Relation
import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.types.ID
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy
import org.springframework.data.annotation.Id
import reactor.core.publisher.Flux
import java.lang.reflect.Modifier


class User

data class Post(
    @Id override val id: ID,
    @Relation(User::class) val authorId: ID
) : Node


class ByteBuddyTest : DescribeSpec({
    describe("ByteBuddy") {
        it("should be define method") {
            val interfaceName = "Repository${'$'}findAllByAuthorIds"
            val methodName = "findAllByAuthorIds"
            val returnType: TypeDescription.Generic = TypeDescription.Generic.Builder
                .parameterizedType(Flux::class.java, Post::class.java).build()

            val parameters = listOf(
                TypeDescription.Generic.Builder
                    .parameterizedType(Collection::class.java, ID::class.java)
                    .build(),
            )

            val unloadedType = ByteBuddy()
                .makeInterface()
                .name(interfaceName)
                .defineMethod(methodName, returnType, Modifier.PUBLIC)
                .withParameters(parameters)
                .withoutCode()
                .make() ?: throw Error("unloadedType is null")

            val type = unloadedType.load(
                this::class.java.classLoader,
                ClassLoadingStrategy.Default.WRAPPER
            ).loaded ?: throw Error("loaded type is null")

            val method = type.getMethod(methodName, Collection::class.java)

            method shouldNotBe null
            type.name shouldBe interfaceName
            val methods = type.methods
            methods[0].name shouldBe methodName
            methods[0].returnType shouldBe Flux::class.java
            methods[0].genericReturnType.typeName.contains(Post::class.java.name) shouldBe true
        }
    }
})