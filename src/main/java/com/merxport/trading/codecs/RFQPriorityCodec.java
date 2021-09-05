package com.merxport.trading.codecs;

import com.merxport.trading.entities.RFQ;
import com.merxport.trading.enumerations.RFQPriority;
import com.merxport.trading.enumerations.Scopes;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class RFQPriorityCodec implements Codec<RFQPriority>
{
    @Override
    public RFQPriority decode(BsonReader bsonReader, DecoderContext decoderContext)
    {
        return RFQPriority.fromValue(bsonReader.readString());
    }
    
    @Override
    public void encode(BsonWriter bsonWriter, RFQPriority status, EncoderContext encoderContext)
    {
        bsonWriter.writeString(status.getValue());
    }
    
    @Override
    public Class<RFQPriority> getEncoderClass()
    {
        return RFQPriority.class;
    }
    
}
