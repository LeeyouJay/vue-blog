import axios from 'axios'
import {Message} from 'element-ui'
import { getToken } from './cookie.js'
import router from '../router';


axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';

/** 
 * 请求失败后的错误统一处理 
 * @param {Number} status 请求失败的状态码
 */
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
			Message.error('权限不足，访问拒绝！')
            break;
        case 404:
           Message.error('请求资源不存在！')
		   router.replace('/404')
            break;
        case 500:
            Message.error('服务器出错！')
        }
	}
// 创建axios实例
var instance = axios.create({
		//withCredentials:  true,
		timeout: 5000 ,//请求超时时间(毫秒)
	});
// 设置post请求头
//instance.defaults.headers.post['Content-Type'] = 'application/json';

/** 
 * 请求拦截器 
 * 每次请求前，如果存在token则在请求头中携带token 
 */ 
instance.interceptors.request.use(
    config => {
        // 登录流程控制中，根据本地是否存在token判断用户的登录情况        
        // 但是即使token存在，也有可能token是过期的，所以在每次的请求头中携带token        
        // 后台根据携带的token判断用户的登录情况，并返回给我们对应的状态码        
        // 而后我们可以在响应拦截器中，根据状态码进行一些统一的操作。        
        const token = getToken();
        token && (config.headers.Authorization = token);
        return config;    
    },    
    error => Promise.error(error))

// 响应拦截器
instance.interceptors.response.use(
    // 请求成功
    res => 
		res.status === 200 ? Promise.resolve(res.data) :  Promise.reject(res)
	,
    // 请求失败
    error => {
        const { response } = error;
        if (response) {
            errorHandle(response.status);
            return Promise.reject(response);
        } else {
            // 处理断网的情况
            // eg:请求超时或断网时，更新state的network状态
            // network状态在app.vue中控制着一个全局的断网提示组件的显示隐藏
            // 关于断网组件中的刷新重新获取数据，会在断网组件中说明
			Message.error('请求失败，服务器可能不在线')
			return Promise.reject(error);
    //         if (!window.navigator.onLine) {
				// Message.error('网络连接中断！')
    //         } else {
    //             return Promise.reject(error);
    //         }
        }
    });

export default instance;