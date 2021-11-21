/**
 * 在主页显示的标签页
 */

export let innerRoutes = [{
		path: '/user_list',
		component: () => import('../components/system/userList.vue'),
		meta: {
			title: '用户列表'
		}
	},
	{
		path: '/menu',
		component: () => import('../components/system/menu.vue'),
		meta: {
			title: '权限管理'
		}
	},
	{
		path: '/sysLog',
		component: () => import('../components/system/sysLog.vue'),
		meta: {
			title: '登入日志'
		}
	},
	{
		path: '/userInfo',
		component: () => import('../components/system/userInfo.vue'),
		meta: {
			title: '个人中心'
		}
	},
	{
		path: '/dashboard',
		component: () => import( '../components/Dashboard.vue'),
		meta: {
			title: '系统首页'
		}
	},

	{
		path: '/403',
		component: () => import( '../views/403.vue'),
		icon: 'el-icon-lx-warn',
		meta: {
			title: '403'
		}
	},
	{
		path: '/404',
		component: () => import( '../views/404.vue'),
		icon: 'el-icon-lx-warn',
		meta: {
			title: '404'
		}
	},
	{
		// 写文章
		path: '/write_markdown',
		component: () => import( '../components/blog/article_markdown.vue'),
		meta: {
			title: 'markdown格式'
		}
	},
	{
		// 写文章
		path: '/write_html',
		component: () => import( '../components/blog/article_html.vue'),
		meta: {
			title: 'HTML格式'
		}
	},
	{
		// 文章列表
		path: '/article_list',
		component: () => import('../components/blog/articleList.vue'),
		meta: {
			title: '文章列表'
		}
	},
	{
		// 角色管理
		path: '/sysRoles',
		component: () => import(  '../components/system/role.vue'),
		meta: {
			title: '角色管理'
		}
	},
	{
		// 友链申请
		path: '/friend_list',
		component: () => import(  '../components/blog/friendList.vue'),
		meta: {
			title: '友链申请'
		}
	},
	{
		// 音乐列表
		path: '/music_list',
		component: () => import(  '../components/blog/musicList.vue'),
		meta: {
			title: '音乐列表'
		}
	},
	{
		path: '/permission',
		component: () => import(  '../components/Permission.vue'),
		beforeEnter: (to, from, next) => {
			const role = localStorage.getItem('ms_username');
			role === 'admin' ? next() : next('/403');
		},
		meta: {
			title: '权限测试'
		}
	}
]
