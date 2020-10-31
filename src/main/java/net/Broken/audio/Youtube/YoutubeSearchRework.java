package net.Broken.audio.Youtube;

import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class YoutubeSearchRework {

    private Logger logger = LogManager.getLogger();

    private static YoutubeSearchRework INSTANCE;

    public static YoutubeSearchRework getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new YoutubeSearchRework();
        }
        return INSTANCE;
    }


    public List<SearchResult> searchVideo(String search, int maxResult, boolean playlist) throws IOException {
        search = URLEncoder.encode(search, StandardCharsets.UTF_8.toString());

        YoutubeSearchProvider searchProvider = new YoutubeSearchProvider();
        BasicAudioPlaylist rawResult = (BasicAudioPlaylist) searchProvider.loadSearchResult(search, audioTrackInfo -> new YoutubeAudioTrack(audioTrackInfo, null));
        List<SearchResult> results = new ArrayList<>();
        for (AudioTrack track : rawResult.getTracks()) {
            String imageUrl = "https://i.ytimg.com/vi/" + track.getIdentifier() + "/hqdefault.jpg";
            long hour = TimeUnit.MILLISECONDS.toHours(track.getInfo().length);
            String hms = hour == 0 ? "" : Long.toString(hour);
            hms += String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(track.getInfo().length) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(track.getInfo().length) % TimeUnit.MINUTES.toSeconds(1));
            results.add(new SearchResult(track.getIdentifier(), track.getInfo().title, "", "", "", track.getInfo().author, imageUrl, hms));
        }

        return results;
    }


    private Document getYoutubeSearchDocument(String URL) throws IOException {

        return Jsoup.connect(URL).userAgent("Googlebot/2.1 (+http://www.googlebot.com/bot.html)").header("Accept-Language", "en-US").get();


    }

    // video = EgIQAQ==
    private List<SearchResult> extractVideoInfo(Document doc, int maxResult, boolean playlist) {
        Elements videosDivs = doc.select(".yt-lockup");
        List<SearchResult> results = new ArrayList<>();
        int i = 0;
        for (Element videoDiv : videosDivs) {
            if (i >= maxResult)
                break;
            Element titleDiv = videoDiv.selectFirst(".yt-uix-tile-link");
            String id;
            if (!playlist)
                id = titleDiv.attributes().get("href").replace("/watch?v=", "");
            else {
                String listUrl = titleDiv.attributes().get("href");
                int listIndex = listUrl.indexOf("list=");
                id = listUrl.substring(listIndex).replace("list=", "");

            }

            String title = titleDiv.text();
            Elements metas = videoDiv.selectFirst(".yt-lockup-meta-info").getElementsByTag("li");
            String view = "";
            String date = "";
            if (!playlist) {
                for (Element metaElem : metas) {

                    if (metaElem.text().contains("view")) {
                        view = metaElem.text();
                    } else {
                        date = metaElem.text();
                    }
                }
            }


            Element chanelTag = videoDiv.selectFirst(".yt-lockup-byline").getElementsByTag("a").get(0);
            String channelTittle = chanelTag.text();
            String channelId = chanelTag.attributes().get("href").replace("/channel/", "");

            String imageUrl;
            String duration;
            if (!playlist) {
                duration = videoDiv.selectFirst(".video-time").text();
                imageUrl = "https://i.ytimg.com/vi/" + id + "/hqdefault.jpg";
            } else {
                String listUrl = titleDiv.attributes().get("href");
                int listIndex = listUrl.indexOf("&list=");
                listUrl = listUrl.substring(0, listIndex).replace("/watch?v=", "");
                imageUrl = "https://i.ytimg.com/vi/" + listUrl + "/hqdefault.jpg";
                duration = videoDiv.selectFirst(".formatted-video-count-label").text();
            }


            results.add(new SearchResult(id, title, "", date, channelId, channelTittle, imageUrl, duration));
            i++;


        }
        return results;
    }

    public String getRelatedVideo(String sourceVideoId) throws IOException, RelatedIdNotFound {
        sourceVideoId = URLEncoder.encode(sourceVideoId, StandardCharsets.UTF_8.toString());
        String url = "https://www.youtube.com/watch?v=" + sourceVideoId;
        Document doc = getYoutubeSearchDocument(url);

        return extractRelatedVideoId(doc);
    }

    private String extractRelatedVideoId(Document doc) throws RelatedIdNotFound {
        Elements elements = doc.select(".autoplay-bar .content-link");
        if (elements.size() == 0) {
            throw new RelatedIdNotFound();
        }
        Element elem = elements.get(0);
        String url = elem.attributes().get("href");
        return url.replace("/watch?v=", "");
    }

}
