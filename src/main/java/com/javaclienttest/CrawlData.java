package com.javaclienttest;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.map.IMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlData {
    public static void main(String[] args) throws Exception {
        List<String> urls = Arrays.asList("https://www.goal.com/", "https://www.dailymail.co.uk/",
                "https://www.espn.com/", "https://www.gazzetta.it/", "https://www.lequipe.fr/");
        for (String url:urls){
            DataCrawl dataCrawl = new DataCrawl(0, "", 20);
            String docText = getDataInURL (url, dataCrawl);
            insertHazelcast ("data_crawl", docText);
        }
    }

    public static void insertHazelcast (String mapName, String docText) throws Exception{
        String[] strArr = docText.split(" ");
        Map<String, HazelcastJsonValue> mapInput = new HashMap<String, HazelcastJsonValue>();

        ObjectMapper mapper = new ObjectMapper();
        int i = 0;
        System.out.println("Insert values: ");
        for (String word:strArr){
            Word wordObj = new Word(true, word);
            String json = mapper.writeValueAsString(wordObj);
            HazelcastJsonValue hazelcastJsonValue = new HazelcastJsonValue(json);
            mapInput.put(String.valueOf(i), hazelcastJsonValue);
            i++;
        }

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setClusterName("dev");
        clientConfig.getNetworkConfig().addAddress("10.1.6.110:5701", "10.1.6.110:5702", "10.1.6.110:5703");

        HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
        IMap<String, HazelcastJsonValue> wordMap = client.getMap(mapName); //creates the map proxy

        wordMap.putAll(mapInput);
        client.shutdown();
    }

    public static String getDataInURL (String url, DataCrawl dataCrawled) {
        System.out.println("Fetching %s..." + url);
        String docText = "";
        Document doc = null;
        if (dataCrawled.getCountSite()>dataCrawled.getMaxNumSite()){
            return docText;
        }

        if (url == null || url.length() == 0 || url.contains("'")){
            return docText;
        }
        try {
            doc = Jsoup.connect(url).get();
            dataCrawled.add1CountSite();
            dataCrawled.addVisittedSite(url);
        } catch (IOException e) {
            e.printStackTrace();
            return docText;
        }

        docText = doc.body().text();

        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String newUrl = link.attr("abs:href");
            if (dataCrawled.getCountSite()>dataCrawled.getMaxNumSite()){
                break;
            }
            if (!dataCrawled.getListSiteVisitted().contains(newUrl)){
                dataCrawled.add1CountSite();
                dataCrawled.addVisittedSite(newUrl);
                docText+=getDataInURL(newUrl, dataCrawled);
            }else{
                continue;
            }
        }
        return docText;
    }
    public static void notEmpty(String string) {
        if (string == null || string.length() == 0)
            throw new IllegalArgumentException("String must not be empty");
    }
}
