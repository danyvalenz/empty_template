package com.coppel.webclient;

import com.coppel.dto.ApiResponseDTO;
import com.coppel.util.LogFile;
import com.coppel.util.PageQuery;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class WebClientConsumer {

    private static final String UNEXPECTED_RESPONSE = "Ocurrió un error al obtener la respuesta, intente de nuevo más tarde. Si el problema persiste favor de comunicarse con mesa de ayuda";

    private final WebClient webClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String stringWithRequestParams(Map<String, Object> requestParams) {
        final StringBuilder sb = new StringBuilder();
        requestParams.forEach((key, object) -> {
            sb.append(key);
            sb.append("=");
            sb.append(object.toString());
            sb.append("&");
        });

        return sb.substring(0, sb.length()-1);
    }

    @SuppressWarnings("unchecked")
    public <T> ApiResponseDTO<PageQuery<T>> getPageResponse(String uri, Class<T> clazz) {
        Gson gson=new Gson();

        final String response = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        final ApiResponseDTO<LinkedTreeMap<String, Object>> apiResponseDTO;
        try {
            apiResponseDTO = gson.fromJson(response, ApiResponseDTO.class);
            final ApiResponseDTO<PageQuery<T>> finalResponse = new ApiResponseDTO<>( apiResponseDTO.getMeta() );
            finalResponse.setData( objectMapper.convertValue(apiResponseDTO.getData(), PageQuery.class) );

            finalResponse.getData().setRows( finalResponse.getData().getRows()
                    .stream()
                    .map(o -> objectMapper.convertValue(o, clazz))
                    .toList());

            return finalResponse;

        } catch (Exception e) {
            LogFile.logExcepcion(e);

            throw new UnexpectedResponseException(UNEXPECTED_RESPONSE);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> ApiResponseDTO<List<T>> getListResponse(String uri, Class<T> clazz) {
        Gson gson=new Gson();

        final String response = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            final ApiResponseDTO<LinkedTreeMap<String, Object>> apiResponseDTO = gson.fromJson(response, ApiResponseDTO.class);

            final ApiResponseDTO<List<T>> finalResponse = new ApiResponseDTO<>( apiResponseDTO.getMeta() );
            finalResponse.setData( objectMapper.convertValue(apiResponseDTO.getData(), List.class) );

            finalResponse.setData( finalResponse.getData()
                    .stream()
                    .map(o -> objectMapper.convertValue(o, clazz))
                    .toList());
            return finalResponse;

        } catch (Exception e) {
            LogFile.logExcepcion(e);

            throw new UnexpectedResponseException(UNEXPECTED_RESPONSE);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> ApiResponseDTO<T> getObjectResponse(String uri, Class<T> clazz) {
        Gson gson=new Gson();

        final String response = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            final ApiResponseDTO<LinkedTreeMap<String, Object>> apiResponseDTO = gson.fromJson(response, ApiResponseDTO.class);

            final ApiResponseDTO<T> finalResponse = new ApiResponseDTO<>( apiResponseDTO.getMeta() );
            finalResponse.setData( objectMapper.convertValue(apiResponseDTO.getData(), clazz) );

            return finalResponse;

        } catch (Exception e) {
            LogFile.logExcepcion(e);

            throw new UnexpectedResponseException(UNEXPECTED_RESPONSE);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> ApiResponseDTO<T> patchObjectResponse(String uri, Class<T> clazz, Object requestBody) {
        Gson gson=new Gson();

        final String response = webClient.patch()
                .uri(uri)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            final ApiResponseDTO<LinkedTreeMap<String, Object>> apiResponseDTO = gson.fromJson(response, ApiResponseDTO.class);

            final ApiResponseDTO<T> finalResponse = new ApiResponseDTO<>( apiResponseDTO.getMeta() );
            finalResponse.setData( objectMapper.convertValue(apiResponseDTO.getData(), clazz) );

            return finalResponse;
        } catch (Exception e) {
            LogFile.logExcepcion(e);

            throw new UnexpectedResponseException(UNEXPECTED_RESPONSE);
        }
    }

    @SuppressWarnings("unchecked")
    public ApiResponseDTO<Void> patch(String uri) {
        Gson gson=new Gson();

        final String response = webClient.patch()
                .uri(uri)
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return gson.fromJson(response, ApiResponseDTO.class);
    }

    @SuppressWarnings("unchecked")
    public ApiResponseDTO<Void> patch(String uri, Object requestBody) {
        Gson gson=new Gson();

        final String response = webClient.patch()
                .uri(uri)
                .acceptCharset(StandardCharsets.UTF_8)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return gson.fromJson(response, ApiResponseDTO.class);
    }

    @SuppressWarnings("unchecked")
    public ApiResponseDTO<Void> post(String uri, Object requestBody) {
        Gson gson=new Gson();

        final String response = webClient.post()
                .uri(uri)
                .acceptCharset(StandardCharsets.UTF_8)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return gson.fromJson(response, ApiResponseDTO.class);
    }

    @SuppressWarnings("unchecked")
    public <T> ApiResponseDTO<T> postObjectResponse(String uri, Class<T> clazz, Object requestBody) {
        Gson gson=new Gson();

        final String response = webClient.post()
                .uri(uri)
                .acceptCharset(StandardCharsets.UTF_8)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            final ApiResponseDTO<LinkedTreeMap<String, Object>> apiResponseDTO = gson.fromJson(response, ApiResponseDTO.class);

            final ApiResponseDTO<T> finalResponse = new ApiResponseDTO<>( apiResponseDTO.getMeta() );
            finalResponse.setData( objectMapper.convertValue(apiResponseDTO.getData(), clazz) );

            return finalResponse;

        } catch (Exception e) {
            LogFile.logExcepcion(e);

            throw new UnexpectedResponseException(UNEXPECTED_RESPONSE);
        }
    }

    @SuppressWarnings("unchecked")
    public ApiResponseDTO<Void> delete(String uri) {
        Gson gson=new Gson();

        final String response = webClient.delete()
                .uri(uri)
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return gson.fromJson(response, ApiResponseDTO.class);
    }

    @SuppressWarnings("unchecked")
    public <T> ApiResponseDTO<T> postWithFiles(String uri, MultipartBodyBuilder builder, Class<T> clazz) {
        Gson gson=new Gson();

        final String response = webClient.post()
                .uri(uri)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            assert response != null;
            final String strResponse = new String(response.getBytes(), StandardCharsets.ISO_8859_1);
            final ApiResponseDTO<LinkedTreeMap<String, Object>> apiResponseDTO = gson.fromJson(strResponse, ApiResponseDTO.class);

            final ApiResponseDTO<T> finalResponse = new ApiResponseDTO<>( apiResponseDTO.getMeta() );
            finalResponse.setData( objectMapper.convertValue(apiResponseDTO.getData(), clazz) );

            return finalResponse;

        } catch (Exception e) {
            LogFile.logExcepcion(e);

            throw new UnexpectedResponseException(UNEXPECTED_RESPONSE);
        }
    }
}