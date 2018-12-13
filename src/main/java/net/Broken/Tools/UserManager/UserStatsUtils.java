package net.Broken.Tools.UserManager;

import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Entity.UserStats;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.DB.Repository.UserStatsRepository;
import net.Broken.MainBot;
import net.Broken.SpringContext;
import net.Broken.Tools.UserManager.Exceptions.UnknownTokenException;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

public class UserStatsUtils {


    private static UserStatsUtils INSTANCE = new UserStatsUtils();

    public static UserStatsUtils getINSTANCE() {
        return INSTANCE;
    }

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
        logger.debug(userEntity.getUserStats());
        logger.debug(userEntity.getUserStats().size());
        if(userEntity.getUserStats() == null || userEntity.getUserStats().size() == 0){
            logger.debug("Stats not found for " + userEntity.getName());
            User user = MainBot.jda.getUserById(userEntity.getJdaId());
            if(user == null)
                return null;
            List<UserStats> stats = new ArrayList<>();
            for(Guild guid : user.getMutualGuilds()){
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

    public void addMessageCount(Member member){
        List<UserEntity> userEntityList = userRepository.findByJdaId(member.getUser().getId());
        UserEntity userEntity;
        if( userEntityList.size() == 0)
            userEntity = genUserEntity(member.getUser());
        else
            userEntity = userEntityList.get(0);

        List<UserStats> userStatsList = userStatsRepository.findByUserAndGuildId(userEntity, member.getGuild().getId());
        logger.debug("First: " + userStatsList.size());
        if(userStatsList.size() == 0){
            getUserStats(userEntity);
            userStatsList = userStatsRepository.findByUserAndGuildId(userEntity, member.getGuild().getId());
        }

        UserStats userStats = userStatsList.get(0);
        userStats.setMessageCount(userStats.getMessageCount() + 1);
        userStatsRepository.save(userStats);


    }

    public void addApiCount(UserEntity userEntity, String guildId){


        List<UserStats> userStatsList = userStatsRepository.findByUserAndGuildId(userEntity, guildId);
        logger.debug("First: " + userStatsList.size());
        if(userStatsList.size() == 0){
            getUserStats(userEntity);
            userStatsList = userStatsRepository.findByUserAndGuildId(userEntity, guildId);
        }
        UserStats userStats = userStatsList.get(0);
        userStats.setApiCommandCount(userStats.getApiCommandCount() + 1);
        userStatsRepository.save(userStats);


    }


    private void addVocalCount(Member member){
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
        UserStats userStats = userStatsList.get(0);
        userStats.setVocalTime(userStats.getVocalTime() + 10);
        userStatsRepository.save(userStats);


    }




    private UserEntity genUserEntity(User user){
        UserEntity userEntity = new UserEntity(user, passwordEncoder);
        return userRepository.save(userEntity);
    }



    public static class VoicePresenceCompter extends Thread{
        private Member member;
        public VoicePresenceCompter(Member member){
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
        }
    }
}
