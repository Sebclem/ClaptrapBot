package net.Broken.DB.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Entity for DB. Represent confirmed user account.
 */
@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    private String name;

    private String jdaId;

    private String apiToken;

    private String password;

    public UserEntity() {
    }

    public UserEntity(PendingUserEntity pendingUserEntity, String apiToken) {
        this.name = pendingUserEntity.getName();
        this.jdaId = pendingUserEntity.getJdaId();
        this.password = pendingUserEntity.getPassword();
        this.apiToken = apiToken;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJdaId() {
        return jdaId;
    }

    public void setJdaId(String jdaId) {
        this.jdaId = jdaId;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }
}
