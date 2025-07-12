import Mock from 'mockjs'

// 设置延迟时间
Mock.setup({
  timeout: '200-600'
})

// 登录接口
Mock.mock('/login', 'post', (options) => {
  const data = JSON.parse(options.body)
  const { jobId, password } = data
  
  // 模拟登录验证
  if (jobId && password) {
    // 根据工号分配不同角色
    const roleMap = {
      '001': '物料员',
      '002': '高级操作员', 
      '003': '操作员'
    }
    const role = roleMap[jobId] || '物料员'
    
    return {
      code: 0,
      message: '登录成功',
      data: {
        jobId,
        role,
        token: Mock.Random.guid()
      }
    }
  } else {
    return {
      code: 1,
      message: '工号或密码错误',
      data: null
    }
  }
})

// 用户管理接口
Mock.mock(/\/users(\?.*)?$/, 'get', (options) => {
  const url = new URL(options.url, 'http://localhost')
  const page = parseInt(url.searchParams.get('page')) || 1
  const pageSize = parseInt(url.searchParams.get('pageSize')) || 5
  const userName = url.searchParams.get('userName') || ''
  const account = url.searchParams.get('account') || ''

  // 生成更多数据用于分页测试
  const totalCount = 28 // 总共28条数据
  const allData = []
  
  for (let i = 0; i < totalCount; i++) {
    allData.push({
      id: i + 1,
      role: Mock.Random.pick(['物料员', '高级操作员', '操作员']),
      account: Mock.Random.string('number', 6),
      userName: Mock.Random.cname(),
      desc: Mock.Random.sentence(3, 10),
      photo: Mock.Random.dataImage('100x100', Mock.Random.cname()),
      createdAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
      updatedAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    })
  }

  // 过滤数据
  let filteredData = allData
  if (userName) {
    filteredData = filteredData.filter(user => user.userName.includes(userName))
  }
  if (account) {
    filteredData = filteredData.filter(user => user.account.includes(account))
  }

  // 分页处理
  const start = (page - 1) * pageSize
  const end = start + pageSize
  const pagedData = filteredData.slice(start, end)

  return {
    code: 200,
    message: '获取成功',
    data: pagedData,
    total: filteredData.length,
    page,
    pageSize
  }
})

Mock.mock('/users', 'post', (options) => {
  const data = JSON.parse(options.body)
  return {
    code: 200,
    message: '创建成功',
    data: {
      ...data,
      id: Mock.Random.id(),
      createdAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
      updatedAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    }
  }
})

Mock.mock(/\/users\/\d+$/, 'put', (options) => {
  const data = JSON.parse(options.body)
  return {
    code: 200,
    message: '更新成功',
    data: {
      ...data,
      updatedAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    }
  }
})

Mock.mock(/\/users\/\d+$/, 'delete', () => {
  return {
    code: 200,
    message: '删除成功',
    data: null
  }
})

// 料罐管理接口
Mock.mock(/\/tanks(\?.*)?$/, 'get', (options) => {
  const url = new URL(options.url, 'http://localhost')
  const page = parseInt(url.searchParams.get('page')) || 1
  const pageSize = parseInt(url.searchParams.get('pageSize')) || 5
  const bucketNo = url.searchParams.get('bucketNo') || ''
  const userkey = url.searchParams.get('userkey') || ''

  // 生成更多数据用于分页测试
  const totalCount = 22 // 总共22条数据
  const allData = []
  
  for (let i = 0; i < totalCount; i++) {
    allData.push({
      id: i + 1,
      bucketNo: Mock.Random.string('upper', 2) + Mock.Random.string('number', 4),
      desc: Mock.Random.sentence(3, 8),
      status: Mock.Random.pick(['正常', '维护中', '停用']),
      capacity: Mock.Random.integer(100, 1000),
      currentLevel: Mock.Random.integer(0, 100),
      person: Mock.Random.cname(),
      userkey: Mock.Random.cname(),
      createdAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
      updatedAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    })
  }

  // 过滤数据
  let filteredData = allData
  if (bucketNo) {
    filteredData = filteredData.filter(tank => tank.bucketNo.includes(bucketNo))
  }
  if (userkey) {
    filteredData = filteredData.filter(tank => tank.userkey.includes(userkey))
  }

  // 分页处理
  const start = (page - 1) * pageSize
  const end = start + pageSize
  const pagedData = filteredData.slice(start, end)

  return {
    code: 200,
    message: '获取成功',
    data: pagedData,
    total: filteredData.length,
    page,
    pageSize
  }
})

Mock.mock('/tanks', 'post', (options) => {
  const data = JSON.parse(options.body)
  return {
    code: 200,
    message: '创建成功',
    data: {
      ...data,
      id: Mock.Random.id(),
      status: '正常',
      capacity: Mock.Random.integer(100, 1000),
      currentLevel: Mock.Random.integer(0, 100),
      createdAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
      updatedAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    }
  }
})

Mock.mock(/\/tanks\/\d+$/, 'put', (options) => {
  const data = JSON.parse(options.body)
  return {
    code: 200,
    message: '更新成功',
    data: {
      ...data,
      updatedAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    }
  }
})

Mock.mock(/\/tanks\/\d+$/, 'delete', () => {
  return {
    code: 200,
    message: '删除成功',
    data: null
  }
})

// 加料管理接口
Mock.mock(/\/feed-manage(\?.*)?$/, 'get', (options) => {
  const url = new URL(options.url, 'http://localhost')
  const page = parseInt(url.searchParams.get('page')) || 1
  const pageSize = parseInt(url.searchParams.get('pageSize')) || 5
  const person = url.searchParams.get('person') || ''
  const bucketNo = url.searchParams.get('bucketNo') || ''

  // 生成更多数据用于分页测试
  const totalCount = 25 // 总共25条数据
  const allData = []
  
  for (let i = 0; i < totalCount; i++) {
    allData.push({
      id: i + 1,
      person: Mock.Random.cname(),
      bucketNo: Mock.Random.string('upper', 2) + Mock.Random.string('number', 4),
      spec: Mock.Random.pick(['10kg', '20kg', '50kg', '100kg']),
      weight: Mock.Random.float(10, 100, 2, 2),
      time: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    })
  }

  // 过滤数据
  let filteredData = allData
  if (person) {
    filteredData = filteredData.filter(item => item.person.includes(person))
  }
  if (bucketNo) {
    filteredData = filteredData.filter(item => item.bucketNo.includes(bucketNo))
  }

  // 分页处理
  const start = (page - 1) * pageSize
  const end = start + pageSize
  const pagedData = filteredData.slice(start, end)

  return {
    code: 200,
    message: '获取成功',
    data: pagedData,
    total: filteredData.length,
    page,
    pageSize
  }
})

Mock.mock('/feed-operation', 'post', (options) => {
  const data = JSON.parse(options.body)
  return {
    code: 200,
    message: '操作成功',
    data: {
      ...data,
      id: Mock.Random.id(),
      status: '已完成',
      completeTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    }
  }
})

// 获取底罐重量和加料重量接口
Mock.mock(/\/tank-weight\/\w+$/, 'get', (options) => {
  const url = new URL(options.url, 'http://localhost')
  const tankId = url.pathname.split('/').pop()
  
  return {
    code: 200,
    message: '获取成功',
    data: {
      baseWeight: Mock.Random.float(50, 200, 2, 2),
      feedWeight: Mock.Random.float(10, 100, 2, 2)
    }
  }
})

// 退料管理接口
Mock.mock(/\/return-manage(\?.*)?$/, 'get', (options) => {
  const url = new URL(options.url, 'http://localhost')
  const page = parseInt(url.searchParams.get('page')) || 1
  const pageSize = parseInt(url.searchParams.get('pageSize')) || 5
  const person = url.searchParams.get('person') || ''
  const bucketNo = url.searchParams.get('bucketNo') || ''

  // 生成更多数据用于分页测试
  const totalCount = 30 // 总共30条数据
  const allData = []
  
  for (let i = 0; i < totalCount; i++) {
    allData.push({
      id: i + 1,
      person: Mock.Random.cname(),
      bucketNo: Mock.Random.string('upper', 2) + Mock.Random.string('number', 4),
      time: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    })
  }

  // 过滤数据
  let filteredData = allData
  if (person) {
    filteredData = filteredData.filter(item => item.person.includes(person))
  }
  if (bucketNo) {
    filteredData = filteredData.filter(item => item.bucketNo.includes(bucketNo))
  }

  // 分页处理
  const start = (page - 1) * pageSize
  const end = start + pageSize
  const pagedData = filteredData.slice(start, end)

  return {
    code: 200,
    message: '获取成功',
    data: pagedData,
    total: filteredData.length,
    page,
    pageSize
  }
})

Mock.mock('/return-operation', 'post', (options) => {
  const data = JSON.parse(options.body)
  return {
    code: 200,
    message: '操作成功',
    data: {
      ...data,
      id: Mock.Random.id(),
      status: '已完成',
      completeTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    }
  }
})

// 加料记录接口
Mock.mock(/\/feed-records(\?.*)?$/, 'get', (options) => {
  const url = new URL(options.url, 'http://localhost')
  const page = parseInt(url.searchParams.get('page')) || 1
  const pageSize = parseInt(url.searchParams.get('pageSize')) || 5
  const person = url.searchParams.get('person') || ''
  const bucketNo = url.searchParams.get('bucketNo') || ''
  const startTime = url.searchParams.get('startTime') || ''
  const endTime = url.searchParams.get('endTime') || ''

  // 生成更多数据用于分页测试
  const totalCount = 35 // 总共35条数据
  const allData = []
  
  for (let i = 0; i < totalCount; i++) {
    allData.push({
      id: i + 1,
      person: Mock.Random.cname(),
      bucketNo: Mock.Random.string('upper', 2) + Mock.Random.string('number', 4),
      baseWeight: Mock.Random.float(50, 200, 2, 2),
      feedWeight: Mock.Random.float(10, 100, 2, 2),
      flameWeight: Mock.Random.float(0, 20, 2, 2),
      time: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    })
  }

  // 过滤数据
  let filteredData = allData
  if (person) {
    filteredData = filteredData.filter(item => item.person.includes(person))
  }
  if (bucketNo) {
    filteredData = filteredData.filter(item => item.bucketNo.includes(bucketNo))
  }
  if (startTime && endTime) {
    filteredData = filteredData.filter(item => {
      const itemTime = new Date(item.time)
      const start = new Date(startTime)
      const end = new Date(endTime)
      return itemTime >= start && itemTime <= end
    })
  }

  // 分页处理
  const start = (page - 1) * pageSize
  const end = start + pageSize
  const pagedData = filteredData.slice(start, end)

  return {
    code: 200,
    message: '获取成功',
    data: pagedData,
    total: filteredData.length,
    page,
    pageSize
  }
})

// 加料申请接口
Mock.mock('/feed-apply', 'post', (options) => {
  const data = JSON.parse(options.body)
  return {
    code: 200,
    message: '申请成功',
    data: {
      ...data,
      id: Mock.Random.id(),
      status: '待审核',
      applyTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    }
  }
})

// 退料申请接口
Mock.mock('/return-apply', 'post', (options) => {
  const data = JSON.parse(options.body)
  return {
    code: 200,
    message: '申请成功',
    data: {
      ...data,
      id: Mock.Random.id(),
      status: '待审核',
      applyTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    }
  }
})

export default Mock 