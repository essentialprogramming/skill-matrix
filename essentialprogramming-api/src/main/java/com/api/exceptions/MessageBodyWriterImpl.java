package com.api.exceptions;

import com.util.web.JsonResponse;
import org.springframework.web.client.HttpClientErrorException;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;

@Provider
@Produces(MediaType.APPLICATION_OCTET_STREAM)
public class MessageBodyWriterImpl implements MessageBodyWriter<JsonResponse> {

    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return JsonResponse.class.equals(type);
    }

    @Override
    public void writeTo(JsonResponse jsonResponse, Class<?> aClass, Type type, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> header,
                        OutputStream outputStream) throws IOException, WebApplicationException {

        header.replace("content-type", Collections.singletonList(MediaType.APPLICATION_JSON));

        Writer writer = new PrintWriter(outputStream);

        String message = String.valueOf(jsonResponse.get("message"));
        String code = String.valueOf(jsonResponse.get("code"));

        writer.write("{\"message\": \"" + message + "\", " +
                "\"code\": \"" + code + "\"}");

        writer.flush();
        writer.close();
    }
}
