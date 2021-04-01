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

supplierConsumer.interceptors.response.use(response => {
  if(response.data.code === 10513){
    Message.error("Token已过期，或没有权限这样做，可以尝试重新登陆或联系管理员开通权限")
  }else {
    return response
  }
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
