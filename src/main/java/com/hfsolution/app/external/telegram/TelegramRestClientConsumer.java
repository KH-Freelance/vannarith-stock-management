package com.hfsolution.app.external.telegram;



import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.hfsolution.app.util.AppLog;
import java.util.concurrent.CompletableFuture;


@Service
public class TelegramRestClientConsumer {

    @Autowired
    private  TelegramRestClient telegramRestClient;

    @Autowired
    private  Environment env;

    @Async
    public CompletableFuture<Void> sendFileToTelegram(MultipartFile file, String chatId) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Prepare the file
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("chat_id", chatId);
            body.add("document", file.getResource());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Send the request
            ResponseEntity<String> response = restTemplate.exchange(
                env.getProperty("rest.telegram.url")+"/sendDocument",
                HttpMethod.POST,
                requestEntity,
                String.class
            );

            // Handle the response
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("File sent successfully!");
            } else {
                System.err.println("Failed to send the file: " + response.getBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(null);
    }
    

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
            ;
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
