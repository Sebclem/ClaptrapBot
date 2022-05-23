package net.Broken.DB.Repository;

import net.Broken.DB.Entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for UserEntity
 */

public interface UserRepository extends CrudRepository<UserEntity, Integer> {
    List<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByDiscordId(String discordId);
}
