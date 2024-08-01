package org.example;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Response {
    private final List<byte[]> body = new ArrayList<>(); // buffers data before it's written to the body OutStream
    private final Headers headers = new Headers();
    private final int statusCode;
    long length = 0;

    public Response(int statusCode) {
        this.statusCode=statusCode;
    }
    public Response bodyAdd(String s) {
        byte[] b = s.getBytes();
        body.addLast(b);
        length += b.length;
        return this;
    }
    public Response headerSet(String s, List<String> ls) {
        headers.put(s, ls);
        return this;
    }
    void writeToExchange(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(statusCode, length);
        OutputStream bodyStream = exchange.getResponseBody();
        for(byte[] b : body) {
            bodyStream.write(b);
        }
    }
}
