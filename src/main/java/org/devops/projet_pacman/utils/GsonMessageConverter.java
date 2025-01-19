package org.devops.projet_pacman.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.util.MimeType;

import java.nio.charset.StandardCharsets;

public class GsonMessageConverter extends AbstractMessageConverter {

    private final Gson gson;

    public GsonMessageConverter() {
        // On indique qu'on gère "application/json"
        super(new MimeType("application", "json", StandardCharsets.UTF_8));
        this.gson = new Gson();
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        // Autorise la conversion pour toutes les classes
        // ou mets une condition si tu veux
        return true;
    }

    @Override
    protected Object convertFromInternal(Message<?> message, Class<?> targetClass, Object conversionHint) {
        byte[] payload = (byte[]) message.getPayload();
        String json = new String(payload, StandardCharsets.UTF_8);

        try {
            return gson.fromJson(json, targetClass);
        } catch (JsonSyntaxException e) {
            throw new MessageConversionException("Erreur de parsing JSON (Gson)", e);
        }
    }

    @Override
    protected Object convertToInternal(Object payload, MessageHeaders headers, Object conversionHint) {
        // Conversion d'un objet -> JSON -> byte[]
        String json = gson.toJson(payload);
        return json.getBytes(StandardCharsets.UTF_8);
    }
}
