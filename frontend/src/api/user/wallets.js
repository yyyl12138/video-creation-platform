import request from '@/utils/request'

/**
 * ==================== 钱包资产服务 (Wallet Service) ====================
 */

/**
 * 3.1 获取钱包余额
 */
export function getWalletBalance() {
  return request({
    url: '/wallets/me',
    method: 'get'
  })
}

/**
 * 3.2 获取交易流水
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页条数
 * @param {string} params.type - 交易类型: RECHARGE, CONSUME
 */
export function getTransactions(params) {
  return request({
    url: '/wallets/me/transactions',
    method: 'get',
    params
  })
}

/**
 * 3.3 发起充值
 * @param {Object} data - 充值数据
 * @param {number} data.packageId - 充值套餐ID
 * @param {string} data.payChannel - 支付方式: ALIPAY, WECHAT
 */
export function recharge(data) {
  return request({
    url: '/wallets/me/recharge',
    method: 'post',
    data
  })
}

/**
 * 3.4 发起提现 (创作者)
 * @param {Object} data - 提现数据
 * @param {number} data.amount - 提现金额
 * @param {Object} data.accountInfo - 收款账号信息
 */
export function withdraw(data) {
  return request({
    url: '/wallets/me/withdraw',
    method: 'post',
    data
  })
}
