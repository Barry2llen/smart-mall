package edu.nchu.mall.models.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "保存SPU的DTO")
public class SpuSaveDTO {
    @Schema(description = "SPU名称")
    private String spuName;

    @Schema(description = "SPU描述")
    private String spuDescription;

    @Schema(description = "所属分类ID")
    private Long catalogId;

    @Schema(description = "所属品牌ID")
    private Long brandId;

    @Schema(description = "重量")
    private BigDecimal weight;

    @Schema(description = "发布状态")
    private Integer publishStatus;

    @Schema(description = "商品描述列表")
    private List<String> decript;

    @Schema(description = "商品描述图片列表")
    private List<String> images;

    @Schema(description = "商品积分信息")
    private Bounds bounds;

    @Schema(description = "商品规格参数")
    private List<BaseAttr> baseAttrs;

    @Schema(description = "SKU信息列表")
    private List<Skus> skus;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "商品积分")
    public static class Bounds {
        @Schema(description = "购物积分")
        private BigDecimal buyBounds;

        @Schema(description = "成长积分")
        private BigDecimal growBounds;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "商品规格参数")
    public static class BaseAttr {

        @Schema(description = "属性id")
        private Long attrId;

        @Schema(description = "属性值")
        private String attrValues;

        @Schema(description = "是否显示在介绍上")
        private Integer showDesc;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "SKU信息")
    public static class Skus {
        @Schema(description = "SKU名称")
        private String skuName;

        @Schema(description = "SKU描述")
        private String skuDesc;

        @Schema(description = "价格")
        private BigDecimal price;

        @Schema(description = "SKU标题")
        private String skuTitle;

        @Schema(description = "SKU副标题")
        private String skuSubtitle;

        @Schema(description = "SKU销售属性/规格属性列表")
        private List<Attr> attr;

        @Schema(description = "SKU图片列表")
        private List<Image> images;

        @Schema(description = "笛卡尔积")
        private List<String> descar;

        @Schema(description = "满减：满几件")
        private Integer fullCount;

        @Schema(description = "满减：打几折（0-1之间）")
        private BigDecimal discount;

        @Schema(description = "满减：是否叠加其他优惠（0-否，1-是）")
        private Integer countStatus;

        @Schema(description = "满减：满多少金额")
        private BigDecimal fullPrice;

        @Schema(description = "满减：减多少金额")
        private BigDecimal reducePrice;

        @Schema(description = "满减：是否叠加其他优惠（0-否，1-是）")
        private Integer priceStatus;

        @Schema(description = "会员价格列表")
        private List<MemberPrice> memberPrice;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @Schema(description = "SKU属性")
        public static class Attr {
            @Schema(description = "属性id")
            private Long attrId;

            @Schema(description = "属性名称")
            private String attrName;

            @Schema(description = "属性值")
            private String attrValue;
        }

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @Schema(description = "会员价格")
        public static class MemberPrice {
            @Schema(description = "会员等级id")
            private Long memberLevelId;

            @Schema(description = "会员等级名称")
            private String memberLevelName;

            @Schema(description = "会员价格")
            private BigDecimal memberPrice;
        }

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @Schema(description = "SKU图片")
        public static class Image{
            @Schema(description = "图片id")
            private String imgUrl;

            @Schema(description = "是否默认图片")
            private Integer defaultImg;
        }
    }

}
