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

    public SearchResult(com.google.api.services.youtube.model.SearchResult result, String duration) {
        if (result.getId().getVideoId() == null)
            id = result.getId().getPlaylistId();
        else
            id = result.getId().getVideoId();
        title = result.getSnippet().getTitle();
        description = result.getSnippet().getDescription();
        publishedAt = result.getSnippet().getPublishedAt().toString();
        channelId = result.getSnippet().getChannelId();
        channelTittle = result.getSnippet().getChannelTitle();
        imageUrl = result.getSnippet().getThumbnails().getDefault().getUrl();
        this.duration = duration;
    }

    public SearchResult(String id, String title, String description, String publishedAt, String channelId, String channelTittle, String imageUrl, String duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.publishedAt = publishedAt;
        this.channelId = channelId;
        this.channelTittle = channelTittle;
        this.imageUrl = imageUrl;
        this.duration = duration;
    }
}
