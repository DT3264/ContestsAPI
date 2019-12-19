package com.cernadaniel.contestsapi.contests_api.Controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.TreeSet;

import com.cernadaniel.contestsapi.contests_api.Models.Contest;
import com.cernadaniel.contestsapi.contests_api.Utils.ContestsFetcher;
import com.cernadaniel.contestsapi.contests_api.Utils.Database;
import com.cernadaniel.contestsapi.contests_api.Utils.NotificationsManager;


import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/api")
@RestController
public class ContestsController {
    Database db = new Database();
    List<Contest> globalContests = new ArrayList<Contest>();
    ContestsFetcher contestsFetcher = new ContestsFetcher();

    @RequestMapping(value = "/update-contests", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public String updateContests() {
        System.out.println("Received request at " + LocalDateTime.now().toString());
        List<Contest> tmpContests = new ArrayList<Contest>();
        long lastUpdate = db.getLastTimeUpdate();
        if (lastUpdate > 3600) {
            contestsFetcher.getAtCoderContests(tmpContests);
            contestsFetcher.getCodeforcesContests(tmpContests);
            tmpContests.sort((Contest c1, Contest c2) -> {
                return c1.start < c2.start ? -1 : 1;
            });
            globalContests.clear();
            db.updateContests(tmpContests);
        }
        tmpContests = db.getContests();
        synchronized (globalContests) {
            tmpContests.forEach((c) -> globalContests.add(c));
        }
        TreeMap<String, Integer> newContests = db.getNewContests();
        NotificationsManager notificationsManager = new NotificationsManager();
        notificationsManager.notificateNewContests(newContests);
        Timer timer = new Timer();
        int timeInterval = 1000*60*15;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //call the method
                HttpGet request = new HttpGet(System.getenv("UPDATE_URL"));
                HttpClient client = HttpClientBuilder.create().build();
                try{
                    System.out.println("Sending request");
                    client.execute(request);
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }
        }, timeInterval);
        return "Updated";
    }

    @RequestMapping(value = "/{req}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<Contest> showRequestedContests(@PathVariable String req) {
        if(req.equals("all")){
            synchronized (globalContests) {
                return globalContests;
            } 
        }
        String plaforms[] = req.split(",");
        TreeSet<String> requestedPlatforms = new TreeSet<String>();
        for(String platform : plaforms){
            requestedPlatforms.add(platform);
        }
        List<Contest> contests = new ArrayList<Contest>();
        synchronized (globalContests) {
            globalContests.forEach((c) -> {
                if (requestedPlatforms.contains(c.platform.toLowerCase())) {
                    contests.add(c);
                }
            });
        }
        return contests;
    }

    
}
