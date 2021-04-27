import axios from '../utils/request';
import base from './base.js'

const system = {

	testAPI() {
		return axios.get(`${base.localUrl}/index/testApi`);
	},
	//数据：用户列表
	userListAPI(params) {
		return axios.post(`${base.localUrl}/user/list`,params);
	},
	//数据：权限列表
	authoritiesAPI() {
		return axios.get(`${base.localUrl}/user/authorityList`);
	},
	//操作：授权
	setAuthorityAPI(params) {
		return axios.post(`${base.localUrl}/user/setAuthority`, params);
	},
	//操作：重置密码
	resetAPI(params){
		return axios.post(`${base.localUrl}/user/resetPassword`, params);
	},
	//操作：修改密码
	changePasswordAPI(params){
		return axios.post(`${base.localUrl}/user/changePassword`, params);
	},
	//数据：获取当前账号信息
	getUserInfoAPI(){
		return axios.get(`${base.localUrl}/user/getUserInfo`);
	},
	//操作：更改当前账号信息
	changeUserInfoAPI(params){
		return axios.post(`${base.localUrl}/user/changeUserInfo`, params);
	},
	//操作：更改状态
	setStateAPI(params){
		return axios.post(`${base.localUrl}/user/setState`, params);
	},
	//操作：上传头像
	changeAvatarAPI(params){
		return axios.post(`${base.localUrl}/user/changeAvatar`, params);
	},
	//数据：获取侧边菜单
	menuListAPI() {
		return axios.get(`${base.localUrl}/user/getMenuList`);
	},
	//数据：通过id获取权限
	findByIdAPI(params) {
		return axios.get(`${base.localUrl}/menu/findById/` + params);
	},
	//操作：修改用户权限
	setMenuAPI(params){
		return axios.post(`${base.localUrl}/menu/setMenu`, params);
	},
	//操作：添加菜单
	addMenuAPI(params){
		return axios.post(`${base.localUrl}/menu/addMenu`, params);
	},
	//操作：删除按钮、菜单
	delMenuAPI(params){
		return axios.get(`${base.localUrl}/menu/delMenu/`+params);
	},
	//数据：获取登入信息
	getLogListAPI(params){
		return axios.post(`${base.localUrl}/syslog/list`,params);
	},
	//数据：获取上次登入信息
	getLatestLogAPI(params){
		return axios.get(`${base.localUrl}/syslog/getLatestLogByUser/`+params);
	},
	//获取角色列表
	getRoleListAPI(){
		return axios.get(`${base.localUrl}/role/list`);
	},
	//获取各角色包含的用户
	getRoleUserListAPI(){
		return axios.get(`${base.localUrl}/role/UserList`);
	},
	//设置角色权限
	setRoleAuthoritiesAPI(params){
		return axios.post(`${base.localUrl}/role/setAuthorities`,params);
	},
	//拖拽分配用户角色
	UserChangeRoleAPI(params){
		return axios.post(`${base.localUrl}/role/UserChangeRole`,params);
	},
	//添加角色
	addSysRoleAPI(params){
		return axios.post(`${base.localUrl}/role/addSysRole`,params);
	},
	//删除角色
	delRoleAPI(params){
		return axios.post(`${base.localUrl}/role/delRole`,params);
	}

}
export default system;
