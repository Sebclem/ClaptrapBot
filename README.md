# BrokenDiscordBot

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
>     environment:
>       - PORT=8081
>       - TOKEN=1111111111111 #CHANGE ME!
>     labels:
>       - "traefik.frontend.rule=Host:exemple.com" #CHANGE ME!
>       - "traefik.port=8080"
>       - "traefik.backend=botdiscord"
>       - "traefik.frontend.entryPoints=http,https"
>     volumes:                                                                              
>        - "./logs:/bot_src/logs"
>     restart: always
> 
> networks:
>     proxy:
>        external: true 
> ```
> Docker hub [repo](https://hub.docker.com/r/brokenfire/brokendiscordbot/)
                                                      
