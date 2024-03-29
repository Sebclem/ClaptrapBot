package net.Broken.Tools.UserManager.Stats;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import net.Broken.MainBot;
import net.Broken.SpringContext;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Entity.UserStats;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.DB.Repository.UserStatsRepository;
import net.Broken.Tools.CacheTools;
import net.Broken.Tools.EmbedMessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class UserStatsUtils {

    static final double XP_PER_VOICE_TIME = 0.01;
    static final double XP_PER_MESSAGE = 4;
    static final double XP_PER_API_COUNT = 1;

    private static UserStatsUtils INSTANCE = new UserStatsUtils();
    private final UserStatsRepository userStatsRepository;
    private final UserRepository userRepository;
    private final Logger logger = LogManager.getLogger();
    public final HashMap<String, VoicePresenceCounter> runningCounters = new HashMap<>();

    private UserStatsUtils() {
        ApplicationContext context = SpringContext.getAppContext();
        userStatsRepository = (UserStatsRepository) context.getBean("userStatsRepository");
        userRepository = (UserRepository) context.getBean("userRepository");

    }

    public static UserStatsUtils getINSTANCE() {
        if (INSTANCE == null)
            INSTANCE = new UserStatsUtils();
        return INSTANCE;
    }

    public List<UserStats> getUserStats(UserEntity userEntity) {
        User jdaUser = CacheTools.getJdaUser(userEntity);
        // TODO clean database for deleted users

        if (jdaUser == null) {
            logger.warn("jdaUser is null, can't find discord user ????");
            return null;
        }

        logger.debug(userEntity);
        logger.debug(userEntity.getUserStats());
        if (userEntity.getUserStats() == null || userEntity.getUserStats().isEmpty()
                || userEntity.getUserStats().size() < jdaUser.getMutualGuilds().size()) {
            logger.debug("Stats not found for {}", userEntity.getUsername());

            List<UserStats> stats;
            if (userEntity.getUserStats() == null || userEntity.getUserStats().isEmpty()) {
                logger.debug("No stats found for user {}, use blank.", jdaUser.getName());
                logger.debug("Creating stats for guilds: ");
                stats = new ArrayList<>();
                for (Guild guid : jdaUser.getMutualGuilds()) {
                    logger.debug("...{}", guid.getName());
                    stats.add(new UserStats(guid.getId(), userEntity));
                }
            } else {
                stats = userEntity.getUserStats();
                ArrayList<String> guildStat = new ArrayList<>();
                for (UserStats stat : stats) {
                    guildStat.add(stat.getGuildId());
                }
                for (Guild guid : jdaUser.getMutualGuilds()) {
                    logger.debug(guid.getName());
                    if (!guildStat.contains(guid.getId())) {
                        logger.debug("Guild {} stats don't exist", guid.getName());
                        stats.add(new UserStats(guid.getId(), userEntity));
                    }
                }
            }

            stats = (List<UserStats>) userStatsRepository.saveAll(stats);
            userEntity.setUserStats(stats);
            userEntity = userRepository.save(userEntity);

        }
        return userEntity.getUserStats();
    }

    public List<UserStats> getUserStats(User user) {
        UserEntity userEntity = userRepository.findByDiscordId(user.getId())
                .orElseGet(() -> genUserEntity(user));
        return getUserStats(userEntity);

    }

    public UserStats getGuildUserStats(Member member) {
        UserEntity userEntity = userRepository.findByDiscordId(member.getUser().getId())
                .orElseGet(() -> genUserEntity(member.getUser()));

        List<UserStats> userStatsList = userStatsRepository.findByUserAndGuildId(userEntity, member.getGuild().getId());
        if (userStatsList.isEmpty()) {
            logger.debug("User stats not found for user {} guild: {}", userEntity.getUsername(),
                    member.getGuild().getId());
            getUserStats(userEntity);
            userStatsList = userStatsRepository.findByUserAndGuildId(userEntity, member.getGuild().getId());
        }

        return userStatsList.get(0);
    }

    public UserStats getGuildUserStats(UserEntity userEntity, String guildId) {
        List<UserStats> userStatsList = userStatsRepository.findByUserAndGuildId(userEntity, guildId);
        if (userStatsRepository.findByUserAndGuildId(userEntity, guildId).isEmpty()) {
            logger.debug("User stats not found for user {} guild: {}", userEntity.getUsername(), guildId);
            getUserStats(userEntity);
            userStatsList = userStatsRepository.findByUserAndGuildId(userEntity, guildId);
        }
        return userStatsList.get(0);
    }

    public void addMessageCount(Member member) {
        UserStats userStats = getGuildUserStats(member);
        userStats.setMessageCount(userStats.getMessageCount() + 1);
        userStatsRepository.save(userStats);

    }

    public void addApiCount(UserEntity userEntity, String guildId) {

        UserStats userStats = getGuildUserStats(userEntity, guildId);
        userStats.setApiCommandCount(userStats.getApiCommandCount() + 1);
        userStatsRepository.save(userStats);

    }

    private void addVocalCount(Member member) {
        UserStats userStats = getGuildUserStats(member);
        userStats.setVocalTime(userStats.getVocalTime() + 10);
        userStatsRepository.save(userStats);

    }

    private UserEntity genUserEntity(User user) {
        UserEntity userEntity = new UserEntity(user);
        return userRepository.save(userEntity);
    }

    public GuildStatsPack getStatPack(UserEntity userEntity, String guildId) {
        GuildStats selfGuildStats = null;

        List<UserStats> allStats = userStatsRepository.findByGuildId(guildId);
        List<GuildStats> ranked = new ArrayList<>();
        List<UserStats> needCache = new ArrayList<>();
        Guild guild = MainBot.jda.getGuildById(guildId);
        for (UserStats stats : allStats) {
            Member member = guild.getMemberById(stats.getUser().getDiscordId());
            if (member == null) {
                needCache.add(stats);
                continue;
            }
            String avatar = member.getUser().getAvatarUrl();
            GuildStats temp = new GuildStats(stats.getUser().getUsername(), 0, avatar, stats.getVocalTime(),
                    stats.getMessageCount(), stats.getApiCommandCount());
            if (stats.getUser().getId().equals(userEntity.getId())) {
                selfGuildStats = temp;
            }
            ranked.add(temp);
        }
        if (!needCache.isEmpty()) {
            logger.info("Cache mismatch, loading all guild");
            MainBot.jda.getGuildById(guildId).loadMembers().get();
            for (UserStats stats : needCache) {
                Member member = guild.getMemberById(stats.getUser().getDiscordId());
                if (member == null) {
                    logger.warn("Can't find member '{}'after load, User leave the guild ?",
                            stats.getUser().getUsername());
                    continue;
                }
                String avatar = member.getUser().getAvatarUrl();
                GuildStats temp = new GuildStats(stats.getUser().getUsername(), 0, avatar, stats.getVocalTime(),
                        stats.getMessageCount(), stats.getApiCommandCount());
                if (stats.getUser().getId().equals(userEntity.getId())) {
                    selfGuildStats = temp;
                }
                ranked.add(temp);
            }
        }
        ranked.sort((guildStats, t1) -> (int) (t1.total - guildStats.total));

        int i = 1;
        for (GuildStats stat : ranked) {
            stat.rank = i;
            i++;
        }

        return new GuildStatsPack(ranked.indexOf(selfGuildStats) + 1, selfGuildStats, ranked);

    }

    public MessageEmbed getRankMessage(Member member) {
        UserStats userStats = getGuildUserStats(member);
        GuildStatsPack pack = getStatPack(userStats.getUser(), member.getGuild().getId());
        StringBuilder stringBuilder = new StringBuilder();
        int i = 1;
        for (GuildStats stats : pack.ranking()) {
            if (i >= 6) {
                break;
            }
            stringBuilder.append(i).append(". ").append(stats.userName).append(" with ").append(stats.total)
                    .append(" points!").append("\n");
            i++;

        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.yellow);
        embedBuilder.setTitle(":trophy:  " + member.getGuild().getName() + " Ranking");
        embedBuilder.addField("Top 5:", stringBuilder.toString(), false);
        String rank = switch (pack.selfStats().rank) {
            case 1 -> "1st";
            case 2 -> "2nd";
            case 3 -> "3rd";
            default -> pack.selfStats().rank + "th";
        };

        embedBuilder.addField("Your stats:", rank + " with " + pack.selfStats().total + " points", false);
        return EmbedMessageUtils.buildStandar(embedBuilder);

    }

    public static class VoicePresenceCounter extends Thread {
        private final Member member;

        public VoicePresenceCounter(Member member) {
            this.member = member;
        }

        @Override
        public void run() {
            while (member.getVoiceState().inAudioChannel()) {
                try {
                    Thread.sleep(10000);
                    if (member.getVoiceState().inAudioChannel())
                        if (member.getGuild().getAfkChannel() != member.getVoiceState().getChannel())
                            UserStatsUtils.getINSTANCE().addVocalCount(member);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            UserStatsUtils.getINSTANCE().runningCounters.remove(member.getId());
        }
    }

}
