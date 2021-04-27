import Vue from 'vue';
import Router from 'vue-router';
import Layout from '../layout'
import {innerRoutes} from './innerRoutes.js'

Vue.use(Router);

export const constantRoutes = [
	{
		path: '/login',
		component: () => import( '../views/login/index.vue'),
		meta: {
			title: '登录'
		}
	},
	{
		path: '/',
		component: Layout,
		redirect: '/dashboard',
		icon: 'el-icon-lx-home',
		children: [...innerRoutes]
	},
]
const routes = [...constantRoutes]

export default new Router({
	mode: 'history',
	base: '/manage',
	routes
});
