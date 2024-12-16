package com.hfsolution.app.external.telegram;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hfsolution.app.config.restclient.RestClientConfig;

@FeignClient(name = "telegramRestClient", url = "${rest.telegram.url}", configuration = RestClientConfig.class)
public interface TelegramRestClient {

    @GetMapping
    Object sentMonitorMsg(@RequestParam("chat_id") String chatId, @RequestParam("text") String message);
}

