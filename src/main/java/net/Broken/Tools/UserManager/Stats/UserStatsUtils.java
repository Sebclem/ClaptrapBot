package net.Broken.Tools.UserManager.Stats;

import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Entity.UserStats;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.DB.Repository.UserStatsRepository;
import net.Broken.MainBot;
import net.Broken.SpringContext;
import net.Broken.Tools.UserManager.Exceptions.UnknownTokenException;
import net.Broken.Tools.UserManager.UserUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class UserStatsUtils {

    static double XP_PER_VOICE_TIME = 0.1;
    static double XP_PER_MESSAGE    = 1;
    static double XP_PER_API_COUNT  = 1;



    private static UserStatsUtils INSTANCE = new UserStatsUtils();

    public static UserStatsUtils getINSTANCE() {
        return INSTANCE;
    }


    public HashMap<Member, VoicePresenceCounter> runningCounters = new HashMap<>();

    private UserStatsRepository userStatsRepository;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    private Logger logger = LogManager.getLogger();



    private UserStatsUtils(){
        ApplicationContext context = SpringContext.getAppContext();
        userStatsRepository = (UserStatsRepository) context.getBean("userStatsRepository");
        userRepository = (UserRepository) context.getBean("userRepository");
        passwordEncoder = (PasswordEncoder) context.getBean("passwordEncoder");

    }

    public List<UserStats> getUserStats(UserEntity userEntity){
        User jdaUser = MainBot.jda.getUserById(userEntity.getJdaId());
        if(userEntity.getUserStats() == null || userEntity.getUserStats().size() == 0 || userEntity.getUserStats().size() != jdaUser.getMutualGuilds().size()){
            logger.debug("Stats not found for " + userEntity.getName());
            User user = MainBot.jda.getUserById(userEntity.getJdaId());
            if(user == null)
                return null;
            List<UserStats> stats = new ArrayList<>();
            for(Guild guid : user.getMutualGuilds()){
                logger.debug(guid.getName());
                stats.add(new UserStats(guid.getId(), userEntity));
            }
            stats = (List<UserStats>) userStatsRepository.save(stats);
            userEntity.setUserStats(stats);
            userEntity = userRepository.save(userEntity);

        }
        return userEntity.getUserStats();
    }

    public List<UserStats> getUserStats(String token) throws UnknownTokenException {
        UserEntity user = UserUtils.getInstance().getUserWithApiToken(userRepository, token);
        return getUserStats(user);
    }

    public List<UserStats> getUserStats(User user){
        UserEntity userEntity;
        List<UserEntity> userList = userRepository.findByJdaId(user.getId());
        if(userList.size() == 0){
            logger.debug("User not registered, generate it. User: " + user.getName() + " "+ user.getDiscriminator());
            userEntity = genUserEntity(user);
        }
        else
            userEntity = userList.get(0);

        return getUserStats(userEntity);

    }



    public UserStats getGuildUserStats(Member member){
        List<UserEntity> userEntityList = userRepository.findByJdaId(member.getUser().getId());
        UserEntity userEntity;
        if( userEntityList.size() == 0)
            userEntity = genUserEntity(member.getUser());
        else
            userEntity = userEntityList.get(0);

        List<UserStats> userStatsList = userStatsRepository.findByUserAndGuildId(userEntity, member.getGuild().getId());
        if(userStatsList.size() == 0){
            getUserStats(userEntity);
            userStatsList = userStatsRepository.findByUserAndGuildId(userEntity, member.getGuild().getId());
        }

        return userStatsList.get(0);
    }

    public UserStats getGuildUserStats(UserEntity userEntity, String guildId){
        List<UserStats> userStatsList = userStatsRepository.findByUserAndGuildId(userEntity, guildId);
        if(userStatsRepository.findByUserAndGuildId(userEntity, guildId).size() == 0){
            getUserStats(userEntity);
            userStatsList = userStatsRepository.findByUserAndGuildId(userEntity, guildId);
        }
        return userStatsList.get(0);
    }


    public void addMessageCount(Member member){
        UserStats userStats = getGuildUserStats(member);
        userStats.setMessageCount(userStats.getMessageCount() + 1);
        userStatsRepository.save(userStats);


    }

    public void addApiCount(UserEntity userEntity, String guildId){


        UserStats userStats = getGuildUserStats(userEntity, guildId);
        userStats.setApiCommandCount(userStats.getApiCommandCount() + 1);
        userStatsRepository.save(userStats);


    }


    private void addVocalCount(Member member){
        UserStats userStats = getGuildUserStats(member);
        userStats.setVocalTime(userStats.getVocalTime() + 10);
        userStatsRepository.save(userStats);


    }




    private UserEntity genUserEntity(User user){
        UserEntity userEntity = new UserEntity(user, passwordEncoder);
        return userRepository.save(userEntity);
    }



    public GuildStatsPack getStatPack(UserEntity userEntity, String guildId){
        UserStats userStats = getGuildUserStats(userEntity, guildId);
        GuildStats selfGuildStats = null;

        List<UserStats> allStats = userStatsRepository.findByGuildId(guildId);
        List<GuildStats> ranked = new ArrayList<>();
        for(UserStats stats : allStats){
            String avatar = MainBot.jda.getUserById(stats.getUser().getJdaId()).getAvatarUrl();
            GuildStats temp = new GuildStats(stats.getUser().getName(), avatar, stats.getVocalTime(), stats.getMessageCount(), stats.getApiCommandCount());
            if(stats.getUser().getId().equals(userEntity.getId())){
                selfGuildStats = temp;
            }
            ranked.add(temp);
        }
        ranked.sort((guildStats, t1) -> (int) (t1.total - guildStats.total));

        return new GuildStatsPack(ranked.indexOf(selfGuildStats) + 1 , selfGuildStats, ranked);

    }

    public static class VoicePresenceCounter extends Thread{
        private Member member;
        public VoicePresenceCounter(Member member){
            this.member = member;
        }

        @Override
        public void run() {
            while (member.getVoiceState().inVoiceChannel()){
                try {
                    Thread.sleep(10000);
                    if(member.getVoiceState().inVoiceChannel())
                        if(member.getGuild().getAfkChannel() != member.getVoiceState().getChannel())
                            UserStatsUtils.getINSTANCE().addVocalCount(member);


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            UserStatsUtils.getINSTANCE().runningCounters.remove(member);
        }
    }



}