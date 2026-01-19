package edu.nchu.mall.components.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.boot.jackson.JsonComponent;
import edu.nchu.mall.models.model.R;

import java.io.IOException;

@JsonComponent
public class RSerializer<T> extends StdSerializer<R<T>> {
    protected RSerializer() {
        this(null);
    }

    protected RSerializer(Class<R<T>> clazz) {
        super(clazz);
    }

    @Override
    public void serialize(R t, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("code", String.valueOf(t.getCode().getInteger()));
        jsonGenerator.writeStringField("msg", t.getMsg());
        if (t.getData() != null) {
            jsonGenerator.writeFieldName("data");
            serializerProvider.defaultSerializeValue(t.getData(), jsonGenerator);
        } else {
            jsonGenerator.writeNullField("data");
        }
        jsonGenerator.writeEndObject();
    }
}
