import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const state = {
	article:{},
    articleEdit:false,
	articleTags:['JAVA', 'SpringBoot', 'Vue','随笔']
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
	}

}

const getters = {
    getArticleEdit: state => state.articleEdit,
	getArticle: state => state.article,
	getArticleTags: state => Object.assign([], state.articleTags)
}

export default new Vuex.Store({
    state,
    mutations,
    actions,
    modules: {},
    getters
})
