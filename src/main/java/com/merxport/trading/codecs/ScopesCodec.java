package com.merxport.trading.codecs;

import com.merxport.trading.enumerations.Scopes;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class ScopesCodec implements Codec<Scopes>
{
    @Override
    public Scopes decode(BsonReader bsonReader, DecoderContext decoderContext)
    {
        return Scopes.fromValue(bsonReader.readString());
    }
    
    @Override
    public void encode(BsonWriter bsonWriter, Scopes status, EncoderContext encoderContext)
    {
        bsonWriter.writeString(status.getValue());
    }
    
    @Override
    public Class<Scopes> getEncoderClass()
    {
        return Scopes.class;
    }
    
}
