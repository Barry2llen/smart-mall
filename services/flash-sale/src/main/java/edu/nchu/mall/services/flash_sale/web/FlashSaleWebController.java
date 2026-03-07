package edu.nchu.mall.services.flash_sale.web;

import edu.nchu.mall.models.annotation.bind.UserId;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.services.flash_sale.dto.Kill;
import edu.nchu.mall.services.flash_sale.rentity.FlashSaleSession;
import edu.nchu.mall.services.flash_sale.service.FlashSaleService;
import edu.nchu.mall.services.flash_sale.vo.OrderConfirm;
import edu.nchu.mall.services.flash_sale.vo.SessionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "秒杀活动接口", description = "提供秒杀活动相关的API")
@RestController
@RequestMapping("/public/flash-sale")
public class FlashSaleWebController {

    @Autowired
    FlashSaleService flashSaleService;

    @Parameters({
            @Parameter(name = "withExpired", description = "是否包含已过期的场次，默认为false", required = false),
            @Parameter(name = "withProducts", description = "是否包含商品信息，默认为false", required = false),
            @Parameter(name = "pageNum", description = "页码（从1开始），默认为1", required = false),
            @Parameter(name = "pageSize", description = "每页数量（1-100），默认为10", required = false)
    })
    @Operation(description = "获取秒杀活动场次列表")
    @GetMapping("/sessions")
    public R<List<SessionVO>> getFlashSaleSessions(
            @RequestParam(required = false, defaultValue = "false") Boolean withExpired,
            @RequestParam(required = false, defaultValue = "false") Boolean withProducts,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize
    ) {
        if (pageNum < 1 || pageSize < 1 || pageSize > 100) {
            return R.fail("pageNum must be >= 1 and pageSize must be between 1 and 100");
        }
        return R.success(flashSaleService.getSession(withExpired, withProducts, pageNum, pageSize));
    }

    @Parameters({@Parameter(description = "SKU主键", name = "skuId", required = true)})
    @Operation(description = "检查某个SKU是否参与了秒杀活动")
    @GetMapping("/sku/{skuId}/in-flash-sale")
    public R<Boolean> isSkuInFlashSale(@UserId Long userId, @PathVariable Long skuId) {
        return R.success(flashSaleService.isSkuInFlashSale(userId, skuId));
    }

    @Parameters({@Parameter(description = "SKU主键", name = "skuId", required = true)})
    @Operation(description = "获取某个SKU参与的秒杀活动场次列表")
    @GetMapping("/sku/{skuId}/sessions")
    public R<List<FlashSaleSession>> getFlashSaleSessionsBySkuId(@PathVariable Long skuId) {
        return R.success(flashSaleService.getFlashSaleSessionsBySkuId(skuId));
    }

    @Parameters({
            @Parameter(description = "场次主键", name = "sessionId", required = true),
            @Parameter(description = "是否包含商品信息，默认为false", name = "withProducts", required = false)
    })
    @Operation(description = "获取秒杀活动场次详情")
    @GetMapping("/session/{sessionId}")
    public R<SessionVO> getFlashSaleSessionById(
            @PathVariable Long sessionId,
            @RequestParam(required = false, defaultValue = "false") Boolean withProducts
    ) {
        return R.success(flashSaleService.getSessionById(sessionId, withProducts));
    }

    @Parameters({
            @Parameter(description = "场次主键", name = "sessionId", required = true),
            @Parameter(description = "商品主键", name = "skuId", required = true),
            @Parameter(description = "购买数量", name = "num", required = true)
    })
    @Operation(description = "确认秒杀订单信息")
    @GetMapping
    public R<OrderConfirm> confirmOrder(@UserId Long userId, @RequestParam Long sessionId, @RequestParam Long skuId, @RequestParam int num) {
        OrderConfirm orderConfirm = flashSaleService.confirmOrder(userId, sessionId, skuId, num);
        return R.result(orderConfirm);
    }

    @Parameters({
            @Parameter(description = "商品sku id", name = "skuId", required = true),
            @Parameter(description = "秒杀随机码", name = "randomCode", required = true),
            @Parameter(description = "秒杀场次id", name = "sessionId", required = true),
            @Parameter(description = "购买数量", name = "num", required = true)
    })
    @Operation(description = "执行秒杀请求")
    @PostMapping("/kill")
    public R<?> kill(@UserId Long userId, @RequestBody Kill dto) {
        R<?> res = null;
        FlashSaleService.KillStatus status;

        try {
            status = flashSaleService.kill(userId, dto);
        } catch (Throwable e) {
            status = FlashSaleService.KillStatus.ERROR;
        }

        switch (status) {
            case SUCCEEDED ->  res = R.success(FlashSaleService.KillStatus.SUCCEEDED.getMessage(), FlashSaleService.ORDER.get().getOrderSn());
            case ERROR -> {
                res = R.fail(FlashSaleService.KillStatus.ERROR.getMessage());
                if (FlashSaleService.ORDER.get() != null) {
                    flashSaleService.deleteUserPurchaseRecord(userId, dto.getSessionId(), dto.getSkuId(), dto.getRandomCode(), dto.getNum());
                }
            }
            default -> res = R.fail(status.getMessage());
        }

        FlashSaleService.ORDER.remove();
        return res;
    }

}
