package com.coppel.filters;

import com.coppel.config.AppConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Order(value = 1)
@Slf4j
@RequiredArgsConstructor
public class AppFilter implements Filter {

    private final AppConfig config;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        final HttpServletResponse res = (HttpServletResponse) response;
        final HttpServletRequest req = (HttpServletRequest) request;

        res.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        res.setHeader("Expect-CT", "max-age=3600, enforce");
        res.setHeader("Content-Security-Policy", "script-src 'self' http://cedisdev.coppel.io:20541/dev/tms-apiux-satmanifestmanager;");

        ContentCachingResponseWrapper resp = new ContentCachingResponseWrapper(res);

        if (req.getMethod().equals("OPTIONS") || config.isIgnoreSession()) {
            filterChain.doFilter(request, response);
        } else {
            // Validar token de sesión
            // Pendiente

            filterChain.doFilter(request, resp);
        }

        // Le pone el status code a la respuesta
        byte[] responseBody = resp.getContentAsByteArray();
        String strResponse = new String(responseBody, StandardCharsets.UTF_8);

        try {
            final JSONObject jsonObject = objectMapper.convertValue(strResponse, JSONObject.class);

            res.setStatus( jsonObject.getJSONObject("meta").getInt("statusCode") );
        } catch (IllegalArgumentException | JSONException e) {
            log.warn("The response haven't Meta's structure");
        }

        resp.copyBodyToResponse();
    }

}
