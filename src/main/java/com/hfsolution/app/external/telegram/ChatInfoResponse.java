package com.hfsolution.app.external.telegram;

import lombok.Data;

@Data
public class ChatInfoResponse {
    private boolean ok;
    private Result result;

    @Data
    public static class Result {
        private String title;
    }
}

