package io.github.wickedev.graphql.kotlin

import com.expediagroup.graphql.server.spring.GraphQLSchemaConfiguration
import com.expediagroup.graphql.server.spring.execution.SpringGraphQLContextFactory
import com.expediagroup.graphql.server.spring.execution.SpringGraphQLRequestParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.zhokhov.graphql.datetime.*
import graphql.scalars.ExtendedScalars
import io.github.wickedev.graphql.AuthDirectiveWiringFactory
import io.github.wickedev.graphql.AuthSchemaDirectiveWiring
import io.github.wickedev.graphql.parser.JetpackSpringGraphQLRequestParser
import io.github.wickedev.graphql.scalars.*
import io.github.wickedev.graphql.types.ID
import io.github.wickedev.graphql.types.Upload
import io.github.wickedev.spring.reactive.security.IdentifiableUserDetails
import io.github.wickedev.spring.reactive.security.SimpleIdentifiableUserDetails
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.core.userdetails.UserDetails
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URL
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

@Configuration
@Import(
    JetpackNonFederatedSchemaAutoConfiguration::class,
    JetpackFederatedSchemaAutoConfiguration::class,
)
@AutoConfigureBefore(
    GraphQLSchemaConfiguration::class
)
class JetpackGraphQLSchemaConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun authSchemaDirectiveWiring() = AuthSchemaDirectiveWiring()

    @Bean
    @ConditionalOnMissingBean
    fun directiveWiringFactory(authSchemaDirectiveWiring: AuthSchemaDirectiveWiring) =
        AuthDirectiveWiringFactory(authSchemaDirectiveWiring)

    @Bean
    @ConditionalOnMissingBean
    fun springGraphQLRequestParser(objectMapper: ObjectMapper): SpringGraphQLRequestParser =
        JetpackSpringGraphQLRequestParser(objectMapper)

    @Bean
    @ConditionalOnMissingBean
    fun springGraphQLContextFactory(): SpringGraphQLContextFactory<*> = JetpackSpringGraphQLContextFactory()

    @Bean
    @ConditionalOnMissingBean
    fun customScalars(): CustomScalars {
        return CustomScalars.of(
            // Primitive
            ID::class to GraphQLIDScalar,
            Set::class to SetScalar,
            Long::class to ExtendedScalars.GraphQLLong,
            Short::class to ExtendedScalars.GraphQLShort,
            Byte::class to ExtendedScalars.GraphQLByte,
            BigDecimal::class to ExtendedScalars.GraphQLBigDecimal,
            BigInteger::class to ExtendedScalars.GraphQLBigInteger,
            Char::class to ExtendedScalars.GraphQLChar,

            // Datetime Scalars
            Date::class to DateScalar.create(null),
            Duration::class to DurationScalar.create(null),
            YearMonth::class to YearMonthScalar.create(null),
            LocalDate::class to LocalDateScalar.create(null, true, DateTimeFormatter.ISO_LOCAL_DATE),
            LocalTime::class to LocalTimeScalar.create(null),
            LocalDateTime::class to LocalDateTimeScalar.create(null, true, DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            OffsetTime::class to ExtendedScalars.Time,
            OffsetDateTime::class to OffsetDateTimeScalar.create(null),
            ZonedDateTime::class to ExtendedScalars.DateTime,

            // Util Scalars
            Upload::class to GraphQLUploadScalar,
            UserDetails::class to SkipScalar,
            IdentifiableUserDetails::class to SkipScalar,
            SimpleIdentifiableUserDetails::class to SkipScalar,
            URL::class to ExtendedScalars.Url,
            Locale::class to ExtendedScalars.Locale,
            Object::class to ExtendedScalars.Object,
        )
    }
}
