package com.merxport.trading.codecs;

import com.merxport.trading.enumerations.CommercialTerms;
import com.merxport.trading.enumerations.Scopes;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class CommercialTermsCodec  implements Codec<CommercialTerms>
{
    @Override
    public CommercialTerms decode(BsonReader bsonReader, DecoderContext decoderContext)
    {
        return CommercialTerms.fromValue(bsonReader.readString());
    }
    
    @Override
    public void encode(BsonWriter bsonWriter, CommercialTerms status, EncoderContext encoderContext)
    {
        bsonWriter.writeString(status.getValue());
    }
    
    @Override
    public Class<CommercialTerms> getEncoderClass()
    {
        return CommercialTerms.class;
    }
}
