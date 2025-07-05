import Mock from 'mockjs'

// 设置延迟时间
Mock.setup({
  timeout: '200-600'
})

// 用户管理接口
Mock.mock(/\/users(\?.*)?$/, 'get', (options) => {
  const url = new URL(options.url, 'http://localhost')
  const page = parseInt(url.searchParams.get('page')) || 1
  const pageSize = parseInt(url.searchParams.get('pageSize')) || 5
  const name = url.searchParams.get('name') || ''
  const jobId = url.searchParams.get('jobId') || ''

  const users = Mock.mock({
    [`data|${pageSize}`]: [{
      'id|+1': 1,
      'role|1': ['物料员', '高级操作员', '操作员'],
      'jobId': () => Mock.Random.string('number', 6),
      'name': () => Mock.Random.cname(),
      'desc': () => Mock.Random.sentence(3, 10),
      'photo': () => Mock.Random.dataImage('100x100', Mock.Random.cname()),
      'createdAt': () => Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
      'updatedAt': () => Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    }]
  })

  // 过滤数据
  let filteredData = users.data
  if (name) {
    filteredData = filteredData.filter(user => user.name.includes(name))
  }
  if (jobId) {
    filteredData = filteredData.filter(user => user.jobId.includes(jobId))
  }

  return {
    code: 200,
    message: '获取成功',
    data: filteredData,
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
  const code = url.searchParams.get('code') || ''

  const tanks = Mock.mock({
    [`data|${pageSize}`]: [{
      'id|+1': 1,
      'code': () => Mock.Random.string('upper', 2) + Mock.Random.string('number', 4),
      'desc': () => Mock.Random.sentence(3, 8),
      'status|1': ['正常', '维护中', '停用'],
      'capacity': () => Mock.Random.integer(100, 1000),
      'currentLevel': () => Mock.Random.integer(0, 100),
      'person': () => Mock.Random.cname(),
      'createdAt': () => Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
      'updatedAt': () => Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    }]
  })

  // 过滤数据
  let filteredData = tanks.data
  if (code) {
    filteredData = filteredData.filter(tank => tank.code.includes(code))
  }

  return {
    code: 200,
    message: '获取成功',
    data: filteredData,
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

  const feedManage = Mock.mock({
    [`data|${pageSize}`]: [{
      'id|+1': 1,
      'tankCode': () => Mock.Random.string('upper', 2) + Mock.Random.string('number', 4),
      'tankName': () => Mock.Random.sentence(2, 4),
      'materialName': () => Mock.Random.sentence(2, 4),
      'amount': () => Mock.Random.integer(10, 100),
      'unit': () => Mock.Random.pick(['kg', 'L', '个']),
      'status|1': ['待加料', '加料中', '已完成'],
      'operator': () => Mock.Random.cname(),
      'applyTime': () => Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
      'completeTime': () => Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    }]
  })

  return {
    code: 200,
    message: '获取成功',
    data: feedManage.data,
    total: feedManage.data.length,
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

// 退料管理接口
Mock.mock(/\/return-manage(\?.*)?$/, 'get', (options) => {
  const url = new URL(options.url, 'http://localhost')
  const page = parseInt(url.searchParams.get('page')) || 1
  const pageSize = parseInt(url.searchParams.get('pageSize')) || 5

  const returnManage = Mock.mock({
    [`data|${pageSize}`]: [{
      'id|+1': 1,
      'tankCode': () => Mock.Random.string('upper', 2) + Mock.Random.string('number', 4),
      'tankName': () => Mock.Random.sentence(2, 4),
      'materialName': () => Mock.Random.sentence(2, 4),
      'amount': () => Mock.Random.integer(5, 50),
      'unit': () => Mock.Random.pick(['kg', 'L', '个']),
      'status|1': ['待退料', '退料中', '已完成'],
      'operator': () => Mock.Random.cname(),
      'applyTime': () => Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
      'completeTime': () => Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    }]
  })

  return {
    code: 200,
    message: '获取成功',
    data: returnManage.data,
    total: returnManage.data.length,
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

  const feedRecords = Mock.mock({
    [`data|${pageSize}`]: [{
      'id|+1': 1,
      'tankCode': () => Mock.Random.string('upper', 2) + Mock.Random.string('number', 4),
      'tankName': () => Mock.Random.sentence(2, 4),
      'materialName': () => Mock.Random.sentence(2, 4),
      'amount': () => Mock.Random.integer(10, 100),
      'unit': () => Mock.Random.pick(['kg', 'L', '个']),
      'operator': () => Mock.Random.cname(),
      'operationTime': () => Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
      'remark': () => Mock.Random.sentence(5, 15)
    }]
  })

  return {
    code: 200,
    message: '获取成功',
    data: feedRecords.data,
    total: feedRecords.data.length,
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