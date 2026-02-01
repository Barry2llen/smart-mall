package edu.nchu.mall.models.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import edu.nchu.mall.models.validation.Groups;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("wms_ware_info")
@Schema(description = "仓库信息")
public class WareInfo {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    @NotNull(groups = Groups.Update.class)
    @Null(groups = Groups.Create.class)
    private Long id;

    @TableField("name")
    @Schema(description = "仓库名")
    private String name;

    @TableField("address")
    @Schema(description = "仓库地址")
    private String address;

    @TableField("areacode")
    @Schema(description = "区域编码")
    private String areacode;
}
