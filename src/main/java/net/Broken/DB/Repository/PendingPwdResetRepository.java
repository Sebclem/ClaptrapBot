package net.Broken.DB.Repository;

import net.Broken.DB.Entity.PendingPwdResetEntity;
import net.Broken.DB.Entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PendingPwdResetRepository extends CrudRepository<PendingPwdResetEntity,Integer>{
    List<PendingPwdResetEntity> findByUserEntity(UserEntity userEntity);
}
