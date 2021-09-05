package com.merxport.trading.codecs;

import com.merxport.trading.enumerations.UserRole;
import com.merxport.trading.enumerations.UserType;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class UserTypeCodec implements Codec<UserType>
{
    @Override
    public UserType decode(BsonReader bsonReader, DecoderContext decoderContext)
    {
        return UserType.fromValue(bsonReader.readString());
    }
    
    @Override
    public void encode(BsonWriter bsonWriter, UserType status, EncoderContext encoderContext)
    {
        bsonWriter.writeString(status.getValue());
    }
    
    @Override
    public Class<UserType> getEncoderClass()
    {
        return UserType.class;
    }
    
}
