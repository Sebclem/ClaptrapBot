package net.Broken.Tools;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FindContentOnWebPage {
        /**
         * Find picture URL on webPage
         *
         * @param url      Web Page URL
         * @param divClass Div class where the picture is
         * @param htmlType HTML tag of image (img)
         * @return Picture URL
         * @throws IOException
         */
        public static String doYourJob(String url, String divClass, String htmlType)
                        throws IOException, InterruptedException {
                // System.out.println(url);
                String source = getSourceUrl(url);
                int divIndex = source.indexOf("class=\"" + divClass);
                String sub = source.substring(divIndex);
                // System.out.println(sub);
                sub = sub.replace(divClass, "");
                sub = sub.substring(sub.indexOf(htmlType));
                sub = sub.substring(sub.indexOf("src"));
                sub = sub.replace("src=\"", "");
                String[] split = sub.split("\"");
                // System.out.println(split[0]);
                return split[0];
        }

        /**
         * Get source code of web page
         *
         * @param url Web page URL
         * @return Web page source as String
         * @throws IOException
         */
        public static String getSourceUrl(String url) throws IOException, InterruptedException {

                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(url))
                                .header("User-Agent",
                                                "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)")
                                .GET()
                                .build();
                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                return response.body();
        }
}