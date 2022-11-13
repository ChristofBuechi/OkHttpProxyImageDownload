package ch.christofbuechi;

import java.io.InputStream;
import java.util.Base64;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.IOUtils;

public class Main {

  public static void main(String[] args) {
    System.out.println("Hello world!");
    String base64ImageString = downloadPicture();
    System.out.println(base64ImageString);
  }


  public static String downloadPicture() {
    int proxyPort = 8080;
    String proxyHost = "proxyHost";
    final String username = "username";
    final String password = "password";

    Authenticator proxyAuthenticator = new Authenticator() {
      @Override
      public Request authenticate(Route route, Response response) throws IOException {
        String credential = Credentials.basic(username, password);
        return response.request().newBuilder().header("Proxy-Authorization", credential).build();
      }
    };
    OkHttpClient client = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS)
//        .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)))
//        .proxyAuthenticator(proxyAuthenticator)
        .build();

    Request request = new Request.Builder().url("https://www.wikipedia.org/portal/wikipedia.org/assets/img/Wikipedia-logo-v2.png").get()
//                .addHeader("authorization", "Basic " + password)
        .addHeader("cache-control", "no-cache").build();

    Response response = null;
    try {
      response = client.newCall(request).execute();
      InputStream inputStream = response.body().byteStream();
      byte[] bytes = IOUtils.toByteArray(inputStream);
      response.body().close();
      return Base64.getEncoder().encodeToString(bytes);
    } catch (IOException e) {
      System.err.println("Failed scraping");
      e.printStackTrace();
    }
    return "failed";
  }
}