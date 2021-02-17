package net.Broken.audio.Youtube;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import com.google.api.services.youtube.model.SearchResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

        YouTube.Builder builder = new YouTube.Builder(new NetHttpTransport(), new GsonFactory(), request -> {
        });
        builder.setApplicationName("BotDiscord");
        return builder.build();

    }


    public String getRelatedVideo(String videoId, ArrayList<String> history) throws IOException {


        YouTube youtube = getYoutubeService();




        YouTube.Search.List searchListRelatedVideosRequest = youtube.search().list(Collections.singletonList("snippet"));
        searchListRelatedVideosRequest.setRelatedToVideoId(videoId);
        searchListRelatedVideosRequest.setType(Collections.singletonList("video"));

        searchListRelatedVideosRequest.setKey(apiKey);

        SearchListResponse response = searchListRelatedVideosRequest.execute();

        for (SearchResult item : response.getItems()) {
            if (!history.contains(item.getId().getVideoId())) {
                if(item.getSnippet() != null)
                    return item.getId().getVideoId();
            } else
                logger.debug("ID already on history");
        }

        logger.debug("All on history ?");
        return response.getItems().get(0).getId().getVideoId();

    }


    public ArrayList<net.Broken.audio.Youtube.SearchResult> search(String query, long max, boolean playlist) throws IOException {
        YouTube youTube = getYoutubeService();
        YouTube.Search.List searchList = youTube.search().list(Collections.singletonList("snippet"));
        if(playlist)
            searchList.setType(Collections.singletonList("playlist"));
        else
            searchList.setType(Collections.singletonList("video"));
        searchList.setSafeSearch("none");
        searchList.setMaxResults(max);
        searchList.setQ(query);
        searchList.setKey(apiKey);
        searchList.setOrder("relevance");

        SearchListResponse response = searchList.execute();

        StringBuilder idString = new StringBuilder();


        if(playlist){
            for(SearchResult item : response.getItems()){
                idString.append(item.getId().getPlaylistId()).append(",");
            }
            HashMap<String, Playlist> playlistHashMap = new HashMap<>();
            YouTube.Playlists.List list = youTube.playlists().list(Collections.singletonList("contentDetails"));
            list.setId(Collections.singletonList(idString.toString()));
            list.setKey(apiKey);
            PlaylistListResponse playlistResponse = list.execute();
            for( Playlist item : playlistResponse.getItems()){
                playlistHashMap.put(item.getId(), item);
            }
            ArrayList<net.Broken.audio.Youtube.SearchResult> finalResult = new ArrayList<>();
            for(SearchResult item : response.getItems()){
                logger.trace(item.getSnippet().getTitle());
                finalResult.add(new net.Broken.audio.Youtube.SearchResult(item, playlistHashMap.get(item.getId().getPlaylistId()).getContentDetails().getItemCount().toString()+ " Video(s)"));

            }
            return finalResult;
        }
        else{
            for(SearchResult item : response.getItems()){
                idString.append(item.getId().getVideoId()).append(",");
            }
            HashMap<String, Video> videoHashMap = new HashMap<>();
            YouTube.Videos.List video = youTube.videos().list(Collections.singletonList("contentDetails"));
            video.setId(Collections.singletonList(idString.toString()));
            video.setKey(apiKey);
            VideoListResponse videoResponse = video.execute();
            for(Video item : videoResponse.getItems()){
                videoHashMap.put(item.getId(), item);
            }
            ArrayList<net.Broken.audio.Youtube.SearchResult> finalResult = new ArrayList<>();
            for(SearchResult item : response.getItems()){
                logger.trace(item.getSnippet().getTitle());
                finalResult.add(new net.Broken.audio.Youtube.SearchResult(item, videoHashMap.get(item.getId().getVideoId()).getContentDetails().getDuration()));
            }
            return finalResult;
        }









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
        logger.trace(time);

        if(time.contains("M")) {

            String matched = time.substring(0, time.indexOf("M")+1);
            time = time.replace(matched,"");
            minutes = Integer.parseInt(matched.replace("M", ""));
        }
        else
            minutes = 0;
        logger.trace(time);
        if(time.contains("S")) {

            String matched = time.substring(0, time.indexOf("S")+1);
            time = time.replace(matched,"");
            seconds = Integer.parseInt(matched.replace("S", ""));
        }
        else
            seconds = 0;
        logger.trace(time);

        String hoursStr = (hours < 10) ? "0" + hours : String.valueOf(hours);
        String minutesStr = (minutes < 10) ? "0" + minutes : String.valueOf(minutes);
        String secondsStr = (seconds < 10) ? "0" + seconds : String.valueOf(seconds);
        if (hours > 0)
            return hoursStr + ":" + minutesStr + ":" + secondsStr;
        else
            return minutesStr + ":" + secondsStr;

    }
}
