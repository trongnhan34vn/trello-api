package com.nhantic.trelloapi.helper;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

@Component
@Slf4j
public class MessageResolver {
    private Map<String, String> messages;
    private static final String MESSAGES_FILE_PATH = "messages.yml";
    @PostConstruct
    public void init() {
        try {
            InputStream is = new ClassPathResource(MESSAGES_FILE_PATH).getInputStream();
            Yaml yaml = new Yaml();
            messages = Map.copyOf(yaml.load(is));
            log.info("Loaded {} messages", messages.size());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load messages.yml", e);
        }
    }

    public String resolve(String code) {
        return messages.getOrDefault(code, code);
    }
}
