package com.cinemo.api.util;

import java.util.UUID;

public class AiUtil {
    private static final String PREFIX = "session";

    /**
     * UUIDベースのセッションID生成（最も一般的）
     * 例: session-a1b2c3d4-e5f6-7890-abcd-ef1234567890
     */
    public static String generateUuidBased() {
        return PREFIX + "-" + UUID.randomUUID().toString();
    }
}
