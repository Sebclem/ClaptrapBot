package net.Broken.DB.Repository;

import net.Broken.DB.Entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<UserEntity, Integer>{
    List<UserEntity> findByName(String name);
    List<UserEntity> findByJdaId(String jdaId);
    List<UserEntity> findByApiToken(String apiToken);
}
