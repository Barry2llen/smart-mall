package edu.nchu.shop.services.file;

import edu.nchu.shop.services.file.properties.OSSProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class FileApplicationTests {

    @Autowired
    OSSProperties ossProperties;

    @Test
    void contextLoad(){
        System.out.println(ossProperties.getOss().getBucket());
    }
}
