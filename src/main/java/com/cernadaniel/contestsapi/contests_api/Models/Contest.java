package com.cernadaniel.contestsapi.contests_api.Models;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

/**
 * Contest
 */
public class Contest {
    public String name;
    public long start;
    public long end;
    public String url;
    public String platform;

    public Contest(String name, long start, long end, String url, String platform) {
        this.name = name;
        this.url = url;
        this.start = start;
        this.end = end;
        this.platform = platform;
    }

    public Contest(Object obj, String platform) {
        if (platform.equals("AtCoder")) {
            createAtCoderContest((Element) obj);
        } else if (platform.equals("Codeforces")) {
            createCodeforcesContest((JSONObject) obj);
        }
    }

    private void createCodeforcesContest(JSONObject obj) {
        this.name = obj.getString("name");
        this.url = "https://codeforces.com/contests/" + obj.getInt("id");
        this.start = obj.getInt("startTimeSeconds");
        this.end = this.start + obj.getInt("durationSeconds");
        this.platform = "Codeforces";
    }

    private void createAtCoderContest(Element element) {
        List<Node> nodesList = element.childNodes();
        Node startNode = nodesList.get(1);
        Node nameNode = nodesList.get(3);
        Node durationNode = nodesList.get(5);
        String startString = ((TextNode) startNode.childNode(0).childNode(0).childNode(0)).text();
        String durationString = ((TextNode) durationNode.childNode(0)).text();
        LocalDateTime startDate = startDateFromString(startString);
        LocalDateTime endDate = endDateFromStart(startDate, durationString);
        this.name = ((TextNode) nameNode.childNode(3).childNode(0)).text();
        this.url = "https://atcoder.jp" + nameNode.childNode(3).attr("href");
        this.start = startDate.toEpochSecond(ZoneOffset.UTC);
        this.end = endDate.toEpochSecond(ZoneOffset.UTC);
        this.platform = "AtCoder";
    }

    private LocalDateTime startDateFromString(String startString) {
        String startSplit[] = startString.split(" ");
        String dateString[] = startSplit[0].split("-");
        String hourString[] = startSplit[1].split(":");
        LocalDateTime startDate = LocalDateTime.of(Integer.parseInt(dateString[0]), Integer.parseInt(dateString[1]),
                Integer.parseInt(dateString[2]), Integer.parseInt(hourString[0]), Integer.parseInt(hourString[1]));
        startDate = startDate.minusHours(9);
        return startDate;
    }

    private LocalDateTime endDateFromStart(LocalDateTime startDate, String durationString) {
        String durationSplit[] = durationString.split(":");
        LocalDateTime endDate = startDate.plusHours(Long.parseLong(durationSplit[0]))
                .plusMinutes(Long.parseLong(durationSplit[1]));
        return endDate;
    }
}