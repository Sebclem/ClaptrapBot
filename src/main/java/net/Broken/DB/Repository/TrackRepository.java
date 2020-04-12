package net.Broken.DB.Repository;

import net.Broken.DB.Entity.PlaylistEntity;
import net.Broken.DB.Entity.TrackEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TrackRepository extends CrudRepository<TrackEntity, Integer> {
    List<TrackEntity> findDistinctByPlaylistOrderByPos(PlaylistEntity playlistEntity);

    TrackEntity findById(int id);
}
