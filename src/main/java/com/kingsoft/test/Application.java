package com.kingsoft.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class Application {

    @RequestMapping("/")
    public String index() {
        return "Index Page";
    }

    @RequestMapping("/hello")
    public String hello() {
        return "Hello World!";
    }
    
    @RequestMapping("/channel_group")
    public String getChannelGroup() {
    	return "{\"code\":0,\"result\":[{\"groupCode\":\"GM\",\"groupName\":\"GM\",\"subCodes\":[\"90\",\"91\"]},{\"groupCode\":\"UWA\",\"groupName\":\"UWA\",\"subCodes\":[\"82\"]},{\"groupCode\":\"内测\",\"groupName\":\"内测\",\"subCodes\":[\"80\"]},{\"groupCode\":\"小米\",\"groupName\":\"小米\",\"subCodes\":[]},{\"groupCode\":\"苹果\",\"groupName\":\"苹果\",\"subCodes\":[\"81\"]},{\"groupCode\":\"雷老板\",\"groupName\":\"雷老板\",\"subCodes\":[\"100\"]},{\"groupCode\":\"项目组\",\"groupName\":\"项目组\",\"subCodes\":[\"0\",\"1\",\"3\",\"4\",\"5\",\"6\",\"7\",\"8\",\"9\",\"10\",\"11\",\"12\",\"13\",\"14\",\"15\",\"16\",\"17\",\"18\",\"19\",\"20\",\"21\",\"22\",\"23\",\"24\",\"25\",\"26\",\"27\",\"29\",\"30\",\"31\",\"32\",\"33\",\"34\",\"35\",\"36\",\"37\",\"38\",\"40\",\"41\",\"42\",\"43\",\"44\",\"45\",\"46\",\"47\",\"48\",\"50\",\"51\",\"52\",\"53\",\"54\",\"55\",\"56\",\"57\",\"58\",\"59\",\"60\",\"61\",\"63\",\"64\",\"65\",\"66\",\"67\",\"68\",\"69\",\"70\",\"71\",\"72\",\"73\",\"74\",\"75\"]}]}";
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }    
}