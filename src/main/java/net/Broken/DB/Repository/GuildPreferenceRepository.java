package net.Broken.DB.Repository;

import net.Broken.DB.Entity.GuildPreferenceEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GuildPreferenceRepository extends CrudRepository<GuildPreferenceEntity, Integer> {
    Optional<GuildPreferenceEntity> findByGuildId(String id);

}
