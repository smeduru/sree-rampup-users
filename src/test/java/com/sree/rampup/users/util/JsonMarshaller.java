package com.sree.rampup.users.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;

/**
 * Class for marshalling/unmarshalling models to json strings and vice-versa.
 */
@Slf4j
public class JsonMarshaller {
    private static final Logger logger = LoggerFactory.getLogger(JsonMarshaller.class);

    /**
     * Converts the given model to json string
     * @param object
     * @param <T>
     * @return
     * @throws JsonProcessingException
     */
    public static <T> String toJSON(final T object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json =  mapper.writeValueAsString(object);
        logger.debug("Sending:\n{}\n", json);
        return json;
    }

    /**
     * Parses the given JSON string and returns an object.
     * @param jsonString
     * @param tClass
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T parseJSON(String jsonString, Class<T> tClass) throws IOException {
        logger.debug("Returned:\n{}\n", jsonString);
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonString, tClass);
    }

    /**
     * Converts JSON string to a collections using generic types.
     * @param jsonString
     * @param typeReference
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T parseJSON(String jsonString, TypeReference<T> typeReference) throws IOException {
        logger.debug("Returned:\n{}\n", jsonString);
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonString, typeReference);
    }
}
