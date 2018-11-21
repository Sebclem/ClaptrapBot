package net.Broken.audio.Youtube;

public class SearchResult {
    public String id;
    public String title;
    public String description;
    public String publishedAt;
    public String channelId;
    public String channelTittle;
    public String imageUrl;
    public String duration;

    public SearchResult(com.google.api.services.youtube.model.SearchResult result, String duration){
        id = result.getId().getVideoId();
        title = result.getSnippet().getTitle();
        description = result.getSnippet().getDescription();
        publishedAt = result.getSnippet().getPublishedAt().toString();
        channelId = result.getSnippet().getChannelId();
        channelTittle = result.getSnippet().getChannelTitle();
        imageUrl = result.getSnippet().getThumbnails().getDefault().getUrl();
        this.duration = duration;
    }
}
