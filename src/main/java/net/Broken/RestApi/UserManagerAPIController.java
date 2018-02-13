package net.Broken.RestApi;

import net.Broken.DB.Entity.PendingUserEntity;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.PendingUserRepository;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.MainBot;
import net.Broken.RestApi.Data.UserManager.CheckResposeData;
import net.Broken.RestApi.Data.UserManager.ConfirmData;
import net.Broken.RestApi.Data.UserManager.UserConnectionData;
import net.Broken.RestApi.Data.UserManager.UserInfoData;
import net.Broken.Tools.UserManager.Exceptions.PasswordNotMatchException;
import net.Broken.Tools.UserManager.Exceptions.TokenNotMatch;
import net.Broken.Tools.UserManager.Exceptions.UserAlreadyRegistered;
import net.Broken.Tools.UserManager.Exceptions.UserNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/userManagement")
public class UserManagerAPIController {
    Logger logger = LogManager.getLogger();
    @Autowired
    PendingUserRepository pendingUserRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @RequestMapping(value = "/preRegister", method = RequestMethod.POST)
    public ResponseEntity<CheckResposeData> command(@RequestBody UserInfoData data){
        if(data != null && data.name != null) {
            try {
                String id = MainBot.userRegister.sendCheckToken(pendingUserRepository, userRepository, passwordEncoder, data);
                return new ResponseEntity<>(new CheckResposeData(true, data.name, "Message sent", id), HttpStatus.OK);
            } catch (UserNotFoundException e) {
                logger.warn("User \"" + data.name + "\" not found!");
                return new ResponseEntity<>(new CheckResposeData(false, data.name, "User not found on server!",""), HttpStatus.NOT_FOUND);
            } catch (PasswordNotMatchException userAlreadyRegistered) {
                return new ResponseEntity<>(new CheckResposeData(false, data.name, "User already registered in pending database and password not match!",""), HttpStatus.NOT_ACCEPTABLE);

            } catch (UserAlreadyRegistered userAlreadyRegistered) {
                return new ResponseEntity<>(new CheckResposeData(false, data.name, "User already registered in database!",""), HttpStatus.NOT_ACCEPTABLE);
            }
        }
        else{
            return new ResponseEntity<>(new CheckResposeData(false, "", "Missing parameter(s)",""), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/confirmAccount", method = RequestMethod.POST)
    public ResponseEntity<UserConnectionData> confirAccount(@RequestBody ConfirmData data){
        //TODO move pending user to accepted and return right things
        try {
            PendingUserEntity pUser = MainBot.userRegister.confirmCheckToken(pendingUserRepository, Integer.parseInt(data.id), data.checkToken);
            UserEntity user = new UserEntity(pUser, MainBot.userRegister.generateApiToken());
            userRepository.save(user);
            pendingUserRepository.delete(pUser);

            return new ResponseEntity<>(new UserConnectionData(true,user.getApiToken(),""),HttpStatus.OK);
        } catch (TokenNotMatch tokenNotMatch) {
            logger.warn("Pre token not match for "+data.id+"!");
            return new ResponseEntity<>(new UserConnectionData(false,"Token not match!","token"),HttpStatus.NOT_ACCEPTABLE);
        } catch (UserNotFoundException e) {
            logger.warn("Id not found in DB ("+data.id+")");
            return new ResponseEntity<>(new UserConnectionData(false,"User not found on DB!", "user"),HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @RequestMapping(value = "/requestToken", method = RequestMethod.POST)
    public ResponseEntity<UserConnectionData> requestToken(@RequestBody UserInfoData data){
        try {
            UserEntity user = MainBot.userRegister.getUser(userRepository, passwordEncoder, data);
            return new ResponseEntity<>(new UserConnectionData(true, user.getName(), user.getApiToken(), ""), HttpStatus.OK);

        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(new UserConnectionData(false,"User not registered!", "user"),HttpStatus.NOT_ACCEPTABLE);
        } catch (PasswordNotMatchException e) {
            return new ResponseEntity<>(new UserConnectionData(false,"Wrong user name or password!", "password"),HttpStatus.NOT_ACCEPTABLE);
        }
    }


}
