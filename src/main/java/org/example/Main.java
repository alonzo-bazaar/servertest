package org.example;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            Router router = Router.create(8080, 10);

            router.bindEndpoint("/exchange/test", (HttpExchange ex) -> {
                Response r = new Response(200).bodyAdd("test for exchange");
                r.writeToExchange(ex);
                ex.close();
            });


            router.bindFunction("/function/test", (Request req) ->
                    new Response(200).bodyAdd("test for function"));

            router.bindFunction("/add", (Request req) -> {
                List<String> params = req.getRemainingUrlPath();
                int acc = 0;
                for(String s : params) {
                    try {
                        acc += Integer.parseInt(s);
                    } catch(NumberFormatException nfe) {
                        return new Response(400).bodyAdd("\"" + s + "\" is not valid number, cannot perform addition");
                    }
                }
                return new Response(200).bodyAdd(Integer.toString(acc));
            });

            router.bindEndpoint("/debugging/print", new LoggingHttpHandler());
            router.bindFunction("/debugging/reload", (Request req) -> {
                // can't be fucked implementing a proper reload mechanism rn
                try {
                    router.loadConfig();
                    return new Response(200).bodyAdd("reloaded successfully");
                } catch (IOException ex) {
                    return new Response(500).bodyAdd("failed to reload config " + ex.getMessage());
                }
            });

            router.startServer();
        } catch(Throwable t) {
            System.err.println("You fucked up");
            System.err.println(t.toString());
            t.printStackTrace();
        }
    }
}
