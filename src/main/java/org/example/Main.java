package org.example;

import com.sun.net.httpserver.HttpExchange;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            Router app = Router.create(8080, 10);

            app.bindEndpoint("/exchange/test", (HttpExchange ex) -> {
                Response r = new Response(200).bodyAdd("test for exchange");
                r.writeToExchange(ex);
                ex.close();
            });


            app.bindFunction("/function/test", (Request req) ->
                    new Response(200).bodyAdd("test for function"));

            app.bindFunction("/add", (Request req) -> {
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

            app.bindEndpoint("/debugging/print", new LoggingHttpHandler());

            app.startServer();
        } catch(Throwable t) {
            System.err.println("You fucked up");
            System.err.println(t.toString());
            t.printStackTrace();
        }
    }
}
