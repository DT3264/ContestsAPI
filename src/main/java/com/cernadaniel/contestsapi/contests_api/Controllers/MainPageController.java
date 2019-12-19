package com.cernadaniel.contestsapi.contests_api.Controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.cernadaniel.contestsapi.contests_api.Models.Contest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainPageController {

    List<Contest> globalContests = new ArrayList<Contest>();

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public TreeMap<String, TreeMap<String, String>> updateContests() {
        TreeMap<String, String> urls = new TreeMap<>();
        urls.put("Codeforces", "/api/codeforces");
        urls.put("AtCoder", "/api/atcoder");
        urls.put("All platforms", "/api/all");
        TreeMap<String, TreeMap<String, String>> data = new TreeMap<>();
        data.put("Available calls", urls);
        return data;
    }
}
