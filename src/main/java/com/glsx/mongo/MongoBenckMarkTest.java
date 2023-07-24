package com.glsx.mongo;

import com.glsx.connection.MysqlConn;
import com.glsx.util.DataUtils;
import com.glsx.util.DateUtils;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 基准测试 测试mysql1000万行时，JSON查询性能
 */
public class MongoBenckMarkTest {
    public static void main(String[] args) {
        long bgnTime = System.currentTimeMillis();

        MongoClient mongoClient = new MongoClient("192.168.3.221" , 27017);
        MongoDatabase database = mongoClient.getDatabase("glsx_scrm_label");
        MongoCollection<Document> collection = database.getCollection("user_tag");

//        Document document = new Document("title", "MongoDB").
//                append("description", "database").
//                append("likes", 100).
//                append("by", "Fly");
        List<Document> documents = new ArrayList<Document>();
        Long startOneId = 100000000000000L;
        Random random = new Random();
        for (int i = 0; i < 10000000; i++) { // 构建1000w基础数据
            Document document = new Document("one_id", Long.toString(startOneId+i)).
                    append("merchant_id", 400000+random.nextInt(1200));

            Document document1 = getRandomDocumentJson(document, random);
            documents.add(document1);

            if (i % 5999 == 0) {
                collection.insertMany(documents);
                documents = new ArrayList<>();
            }

            if (i % 99999 == 0) System.out.println("sumCount=" + i);
        }

        if (documents.size() > 0 ) {
            collection.insertMany(documents);
        }

        System.out.println("The job total cost " + (System.currentTimeMillis()-bgnTime)/1000 + "s");
    }

    private static Document getRandomDocumentJson(Document document, Random random) {
//        Document document = new Document();
        String kvs = "";
        int tagCount = random.nextInt(30)+1;
        int staffCount = random.nextInt(2) + 1;


        for (int index = 0; index < tagCount; index++) {

            Document staffDocument = new Document();

            for (int i = 0; i < staffCount; i++) {

                if (staffCount == 1 && random.nextInt(4) % 2 == 0){
                    staffDocument.append("type", 0);
                    String staffId = "staff_system";
                    staffDocument.append(staffId,
                            new Document("value",  random.nextInt(2400)+1).append("update_time", DataUtils.getToday()));
                }
                else{
                    staffDocument.append("type", 1);
                    String staffId = "staff_" + 1000 + i;
                    staffDocument.append(staffId,
                            new Document("value",  random.nextInt(2400)+1).append("update_time", DataUtils.getToday()));
                }

            }
            return  document.append("g"+random.nextInt(1200)+1, staffDocument);
        }
        return document;
    }

    private static String getRandomJson(Random random) {
        String kvs = "";
        int tagCount = random.nextInt(30)+1;
        for (int index = 0; index < tagCount; index++) {
            kvs += "\"g" + random.nextInt(1200)+1 + "\":\"tag_" + random.nextInt(2400)+1 + "\",";
        }
        return "{" + kvs.substring(0,kvs.length()-1) + "}";
    }
}
