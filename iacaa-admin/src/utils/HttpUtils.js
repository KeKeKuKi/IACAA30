import axios from 'axios'
import { getToken } from '@/utils/auth'
import { Message } from 'element-ui'
export const supplierConsumer = axios.create({
  baseURL: 'http://localhost:19999/Iacaa20Server',
  // baseURL: 'http://dev.51ishare.com:8182/',
  withCredentials: false
})

export const authCenterServer = axios.create({
  baseURL: 'http://localhost:19999/auth-center-server',
  // baseURL: 'http://dev.51ishare.com:8182/',
  withCredentials: false
})

export const UserServer = axios.create({
  baseURL: 'http://localhost:19999/user-server',
  // baseURL: 'http://dev.51ishare.com:8182/',
  withCredentials: false
})

export const AuthServer = axios.create({
  baseURL: 'http://localhost:19999/auth-center-server',
  // baseURL: 'http://dev.51ishare.com:8182/',
  withCredentials: false
})

UserServer.interceptors.response.use(response => {
  // if(response.data.code === 10513){
  //   Store.dispatch('user/logout')
  //   this.$router.push(`/login?redirect=${this.$route.fullPath}`)
  // }
  return response
})


//业务系统响应全局拦截器
supplierConsumer.interceptors.response.use(response => {
  if(response.data.code === 10513){
    Message.error("Token已过期，或没有权限这样做，可以尝试重新登陆或联系管理员开通权限")
  }else if(!response.data.succ){
    Message.error(response.data.msg)
  }else {
    return response
  }
})

//业务系统请求全局拦截器
supplierConsumer.interceptors.request.use(request => {
  //设置请求方式为application/x-www-form-urlencoded后台参数容易获取
  request.headers.contentType = 'application/x-www-form-urlencoded; charset=utf-8'
})


export function requestByClient(client, method, url, data, then) {
  client({
    headers: {
      '_token': getToken()
    },
    method: method,
    url: url,
    data: data
  })
    .then(then)
    .catch(error => {
      console.error(error)
    })
}
// this.$store.getters
