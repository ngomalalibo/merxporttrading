package com.merxport.trading.codecs;

import com.merxport.trading.enumerations.UserScopes;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class UserScopesCodec implements Codec<UserScopes>
{
    @Override
    public UserScopes decode(BsonReader bsonReader, DecoderContext decoderContext)
    {
        return UserScopes.fromValue(bsonReader.readString());
    }
    
    @Override
    public void encode(BsonWriter bsonWriter, UserScopes status, EncoderContext encoderContext)
    {
        bsonWriter.writeString(status.getValue());
    }
    
    @Override
    public Class<UserScopes> getEncoderClass()
    {
        return UserScopes.class;
    }
    
}
