package net.Broken.audio.Youtube;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.hibernate.engine.jdbc.Size.LobMultiplier.M;

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

        StringBuilder idString = new StringBuilder();
        for(SearchResult item : response.getItems()){
            idString.append(item.getId().getVideoId()).append(",");
        }


        HashMap<String, Video> videoHashMap = new HashMap<>();
        YouTube.Videos.List video = youTube.videos().list("contentDetails");
        video.setId(idString.toString());
        video.setKey(apiKey);
        VideoListResponse videoResponse = video.execute();
        for(Video item : videoResponse.getItems()){
            videoHashMap.put(item.getId(), item);
        }
        ArrayList<net.Broken.audio.Youtube.SearchResult> finalResult = new ArrayList<>();
        for(SearchResult item : response.getItems()){
            logger.debug(item.getSnippet().getTitle());
            finalResult.add(new net.Broken.audio.Youtube.SearchResult(item, videoHashMap.get(item.getId().getVideoId()).getContentDetails().getDuration()));
        }





        return finalResult;

    }


    public String ytTimeToString(String time){
        int hours;
        int minutes;
        int seconds;
        if(time.equals("PT0S"))
            return ":red_circle: LIVE";

        time = time.replace("PT","");
        if(time.contains("H")) {

            String matched = time.substring(0, time.indexOf("H")+1);
            time = time.replace(matched,"");
            hours = Integer.parseInt(matched.replace("H", ""));
        }
        else
            hours = 0;
        logger.debug(time);

        if(time.contains("M")) {

            String matched = time.substring(0, time.indexOf("M")+1);
            time = time.replace(matched,"");
            minutes = Integer.parseInt(matched.replace("M", ""));
        }
        else
            minutes = 0;
        logger.debug(time);
        if(time.contains("S")) {

            String matched = time.substring(0, time.indexOf("S")+1);
            time = time.replace(matched,"");
            seconds = Integer.parseInt(matched.replace("S", ""));
        }
        else
            seconds = 0;
        logger.debug(time);

        String hoursStr = (hours < 10) ? "0" + hours : String.valueOf(hours);
        String minutesStr = (minutes < 10) ? "0" + minutes : String.valueOf(minutes);
        String secondsStr = (seconds < 10) ? "0" + seconds : String.valueOf(seconds);
        if (hours > 0)
            return hoursStr + ":" + minutesStr + ":" + secondsStr;
        else
            return minutesStr + ":" + secondsStr;

    }
}
