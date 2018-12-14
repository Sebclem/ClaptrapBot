package net.Broken.Tools.UserManager.Stats;

import java.util.List;

public class GuildStatsPack {


    public int rank;
    public GuildStats selfStats;
    public List<GuildStats> ranking;

    public GuildStatsPack(int rank, GuildStats selfStats, List<GuildStats> ranking) {
        this.rank = rank;
        this.selfStats = selfStats;
        this.ranking = ranking;
    }
}
