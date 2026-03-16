package com.huike.video.modules.payment.service;

import java.util.Map;

/**
 * 支付宝交互服务
 * 封装与支付宝 SDK 的所有交互逻辑
 */
public interface AlipayService {

    /**
     * 创建支付宝电脑网站支付表单
     * @param outTradeNo 系统订单号
     * @param totalAmount 总金额 (元,保留两位小数)
     * @param subject 商品描述
     * @return 支付宝返回的 HTML 表单字符串 (前端直接渲染即可跳转)
     */
    String createPagePayForm(String outTradeNo, String totalAmount, String subject);

    /**
     * 验证支付宝异步回调签名
     * @param params 回调请求中的所有参数
     * @return 验签是否通过
     */
    boolean verifyCallback(Map<String, String> params);

    /**
     * 主动查询支付宝订单状态
     * @param outTradeNo 系统订单号
     * @return 支付宝交易状态 (WAIT_BUYER_PAY, TRADE_SUCCESS, TRADE_CLOSED 等)，查询失败返回 null
     */
    String queryTradeStatus(String outTradeNo);
}
