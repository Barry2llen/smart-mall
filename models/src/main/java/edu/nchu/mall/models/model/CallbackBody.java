package edu.nchu.mall.models.model;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.nchu.mall.models.strategy.PrefixNamingStrategy;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Data
@Slf4j
public class CallbackBody {
    @NotNull
    private String filename;
    @NotNull
    private String size;
    @NotNull
    private String mimeType;
    @NotNull
    private String ip;

    private static final List<String> initFields = List.of("filename", "size", "mimeType", "ip");

    public void log(){
        log.info("IP {} uploaded file \"{}\", size {}, type {}",  ip, filename, size, mimeType);
    }

    public static String callbackBody(Map<String, Object> vars){

        StringBuilder body = new StringBuilder("""
                        {\
                            "filename":${object},\
                            "size":${size},\
                            "mimeType":${mimeType},\
                            "ip":${clientIp}\
                """);

        for (String key : vars.keySet()) {
            if (initFields.contains(key)) {
                continue;
            }
            body.append(String.format(",\"%s\":${x:%s}", key, key));
        }
        body.append("}");
        return body.toString();
    }

    public static String callbackVar(Map<String, Object> vars) {
        JSONObject obj = new JSONObject();
        for(Map.Entry<String, Object> entry : vars.entrySet()){
            obj.put("x:" + entry.getKey(), entry.getValue());
        }
        return obj.toJSONString();
    }

    public static String callbackVar(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(new PrefixNamingStrategy("x:"));
        return mapper.writeValueAsString(obj);
    }
}
