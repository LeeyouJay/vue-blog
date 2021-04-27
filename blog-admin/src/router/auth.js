import router from './index'
import { getToken } from '../utils/cookie'
import { innerRoutes} from './innerRoutes.js'

const whiteList = ['/login','/blog']

router.beforeEach((to, from, next) => {
	if (getToken()) { //已登入的情况
		if (to.path === '/login') {
			next('/')
		} else if (!innerRoutes.some(val => val.path === to.path)) {
			next('/404')
		} else if (navigator.userAgent.indexOf('MSIE') > -1 && to.path === '/editor') {
			Vue.prototype.$alert('vue-quill-editor组件不兼容IE10及以下浏览器，请使用更高版本的浏览器查看', '浏览器不兼容通知', {
				confirmButtonText: '确定'
			});
		} else {
			next();
		}
	} else { //未登入的情况
		if (whiteList.includes(to.path)) {
			next()
		} else {
			next(`/login?redirect=${to.fullPath}`)
		}
	}
});