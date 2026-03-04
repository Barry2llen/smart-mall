package edu.nchu.mall.models.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "秒杀活动场次视图")
public class SeckillSessionVO {
    
    @Schema(description = "id")
    private Long id;

    
    @Schema(description = "场次名称")
    private String name;

    
    @Schema(description = "每日开始时间")
    private LocalDateTime startTime;

    
    @Schema(description = "每日结束时间")
    private LocalDateTime endTime;

    
    @Schema(description = "启用状态")
    private Integer status;

    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;


    @Schema(description = "关联的秒杀商品列表")
    private List<SeckillSkuRelationVO> relations;
}
