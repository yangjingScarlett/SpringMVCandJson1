package com.mkyong.common.app;

import com.mkyong.common.util.HttpsConfigUtil;
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

public class AwbatUploadFile {

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



  public static void sendFileHttps(String callback, String zipFilePath, String imageFileName)
      throws IOException {
    HttpsConfigUtil httpsConfig = new HttpsConfigUtil(callback, "UTF-8", null);
    // Add form field
    httpsConfig.addFormField("sweepStatus", "S");
    httpsConfig.addFormField("sweepMessage", "Successful");
    // Add file
    httpsConfig.addFilePart("awFile", new File(zipFilePath));
    // Print result
    String response = httpsConfig.finish();
    System.out.println(response);
  }


  public static void main(String[] args) throws IOException {
    String linuxPath = "/home/jingyang/gePrograms/glassfish5/AW/awbat/OLCLOWTIER.14.Jul.21.10.28.23.AW_BAT_Data.tar.gz";
    String linuxImage = "/home/jingyang/gePrograms/glassfish5/AW/awbat/ge.png";
    String windowsPath = "C:\\GeCodes\\batApp2\\OLCLOWTIER.14.Jul.21.10.28.23.AW_BAT_Data.tar.gz";
    String windowsImage = "C:\\GeCodes\\batApp2\\ge.png";
    String path, imagePath;
    if (System.getProperty("os.name").startsWith("Windows")) {
      path = windowsPath;
      imagePath = windowsImage;
    } else {
      path = linuxPath;
      imagePath = linuxImage;
    }
    String httpUrl = "http://localhost:8080/file/upload/";
    String httpsUrl = "https://localhost:8181/awbat/awResponse/testRequestId";
    String url = httpsUrl;
    if (url.contains("http://")) {
      sendFile(url, path, imagePath);
    } else {
      sendFileHttps(url, path, imagePath);
    }
  }


}
