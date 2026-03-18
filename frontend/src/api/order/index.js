import request from '@/utils/request'

/**
 * ==================== 订单与支付服务 (Order & Payment Service) ====================
 */

/**
 * 1.1 创建支付订单
 * @param {Object} data - 订单数据
 * @param {string} data.orderType - 订单类型: RECHARGE, VIP_SUBSCRIPTION, TEMPLATE_PURCHASE
 * @param {number} data.amount - 金额 (>0.01)
 * @param {string} data.payChannel - 支付方式: ALIPAY, WECHAT, BALANCE
 * @param {Object} data.bizContent - 业务参数
 *   - 充值: { pkgId }
 *   - 会员: { planId }
 *   - 模版: { templateId }
 */
export function createOrder(data) {
  return request({
    url: '/orders',
    method: 'post',
    data
  })
}

/**
 * 1.2 查询订单状态
 * @param {string} orderId - 订单ID
 */
export function getOrderStatus(orderId) {
  return request({
    url: `/orders/${orderId}`,
    method: 'get'
  })
}

/**
 * 1.3 支付回调通知 (Webhook) - 内部使用，前端不直接调用
 * @param {string} channel - 支付渠道: alipay, wechat
 * @param {Object} data - 回调数据
 */
export function paymentCallback(channel, data) {
  return request({
    url: `/callbacks/payment/${channel}`,
    method: 'post',
    data
  })
}

/**
 * 1.4 取消订单
 * @param {string} orderId - 订单ID
 */
export function cancelOrder(orderId) {
  return request({
    url: `/orders/${orderId}/cancel`,
    method: 'post'
  })
}

/**
 * 1.5 获取订单列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页条数
 * @param {string} params.orderType - 订单类型
 * @param {string} params.status - 订单状态: PENDING, PAID, CANCELLED, REFUNDED
 */
export function getOrderList(params) {
  return request({
    url: '/orders',
    method: 'get',
    params
  })
}

/**
 * ==================== 充值套餐服务 (Recharge Package Service) ====================
 */

/**
 * 获取充值套餐列表
 */
export function getRechargePackages() {
  return request({
    url: '/recharge/packages',
    method: 'get'
  })
}

/**
 * ==================== 会员套餐服务 (VIP Plan Service) ====================
 */

/**
 * 获取会员套餐列表
 */
export function getVipPlans() {
  return request({
    url: '/vip/plans',
    method: 'get'
  })
}
