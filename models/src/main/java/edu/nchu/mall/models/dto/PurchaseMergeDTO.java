package edu.nchu.mall.models.dto;

import edu.nchu.mall.models.annotation.validation.NotNullCollection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "采购单合并信息")
public class PurchaseMergeDTO {

    @Schema(description = "采购单ID，不传则新建")
    private Long purchaseId;

    @Schema(description = "要合并的采购需求的id")
    @NotNullCollection(message = "要合并的采购需求不能为空")
    private List<Long> purchaseDetailIds;
}
