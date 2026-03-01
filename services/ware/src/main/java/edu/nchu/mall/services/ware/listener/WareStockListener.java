package edu.nchu.mall.services.ware.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.rabbitmq.client.Channel;
import edu.nchu.mall.components.feign.order.OrderFeignClient;
import edu.nchu.mall.models.dto.mq.StockLocked;
import edu.nchu.mall.models.entity.Order;
import edu.nchu.mall.models.entity.WareOrderTask;
import edu.nchu.mall.models.entity.WareOrderTaskDetail;
import edu.nchu.mall.models.enums.OrderStatus;
import edu.nchu.mall.models.enums.WareOrderLockStatus;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.models.model.Try;
import edu.nchu.mall.services.ware.dao.WareSkuMapper;
import edu.nchu.mall.services.ware.service.WareOrderTaskDetailService;
import edu.nchu.mall.services.ware.service.WareOrderTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RabbitListener(queues = "stock.release.queue")
public class WareStockListener {

    @Autowired
    WareOrderTaskService wareOrderTaskService;

    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    WareSkuMapper wareSkuMapper;

    @Autowired
    OrderFeignClient orderFeignClient;

    @RabbitHandler
    @Transactional(rollbackFor = Exception.class)
    public void handleStockRelease(StockLocked stock, Channel channel, Message message) throws IOException {
        log.info("解锁库存...");

        WareOrderTask task = wareOrderTaskService.getById(stock.getTaskId());

        Order order = null;
        if (task != null) {
            var orderTry = Try.of(orderFeignClient::getOrderBySn, task.getOrderSn());
            if (orderTry.succeeded()) {
                R<Order> res = orderTry.getFirst();
                if (res.getCode() == RCT.SUCCESS) {
                    // -- nullable --
                    order = orderTry.getValue().getData();
                }
            } else {
                log.error("无法获取订单信息: ");
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            }
        }

        if (task != null && (order == null || order.getStatus() == OrderStatus.CLOSED)) {
            LambdaQueryWrapper<WareOrderTaskDetail> qw = Wrappers.lambdaQuery();
            qw.in(WareOrderTaskDetail::getId, stock.getDetailIds());
            List<WareOrderTaskDetail> details = wareOrderTaskDetailService.list(qw);

            for (WareOrderTaskDetail detail : details) {
                if (detail.getLockStatus() != WareOrderLockStatus.LOCKED) {
                    continue;
                }

                Long wareId = detail.getWareId();
                Long skuId = detail.getSkuId();
                wareSkuMapper.unlockStock(wareId, skuId, detail.getSkuNum());
                wareOrderTaskDetailService.update(
                        Wrappers.<WareOrderTaskDetail>lambdaUpdate()
                                .set(WareOrderTaskDetail::getLockStatus, WareOrderLockStatus.UNLOCKED.getValue())
                                .eq(WareOrderTaskDetail::getId, detail.getId())
                );
            }
        }

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitHandler
    @Transactional
    public void handleStockRelease(Order order, Channel channel, Message message) throws IOException {
        log.info("订单[sn={}]关闭，解锁库存...", order.getOrderSn());

        var taskTry = Try.of(wareOrderTaskService::getOne, Wrappers.<WareOrderTask>lambdaQuery().eq(WareOrderTask::getOrderSn, order.getOrderSn()));
        if (taskTry.succeeded()) {
            WareOrderTask task = taskTry.getValue();
            if (task != null) {
                LambdaQueryWrapper<WareOrderTaskDetail> qw = Wrappers.lambdaQuery();
                qw.eq(WareOrderTaskDetail::getTaskId, task.getId());
                List<WareOrderTaskDetail> details = wareOrderTaskDetailService.list(qw);

                for (WareOrderTaskDetail detail : details) {
                    if (detail.getLockStatus() != WareOrderLockStatus.LOCKED) {
                        continue;
                    }

                    Long wareId = detail.getWareId();
                    Long skuId = detail.getSkuId();
                    wareSkuMapper.unlockStock(wareId, skuId, detail.getSkuNum());
                    wareOrderTaskDetailService.update(
                            Wrappers.<WareOrderTaskDetail>lambdaUpdate()
                                    .set(WareOrderTaskDetail::getLockStatus, WareOrderLockStatus.UNLOCKED.getValue())
                                    .eq(WareOrderTaskDetail::getId, detail.getId())
                    );
                }
            }
        } else {
            log.error("无法获取库存锁定信息: ");
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
