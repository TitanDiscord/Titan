package me.anutley.titan;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class Config {
    public static Config instance;

    public Config() throws IOException {
        instance = this;
    }


    private ObjectMapper mapper = new ObjectMapper();
    JsonNode config = mapper.readTree(new File("./config.json"));


    public static Config getInstance() {
        return instance;
    }

    public String get(String key) {
        return config.get(key).toString().replace("\"", "");
    }


}
