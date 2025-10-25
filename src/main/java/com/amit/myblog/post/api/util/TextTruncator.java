package com.amit.myblog.post.api.util;

public class TextTruncator {

    private static final int MAX_TEXT_CHARACTERS = 128;

    private static final String ELLIPSIS = "...";

    public static String truncate(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        return text.length() <= MAX_TEXT_CHARACTERS ? text : text.substring(0, MAX_TEXT_CHARACTERS) + ELLIPSIS;
    }

    private TextTruncator() {
        throw new UnsupportedOperationException();
    }

}
