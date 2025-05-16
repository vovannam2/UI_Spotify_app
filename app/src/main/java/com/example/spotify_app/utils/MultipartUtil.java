package com.example.spotify_app.utils;

import android.content.Context;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MultipartUtil {

    public static MultipartBody.Part createMultipartFromUri(Context context, Uri uri, String partName, String filename) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        byte[] bytes = readBytes(inputStream);
        MediaType mediaType = getMediaType(context.getContentResolver().getType(uri));
        RequestBody requestBody = RequestBody.create(mediaType, bytes);
        return MultipartBody.Part.createFormData(partName, filename, requestBody);
    }

    private static byte[] readBytes(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[4096];
        int bytesRead;
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, bytesRead);
        }
        return byteBuffer.toByteArray();
    }

    private static MediaType getMediaType(String contentType) {
        if (contentType != null) {
            switch (contentType) {
                case "image/jpeg":
                case "image/png":
                    return MediaType.parse("image/*");
                case "audio/mpeg":
                    return MediaType.parse("audio/*");
                // Add more cases for other media types if needed
            }
        }
        return null;
    }
}
