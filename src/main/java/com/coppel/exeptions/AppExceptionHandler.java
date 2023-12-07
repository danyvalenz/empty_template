package com.coppel.exeptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.coppel.dto.ApiResponseDTO;
import com.coppel.dto.Meta;

import com.coppel.util.LogFile;
import com.coppel.util.MetaGenerator;
import com.coppel.webclient.UnexpectedResponseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static com.coppel.util.AppMessages.*;

/**
 * Clase para manejo de excepciones no controladas.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class AppExceptionHandler {

    private final MetaGenerator metaGenerator;

    private final ObjectMapper objectMapper = new ObjectMapper();


    /*
     * Cualquier excepcion tipo NullPointer que ocurra
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleNullPointer(NullPointerException exception) {
        ApiResponseDTO<Void> apiResponseDTO = new ApiResponseDTO<>();

        LogFile.logExcepcion(exception);

        apiResponseDTO.setMeta(metaGenerator.crearMetaObject(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR, exception.getMessage()));
        return new ResponseEntity<>(apiResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /*
     * Si un paramatro no llega por requestParam
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleMissingRequestParam(MissingServletRequestParameterException exception) {
        ApiResponseDTO<Void> apiResponseDTO = new ApiResponseDTO<>();

        LogFile.logExcepcion(exception);

        apiResponseDTO.setMeta(metaGenerator.crearMetaObject(HttpStatus.BAD_REQUEST, SERVER_CLIENT_ERROR, exception.getMessage()));
        return new ResponseEntity<>(apiResponseDTO, HttpStatus.BAD_REQUEST);
    }


    /*
     * Cuando mandas un parametro con tipo de dato incorrecto
     */
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleTypeMismatch(TypeMismatchException exception) {
        ApiResponseDTO<Object> apiResponseDTO = new ApiResponseDTO<>();

        LogFile.logExcepcion(exception);

        apiResponseDTO.setMeta(metaGenerator.crearMetaObject(HttpStatus.BAD_REQUEST, DATA_TYPE_BAD_REQUEST_MESSAGE));
        return new ResponseEntity<>(apiResponseDTO, HttpStatus.BAD_REQUEST);
    }

    /*
     * Cuando una o mas regla de RequestBody no se cumple
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        ApiResponseDTO<Object> apiResponseDTO = new ApiResponseDTO<>();
        List<String> listaErrores = new ArrayList<>();

        StringBuilder sb = new StringBuilder();

        LogFile.logExcepcion(exception);

        exception.getAllErrors().forEach(
                objectError -> {
                    listaErrores.add(
                            String.format("%s - %s",
                                    Objects.requireNonNull(objectError.getCodes())[1],
                                    objectError.getDefaultMessage())
                    );

                    sb.append(objectError.getDefaultMessage());
                    sb.append(". ");
                });

        String messageMeta = sb.substring(0, sb.length() - 2);
        apiResponseDTO.setMeta(metaGenerator.crearMetaObject(HttpStatus.BAD_REQUEST, messageMeta, exception.getMessage()));
        apiResponseDTO.setData(listaErrores);
        return new ResponseEntity<>(apiResponseDTO, HttpStatus.BAD_REQUEST);
    }

    /*
     * Cuando uno o mas campos del body tienen mal formato
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        ApiResponseDTO<Object> apiResponseDTO = new ApiResponseDTO<>();

        LogFile.logExcepcion(exception);

        apiResponseDTO.setMeta(metaGenerator.crearMetaObject(HttpStatus.BAD_REQUEST, DATA_TYPE_BAD_REQUEST_MESSAGE));
        return new ResponseEntity<>(apiResponseDTO, HttpStatus.BAD_REQUEST);
    }

    /*
     * Cuando un servicio no responde (timeout)
     */
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleServiceTimeout(ResourceAccessException ex) {
        // Servicio da timeout o no se tiene acceso
        LogFile.logExcepcion(ex);
        final Meta meta = metaGenerator.crearMetaObject(HttpStatus.GATEWAY_TIMEOUT, ERROR_TIMEOUT, ex.getMessage());

        return new ResponseEntity<>(new ApiResponseDTO<>(meta, null), HttpStatus.GATEWAY_TIMEOUT);
    }


    /*
     * Cuando un servicio no esta disponible
     */
    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleWebClientRequest(WebClientRequestException ex) {
        // Servicio da timeout o no se tiene acceso
        LogFile.logExcepcion(ex);

        final Meta meta = metaGenerator.crearMetaObject(HttpStatus.SERVICE_UNAVAILABLE, ERROR_TIMEOUT, ex.getMessage());
        return new ResponseEntity<>(new ApiResponseDTO<>(meta, null), HttpStatus.SERVICE_UNAVAILABLE);
    }

    /*
     * Cuando la respuesta de un servicio consumido no fue la esperada
     */
    @ExceptionHandler(UnexpectedResponseException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleUnexpectedResponse(UnexpectedResponseException ex) {
        final Meta meta = metaGenerator.crearMetaObject(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        return new ResponseEntity<>(new ApiResponseDTO<>(meta, null), HttpStatus.SERVICE_UNAVAILABLE);
    }

    /*
     * Cuando falla una peticion
     */
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleWebClientResponseException(WebClientResponseException ex) {
        final String responseString = ex.getResponseBodyAsString();

        LogFile.logExcepcion(ex);

        String message;
        final int statusCode = ex.getStatusCode().value();
        if (statusCode < 500) {
            message = SERVER_CLIENT_ERROR;
        } else if (statusCode == 500) {
            message = INTERNAL_SERVER_ERROR;
        } else {
            message = SERVER_ERROR;
        }

        final JSONObject responseJSON;
        try {
            responseJSON = new JSONObject(responseString);
            if (responseJSON.getJSONObject("meta").has("message")) {
                message = objectMapper.convertValue(responseJSON.getJSONObject("meta").get("message").toString(), String.class);
            } else {
                // Si no tiene mensaje
                LogFile.logRequest(ex);
            }
        } catch (JSONException  e) {
            // Si no se puede castear a JSON
            LogFile.logRequest(ex);
        }

        final Meta meta = metaGenerator.crearMetaObject(ex.getStatusCode(), message, ex.getMessage());
        return new ResponseEntity<>(new ApiResponseDTO<>(meta, null), ex.getStatusCode());
    }
}