package edu.nchu.mall.components.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("file")
public interface OSSFeignClient {
    @PostMapping("/oss/get_post_signature_for_oss_upload")
    ResponseEntity<Map<String, String>>
    getPostSignatureForOssUpload(@RequestParam String callback,
                                 @RequestBody Map<String, Object> callbackVars) throws Exception;

    @DeleteMapping("/oss/object")
    ResponseEntity<?> deleteObject(@RequestParam String bucket, @RequestParam String filename);
}
