package net.Broken.DB.Repository;

import net.Broken.DB.Entity.PendingUserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository for PendingUserEntity
 */
public interface PendingUserRepository extends CrudRepository<PendingUserEntity, Integer> {
    List<PendingUserEntity> findByJdaId(String jdaId);


    PendingUserEntity findById(int id);

}
