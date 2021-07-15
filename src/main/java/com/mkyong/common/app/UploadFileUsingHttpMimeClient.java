package com.mkyong.common.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

public class UploadFileUsingHttpMimeClient {

  public static void sendFile(String callback, String zipFilePath, String imageFileName) {
    try {
      HttpPost post = new HttpPost(callback);
      InputStream inputStream = new FileInputStream(zipFilePath);
      File file = new File(imageFileName);
      String message = "This is a multipart post";
      MultipartEntityBuilder builder = MultipartEntityBuilder.create();
      builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
      builder.addBinaryBody("file", file, ContentType.DEFAULT_BINARY, imageFileName);
      builder.addBinaryBody("upstream", inputStream, ContentType.create("application/zip"),
          zipFilePath);
      builder.addTextBody("text", message, ContentType.TEXT_PLAIN);

      HttpEntity entity = builder.build();
      post.setEntity(entity);

      HttpClient client = HttpClientBuilder.create().build();
      HttpResponse response = client.execute(post);
      System.out.println(response.getStatusLine().getStatusCode());
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public static void main(String[] args) {
    String url = "https://localhost:8181/file/upload/";
    String path = "C:\\GeCodes\\batApp2\\OLCLOWTIER.14.Jul.21.10.28.23.AW_BAT_Data.tar.gz";
    String imagePath = "C:\\GeCodes\\batApp2\\ge.png";
    sendFile(url, path, imagePath);
  }

}
