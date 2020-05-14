package com.sree.rampup.users.util;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static com.sree.rampup.users.util.JsonMarshaller.toJSON;

/**
 * MockMVC Wrapper that implements customized POST/GET/PUT/DELETE
 * Author: Sreedhar Meduru
 */
public class TestMockMvc {
    MockMvc mockMvc;

    public TestMockMvc(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    /**
     * Post Request
     * @param url
     * @param <T>
     * @return
     */
    public <T> ModelProcessor post(String url) {
        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .accept(MediaType.APPLICATION_JSON);

        return (model) -> {
            return (ResultMatcher matcher) -> {
                request.content(toJSON(model));
                return mockMvc.perform(request)
                        .andExpect(matcher)
                        .andReturn();
            };
        };
    }

    /**
     * Get Request
     * @param url
     * @param parameterMap
     * @param <T>
     * @return
     */
    public <T> ResponseValidator get(String url, Map<String, String> parameterMap) {
        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(url)
                .contentType(MediaType.APPLICATION_JSON);
        if (parameterMap != null) {
            parameterMap.forEach(  (name, value) -> request.param(name, value));
        }
        request.characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON);

        return resultMatcher -> mockMvc.perform(request)
                .andExpect(resultMatcher)
                .andReturn();
    }

    /**
     * Get Request
     * @param url
     * @return
     */
    public ResponseValidator get(String url) {
        return get(url, null);
    }

    /**
     * Put Request
     * @param url
     * @return
     */
    public ModelProcessor put(String url) {
        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .accept(MediaType.APPLICATION_JSON);

        return (model) -> {
            return (ResultMatcher matcher) -> {
                request.content(toJSON(model));
                return mockMvc.perform(request)
                        .andExpect(matcher)
                        .andReturn();
            };
        };
    }

    /**
     * Delete Request
     * @param url
     * @param <T>
     * @return
     */
    public <T> ResponseValidator delete(String url) {
        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_JSON);

        request.characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON);

        return resultMatcher -> mockMvc.perform(request)
                .andExpect(resultMatcher)
                .andReturn();
    }

    /**
     * Functional interface for model processor
     */
    @FunctionalInterface
    public interface ModelProcessor {
        ResponseValidator withModel(Object model) throws Exception;
    }

    /**
     * Functional interface for response validation
     */
    @FunctionalInterface
    public interface ResponseValidator {
         MvcResult thenValidate(ResultMatcher matcher) throws Exception;
    }

}
