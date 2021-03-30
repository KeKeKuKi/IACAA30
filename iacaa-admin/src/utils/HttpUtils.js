import axios from 'axios'
import { getToken } from '@/utils/auth'
import ElementUI from "element-ui";


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
