package net.Broken.Tools.UserManager.Stats;

public class GuildStats {
    public String userName;
    public int rank;
    public String avatarUrl;

    public long voiceTime;
    public long voiceXp;

    public long messageCount;
    public long messageXp;

    public long apiCount;
    public long apiXp;

    public long total;


    public GuildStats() {
    }

    public GuildStats(String userName, int rank, String avatarUrl, long voiceTime, long messageCount, long apiCount) {
        this.userName = userName;
        this.avatarUrl = avatarUrl;
        this.voiceTime = voiceTime;
        this.messageCount = messageCount;
        this.apiCount = apiCount;
        this.rank = rank;

        voiceXp = (long) (this.voiceTime * UserStatsUtils.XP_PER_VOICE_TIME);
        messageXp = (long) (this.messageCount * UserStatsUtils.XP_PER_MESSAGE);
        apiXp = (long) (this.apiCount * UserStatsUtils.XP_PER_API_COUNT);
        total = voiceXp + messageXp + apiXp;

    }
}
