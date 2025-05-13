package com.finwise.backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    //private final String endpoint = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-pro:generateContent";
    private final String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    public String generarSugerencia(String prompt) {
        try {
            Map<String, Object> request = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt)
                            ))
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<Map> response = restTemplate.exchange(
                    endpoint + "?key=" + apiKey,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                return parts.get(0).get("text").toString();
            }

            return "No se pudo generar una sugerencia en este momento.";

        } catch (Exception e) {
            System.err.println("❌ Error al llamar a la API de Gemini: " + e.getMessage());
            return "❌ Error inesperado al generar la sugerencia. Intenta más tarde.";
        }
    }
}
