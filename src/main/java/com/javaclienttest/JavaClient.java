package com.javaclienttest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastJsonValue;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import com.hazelcast.query.Predicates;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.lang.reflect.Array;
import java.util.*;

public class JavaClient {
    public static void main(String[] args) throws Exception {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setClusterName("dev");
        clientConfig.getNetworkConfig().addAddress("10.1.6.110:5701", "10.1.6.110:5702", "10.1.6.110:5703");

        HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);

        IMap<String, HazelcastJsonValue> mapEmployees = client.getMap("employees11"); //creates the map proxy

        ObjectMapper mapper = new ObjectMapper();
        List<Integer> listAge = Arrays.asList(10, 20, 23, 18);
        List<Integer> listSalary = Arrays.asList(1000, 2000, 2300, 1800);
        Random rand = new Random();
        JavaClient obj = new JavaClient();
        System.out.println("data đã insert: ");
        int i = 0;
        for (String val:Arrays.asList("Hung", "Huy", "Tung")){
            com.javaclienttest.Employee employee_i = new com.javaclienttest.Employee(val, obj.getRandomElement(listAge), true, obj.getRandomElement(listSalary));

            String json = mapper.writeValueAsString(employee_i);
            HazelcastJsonValue hazelcastJsonValue = new HazelcastJsonValue(json);
            mapEmployees.put(String.valueOf(i), hazelcastJsonValue);
            i++;
            System.out.println(json);
        }

        PredicateBuilder.EntryObject e = Predicates.newPredicateBuilder().getEntryObject();
        Predicate agePredicate = e.get( "age" ).lessThan( 22 );
        Collection<HazelcastJsonValue> employees = mapEmployees.values(agePredicate);
        System.out.println("Data querry duoc: ");
        for (HazelcastJsonValue employee : employees) {
            System.out.println(employee.toString());
        }
        client.shutdown();
    }

    public int getRandomElement(List<Integer> list)
    {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }
}
