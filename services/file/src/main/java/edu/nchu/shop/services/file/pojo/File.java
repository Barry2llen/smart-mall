package edu.nchu.shop.services.file.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "上传成功的文件信息")
@AllArgsConstructor
@NoArgsConstructor
public class File {
    @Schema(description = "文件访问的url地址")
    private String url;
}
