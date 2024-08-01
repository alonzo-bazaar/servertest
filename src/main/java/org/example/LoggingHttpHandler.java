package org.example;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.OutputStream;
import java.util.Map;

public class LoggingHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            System.out.println("Stupid Little Information about the exchange and the request");
            System.out.println();
            System.out.println("the context is : " + exchange.getHttpContext());
            System.out.println("its contents are : ");
            printDebugContext(exchange.getHttpContext());
            System.out.println();
            System.out.println("protocol is : " + exchange.getProtocol());
            System.out.println();
            System.out.println("local address is : " + exchange.getLocalAddress());
            System.out.println("remote address : " + exchange.getRemoteAddress());
            System.out.println();
            System.out.println("request method is : " + exchange.getRequestMethod());
            System.out.println("request uri is : " + exchange.getRequestURI());
            System.out.println();
            System.out.println("Request information that's actually used (as listed in the docs)");
            System.out.println("request method is : " + exchange.getRequestMethod());
            System.out.println("request headers are : " + exchange.getRequestHeaders());
            printDebugMap(exchange.getRequestHeaders());
            System.out.println();
            System.out.println("request body is : " + exchange.getRequestBody().readAllBytes().toString());

            // crea una piccola response
            byte[] response = "vorrei comunque dire che madonna troia".getBytes();
            long responseLength = response.length;
            exchange.sendResponseHeaders(200, responseLength);
            OutputStream s = exchange.getResponseBody();
            s.write(response);
            exchange.close();
        }
        catch (Throwable ignored) {}
    }

    private static void printDebugContext(HttpContext context) {
        printDebugMap(context.getAttributes());
    }

    private static<K,V> void printDebugMap(Map<K, V> map) {
        map.keySet().iterator().forEachRemaining(key -> {
            System.out.println("key : " + key + " - val : " + map.get(key));
        });
    }
}
