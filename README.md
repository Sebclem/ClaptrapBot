# BrokenDiscordBot
Master:<br/>[![Build Status](https://jenkins.seb6596.ovh/job/Bot%20Discord%20Gradle/badge/icon)](https://jenkins.seb6596.ovh/job/Bot%20Discord%20Gradle/)

Devel: <br/>[![Build Status](https://jenkins.seb6596.ovh/buildStatus/icon?job=BrokenDiscordBot/devel)](https://jenkins.seb6596.ovh/job/BrokenDiscordBot/job/devel/)

## Install:
 
> The easiest method it's to use docker-compose:
> 
> docker-compose.yml:
> ```YAML
> version: '2'
> 
> services:
>   botdiscord:
>     image: brokenfire/brokendiscordbot:latest
>     networks:
>       - proxy
>       - internal
>     environment:
>       - PORT=8080
>       - TOKEN=1111111111111 #CHANGE ME!
>       - DB_URL=jdbc:mysql://database/botdiscord 
>       - DB_USER=root 
>       - DB_PWD=ogahxu1eigohN2Eok0xoawae #CHANGE ME!
>     labels:
>       - "traefik.frontend.rule=Host:exemple.com" #CHANGE ME!
>       - "traefik.docker.network=proxy"
>       - "traefik.port=8080"
>       - "traefik.backend=botdiscord"
>       - "traefik.frontend.entryPoints=http,https"
>     volumes:
>       - "./logs:/bot_src/logs"
>     depends_on:
>       - "database"
>     restart: always
> 
>   phpmyadmin:
>     image: phpmyadmin/phpmyadmin:latest
>     labels:
>       - "traefik.frontend.rule=Host:phpmyadmin.exemple.com" #CHANGE ME!
>       - "traefik.port=80"
>       - "traefik.docker.network=proxy"
>       - "traefik.backend=phpmyadmin"
>       - "traefik.frontend.entryPoints=http,https"
>     environment:
>       - PMA_HOST=database
>     depends_on:
>       - "database"
>
>   database:
>     image: mariadb:latest
>     volumes:
>       - ./db:/var/lib/mysql
>     labels:
>       - "traefik.enable=false"
>     environment:
>       - MYSQL_ROOT_PASSWORD= #CHANGE ME!
>       - MYSQL_DATABASE=botdiscord
>       - MYSQL_USER=bot
>       - MYSQL_PASSWORD=Ho0duiWo3noo3Ahrahx0rohz #CHANGE ME!
>     networks:
>       - internal
>
> networks:
>     proxy:
>       external: true
>     internal:
>       external: false
> ```
> Docker hub [repo](https://hub.docker.com/r/brokenfire/brokendiscordbot/)

### [Javadoc](https://jenkins.seb6596.ovh/job/Bot%20Discord%20Gradle/javadoc/)

### [Jenkins](https://jenkins.seb6596.ovh/job/Bot%20Discord%20Gradle/)

#### Jenkisfile
`git config --global merge.ours.driver true`

```
[merge "ours"]
    name = "Keep ours merge"
    driver = true
```

