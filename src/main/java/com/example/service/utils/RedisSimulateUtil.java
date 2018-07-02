package com.example.service.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedisSimulateUtil {

    private static final Map<String, List<String>> redisRef = new HashMap<>();

    static {
        //模拟redis
        List<String> list = new ArrayList<>();
        list.add("18058148144");
        list.add("18868707977");
        redisRef.put("france", list);
    }

    public static List<String> get(String key) {
        return redisRef.get(key);
    }
}
