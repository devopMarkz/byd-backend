package com.rideprofit.infraestrutura.web;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public final class RespostaErro {

    private static final DateTimeFormatter FORMATO_TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private RespostaErro() {
    }

    public static Map<String, Object> criar(int status, String message, String path) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("status", status);
        corpo.put("message", message);
        corpo.put("path", path);
        corpo.put("timestamp", LocalDateTime.now().format(FORMATO_TIMESTAMP));
        return corpo;
    }
}
