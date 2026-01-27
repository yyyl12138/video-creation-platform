import request from '@/utils/request'

/**
 * 获取钱包余额
 */
export function getWalletBalance() {
  return request({
    url: '/wallets/me',
    method: 'get'
  })
}

/**
 * 获取交易流水
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页条数
 * @param {string} [params.type] - 交易类型（RECHARGE/CONSUME）
 */
export function getTransactions(params) {
  return request({
    url: '/wallets/me/transactions',
    method: 'get',
    params
  })
}