<template>
	<div class="header">
		<!-- 折叠按钮 -->
		<div class="collapse-btn" @click="collapseChage">
			<i v-if="!collapse" class="el-icon-s-fold"></i>
			<i v-else class="el-icon-s-unfold"></i>
		</div>
		<div class="bread-crumbs-wrapper">

			<el-breadcrumb separator="/">
				<el-breadcrumb-item v-for="item in levelList" :key="item.path">
					<el-button type="text" class="home-link">
						{{ item.meta.title }}
					</el-button>
				</el-breadcrumb-item>
			</el-breadcrumb>
		</div>
		<div class="header-right">
			<!-- 消息中心 -->
			<!-- <el-tooltip effect="dark" :content="message?`有${message}条未读申请`:`消息中心`" placement="bottom">
				<router-link to="/friendList">
					<el-badge :value="message" class="item">
						<i class="el-icon-bell"></i>
					</el-badge>
				</router-link>
			</el-tooltip> -->
			<!-- 全屏显示 -->
			<div class="btn-fullscreen" @click="handleFullScreen">
				<el-tooltip effect="dark" :content="fullscreen?`取消全屏`:`全屏`" placement="bottom">
					<i class="el-icon-rank"></i>
				</el-tooltip>
			</div>
			<!-- 退出按钮 -->
			<el-tooltip effect="dark" content="注销" placement="bottom">
				<div class="collapse-btn" @click="logout()">
					<i class="el-icon-switch-button"></i>
				</div>
			</el-tooltip>
		</div>
	</div>
</template>
<script>
	import {
		removeToken
	} from '../utils/cookie.js';
	export default {
		data() {
			return {
				collapse: false,
				fullscreen: false,
				name: 'linxin',
				message: 2,
				levelList: [],
				hasClick: false
			};
		},
		computed: {
			username() {
				let username = localStorage.getItem('ms_username');
				return username ? username : this.name;
			}
		},
		watch: {
			$route(route) {
				this.isHome(route.path)
				this.getBreadCrumb()
			}
		},
		created() {
			this.getBreadCrumb()
			this.isHome(this.$route.path)
		},
		methods: {
			// 注销登入
			logout() {
				this.$confirm('注销登入?', '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning',
					 center: true
				}).then(() => {
					this.$message.info("退出成功！")
					removeToken();
					location.reload()
				}).catch((e) => {
					console.log(e)
				});
				
			},
			// 侧边栏折叠
			collapseChage() {
				this.collapse = !this.collapse;
				this.$bus.$emit('collapse', this.collapse);
			},
			// 全屏事件
			handleFullScreen() {
				let element = document.documentElement;
				if (this.fullscreen) {
					if (document.exitFullscreen) {
						document.exitFullscreen();
					} else if (document.webkitCancelFullScreen) {
						document.webkitCancelFullScreen();
					} else if (document.mozCancelFullScreen) {
						document.mozCancelFullScreen();
					} else if (document.msExitFullscreen) {
						document.msExitFullscreen();
					}
				} else {
					if (element.requestFullscreen) {
						element.requestFullscreen();
					} else if (element.webkitRequestFullScreen) {
						element.webkitRequestFullScreen();
					} else if (element.mozRequestFullScreen) {
						element.mozRequestFullScreen();
					} else if (element.msRequestFullscreen) {
						// IE11
						element.msRequestFullscreen();
					}
				}
				this.fullscreen = !this.fullscreen;
			},
			isHome(path) {
				if (path === '/home') {
					this.hasClick = false
				} else {
					this.hasClick = true
				}
			},
			goHome() {
				this.$router.push('/')
			},
			getBreadCrumb() {
				const matched = this.$route.matched.filter(item => item.meta && item.meta.title)
				this.levelList = matched.filter(item => item.meta && item.meta.title && item.meta.title !== '首页')
			}
		},
		mounted() {
			if (document.body.clientWidth < 1500) {
				this.collapseChage();
			}
		}
	};
</script>
<style scoped>
	.header {
		position: relative;
		box-sizing: border-box;
		width: 100%;
		font-size: 22px;
		color: #000;
	}

	.collapse-btn {
		float: left;
		padding: 0 21px;
		cursor: pointer;
		line-height: 50px;
	}

	.header .logo {
		float: left;
		width: 250px;
		line-height: 50px;
	}

	.header-right {
		float: right;
		padding-right: 15px;
		display: flex;
		align-items: center;
	}

	.btn-fullscreen {
		transform: rotate(45deg);
		margin-right: 10px;
		margin-left: 25px;
		font-size: 24px;
		position: relative;
		width: 30px;
		height: 30px;
		text-align: center;
		border-radius: 15px;
		cursor: pointer;
	}

	.user-name {
		margin-left: 10px;
	}

	.bread-crumbs-wrapper {
		float: left;
		width: 35%;
		padding-top: 7px;
	}

	.home-link {
		font-weight: bold;
		font-size: 14px;
		color: #333 !important;
	}
</style>
