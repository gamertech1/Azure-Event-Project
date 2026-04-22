package com.example.eventjava.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;

@Controller("/webhookshookdeck")
public class HookdeckController {

    @Post
    public HttpResponse<?> handle(@Body String body) {
        return HttpResponse.ok();
    }
}
