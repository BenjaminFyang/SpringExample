package com.example.springreactive.s03;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Component
@Order(-2)
public class C05GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    public C05GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes,
                                             ResourceProperties resourceProperties,
                                             ApplicationContext applicationContext,
                                             ServerCodecConfigurer configurer) {
        super(errorAttributes, resourceProperties, applicationContext);
        this.setMessageWriters(configurer.getWriters());
    }

    @RequiredArgsConstructor
    @Data
    private static class Error {
        final List<InvalidField> invalidFields;
        final List<String> errors;
    }

    @RequiredArgsConstructor
    @Data
    private static class InvalidField {
        final String name;
        final String message;
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), (serverRequest -> {
            Throwable throwable = errorAttributes.getError(serverRequest);
            if (throwable instanceof C04ReactiveControllerHelper.ValidationException) {
                return handleValidationException((C04ReactiveControllerHelper.ValidationException) throwable);
            }
            if (throwable instanceof ResponseStatusException) {
                return handleResponseStatusException((ResponseStatusException) throwable);
            }
            log.error("Ops, just caught an unknown exception, " +
                    "please have a look at the stack trace of more details", throwable);
            return ServerResponse.status(INTERNAL_SERVER_ERROR).build();
        }));
    }

    private Mono<ServerResponse> handleResponseStatusException(ResponseStatusException exception) {
        Error error = new Error(null, Collections.singletonList(exception.getReason()));
        return ServerResponse.status(exception.getStatus())
                .bodyValue(error);
    }

    private Mono<ServerResponse> handleValidationException(C04ReactiveControllerHelper.ValidationException exception) {
        Errors errors = exception.getErrors();
        List<InvalidField> invalidFields = errors.getFieldErrors().stream()
                .map(error -> new InvalidField(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        List<String> theErrors = errors.getGlobalErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());
        Error error = new Error(invalidFields, theErrors);
        return ServerResponse.badRequest().bodyValue(error);
    }
}
