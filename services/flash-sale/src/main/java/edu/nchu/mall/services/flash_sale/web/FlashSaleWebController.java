package edu.nchu.mall.services.flash_sale.web;

import edu.nchu.mall.models.model.R;
import edu.nchu.mall.services.flash_sale.service.FlashSaleService;
import edu.nchu.mall.services.flash_sale.vo.SessionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
