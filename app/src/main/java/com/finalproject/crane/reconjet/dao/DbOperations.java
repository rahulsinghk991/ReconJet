package com.finalproject.crane.reconjet.dao;


import com.finalproject.crane.reconjet.core.Consts;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.util.ArrayList;

/**
 * Created by crane on 27/06/16.
 */
public class DbOperations {
    private String ipaddr = Consts.LOCAL_HOST;
    private int port = Consts.LOCAL_PORT;


    public String testConnect(){
        MongoClient mongoClient = new MongoClient(ipaddr, port);
        MongoDatabase db = mongoClient.getDatabase("reconjet");
        mongoClient.close();
        return db.getCollection("user").toString();
    }

    public String login(String username, final String password){
        MongoClient mongoClient = new MongoClient(ipaddr, port);
        MongoDatabase db = mongoClient.getDatabase("reconjet");
        MongoCollection userCollection= db.getCollection("user");
        Document document = new Document("username",username).append("password",password);
        FindIterable<Document> iterable = userCollection.find(document);
        if (iterable.first() == null) {
            mongoClient.close();
            return "false";
        }
        else {
            mongoClient.close();
            return "true";
        }
    }

    public void updateRealTimeRun(ArrayList<Double> list, String username){
        MongoClient mongoClient = new MongoClient(ipaddr, port);
        MongoDatabase db = mongoClient.getDatabase("reconjet");
        String str_mile = String.format("%.2f",list.get(1));
        Document upload = new Document().append("speed",list.get(0))
                                        .append("mile",str_mile)
                                        .append("pace",list.get(2))
                                        .append("heartbeat",list.get(3));
        db.getCollection("user").updateOne(new Document("username",username),
                new Document("$set",new Document("realtime",upload)));
    }

    public void updateRun(ArrayList<Double> list, String username){

        MongoClient mongoClient = new MongoClient(ipaddr, port);
        MongoDatabase db = mongoClient.getDatabase("reconjet");
        Document upload = new Document().append("avg_speed",list.get(0))
                                        .append("mile",list.get(1))
                                        .append("pace",list.get(2))
                                        .append("avg_heartbeat",list.get(3));

        db.getCollection("user").updateOne(new Document("username",username),
                new Document("$push",new Document("running",upload)));
        mongoClient.close();

    }

    public ArrayList<String> getUserInfo(String username){
        MongoClient mongoClient = new MongoClient(ipaddr, port);
        MongoDatabase db = mongoClient.getDatabase("reconjet");
        MongoCollection uc= db.getCollection("user");
        Document document = new Document("username",username);
        uc.find(new Document("username",username));
        return null;
    }
}
