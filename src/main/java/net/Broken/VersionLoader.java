package net.Broken;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VersionLoader {
    @Value("${git.branch}")
    private String branch;
    @Value("${git.tags}")
    private String tags;
    @Value("${git.commit.id.abbrev}")
    private String commitId;

    public String getVersion(){
        String version;
        if(tags.isEmpty()){
            version = "DEV-" + branch + "-" + commitId;
        }else{
            version = tags;
        }
        return version;
    }
}
