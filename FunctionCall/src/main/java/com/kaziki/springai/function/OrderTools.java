package com.kaziki.springai.function;


import com.kaziki.springai.service.OrderManageService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderTools {
    @Autowired
    private OrderManageService orderManageService;
    @Tool(name = "申请退款",description = "根据用户传入申请退款")
    public String refund(@ToolParam(description = "订单编号，为数字类型") String orderId, @ToolParam(description = "商品名称") String name, @ToolParam(description = "退款原因") String reason){
        System.out.println("已为商品"+name+",订单号:"+orderId+"申请退款，原因:"+reason);
        orderManageService.refund(orderId,reason);
        return "已为商品"+name+",订单号:"+orderId+"申请退款，原因:"+reason;
    }
}
