package edu.nchu.mall.models.dto;

import edu.nchu.mall.models.validation.Groups;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "spu信息")
public class SpuInfoDTO {

    @Null(groups = Groups.Update.class)
    @Schema(description = "商品id")
    private Long id;
    
    @Schema(description = "商品名称")
    private String spuName;
    
    @Schema(description = "商品描述")
    private String spuDescription;

    @Schema(description = "weight")
    private BigDecimal weight;

    @Schema(description = "上架状态[0 - 下架，1 - 上架]")
    private Integer publishStatus;

    @Schema(description = "创建时间")
    private Date createTime;
    
    @Schema(description = "更新时间")
    private Date updateTime;
}
