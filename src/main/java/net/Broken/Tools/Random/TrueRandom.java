package net.Broken.Tools.Random;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.Broken.BotConfigLoader;
import net.Broken.SpringContext;
import net.Broken.Tools.Random.Data.RandomData;

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

    public List<Integer> getNumbers(int min, int max) throws IOException {

        // TODO Migrate to native http client
        HttpClient httpClient = HttpClientBuilder.create().build();

        RandomData postData = new RandomData("2.0", "generateIntegers", 41,
                new RandomData.Params(apiKey, 50, min, max, (max - min) < 50), null, null);
        ObjectMapper mapper = new ObjectMapper();

        StringEntity entity = new StringEntity(mapper.writeValueAsString(postData), ContentType.APPLICATION_JSON);
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
        String content = IOUtils.toString(responseIS, StandardCharsets.UTF_8);

        RandomData responseData = mapper.readValue(content, RandomData.class);

        if (responseData.error() != null) {
            logger.error("Request fail!");
            logger.error("Response : " + responseData.error().message());
            throw new IOException();

        }

        logger.debug("Request left: " + responseData.result().requestsLeft());
        logger.debug("Bits left: " + responseData.result().bitsLeft());
        logger.debug("Numbers: " + responseData.result().random().data());

        return responseData.result().random().data();

    }

}
