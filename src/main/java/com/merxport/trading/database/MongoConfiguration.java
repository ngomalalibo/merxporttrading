package com.merxport.trading.database;

import com.merxport.trading.entities.Commodity;
import com.merxport.trading.entities.User;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Slf4j
@Getter
@Configuration
public class MongoConfiguration extends AbstractMongoClientConfiguration
{
    
    @Autowired
    private MappingMongoConverter mongoConverter;
    
    /**
     * Connect to mongo database using database url stored in system variables.
     */
    private final String DBNAME = "merxporttrading";
    private final String DB_ORGANIZATION = "Merxport Commodities Trading.";
    private final String DB_USER = "user";
    private final String DB_COMMODITIES = "commodities";
    private MongoClient mongo = null;
    private MongoDatabase db = null;
    private MongoCollection<User> users;
    private MongoCollection<Commodity> commodity;
    
    
    private String DBSTR = String.format(System.getenv().get("MERXPORTDBURL"), 1, "majority"); // write and read concerns are adjustable
    private HashSet<String> cols = new HashSet<>();
    
    @Override
    public Collection getMappingBasePackages()
    {
        return Collections.singleton("com.merxport.trading");
    }
    
    @Override
    protected String getDatabaseName()
    {
        return DBNAME;
    }
    
    @Bean
    public GridFsTemplate gridFsTemplate()
    {
        return new GridFsTemplate(mongoDbFactory(), mongoConverter);
    }
    
    
    @Override
    public MongoClient mongoClient()
    {
        final CodecRegistry defaultCodecRegistry = MongoClientSettings.getDefaultCodecRegistry();
        final CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        final CodecRegistry cvePojoCodecRegistry = CodecRegistries.fromProviders(pojoCodecProvider);
        CodecRegistry codecRegistry = fromRegistries(defaultCodecRegistry, cvePojoCodecRegistry);
        ConnectionString connectionString = new ConnectionString(DBSTR);
        
        MongoClientSettings settings = MongoClientSettings.builder()
                                                          .applyConnectionString(connectionString)
                                                          .retryWrites(true)
                                                          .codecRegistry(codecRegistry)
                                                          .build();
        
        if (db == null)
        {
            mongo = MongoClients.create(settings);
            db = mongo.getDatabase(DBNAME);
            //getDBStats();
        }
        
        users = db.getCollection(DB_USER, User.class).withCodecRegistry(codecRegistry);
        commodity = db.getCollection(DB_COMMODITIES, Commodity.class).withCodecRegistry(codecRegistry);
        
        
        return mongo;
    }
    
    /**
     * Codecs are use to tell mongo how to handle conversion to and from java objects
     */
    
    // Connect to database
    public MongoDatabase getDB()
    {
        return db;
    }
    
    // disconnect from database at context shutdown
    @EventListener(ContextStoppedEvent.class)
    public void disconnectFromDB()
    {
        if (mongo != null)
        {
            mongo.close();
        }
        mongo = null;
        db = null;
        System.out.println("Database connection closed.");
    }
    
    public Document getDBStats()
    {
        MongoDatabase ds = mongo.getDatabase(DBNAME);
        Document stats = ds.runCommand(new Document("dbstats", 1024));
        System.out.println("DBStats: " + stats.toJson());
        
        return stats;
    }
}
