package edu.nchu.mall.services.product.exception;

public class RedisDeserializeException extends RuntimeException{
    private String key;
    public RedisDeserializeException(String key){
        this.key = key;
    }
    public RedisDeserializeException(String key,String message){
        super(message);
        this.key = key;
    }
    public RedisDeserializeException(String key,String message,Throwable cause){
        super(message,cause);
        this.key = key;
    }
    public RedisDeserializeException(Throwable cause){
        super(cause);
    }
}
