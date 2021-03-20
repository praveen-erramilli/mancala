package com.praveen.mancala.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class CustomPitSerializer extends StdSerializer<Pit> {

    public CustomPitSerializer() {
        this(null);
    }
    protected CustomPitSerializer(Class<Pit> t) {
        super(t);
    }

    @Override
    public void serialize(Pit pit, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeObject(pit.getId());
    }
}
