package com.hfsolution.app.external.telegram;



import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.hfsolution.app.util.AppLog;

import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
public class TelegramRestClientConsumer {

    private final TelegramRestClient telegramRestClient;
    private final Environment env;


    @Async
    public CompletableFuture<String> sendAsync(String title, String summary, String errorMsg) {
        String status = "fail";

        try {

            if (errorMsg.isBlank() || errorMsg.isEmpty()) throw new Exception("errorMsg can not empty");

            StringBuilder textMsg = new StringBuilder();
            String telegramId = env.getProperty("rest.telegram.chat-id");

            if (!title.isEmpty()) {
                textMsg.append(title);
            }

            if (!summary.isEmpty()) {
                textMsg.append("\n#SUMMARY: ").append(summary);
            }

            textMsg.append("\n#ERROR: ").append(errorMsg);

            telegramRestClient.sentMonitorMsg(telegramId, textMsg.toString());

        } catch (Exception e) {
            var appLog = new AppLog<>();
            appLog.setInfo(e.getMessage());
            appLog.setAction("Send Telegram Async");
            appLog.writeToLog();
        }
        return CompletableFuture.completedFuture(status);
    }
}
