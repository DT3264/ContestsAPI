package com.cernadaniel.contestsapi.contests_api.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import java.util.List;

import com.cernadaniel.contestsapi.contests_api.Models.Contest;

/**
 * ContestsFetcher
 */
public class ContestsFetcher {

    public void getAtCoderContests(List<Contest> contests) {
        String url = "https://atcoder.jp/contests/";
        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        try {
            Document doc = Jsoup.connect(url).get();
            Elements links = doc.select("tr");
            for (Element e : links) {
                if (e.childNodeSize() == 9) {
                    if (e.childNode(1).childNode(0).attr("href").contains("timeanddate")) {
                        Contest c = new Contest(e, "AtCoder");
                        if (c.start > now) {
                            contests.add(c);
                        }
                    }
                }
            }
        } catch (IOException e) {
        }
    }

    public void getCodeforcesContests(List<Contest> contestsList) {
        JSONObject jsonObj = null;
        StringBuffer str = new StringBuffer();
        try {
            HttpGet request = new HttpGet("https://codeforces.com/api/contest.list?gym=false");
            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse response = client.execute(request);
            BufferedReader bf = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            while (bf.ready()) {
                str.append(bf.readLine());
            }
            // JsonObjectDeserializer
            // response.getEntity().getContent()
            jsonObj = new JSONObject(str.toString());
            JSONArray contestsArray = (JSONArray) jsonObj.get("result");
            for (Object object : contestsArray) {
                JSONObject contest = (JSONObject) object;
                if (contest.get("phase").equals("BEFORE")) {
                    Contest c = new Contest(contest, "Codeforces");
                    contestsList.add(c);
                }
            }
        } catch (IOException e) {
        }
    }
}