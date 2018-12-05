package net.Broken.DB.Repository;

import net.Broken.DB.Entity.GuildPreferenceEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GuildPreferenceRepository extends CrudRepository<GuildPreferenceEntity, Integer> {
    List<GuildPreferenceEntity> findByGuildId(String id);

}
