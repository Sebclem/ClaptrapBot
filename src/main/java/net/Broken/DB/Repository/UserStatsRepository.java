package net.Broken.DB.Repository;

import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Entity.UserStats;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserStatsRepository extends CrudRepository<UserStats, Long> {
    List<UserStats> findByUser(UserEntity userEntity);
    List<UserStats> findByGuildId(String guildId);
    List<UserStats> findByUserAndGuildId(UserEntity user, String guildId);
}
