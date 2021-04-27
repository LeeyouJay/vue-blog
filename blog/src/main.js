import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import hljs from 'highlight.js'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import 'highlight.js/styles/github.css'
import './assets/css/style.less'
import './assets/font/iconfont.css'
import md5 from 'js-md5';

import moment from "moment"; // 1.引入

Vue.use(ElementUI);
Vue.prototype.$moment = moment // 2.挂载到原型
moment.locale("zh-cn") // 3.汉化

Vue.config.productionTip = false
Vue.prototype.$md5 = md5;
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
}).$mount('#app')


Vue.directive('highlight', (el) => {
  let blocks = el.querySelectorAll('pre code')
  blocks.forEach((block) => {
    hljs.highlightBlock(block)
  })
})