import axios from 'axios'
import store from '@/store'
import {
	getToken
} from './cookie.js'
import {
	Message
} from 'element-ui'
// create an axios instance
const service = axios.create({
	//baseURL: process.env.VUE_APP_BASE_API,
	timeout: 5000 // request timeout
})
const errorHandle = (status) => {
	// 状态码判断
	switch (status) {
		case 400:
			Message.error('参数解析异常！')
			break;
		case 405:
			Message.error('请求方式异常！')
			break;
		case 403:
			Message.error('访问拒绝！')
			break;
		case 404:
			Message.error('请求资源不存在！')
			router.replace('/404')
			break;
		case 500:
			Message.error('服务器出错！')
	}
}
// request interceptor
service.interceptors.request.use(
	config => {
		const token = getToken();
		token && (config.headers.Authorization = token);
		return config;
	},
	error => {
		// do something with request error
		console.log(error) // for debug
		return Promise.reject(error)
	}
)


service.interceptors.response.use(
	res => res.status === 200 ? Promise.resolve(res.data) : Promise.reject(res),
	error => {
		const { response } = error;
		if (response) {
			errorHandle(response.status);
			return Promise.reject(response);
		} else {
			Message.error('请求失败，服务器可能不在线')
			return Promise.reject(error);
		}
	}
)
export default service
