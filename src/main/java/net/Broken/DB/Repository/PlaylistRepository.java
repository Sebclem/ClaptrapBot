package net.Broken.DB.Repository;

import net.Broken.DB.Entity.PlaylistEntity;
import org.springframework.data.repository.CrudRepository;

public interface PlaylistRepository extends CrudRepository<PlaylistEntity, Integer> {
    PlaylistEntity findById(int id);
}
