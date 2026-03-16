package com.huike.video.modules.payment.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.huike.video.modules.payment.config.AlipayProperties;
import com.huike.video.modules.payment.service.AlipayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 支付宝交互服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlipayServiceImpl implements AlipayService {

    private final AlipayClient alipayClient;
    private final AlipayProperties alipayProperties;

    @Override
    public String createPagePayForm(String outTradeNo, String totalAmount, String subject) {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(alipayProperties.getNotifyUrl());
        request.setReturnUrl(alipayProperties.getReturnUrl());

        // 构建业务参数 JSON
        String bizContent = String.format(
                "{\"out_trade_no\":\"%s\",\"total_amount\":\"%s\",\"subject\":\"%s\",\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}",
                outTradeNo, totalAmount, subject
        );
        request.setBizContent(bizContent);

        try {
            // 调用 SDK 生成支付表单 HTML
            String form = alipayClient.pageExecute(request).getBody();
            log.info("支付宝下单成功, outTradeNo={}, amount={}", outTradeNo, totalAmount);
            return form;
        } catch (AlipayApiException e) {
            log.error("支付宝下单失败, outTradeNo={}, error={}", outTradeNo, e.getMessage(), e);
            throw new RuntimeException("支付宝下单失败: " + e.getErrMsg());
        }
    }

    @Override
    public boolean verifyCallback(Map<String, String> params) {
        try {
            boolean result = AlipaySignature.rsaCheckV1(
                    params,
                    alipayProperties.getAlipayPublicKey(),
                    "UTF-8",
                    alipayProperties.getSignType()
            );
            if (!result) {
                log.warn("支付宝回调验签失败, params={}", params);
            }
            return result;
        } catch (AlipayApiException e) {
            log.error("支付宝回调验签异常, error={}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String queryTradeStatus(String outTradeNo) {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizContent(String.format("{\"out_trade_no\":\"%s\"}", outTradeNo));

        try {
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                log.info("支付宝查询成功, outTradeNo={}, tradeStatus={}", outTradeNo, response.getTradeStatus());
                return response.getTradeStatus();
            } else {
                log.warn("支付宝查询返回失败, outTradeNo={}, subCode={}, subMsg={}",
                        outTradeNo, response.getSubCode(), response.getSubMsg());
                return null;
            }
        } catch (AlipayApiException e) {
            log.error("支付宝查询异常, outTradeNo={}, error={}", outTradeNo, e.getMessage(), e);
            return null;
        }
    }
}
