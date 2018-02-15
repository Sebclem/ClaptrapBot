package net.Broken.DB.Entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PendingUserEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    private String name;

    private String jdaId;

    private String checkToken;

    private String password;

    public PendingUserEntity() {
    }

    public PendingUserEntity(String name, String jdaId, String checkToken, String password) {
        this.name = name;
        this.jdaId = jdaId;
        this.checkToken = checkToken;
        this.password = password;
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

    public String getCheckToken() {
        return checkToken;
    }

    public void setCheckToken(String checkToken) {
        this.checkToken = checkToken;
    }
}
