// 页面活动监测器
class ActivityMonitor {
  constructor() {
    this.timeout = null
    this.inactivityTime = 1 * 60 * 1000 // 1分钟
    this.isActive = false
    this.onLogout = null
    this.init()
  }

  init() {
    // 监听用户活动事件
    const events = ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart', 'click']
    events.forEach(event => {
      document.addEventListener(event, () => this.resetTimer(), true)
    })
    // 移除visibilitychange监听，不再切后台暂停计时
  }

  start(onLogout) {
    this.onLogout = onLogout
    this.isActive = true
    this.resetTimer()
  }

  stop() {
    this.isActive = false
    if (this.timeout) {
      clearTimeout(this.timeout)
      this.timeout = null
    }
  }

  resetTimer() {
    if (!this.isActive) return
    
    if (this.timeout) {
      clearTimeout(this.timeout)
    }
    
    this.timeout = setTimeout(() => {
      if (this.isActive && this.onLogout) {
        this.onLogout()
      }
    }, this.inactivityTime)
  }

  pauseTimer() {
    if (this.timeout) {
      clearTimeout(this.timeout)
      this.timeout = null
    }
  }

  resumeTimer() {
    if (this.isActive) {
      this.resetTimer()
    }
  }
}

// 创建全局实例
const activityMonitor = new ActivityMonitor()

export default activityMonitor 