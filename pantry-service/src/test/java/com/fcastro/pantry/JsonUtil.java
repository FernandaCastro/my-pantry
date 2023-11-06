package com.fcastro.pantry;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtil {
    public static byte[] toJson(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper.writeValueAsBytes(object);
    }
}
