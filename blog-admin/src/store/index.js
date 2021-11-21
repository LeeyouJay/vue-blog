import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const state = {
	article:{},
    articleEdit:false,
	articleTags:['JAVA', 'SpringBoot', 'Vue','随笔'],
	authSocial:[{
		platform:'QQ',
		icon:'icon-qq',
		bind:false
	},{
		platform:'github',
		icon:'icon-github',
		bind:false
	},{
		platform:'gitee',
		icon:'icon-gitee',
		bind:false
	},{
		platform:'baidu',
		icon:'icon-baidu',
		bind:false
	}],
	signupParam:{},
	openSignup:false
}
const mutations ={
	SET_ARTICLEEDIT: (state, v) => {
	    state.articleEdit = v;
	},
	SET_ARTICLE: (state, v) => {
	    state.article = v;
	},
	SET_ARTICLE_TAGS: (state, v) => {
	    state.articleTags = v;
	},
	SET_SIGNUPPARAM: (state, v) => {
		state.signupParam = v
	},
	RESET_SIGNUPPARAM: (state) => {
		for (let key in state.signupParam) {
		    state.signupParam[key] = ''
		}
	},
}
const actions = {
	setArticleEdit: ({commit}, v) => {
		 commit('SET_ARTICLEEDIT', v);
	},
	setArticle: ({commit}, v) => {
		 commit('SET_ARTICLE', v);
	},
	setArticleTags: ({commit}, v) => {
		 commit('SET_ARTICLE_TAGS', v);
	},
	setSignupParam: ({commit}, v) => {
		 commit('SET_SIGNUPPARAM', v);
	},
	resetSignupParam: ({commit}, v) => {
		 commit('RESET_SIGNUPPARAM', v);
	},
}

const getters = {
    getArticleEdit: state => state.articleEdit,
	getArticle: state => state.article,
	getArticleTags: state => Object.assign([], state.articleTags),
	getSignupParam: state => Object.assign({}, state.signupParam),
}

export default new Vuex.Store({
    state,
    mutations,
    actions,
    modules: {},
    getters
})
