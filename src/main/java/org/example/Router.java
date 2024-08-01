package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;

import java.net.URI;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.jelly.eval.evaluable.procedure.Procedure;
import org.jelly.eval.runtime.JellyRuntime;

public class Router {
    HttpServer server;
    JellyRuntime jellyRuntime = new JellyRuntime();

    public static Router create(int port, int backlog) throws IOException {
        Router router = new Router();
        router.server = HttpServer.create(new InetSocketAddress(port), backlog);
        router.jellyRuntime.define("router", router);
        try {
            File serverConfig = new File(Objects.requireNonNull(Router.class.getClassLoader().getResource("serverconfig.scm")).getFile());
            router.jellyRuntime.evalFile(serverConfig);
        } catch (NullPointerException ne) {
            throw new FileNotFoundException("cannot find server config file, server not starting");
        }

        return router;
    }

    public void bindEndpoint(String endpoint, HttpHandler handler) {
        server.createContext(endpoint, handler);
    }

    public void bindFunction(String endpoint, Function<Request, Response> callback) {
        server.createContext(endpoint, (HttpExchange exchange) -> {
            // remove matching prefix off of url before giving the request to the handler
            List<String> urlPostfix = removeUriPrefix(endpoint, exchange.getRequestURI());
            Request req = new Request (exchange.getRequestMethod(), urlPostfix, exchange.getRequestHeaders(), exchange.getRequestBody());
            Response res = callback.apply(req);
            res.writeToExchange(exchange);
            exchange.close();
        });
    }

    public static List<String> splitSlash(String s) {
        return Arrays.stream(s.split("/")).filter(a -> !a.isEmpty()).toList();
    }

    public static List<String> removeUriPrefix(String endpoint, URI uri) throws InvalidParameterException {
        List<String> uriPath = splitSlash(uri.getPath());
        List<String> endpointPrefix = splitSlash(endpoint);

        // check prefix
        for(int i = 0; i<endpointPrefix.size(); ++i) {
            if(!endpointPrefix.get(i).equals(uriPath.get(i)))
                throw new InvalidParameterException("endpoint \"" + endpoint + "\" is not a prefix of uri, \"" + uri + "\"'s path something went wrong");
        }

        return uriPath.subList(endpointPrefix.size(), uriPath.size());
    }

    public void startServer() {
        server.start();
    }

    public void stopServer(int timeout) {
        server.stop(timeout);
    }

    // hic sunt jelly
    public void bindJellyFunctionName(String endpoint, String jellyFunction) {
        bindFunction(endpoint, (Request r) -> {
            try {
                return (Response)jellyRuntime.call(jellyFunction, r);
            } catch(Throwable t) {
                return new Response(500).bodyAdd(t.toString());
            }});
    }

    public void bindJellyFunction(String endpoint, Procedure jellyFunction) {
        bindFunction(endpoint, (Request r) -> {
            try {
                return (Response)jellyFunction.call(List.of(r));
            } catch(Throwable t) {
                return new Response(500).bodyAdd(t.toString());
            }});
    }
}
