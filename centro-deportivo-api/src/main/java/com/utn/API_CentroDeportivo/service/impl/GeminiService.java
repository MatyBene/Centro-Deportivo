package com.utn.API_CentroDeportivo.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GeminiService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public String generateResponse(String prompt) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return "Error: La API Key de Gemini no está configurada. Por favor, configura la variable de entorno GEMINI_API_KEY.";
        }

        int maxRetries = 3;
        int retryDelayMs = 2000;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                String url = apiUrl + "?key=" + apiKey;

                if (attempt > 1) {
                    log.info("Reintentando petición (intento {}/{})...", attempt, maxRetries);
                } else {
                    log.info("URL de Gemini API: {}", apiUrl);
                    log.info("API Key configurada: {}...{}",
                            apiKey.substring(0, Math.min(8, apiKey.length())),
                            apiKey.length() > 8 ? apiKey.substring(apiKey.length() - 4) : "");
                    log.info("Enviando petición a Gemini API...");
                }

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                Map<String, Object> requestBody = new HashMap<>();

                Map<String, Object> content = new HashMap<>();
                Map<String, String> part = new HashMap<>();
                part.put("text", prompt);
                content.put("parts", List.of(part));

                requestBody.put("contents", List.of(content));

                Map<String, Object> generationConfig = new HashMap<>();
                generationConfig.put("temperature", 0.7);
                generationConfig.put("maxOutputTokens", 1024);
                requestBody.put("generationConfig", generationConfig);

                List<Map<String, Object>> safetySettings = List.of(
                        Map.of("category", "HARM_CATEGORY_HARASSMENT", "threshold", "BLOCK_NONE"),
                        Map.of("category", "HARM_CATEGORY_HATE_SPEECH", "threshold", "BLOCK_NONE"),
                        Map.of("category", "HARM_CATEGORY_DANGEROUS_CONTENT", "threshold", "BLOCK_NONE")
                );
                requestBody.put("safetySettings", safetySettings);

                HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

                ResponseEntity<String> response = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        request,
                        String.class
                );

                if (response.getStatusCode() == HttpStatus.OK) {
                    log.info("Respuesta exitosa de Gemini API");
                    return parseGeminiResponse(response.getBody());
                } else {
                    log.error("Error en la respuesta de Gemini: {}", response.getStatusCode());
                    return "Lo siento, no pude procesar tu solicitud en este momento. (Error: " + response.getStatusCode() + ")";
                }

            } catch (org.springframework.web.client.HttpServerErrorException.ServiceUnavailable e) {
                // Error 503 - Servidor sobrecargado
                if (attempt < maxRetries) {
                    log.warn("Servidor sobrecargado (503). Esperando {} ms antes de reintentar...", retryDelayMs);
                    try {
                        Thread.sleep(retryDelayMs);
                        retryDelayMs *= 2; // Espera exponencial: 2s, 4s, 8s...
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Interrupción durante el reintento");
                        break;
                    }
                } else {
                    log.error("Servidor de Gemini sobrecargado después de {} intentos", maxRetries);
                    return "El servidor de Gemini está experimentando alta demanda en este momento. Por favor, intenta de nuevo en unos segundos.";
                }
            } catch (Exception e) {
                log.error("Error al llamar a Gemini API (intento {}): {}", attempt, e.getMessage());
                if (attempt >= maxRetries) {
                    return "Lo siento, ocurrió un error al procesar tu mensaje. Por favor intenta de nuevo en unos momentos.";
                }
            }
        }

        return "No se pudo obtener respuesta de Gemini después de varios intentos.";
    }

    private String parseGeminiResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);

            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode firstCandidate = candidates.get(0);

                String finishReason = firstCandidate.path("finishReason").asText("");
                if ("SAFETY".equals(finishReason)) {
                    log.warn("Respuesta bloqueada por filtros de seguridad de Gemini");
                    return "Lo siento, no puedo dar consejos médicos específicos. " +
                            "Sin embargo, te recomiendo que consultes con nuestros instructores para encontrar " +
                            "actividades de bajo impacto como yoga, pilates o natación que pueden ser beneficiosas. " +
                            "Es importante que consultes con un profesional de la salud antes de comenzar cualquier actividad física.";
                }

                JsonNode content = firstCandidate.path("content");
                JsonNode parts = content.path("parts");

                if (parts.isArray() && parts.size() > 0) {
                    return parts.get(0).path("text").asText();
                }
            }

            log.warn("No se pudo parsear la respuesta de Gemini: {}", responseBody);
            return "No se pudo obtener una respuesta válida.";
        } catch (Exception e) {
            log.error("Error al parsear respuesta de Gemini", e);
            return "Error al procesar la respuesta.";
        }
    }
}
