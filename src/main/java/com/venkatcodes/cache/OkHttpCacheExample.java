package com.venkatcodes.cache;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class OkHttpCacheExample {
    private static final int CACHE_MAX_AGE_SECONDS = 60; // 1 minute

    public static void main(String[] args) {
        try {
            // 1. Create a cache directory
            File cacheDir = new File("/Users/venkats/Documents/GitHub/CacheDir2");
            cacheDir.mkdirs();

            // 2. Create a cache instance
            int cacheSize = 1 * 1024 * 1024 ; // 10 MB cache size
            Cache cache = new Cache(cacheDir, cacheSize);

            // 3. Create an OkHttpClient instance with the cache enabled
            OkHttpClient client = new OkHttpClient.Builder()
                    .cache(cache)
                    .build();

            // 4. Create a request with cache control
            Request request = new Request.Builder()
                    .url("https://www.vogella.com/")
                    .cacheControl(new CacheControl.Builder()
                            .maxAge(CACHE_MAX_AGE_SECONDS, TimeUnit.SECONDS)
                            .build())
                    .build();

            corruptCacheEntry(cacheDir);

            // 5. Execute the request and handle the response
            Response response = client.newCall(request).execute();
            if (response.cacheResponse() != null) {
                // The response was retrieved from the cache
                System.out.println("Response from cache: " + response.cacheResponse());
            } else {
                // The response was fetched from the network
                System.out.println("Response from network: " + response.body().string());
            }

            Request request2 = new Request.Builder()
                    .url("https://www.vogella.com/")
                    .cacheControl(new CacheControl.Builder()
                            .maxAge(CACHE_MAX_AGE_SECONDS, TimeUnit.SECONDS)
                            .build())
                    .build();

            // 5. Execute the request and handle the response
            response = client.newCall(request2).execute();
            if (response.cacheResponse() != null) {
                // The response was retrieved from the cache
                System.out.println("Response from cache: " + response.cacheResponse());
            } else {
                // The response was fetched from the network
                System.out.println("Response from network: " + response.body().string());
            }
            corruptCacheEntry(cacheDir);
            Request request3 = new Request.Builder()
                    .url("https://www.vogella.com/")
                    .cacheControl(new CacheControl.Builder()
                            .maxAge(CACHE_MAX_AGE_SECONDS, TimeUnit.SECONDS)
                            .build())
                    .build();

            // 5. Execute the request and handle the response
            response = client.newCall(request3).execute();
            if (response.cacheResponse() != null) {
                // The response was retrieved from the cache
                System.out.println("Response from cache: " + response.cacheResponse());
            } else {
                // The response was fetched from the network
                System.out.println("Response from network: " + response.body().string());
            }

            Request request4 = new Request.Builder()
                    .url("https://www.vogella.com/")
                    .cacheControl(new CacheControl.Builder()
                            .maxAge(CACHE_MAX_AGE_SECONDS, TimeUnit.SECONDS)
                            .build())
                    .build();

            // 5. Execute the request and handle the response
            response = client.newCall(request4).execute();
            if (response.cacheResponse() != null) {
                // The response was retrieved from the cache
                System.out.println("Response from cache: " + response.cacheResponse());
            } else {
                // The response was fetched from the network
                System.out.println("Response from network: " + response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void corruptCacheEntry(File cacheDirectory) throws IOException {
        File[] files = cacheDirectory.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".0")) {
                    Path path = Paths.get(file.getPath());
                    byte[] data = Files.readAllBytes(path);
                    // Modify a portion of the data to be invalid
                    for (int i = 0; i < 10; i++) {
                        data[i] = 0;
                    }
                    Files.write(path, data);
                    System.out.println("Corrupted cache entry: " + file.getPath());
                }
            }
        }
    }
}

