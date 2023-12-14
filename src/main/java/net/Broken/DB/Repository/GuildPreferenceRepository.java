package net.Broken.DB.Repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import net.Broken.DB.Entity.GuildPreferenceEntity;

public interface GuildPreferenceRepository extends CrudRepository<GuildPreferenceEntity, Integer> {
    Optional<GuildPreferenceEntity> findByGuildId(String id);

}
