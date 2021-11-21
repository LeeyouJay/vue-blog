import router from './index'
import {
	getToken
} from '../utils/cookie'
import {
	innerRoutes
} from './innerRoutes.js'

const whiteList = ['/login', '/callback']

router.beforeEach((to, from, next) => {
	if (getToken()) {
		if (to.path === '/login') {
			next('/')
		} else if (to.path == '/callback') {
			next();
		} else if (!innerRoutes.some(val => val.path === to.path)) {
			next('/404')
		} else {
			next();
		}
	} else {
		if (whiteList.includes(to.path)) {
			next()
		} else {
			next(`/login?redirect=${to.fullPath}`)
		}
	}
});
