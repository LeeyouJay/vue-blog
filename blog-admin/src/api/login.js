import axios from '../utils/request';
import QS from 'qs';
import base from './base.js'

const login = {
	// 账号密码登入提交 
	loginApi(params) {
		return axios.post(`${base.localUrl}/login`, QS.stringify(params), {
			headers: {
				"Content-Type": "application/x-www-form-urlencoded"
			}
		});
	},
	//手机验证码登入
	mobileCodeLoginApi(params) {
		return axios.post(`${base.localUrl}/mobileCodeLogin`, QS.stringify(params), {
			headers: {
				"Content-Type": "application/x-www-form-urlencoded"
			}
		});
	},
	// 注册提交
	signupApi(params) {
		return axios.post(`${base.localUrl}/user/signup`, params);
	},
	//获取图片验证码
	getCaptchaImage() {
		return axios.get(`${base.localUrl}/user/captchaImage`)
	},
	//获取注册验证码
	getVerifyCode(params) {
		return axios.get(`${base.localUrl}/user/sendVerifyCode/`+params)
	},
	//获取短信验证码
	getMobileCode(params) {
		return axios.get(`${base.localUrl}/user/getMobileCode/`+params)
	},
	//登出
	logoutApi() {
		return axios.get(`${base.localUrl}/logout`);
	},
	//获取滑动解锁图片
	getCaptcha() {
		return axios.get(`${base.localUrl}/slider/image`);
	},
	//获取IP地址
	getIpName(){
		return axios.get("https://chenyi.ink:8900/getIp");
	}
	
}
export default login;
