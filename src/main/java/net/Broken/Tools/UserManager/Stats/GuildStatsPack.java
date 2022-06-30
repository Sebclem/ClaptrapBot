package net.Broken.Tools.UserManager.Stats;

import java.util.List;

public record GuildStatsPack(int rank, GuildStats selfStats, List<GuildStats> ranking) {
}
