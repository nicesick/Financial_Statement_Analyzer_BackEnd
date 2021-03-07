package com.jihun.study.openDartApi.utils.evaluate;

public class EvaluateUtils {
    public static long parseLong(String str) {
        try {
            return Long.parseLong(str.replaceAll("\\,", ""));
        } catch(Exception e) {
            throw e;
        }
    }
}
