package com.eims.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eims.dto.ApiResponse;
import com.eims.dto.PageRequest;
import com.eims.entity.PurchaseOrder;
import com.eims.entity.PurchaseOrderItem;
import com.eims.repository.PurchaseOrderItemRepository;
import com.eims.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/order")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderRepository orderRepository;

    @Autowired
    private PurchaseOrderItemRepository itemRepository;

    @GetMapping("/list")
    public ApiResponse<Page<PurchaseOrder>> list(PageRequest pageRequest) {
        Page<PurchaseOrder> page = new Page<>(pageRequest.getPage(), pageRequest.getPageSize());
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();

        if (pageRequest.getKeyword() != null && !pageRequest.getKeyword().isEmpty()) {
            wrapper.like(PurchaseOrder::getCode, pageRequest.getKeyword());
        }

        wrapper.orderByDesc(PurchaseOrder::getCreateTime);
        Page<PurchaseOrder> result = orderRepository.selectPage(page, wrapper);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<PurchaseOrder> getById(@PathVariable Long id) {
        PurchaseOrder order = orderRepository.selectById(id);
        if (order == null) {
            return ApiResponse.error("订单不存在");
        }
        return ApiResponse.success(order);
    }

    @GetMapping("/{id}/items")
    public ApiResponse<List<PurchaseOrderItem>> getItems(@PathVariable Long id) {
        LambdaQueryWrapper<PurchaseOrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchaseOrderItem::getOrderId, id);
        List<PurchaseOrderItem> items = itemRepository.selectList(wrapper);
        return ApiResponse.success(items);
    }

    @PostMapping
    public ApiResponse<PurchaseOrder> create(@RequestBody PurchaseOrder order,
                                             @RequestAttribute("userId") Long userId) {
        // 生成单号
        String code = generateCode();
        order.setCode(code);
        order.setCreateUserId(userId);
        order.setStatus("DRAFT");

        // 计算总金额
        BigDecimal total = BigDecimal.ZERO;
        if (order.getTotalAmount() != null) {
            total = order.getTotalAmount();
        }

        orderRepository.insert(order);
        return ApiResponse.success("创建成功", order);
    }

    @PostMapping("/{id}/items")
    public ApiResponse<Void> addItems(@PathVariable Long id, @RequestBody List<PurchaseOrderItem> items) {
        for (PurchaseOrderItem item : items) {
            item.setOrderId(id);
            if (item.getTotalPrice() == null && item.getQuantity() != null && item.getUnitPrice() != null) {
                item.setTotalPrice(item.getQuantity().multiply(item.getUnitPrice()));
            }
            itemRepository.insert(item);
        }

        // 更新订单总金额
        updateOrderTotal(id);

        return ApiResponse.success("添加成功", null);
    }

    @PutMapping("/{id}/confirm")
    public ApiResponse<Void> confirm(@PathVariable Long id) {
        PurchaseOrder order = orderRepository.selectById(id);
        if (order == null) {
            return ApiResponse.error("订单不存在");
        }

        order.setStatus("CONFIRMED");
        orderRepository.updateById(order);
        return ApiResponse.success("订单已确认", null);
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Long id) {
        PurchaseOrder order = orderRepository.selectById(id);
        if (order == null) {
            return ApiResponse.error("订单不存在");
        }

        order.setStatus("CANCELLED");
        orderRepository.updateById(order);
        return ApiResponse.success("订单已取消", null);
    }

    private void updateOrderTotal(Long orderId) {
        LambdaQueryWrapper<PurchaseOrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchaseOrderItem::getOrderId, orderId);
        List<PurchaseOrderItem> items = itemRepository.selectList(wrapper);

        BigDecimal total = items.stream()
                .map(PurchaseOrderItem::getTotalPrice)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PurchaseOrder order = orderRepository.selectById(orderId);
        order.setTotalAmount(total);
        orderRepository.updateById(order);
    }

    private String generateCode() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(PurchaseOrder::getCode, "PO-" + date);
        wrapper.orderByDesc(PurchaseOrder::getCode);
        wrapper.last("LIMIT 1");

        PurchaseOrder last = orderRepository.selectOne(wrapper);
        int seq = 1;
        if (last != null) {
            String lastCode = last.getCode();
            String lastSeq = lastCode.substring(lastCode.lastIndexOf("-") + 1);
            seq = Integer.parseInt(lastSeq) + 1;
        }

        return String.format("PO-%s-%03d", date, seq);
    }
}
