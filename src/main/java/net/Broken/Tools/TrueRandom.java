package net.Broken.Tools;

import net.Broken.BotConfigLoader;
import net.Broken.SpringContext;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TrueRandom {

    private static final TrueRandom INSTANCE = new TrueRandom();
    private final Logger logger = LogManager.getLogger();
    private final String apiKey;
    private TrueRandom() {
        apiKey = SpringContext.getAppContext().getBean(BotConfigLoader.class).randomApiKey();
    }

    public static TrueRandom getINSTANCE() {
        return INSTANCE;
    }

    public ArrayList<Integer> getNumbers(int min, int max) throws IOException {

//        TODO Migrate to native http client
        HttpClient httpClient = HttpClientBuilder.create().build();

        String postVal = "{\"jsonrpc\":\"2.0\",\"method\":\"generateIntegers\",\"params\":{\"apiKey\":\"" + apiKey + "\",\"n\":50,\"min\":" + min + ",\"max\":" + max + ",\"replacement\":" + (((max - min) >= 50) ? "false" : "true") + "},\"id\":41}";
        StringEntity entity = new StringEntity(postVal, ContentType.APPLICATION_JSON);
        String url = "https://api.random.org/json-rpc/2/invoke";
        HttpPost request = new HttpPost(url);
        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        HttpResponse response = httpClient.execute(request);
        int status = response.getStatusLine().getStatusCode();
        logger.debug("Status: " + status);

        if (status != 200) {
            logger.error("Request fail! Status: " + status);
            throw new IOException();
        }


        InputStream responseIS = response.getEntity().getContent();
        String content = IOUtils.toString(responseIS, "UTF-8");
        logger.trace(content);

        JSONObject json = new JSONObject(content);
        if (json.keySet().contains("error")) {
            logger.error("Request fail!");
            logger.error("Request : " + postVal);
            logger.error("Response : " + content);
            throw new IOException();

        }

        logger.debug("Request left: " + json.getJSONObject("result").getInt("requestsLeft"));
        logger.debug("Bits left: " + json.getJSONObject("result").getInt("bitsLeft"));
        logger.debug("Numbers: " + json.getJSONObject("result").getJSONObject("random").getJSONArray("data"));

        List<Object> numbers = json.getJSONObject("result").getJSONObject("random").getJSONArray("data").toList();

        ArrayList<Integer> converted = new ArrayList<>();
        for (Object nbr : numbers) {
            converted.add((Integer) nbr);
        }
        return converted;

    }

}
