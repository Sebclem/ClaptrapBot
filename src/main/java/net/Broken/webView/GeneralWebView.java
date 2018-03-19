package net.Broken.webView;

import net.Broken.DB.Entity.PlaylistEntity;
import net.Broken.DB.Entity.TrackEntity;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.RestApi.Commands.Play;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Web page controller for index
 */
@Controller
public class GeneralWebView {


    @RequestMapping("/")
    public String music(Model model){

        return CheckPage.getPageIfReady("index");
    }
    @RequestMapping("/loading")
    public String loading(Model model){
        return "loading";
    }




}
