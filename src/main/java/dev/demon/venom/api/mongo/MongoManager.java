package dev.demon.venom.api.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


public class MongoManager {

    public MongoDatabase mongoDatabase;
    public MongoClient mongoClient;
    public MongoCollection<Document> collection;

    public MongoManager() {
        mongoClient = new MongoClient("localhost");
        mongoDatabase = mongoClient.getDatabase("venom");
        collection = mongoDatabase.getCollection("logs");
    }

    public MongoCollection<Document> getLogs() {
        return collection;
    }
}
