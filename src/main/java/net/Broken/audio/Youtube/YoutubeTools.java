package net.Broken.audio.Youtube;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.SearchListResponse;
import net.dv8tion.jda.core.entities.Guild;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public class YoutubeTools {
    /** Application name. */
    private final String APPLICATION_NAME = "Discord Bot";

    /** Directory to store user credentials for this application. */
    private final File DATA_STORE_DIR = new File(".credentials/java-youtube-api");
    private final File CLIENT_SECRET_DIR = new File(".credentials/client_secret.json");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private HttpTransport HTTP_TRANSPORT;

    private Logger logger = LogManager.getLogger();

    private Guild guild;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/drive-java-quickstart
     */
    private final Collection<String> SCOPES = Arrays.asList(YouTubeScopes.YOUTUBEPARTNER, YouTubeScopes.YOUTUBE_FORCE_SSL);

    private static YoutubeTools INSTANCE ;

    private YoutubeTools(Guild guild){

           try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            logger.catching(t);
        }
        this.guild = guild;
    }

    public static YoutubeTools getInstance(Guild guild){
        if(INSTANCE == null)
            INSTANCE = new YoutubeTools(guild);
        return INSTANCE;
    }



    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    private Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in = new FileInputStream(CLIENT_SECRET_DIR);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader( in ));


        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("online")
                .build();



        Credential credential = new Authorization(flow, Receiver.getInstance(null), guild).authorize("user");
        logger.debug("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized API client service, such as a YouTube
     * Data API client service.
     * @return an authorized API client service
     * @throws IOException
     */
    public YouTube getYouTubeService() throws IOException {
        Credential credential = authorize();
        return new YouTube.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public String getRelatedVideo(String videoId) throws IOException, GoogleJsonResponseException, Throwable {

        YouTube youtube = getYouTubeService();

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("part", "snippet");
        parameters.put("relatedToVideoId", videoId);
        parameters.put("type", "video");

        YouTube.Search.List searchListRelatedVideosRequest = youtube.search().list(parameters.get("part").toString());
        if (parameters.containsKey("relatedToVideoId") && parameters.get("relatedToVideoId") != "") {
            searchListRelatedVideosRequest.setRelatedToVideoId(parameters.get("relatedToVideoId"));
        }

        if (parameters.containsKey("type") && !parameters.get("type").equals("")) {
            searchListRelatedVideosRequest.setType(parameters.get("type"));
        }

        SearchListResponse response = searchListRelatedVideosRequest.execute();

        return response.getItems().get(0).getId().getVideoId();

    }
}
