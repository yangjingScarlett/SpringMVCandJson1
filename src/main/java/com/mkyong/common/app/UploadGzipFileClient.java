package com.mkyong.common.app;

import com.mkyong.common.util.HttpsConfigUtil;
import com.mkyong.common.util.JacksonUtil;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import javax.activation.UnsupportedDataTypeException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class UploadGzipFileClient {

  public static void main(String[] args) throws IOException, InterruptedException {

    String remotePath = "/mnt/AW/awbat/OLCLOWTIER.09.Jul.21.15.05.43.AW_BAT_Data.tar.gz";
    String gzipFilePath = copyFile(remotePath);

    byte[] bytes = readGzipToBytes(gzipFilePath);
    String fileContents = Base64.getEncoder().encodeToString(bytes);

    try {
      String callback = "https://localhost:8181/awResponse/testRequestId";
      Map<String, String> awBatResponse = new HashMap<>();
      awBatResponse.put("sweepStatus", "S");
      awBatResponse.put("sweepMessage", "Successful");
      awBatResponse.put("fileContents", fileContents);

      // Create data that needs to be posted
      String postJsonDataString = JacksonUtil.parseObj2Json(awBatResponse);

      URL url = new URL(callback);
      HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json; utf-8");

      connection = HttpsConfigUtil.configureConnection(connection);
      // Ensure the Connection Will Be Used to Send Content
      connection.setDoOutput(true);
      try (DataOutputStream writer = new DataOutputStream(connection.getOutputStream())) {
        writer.writeBytes(Objects.requireNonNull(postJsonDataString)); // write the post data
        writer.flush();
        writer.close();

        // Read through the whole response content, and print the final response string:
        try (BufferedReader in = new BufferedReader(
            new InputStreamReader(connection.getInputStream()))) {
          String line;
          StringBuilder content = new StringBuilder();
          while ((line = in.readLine()) != null) {
            content.append(line);
            content.append(System.lineSeparator());
          }
          String response = content.toString();
          System.out.println(response);
        }
      } catch (IOException e) {
        System.out.println(e.getMessage());
      } finally {
        connection.disconnect();
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }


  // sample of filePath: /mnt/AW/awbat/xxx.AW_BAT_Data.tar.gz
  public static String copyFile(String filePath) throws IOException, InterruptedException {
    StringBuilder dirPathBuilder = new StringBuilder("C:\\Temp" + File.separator);
    if (filePath.contains("AW_BAT_Data.tar.gz")) {
      String[] dirs = filePath.split("/");
      if (dirs.length != 5) {
        throw new UnsupportedDataTypeException("Incorrect file path: " + filePath);
      }
      for (int i = 2; i < dirs.length - 1; i++) {
        dirPathBuilder.append(dirs[i]);
        dirPathBuilder.append(File.separator);
      }
      String dirPath = dirPathBuilder.toString();
      String fileName = dirs[dirs.length - 1];

      File dir = new File(dirPath);
      if (!dir.exists()) {
        boolean s = dir.mkdirs();
        if (!s) {
          throw new IOException("Create directory " + dir.getPath() + " failed");
        }
        System.out.println("Directory created: " + dir.getPath());
      }

      String copyCommand = "cp " + filePath + " " + dirPath;
      ProcessBuilder pb = new ProcessBuilder("bash", "-c", copyCommand);
      Process p = pb.start();
      int exitCode = p.waitFor();
      if (exitCode == 0) {
        System.out.println("File copied: " + filePath);
      } else {
        throw new IOException("Copy file failed: " + copyCommand);
      }
      return dirPath + fileName;
    }
    System.out.println("Unable to find wanted file");
    return null;
  }

  public static byte[] readGzipToBytes(String gzipFilePath) {
    ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
    GZIPInputStream gzipInputStream = null;
    try {
      gzipInputStream = new GZIPInputStream(new FileInputStream(gzipFilePath));
      byte[] buffer = new byte[1024];
      int len;
      while ((len = gzipInputStream.read(buffer)) > 0) {
        byteOutput.write(buffer, 0, len);
      }
      byteOutput.flush();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    } finally {
      if (gzipInputStream != null) {
        try {
          gzipInputStream.close();
        } catch (IOException e) {
          System.out.println(e.getMessage());
        }
      }
    }
    return byteOutput.toByteArray();
  }



}
