package edu.nchu.mall.models.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_shop")
public class Shop {

    @Schema(description = "店铺ID")
    @TableId(value = "ID", type = IdType.ASSIGN_ID)
    private Long ID;

    @Schema(description = "商户ID")
    @TableField("owner_user_id")
    private Long ownerUserID;

    @Schema(description = "商铺名称")
    private String name;


}
