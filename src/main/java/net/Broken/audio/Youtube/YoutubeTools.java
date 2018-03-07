package net.Broken.audio.Youtube;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import net.dv8tion.jda.core.entities.Guild;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class YoutubeTools {

    private Logger logger = LogManager.getLogger();

    private Guild guild;

    private static YoutubeTools INSTANCE ;

    private YoutubeTools(Guild guild){

        this.guild = guild;
    }

    public static YoutubeTools getInstance(Guild guild){
        if(INSTANCE == null)
            INSTANCE = new YoutubeTools(guild);
        return INSTANCE;
    }


    public String getRelatedVideo(String videoId, ArrayList<String> history) throws IOException, GoogleJsonResponseException, Throwable {

//        YouTube youtube = getYouTubeService();

        YouTube.Builder builder = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        });
        builder.setApplicationName("youtube-cmdline-search-sample");
        YouTube youtube = builder.build();


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

        searchListRelatedVideosRequest.setKey(System.getenv("GOOGLE_API_KEY"));

        SearchListResponse response = searchListRelatedVideosRequest.execute();

        for(SearchResult item : response.getItems()){
            if(!history.contains(item.getId().getVideoId())){
                return item.getId().getVideoId();
            }
            else
                logger.debug("ID already on history");
        }

        logger.debug("All on history ?");
        return response.getItems().get(0).getId().getVideoId();

    }
}
