package net.Broken.audio.Youtube;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class YoutubeTools {

    private Logger logger = LogManager.getLogger();
    private String apiKey = System.getenv("GOOGLE_API_KEY");

    private static YoutubeTools INSTANCE;

    private YoutubeTools() {

    }

    public static YoutubeTools getInstance() {
        if (INSTANCE == null)
            INSTANCE = new YoutubeTools();
        return INSTANCE;
    }


    private YouTube getYoutubeService() {

        YouTube.Builder builder = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), request -> {
        });
        builder.setApplicationName("BotDiscord");
        return builder.build();

    }


    public String getRelatedVideo(String videoId, ArrayList<String> history) throws IOException {


        YouTube youtube = getYoutubeService();


        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("part", "snippet");
        parameters.put("relatedToVideoId", videoId);
        parameters.put("type", "video");

        YouTube.Search.List searchListRelatedVideosRequest = youtube.search().list(parameters.get("part"));
        if (parameters.containsKey("relatedToVideoId") && parameters.get("relatedToVideoId") != "") {
            searchListRelatedVideosRequest.setRelatedToVideoId(parameters.get("relatedToVideoId"));
        }

        if (parameters.containsKey("type") && !parameters.get("type").equals("")) {
            searchListRelatedVideosRequest.setType(parameters.get("type"));
        }

        searchListRelatedVideosRequest.setKey(apiKey);

        SearchListResponse response = searchListRelatedVideosRequest.execute();

        for (SearchResult item : response.getItems()) {
            if (!history.contains(item.getId().getVideoId())) {
                return item.getId().getVideoId();
            } else
                logger.debug("ID already on history");
        }

        logger.debug("All on history ?");
        return response.getItems().get(0).getId().getVideoId();

    }


    public ArrayList<net.Broken.audio.Youtube.SearchResult> search(String query, long max) throws IOException {
        YouTube youTube = getYoutubeService();
        YouTube.Search.List searchList = youTube.search().list("snippet");
        searchList.setType("video");
        searchList.setSafeSearch("none");
        searchList.setMaxResults(max);
        searchList.setQ(query);
        searchList.setKey(apiKey);
        searchList.setOrder("relevance");

        SearchListResponse response = searchList.execute();
        ArrayList<net.Broken.audio.Youtube.SearchResult> finalResult = new ArrayList<>();
        for(SearchResult item : response.getItems()){
            logger.debug(item.getSnippet().getTitle());
            finalResult.add(new net.Broken.audio.Youtube.SearchResult(item));
        }

        return finalResult;

    }
}
