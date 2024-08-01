package org.example;

import com.sun.net.httpserver.Headers;

import java.io.InputStream;
import java.util.List;

public class Request {
    final String method;
    final List<String> remainingPath; // url prefix removed by router to facilitate rebasing in the resource tree
    final Headers headers;
    final InputStream body;
    public Request(String method, List<String> remainingUrlPath ,Headers headers, InputStream body) {
        this.method = method;
        this.headers = headers;
        this.body = body;
        this.remainingPath = remainingUrlPath;
    }
    String getMethod() { return method; }
    Headers getHeaders() { return headers; }
    InputStream getBody() { return body; }
    List<String> getRemainingUrlPath() { return remainingPath; }
}
