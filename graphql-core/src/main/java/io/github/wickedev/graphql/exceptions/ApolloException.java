package io.github.wickedev.graphql.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import graphql.*;
import graphql.execution.ResultPath;
import graphql.language.SourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static graphql.Assert.assertNotNull;

public class ApolloException extends GraphQLException implements GraphQLError, ErrorClassification {

    private final List<Object> path;
    private final List<SourceLocation> locations;
    private final Map<String, Object> extensions;

    public ApolloException(String code, String message, ResultPath path, SourceLocation sourceLocation) {
        super(message);
        this.path = assertNotNull(path).toList();
        this.locations = Collections.singletonList(sourceLocation);
        this.extensions = Collections.singletonMap(code, new ErrorExtension(this));

    }

    @Override
    public List<SourceLocation> getLocations() {
        return locations;
    }

    @Override
    public ErrorClassification getErrorType() {
        return ErrorType.DataFetchingException;
    }

    @Override
    public List<Object> getPath() {
        return path;
    }

    @JsonIgnore
    @Override
    public Map<String, Object> getExtensions() {
        return extensions;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        return GraphqlErrorHelper.equals(this, o);
    }

    @Override
    public int hashCode() {
        return GraphqlErrorHelper.hashCode(this);
    }
}
