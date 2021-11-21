import Vue from 'vue';
import App from './App.vue';
import router from './router';
import store from './store'
import api from './api' 
import {getToken,removeToken}  from './utils/cookie.js'

import ElementUI from 'element-ui';

import iconPicker from 'e-icon-picker'; //图标选择器
import 'element-ui/lib/theme-chalk/index.css'; // 默认主题
// import './assets/css/theme-green/index.css'; // 浅绿色主题
// import './assets/css/icon.css';
//import './utils/directives'; // 拖拽弹出窗
import 'babel-polyfill';
import './router/auth'


import "e-icon-picker/dist/symbol.js"; //基本彩色图标库
import 'e-icon-picker/dist/index.css'; // 基本样式，包含基本图标
import 'font-awesome/css/font-awesome.min.css'; //font-awesome 图标库
import 'element-ui/lib/theme-chalk/icon.css'; //element-ui 图标库
import './assets/font/iconfont.css'

Vue.prototype.$api = api; // 将api挂载到vue的原型上
Vue.prototype.$getToken = getToken;
Vue.prototype.$removeToken = removeToken;
Vue.config.productionTip = false;
Vue.use(iconPicker, { //使用e-icon-picker
	ElementUI: true
}); 
Vue.use(ElementUI, {
	size: 'small'
});
var EventBus = new Vue();
Object.defineProperties(Vue.prototype, {
  $bus: {
    get: function () {
      return EventBus
    }
  }
})
new Vue({
	router,
	store,
	render: h => h(App)
}).$mount('#app');
