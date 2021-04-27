import Vue from 'vue'
import Vuex from 'vuex'
import { getToken } from '@/utils/cookie.js'


Vue.use(Vuex)
// 略:后台获取系统运行时间
const runAt = Date.parse('2021-03-26 21:00:08');
let timer = null;
const state = {
	loading: false,
	runTimeInterval: '',
	tag:'',
	content:'',
	needInfo:true,
	websiteInfo: '',
	param: {
		fromUserName: '',
		fromUserEmail: '',
		fromUserSite: '',
		fromUserAvatar:'',
		content: ''
	},
}
const mutations = {
	SET_LOADING: (state, v) => {
		state.loading = v;
	},
	SET_TAG: (state, v) => {
		state.tag = v;
	},
	SET_CONTENT: (state, v) => {
		state.content = v;
	},
	SET_NEED: (state, v) => {
		state.needInfo = v;
	},
	SET_PARAM: (state, v) => {
		state.param = v;
	},
	GET_RUNTIME_INTERVAL: (state) => {
		if (!timer || !state.runTimeInterval) {
			clearInterval(timer)
			timer = setInterval(() => {
				state.runTimeInterval = getTimeInterval(runAt);
			}, 1000);
		}
	}
}
const actions = {
	setLoading: ({commit}, v) => {
		commit('SET_LOADING', v);
	},
	setTag: ({commit}, v) => {
		commit('SET_TAG', v);
	},
	setContent: ({commit}, v) => {
		commit('SET_CONTENT', v);
	},
	setNeedInfo: ({commit}, v) => {
		commit('SET_NEED', v);
	},
	setParam: ({commit}, v) => {
		commit('SET_PARAM', v);
	},
	initComputeTime: ({commit}) => {
		commit('GET_RUNTIME_INTERVAL');
	},
	
}
const getters = {
	loading: state => state.loading,
	runTimeInterval: state => state.runTimeInterval,
	needInfo: state => state.needInfo,
	param: state => state.param
}
function getTimeInterval(startDate, endDate = Date.now()) {
  if (arguments.length === 0) {
    return null
  }
  let startTime;
  let endTime;
  if (typeof startDate === 'object') {
    startTime = startDate.getTime();
  } else {
    startTime = startDate;
  }
  if (typeof endDate === 'object') {
    endTime = endDate.getTime();
  } else {
    endTime = endDate;
  }
  let dateInterval =  endTime - startTime;
  // //计算出相差天数
  let days = Math.floor(dateInterval / (24 * 60 * 60 * 1000));
  //计算小时数
  let hourLevel = dateInterval % (24 * 60 * 60 * 1000);
  let hours = Math.floor(hourLevel / (60 * 60 * 1000))
  //计算分钟数
  let minutesLevel = hourLevel % (60 * 60 * 1000);
  let minutes = Math.floor(minutesLevel / (60 * 1000));
  //计算秒数
  let seconds = Math.round((minutesLevel % (60 * 1000)) / 1000);
  return `${days} 天 ${hours} 时 ${minutes} 分 ${seconds} 秒`;
}
export default new Vuex.Store({
	state,
	mutations,
	actions,
	modules: {},
	getters
})
