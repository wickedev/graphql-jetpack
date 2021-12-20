package io.github.wickedev.graphql.spring.data.r2dbc.configuration

import io.github.wickedev.graphql.spring.data.r2dbc.factory.GraphQLR2dbcRepositoryFactoryBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryFactoryBean
import org.springframework.data.repository.config.DefaultRepositoryBaseClass
import org.springframework.data.repository.query.QueryLookupStrategy
import java.lang.annotation.Inherited
import kotlin.reflect.KClass


@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
@Import(
    GraphQLR2dbcRepositoriesRegistrar::class
)
annotation class EnableGraphQLR2dbcRepositories(
    /**
     * Alias for the [.basePackages] attribute. Allows for more concise annotation declarations e.g.:
     * `@EnableR2dbcRepositories("org.my.pkg")` instead of
     * `@EnableR2dbcRepositories(basePackages="org.my.pkg")`.
     */
    vararg val value: String = [],
    /**
     * Base packages to scan for annotated components. [.value] is an alias for (and mutually exclusive with) this
     * attribute. Use [.basePackageClasses] for a type-safe alternative to String-based package names.
     */
    val basePackages: Array<String> = [],
    /**
     * Type-safe alternative to [.basePackages] for specifying the packages to scan for annotated components. The
     * package of each class specified will be scanned. Consider creating a special no-op marker class or interface in
     * each package that serves no purpose other than being referenced by this attribute.
     */
    val basePackageClasses: Array<KClass<*>> = [],
    /**
     * Specifies which types are eligible for component scanning. Further narrows the set of candidate components from
     * everything in [.basePackages] to everything in the base packages that matches the given filter or filters.
     */
    val includeFilters: Array<ComponentScan.Filter> = [],
    /**
     * Specifies which types are not eligible for component scanning.
     */
    val excludeFilters: Array<ComponentScan.Filter> = [],
    /**
     * Returns the postfix to be used when looking up custom repository implementations. Defaults to Impl. So
     * for a repository named `PersonRepository` the corresponding implementation class will be looked up scanning
     * for `PersonRepositoryImpl`.
     *
     * @return
     */
    val repositoryImplementationPostfix: String = "Impl",
    /**
     * Configures the location of where to find the Spring Data named queries properties file. Will default to
     * `META-INF/r2dbc-named-queries.properties` if not configured otherwise.
     *
     * @return
     */
    val namedQueriesLocation: String = "",
    /**
     * Returns the key of the [QueryLookupStrategy] to be used for lookup queries for query methods. Defaults to
     * [Key.CREATE_IF_NOT_FOUND].
     *
     * @return
     */
    val queryLookupStrategy: QueryLookupStrategy.Key = QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND,
    /**
     * Returns the [FactoryBean] class to be used for each repository instance. Defaults to
     * [R2dbcRepositoryFactoryBean].
     *
     * @return
     */
    val repositoryFactoryBeanClass: KClass<*> = GraphQLR2dbcRepositoryFactoryBean::class,
    /**
     * Configure the repository base class to be used to create repository proxies for this particular configuration.
     *
     * @return
     */
    val repositoryBaseClass: KClass<*> = DefaultRepositoryBaseClass::class,
    /**
     * Configures the name of the [org.springframework.data.r2dbc.core.DatabaseClient] bean to be used with the
     * repositories detected.
     *
     * @return
     * @see .entityOperationsRef
     */
    @Deprecated("since 1.2, in favor of {@link #entityOperationsRef()}.") val databaseClientRef: String = "",
    /**
     * Configures the name of the [org.springframework.data.r2dbc.core.R2dbcEntityOperations] bean to be used with
     * the repositories detected. Used as alternative to [.databaseClientRef] to configure an access strategy when
     * using repositories with different database systems/dialects. If this attribute is set, then
     * [.databaseClientRef] is ignored.
     *
     * @return
     * @since 1.1.3
     */
    val entityOperationsRef: String = "r2dbcEntityTemplate",
    /**
     * Configures whether nested repository-interfaces (e.g. defined as inner classes) should be discovered by the
     * repositories infrastructure.
     */
    val considerNestedRepositories: Boolean = false
)