package com.merxport.trading.codecs;

import com.merxport.trading.enumerations.UserRole;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class UserRoleCodec implements Codec<UserRole>
{
    @Override
    public UserRole decode(BsonReader bsonReader, DecoderContext decoderContext)
    {
        return UserRole.fromValue(bsonReader.readString());
    }
    
    @Override
    public void encode(BsonWriter bsonWriter, UserRole status, EncoderContext encoderContext)
    {
        bsonWriter.writeString(status.getValue());
    }
    
    @Override
    public Class<UserRole> getEncoderClass()
    {
        return UserRole.class;
    }
    
}
