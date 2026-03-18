import request from '@/utils/request'

/**
 * 创建支付订单
 * @param {Object} data - 订单数据
 * @param {string} data.orderType - 订单类型 (RECHARGE/VIP_SUBSCRIPTION/TEMPLATE_PURCHASE)
 * @param {number} data.amount - 金额
 * @param {string} data.payChannel - 支付渠道 (ALIPAY/WECHAT/BALANCE)
 * @param {string} data.bizContent - 业务参数 JSON
 */
export function createOrder(data) {
  return request({
    url: '/orders',
    method: 'post',
    data
  })
}

/**
 * 查询订单支付状态
 * @param {string} orderId - 订单号
 */
export function getOrderStatus(orderId) {
  return request({
    url: `/orders/${orderId}`,
    method: 'get'
  })
}

/**
 * 取消待支付订单
 * @param {string} orderId - 订单号
 */
export function cancelOrder(orderId) {
  return request({
    url: `/orders/${orderId}/cancel`,
    method: 'post'
  })
}
