import request from '@/utils/request'
import base from './base.js'
import QS from 'qs';

//获取qq昵称头像
export function getQQUser(qq) {
	return request({
		url: `${base.localUrl}/blog/getQQInfo/`+qq,
		method: 'get',
		params: {}
	})
}

//获取文章分类
export function getTagsAPI() {
	return request({
		url: `${base.localUrl}/blog/tags`,
		method: 'get',
		params: {}
	})
}

//获取分页文章
export function articleListAPI(params) {
	return request({
		url: `${base.localUrl}/article/publicList`,
		method: 'post',
		data: params
	})
}
//获取所有文章归档
export function getArchivesAPI() {
	return request({
		url: `${base.localUrl}/blog/getArchives`,
		method: 'get',
		params:{}
	})
}

//根据ID获取文章内容
export function articleAPI(params) {
	return request({
		url: `${base.localUrl}/blog` + params,
		method: 'get',
		params:{}
	})
}

//获取文章留言
export function getCommentsAPI(params,page) {
	return request({
		url: `${base.localUrl}/blog/getComments/` + params,
		method: 'post',
		data: page
	})
}

//添加留言
export function addCommentAPI(params) {
	return request({
		url: `${base.localUrl}/blog/addComment`,
		method: 'post',
		data: params
	})
}
//申请友链
export function applyFriendAPI(params) {
	return request({
		url: `${base.localUrl}/blog/applyFriend`,
		method: 'post',
		data: params
	})
}
//获取友链
export function getFriends() {
	return request({
		url: `${base.localUrl}/blog/getFriends`,
		method: 'get',
		params:{}
	})
}


// 账号密码登入提交 
export function loginAPI(params) {
	return request.post(`${base.localUrl}/login`, QS.stringify(params), {
		headers: {
			"Content-Type": "application/x-www-form-urlencoded"
		}
	});
}
//手机验证码登入
export function mobileCodeLoginAPI(params) {
	return request.post(`${base.localUrl}/mobileCodeLogin`, QS.stringify(params), {
		headers: {
			"Content-Type": "application/x-www-form-urlencoded"
		}
	});
}
//获取滑动解锁
export function getCaptchaImageAPI() {
	return request.get(`${base.localUrl}/slider/image`);
}
//获取短信验证码
export function getMobileCodeAPI(params) {
	return request.get(`${base.localUrl}/user/getMobileCode/` + params)
}
