package net.Broken.DB.Entity;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;


@Entity
public class PendingPwdResetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToOne
    private UserEntity userEntity;
    private String securityToken;
    private Date expirationDate;


    public PendingPwdResetEntity(UserEntity userEntity, String token) {
        this.userEntity = userEntity;
        this.securityToken = token;
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, 24);
        expirationDate = cal.getTime();

    }

    public PendingPwdResetEntity() {
    }


    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}
