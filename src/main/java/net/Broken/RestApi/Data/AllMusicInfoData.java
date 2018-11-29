package net.Broken.RestApi.Data;

public class AllMusicInfoData {
    public CurrentMusicData currentMusic;
    public PlaylistData playlist;

    public AllMusicInfoData(CurrentMusicData currentMusic, PlaylistData playlist) {
        this.currentMusic = currentMusic;
        this.playlist = playlist;
    }
}
