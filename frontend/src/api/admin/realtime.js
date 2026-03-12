import axios from 'axios'

const s = axios.create({ baseURL: '/bigdata-api/api/realtime', timeout: 10000 })
s.interceptors.response.use(r => r.data, e => { console.error('[RT API]', e); return Promise.reject(e) })

export const fetchSnapshot = () => s.get('/snapshot')

export class Poller {
  constructor(onData, onStatus) { this.onData = onData; this.onStatus = onStatus; this.t = null }
  start(ms = 3000) {
    this.stop(); this.onStatus?.('connected'); this._p()
    this.t = setInterval(() => this._p(), ms)
  }
  async _p() {
    try { const d = await fetchSnapshot(); this.onData?.(d) }
    catch { this.onStatus?.('error') }
  }
  stop() { if (this.t) { clearInterval(this.t); this.t = null }; this.onStatus?.('disconnected') }
}
