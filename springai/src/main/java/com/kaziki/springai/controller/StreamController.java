package com.kaziki.springai.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.Executors;


@RestController
@RequestMapping("/stream")
public class StreamController {

    private static final String API_KEY = System.getenv("XIAOMI_API_KEY");
    private static final String API_URL = "https://api.xiaomimimo.com/v1/chat/completions";

/*    String requestBody = """
                {
                    "model": "mimo-v2.5",
                    "messages": [
                        {
                            "role": "system",
                            "content": "You are a helpful assistant."
                        },
                        {
                            "role": "user",
                            "content": "你好，介绍下JAVA？"
                        }
                    ],
                    "stream": true
                }
                """;

    HttpClient client = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_URL))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + API_KEY)
            .header("X-DashScope-SSE", "enable")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

    HttpResponse<String> response = client.send(
            request, HttpResponse.BodyHandlers.ofString());


        System.out.println(response.body());*/
    @GetMapping("/sse")
    public SseEmitter sse() throws IOException, InterruptedException {
        if (API_KEY == null || API_KEY.isBlank()) {
            throw new IllegalStateException("环境变量 XIAOMI_API_KEY 未设置");
        }
        //定义sse
        SseEmitter sseEmitter = new SseEmitter(60000L) ;
        Executors.newVirtualThreadPerTaskExecutor().submit(()->{

                try {
                    for (int i = 0; i < 10; i++) sseEmitter.send("message " + i);
                } catch (IOException e) {
                    sseEmitter.completeWithError(e);
                }finally {
                    sseEmitter.complete();
                }
        });

        return sseEmitter;
    }

    @GetMapping(value = "/sse/flux")
    public Flux<String> fluxStream() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(seq -> "Stream element - " + seq);
    }
}
