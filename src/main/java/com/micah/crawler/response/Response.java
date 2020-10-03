package com.micah.crawler.response;

import com.micah.crawler.request.Request;
import lombok.Getter;

import java.io.InputStream;

/**
 * Response Class
 *
 * @author Micah
 * @date 2020/10/3
 */
public class Response {

    @Getter
    private Request request;
    private Body    body;

    public Response(Request request, InputStream inputStream) {
        this.request = request;
        this.body = new Body(inputStream, request.charset());
    }

    public Body body() {
        return body;
    }

}
