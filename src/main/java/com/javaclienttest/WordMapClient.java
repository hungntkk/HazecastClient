package com.javaclienttest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.map.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import com.hazelcast.query.Predicates;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class WordMapClient {
    public static void main(String[] args) throws Exception {
        String content = readFile("C:\\Users\\84974\\IdeaProjects\\HazelcastClient\\src\\data\\input.txt", StandardCharsets.US_ASCII);
        String[] strArr = content.split(" ");

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setClusterName("dev");
        clientConfig.getNetworkConfig().addAddress("10.1.6.110:5701", "10.1.6.110:5702", "10.1.6.110:5703");

        HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);

        IMap<String, HazelcastJsonValue> wordMap = client.getMap("word_map12"); //creates the map proxy

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
            System.out.print(json);
        }
        wordMap.putAll(mapInput);

//        Collection<HazelcastJsonValue> words = getValuesWithIsActive(wordMap, "active");
//        System.out.println("Data querry duoc: ");
//        for (HazelcastJsonValue wordObj : words) {
//            Word word = mapper.readValue(wordObj.toString(), Word.class);
//            System.out.print(word.getWord());
//        }

        client.shutdown();
    }

    public int getRandomElement(List<Integer> list)
    {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    static String readFile(String path, Charset encoding) throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static Collection<HazelcastJsonValue> getValuesWithIsActive(IMap<String, HazelcastJsonValue> iMap, String isActiveName) {
        PredicateBuilder.EntryObject e = Predicates.newPredicateBuilder().getEntryObject();
        Predicate predicate = e.is( isActiveName );
        return iMap.values( predicate );
    }
}
