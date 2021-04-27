import axios from '../utils/request';
import base from './base.js'

const blog = {
	//新增文章
	addArticleAPI(params) {
		return axios.post(`${base.localUrl}/article/add`, params);
	},
	//修改文章
	editArticleAPI(params){
		return axios.post(`${base.localUrl}/article/edit`,params);
	},
	//修改标签
	saveTagsAPI(params){
		return axios.post(`${base.localUrl}/article/saveTags`,params);
	},
	//修改文章
	delArticleAPI(params){
		return axios.post(`${base.localUrl}/article/del`,params);
	},
	//添加图片
	imgAddAPI(params){
		return axios.post(`${base.localUrl}/article/imgAdd`, params,{
			headers: {'Content-Type': 'multipart/form-data'}
		});
	},
	//获取文章列表
	articleListAPI(params){
		return axios.post(`${base.localUrl}/article/list`,params);
	},
	getArticleTagsAPI(){
		return axios.get(`${base.localUrl}/article/getPersonalTags`);
	},
	getCommentListAPI(params,query){
		return axios.post(`${base.localUrl}/article/getCommentVO/`+params,query);
	},
	getCommentAPI(params,query){
		return axios.post(`${base.localUrl}/blog/getComments/`+params,query);
	},
	addCommentAPI(params){
		return axios.post(`${base.localUrl}/blog/addComment`,params);
	},
	delCommentAPI(params1,params2){
		return axios.get(`${base.localUrl}/article/delComment/`+params1+'/'+params2);
	},
	getStatisticsAPI(params){
		return axios.get(`${base.localUrl}/article/statistics/`+params);
	},
	getFriendsAPI(params){
		return axios.post(`${base.localUrl}/friend/getFriends`,params);
	},
	
	passApplyAPI(params){
		return axios.post(`${base.localUrl}/friend/passApply`,params);
	},
	delFriendAPI(params){
		return axios.post(`${base.localUrl}/friend/delFriend`,params);
	},
}
	export default blog;
	
