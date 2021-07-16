package com.mkyong.common.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpsConfigUtil {

  private final String boundary;
  private static final String LINE = "\r\n";
  private HttpsURLConnection httpsConn;
  private String charset;
  private OutputStream outputStream;
  private PrintWriter writer;

  /**
   * This constructor initializes a new HTTP POST request with content type is set to
   * multipart/form-data
   */
  public HttpsConfigUtil(String requestURL, String charset, Map<String, String> headers)
      throws IOException {
    this.charset = charset;
    boundary = UUID.randomUUID().toString();
    URL url = new URL(requestURL);
    httpsConn = (HttpsURLConnection) url.openConnection();
    httpsConn.setUseCaches(false);
    httpsConn.setDoOutput(true);    // indicates POST method
    httpsConn.setDoInput(true);
    httpsConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

    httpsConn = configureConnection(httpsConn);
    if (headers != null && headers.size() > 0) {
      for (String key : headers.keySet()) {
        String value = headers.get(key);
        httpsConn.setRequestProperty(key, value);
      }
    }
    outputStream = httpsConn.getOutputStream();
    writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
  }

  /**
   * Adds a form field to the request
   */
  public void addFormField(String name, String value) {
    writer.append("--").append(boundary).append(LINE);
    writer.append("Content-Disposition: form-data; name=\"").append(name).append("\"").append(LINE);
    writer.append("Content-Type: text/plain; charset=").append(charset).append(LINE);
    writer.append(LINE);
    writer.append(value).append(LINE);
    writer.flush();
  }

  /**
   * Adds a upload file section to the request
   */
  public void addFilePart(String fieldName, File uploadFile)
      throws IOException {
    String fileName = uploadFile.getName();
    writer.append("--").append(boundary).append(LINE);
    writer.append("Content-Disposition: form-data; name=\"").append(fieldName)
        .append("\"; filename=\"").append(fileName).append("\"").append(LINE);
    writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(fileName))
        .append(LINE);
    writer.append("Content-Transfer-Encoding: binary").append(LINE);
    writer.append(LINE);
    writer.flush();

    FileInputStream inputStream = new FileInputStream(uploadFile);
    byte[] buffer = new byte[4096];
    int bytesRead = -1;
    while ((bytesRead = inputStream.read(buffer)) != -1) {
      outputStream.write(buffer, 0, bytesRead);
    }
    outputStream.flush();
    inputStream.close();
    writer.append(LINE);
    writer.flush();
  }

  /**
   * Completes the request and receives response from the server.
   *
   * @return String as response in case the server returned status OK, otherwise an exception is
   * thrown.
   * @throws IOException
   */
  public String finish() throws IOException {
    String response = "";
    writer.flush();
    writer.append("--").append(boundary).append("--").append(LINE);
    writer.close();

    // checks server's status code first
    int status = httpsConn.getResponseCode();
    if (status == HttpURLConnection.HTTP_OK) {
      ByteArrayOutputStream result = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      int length;
      while ((length = httpsConn.getInputStream().read(buffer)) != -1) {
        result.write(buffer, 0, length);
      }
      response = result.toString(this.charset);
      httpsConn.disconnect();
    } else {
      throw new IOException("Server returned non-OK status: " + status);
    }
    return response;
  }

  public static HttpsURLConnection configureConnection(HttpsURLConnection con) {
    TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

      public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return null;
      }

      public void checkClientTrusted(
          java.security.cert.X509Certificate[] certs, String authType) {
      }

      public void checkServerTrusted(
          java.security.cert.X509Certificate[] certs, String authType) {
      }
    }};

    HostnameVerifier allHostsValid = new HostnameVerifier() {
      @Override
      public boolean verify(String arg0, SSLSession arg1) {
        return true;
      }
    };

    // Install the all-trusting trust manager
    try {
      SSLContext sc = SSLContext.getInstance("TLSv1.2");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      con.setHostnameVerifier(allHostsValid);
      con.setSSLSocketFactory(sc.getSocketFactory());
    } catch (Exception ignored) {
    }
    return con;
  }

}
