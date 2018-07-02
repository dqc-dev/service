package com.example.service.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolUtil {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public static void execute(Runnable job) {
        executorService.execute(job);
    }
}
