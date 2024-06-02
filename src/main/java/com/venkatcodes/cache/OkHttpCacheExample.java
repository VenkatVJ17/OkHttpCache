package com.venkatcodes.cache;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.net.CacheResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.Certificate;
import java.util.List;
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

            //corruptCacheEntry(cacheDir);

            // 5. Execute the request and handle the response
            Response response = client.newCall(request).execute();
            if (response.cacheResponse() != null) {
                // The response was retrieved from the cache
                System.out.println("Response from cache: " + response.cacheResponse());
            } else {
                // The response was fetched from the network
                System.out.println("Response from network: " + response.body().string());
            }

            printResponseDetails(response);
            Request request2 = new Request.Builder()
                    .url("https://www.vogella.com/")
                    .cacheControl(new CacheControl.Builder()
                            .maxAge(CACHE_MAX_AGE_SECONDS, TimeUnit.SECONDS)
                            .build())
                    .build();

            // 5. Execute the request and handle the response
            Response response2 = client.newCall(request2).execute();
            if (response2.cacheResponse() != null) {
                // The response was retrieved from the cache
                System.out.println("Response from cache: " + response2.cacheResponse());
            } else {
                // The response was fetched from the network
                System.out.println("Response from network: " + response2.body().string());
            }
            //corruptCacheEntry(cacheDir);

            printResponseDetails(response2);
            corruptCacheCertificate(cacheDir);
            Request request3 = new Request.Builder()
                    .url("https://www.vogella.com/")
                    .cacheControl(new CacheControl.Builder()
                            .maxAge(CACHE_MAX_AGE_SECONDS, TimeUnit.SECONDS)
                            .build())
                    .build();

            // 5. Execute the request and handle the response
            Response response3 = client.newCall(request3).execute();
            if (response3.cacheResponse() != null) {
                // The response was retrieved from the cache
                System.out.println("Response from cache: " + response3.cacheResponse());
            } else {
                // The response was fetched from the network
                System.out.println("Response from network: " + response3.body().string());
            }

            Request request4 = new Request.Builder()
                    .url("https://www.vogella.com/")
                    .cacheControl(new CacheControl.Builder()
                            .maxAge(CACHE_MAX_AGE_SECONDS, TimeUnit.SECONDS)
                            .build())
                    .build();

            // 5. Execute the request and handle the response
            Response response4 = client.newCall(request4).execute();
            if (response4.cacheResponse() != null) {
                // The response was retrieved from the cache
                System.out.println("Response from cache: " + response4.cacheResponse());
                Handshake handshake = response4.handshake();
                if (handshake != null) {
                    List<Certificate> peerCertificates = handshake.peerCertificates();
                    for (Certificate certificate : peerCertificates) {
                        System.out.println("Certificate: " + certificate);
                    }
                } else {
                    System.out.println("No handshake information available for the cached response.");
                }
            } else {
                // The response was fetched from the network
                System.out.println("Response from network: " + response4.body().string());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 5. Read the cache metadata

    }

    private static void printResponseDetails(Response response) throws IOException {
        System.out.println("Response URL: " + response.request().url());
        System.out.println("Response Code: " + response.code());
        System.out.println("Response Protocol: " + response.protocol());
        System.out.println("Response Message: " + response.message());
        System.out.println("Response Handshake: " + response.handshake());
        System.out.println("Response Cache Response: " + response.cacheResponse());
        System.out.println("Response Network Response: " + response.networkResponse());
        System.out.println("Response Headers: " + response.headers());
        System.out.println("Response Body: " + response.body().string());
        System.out.println("---------------");
    }

    private static void corruptCacheCertificate(File cacheDirectory) throws IOException {
        // Locate the cache files
        Files.walk(cacheDirectory.toPath())
                .filter(Files::isRegularFile)
                .forEach(filePath -> {
                    try {
                        // Read the contents of the file
                        byte[] fileContent = Files.readAllBytes(filePath);

                        // Corrupt the part of the file where the certificate is likely stored
                        // For simplicity, we'll just overwrite some bytes in the middle of the file
                        int corruptPosition = fileContent.length / 2;
                        for (int i = 0; i < 10; i++) {
                            fileContent[corruptPosition + i] = (byte) 0xFF;
                        }

                        // Write the corrupted content back to the file
                        Files.write(filePath, fileContent);
                        System.out.println("Corrupted cache file: " + filePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
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

