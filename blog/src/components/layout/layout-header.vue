<template>
	<div>
		<div id="layout-header" :class="{'fixed':fixed,'hidden':hidden}" >
			<div class="site-logo">
				<div @click="toHome">
					<img src="@/assets/site-logo.svg" alt="">
				</div>
			</div>
			<div class="menus-btn" @click.stop="mobileShow=!mobileShow">
				Menus
			</div>
			<div class="site-menus" :class="{'mobileShow':mobileShow}" @click.stop="mobileShow=!mobileShow">
				<div class="menu-item header-search">
					<header-search />
				</div>
				<div class="menu-item hasChild">
					<a >分类</a>
					<div class="childMenu" v-if="tags.length">
						<div class="sub-menu" v-for="(item, index) in tags" :key="index">
							<a  @click="toCategory(item)">{{item}}</a>
						</div>
					</div>
				</div>
				<div class="menu-item" >
					<router-link to="/Archives">归档</router-link>
				</div>
				<div class="menu-item">
					<router-link to="/about" >关于</router-link>
				</div>
				<div class="menu-item">
					<router-link to="/friend" >友链</router-link>
				</div>
				<div class="menu-item" v-if="needInfo" @click="outerVisible = true" >
					登入
				</div>
				<div class="menu-item" v-if="!needInfo" >
					<span @click="longout"  >注销</span>
					<div class="avatar" @click="longout" >
						<img :src="userAvatar" />
					</div>
				</div>
			</div>
		</div>


		<!-- 登入弹窗 -->
		<el-dialog width="360px"  center :visible.sync="outerVisible">
			<el-dialog width="340px" :visible.sync="innerVisible" append-to-body :show-close="false"
				:close-on-click-modal="true">
				<template slot="title">
					<span class="dialog-title">请完成图片验证</span>
				</template>
				<Captcha v-if="refreshCode" ref="dialogopen" :l="42" :r="10" :w="catcha.w" :h="catcha.h" :sw="catcha.sw"
					:sh="catcha.sh" :blocky="catcha.blocky" :imgurl="catcha.imgurl" :miniimgurl="catcha.miniimgurl"
					:slider-text="catcha.text" @success="onSuccess" @fail="onFail" @refresh="onRefresh" />
			</el-dialog>
			<div class="ms-login">
				<el-tabs v-model="activeName">
					<el-tab-pane label="账号密码登入" name="first">
						<el-form :model="param" :rules="rules" ref="login" label-width="0px" class="ms-content">
							<el-form-item prop="username">
								<el-input placeholder="账号" v-model="param.username">
									<el-button slot="prepend" icon="el-icon-user" size="mini"></el-button>
								</el-input>
							</el-form-item>
							<el-form-item prop="password">
								<el-input placeholder="密码" type="password" v-model="param.password"@keyup.enter.native="submitLogin()">
									<el-button slot="prepend" icon="el-icon-lock" size="mini"></el-button>
								</el-input>
							</el-form-item>
							<div class="login-btn">
								<el-button @click="outerVisible = false">取 消</el-button>
								<el-button type="primary" @click="submitLogin()">登录</el-button>
							</div>
						</el-form>
					</el-tab-pane>
					<el-tab-pane label="验证码登入" name="second">
						<el-form :model="phoneLog" :rules="phoneRules" ref="phonelogin" label-width="0px" class="ms-content">
							<el-form-item prop="mobile">
								<el-input placeholder="手机号/邮箱" v-model="phoneLog.mobile">
									<el-button slot="prepend" icon="el-icon-phone" size="mini"></el-button>
								</el-input>
							</el-form-item>
							<el-form-item prop="code">
								<el-row :gutter="20">
									<el-col :span="14">
										<el-input placeholder="验证码" v-model="phoneLog.code">
											<el-button slot="prepend" icon="el-icon-key" size="mini"></el-button>
										</el-input>
									</el-col>
									<el-col :span="10">
										<el-button @click="sendMsg" size="mini" type="primary" :disabled="isDisabled">{{buttonName}}
										</el-button>
									</el-col>
								</el-row>
							</el-form-item>
							<div class="login-btn">
								<el-button @click="outerVisible = false">取 消</el-button>
								<el-button type="primary" @click="mobileLogin()">登录</el-button>
							</div>
						</el-form>
					</el-tab-pane>
				</el-tabs>
			</div>
		</el-dialog>
	</div>
</template>

<script>
	import HeaderSearch from '@/components/header-search'
	import Captcha from './captcha.vue'
	import {
		setToken,
		getToken,
		removeToken,
		setAvatar,
		getAvatar
	} from '@/utils/cookie.js'
	import {
		Message,
		MessageBox
	} from 'element-ui';
	import base from '@/api/base.js'

	import {
		getTagsAPI,
		loginAPI,
		mobileCodeLoginAPI,
		getCaptchaImageAPI,
		getMobileCodeAPI,
		getIpName
	} from '@/api'

	export default {
		name: "layout-header",
		components: {
			HeaderSearch,
			Captcha
		},
		data() {
			return {
				param: {
					username: '',
					password: '',
					captchaUUid: '',
					moveX: ''
				},
				rules: {
					username: [{
						required: true,
						message: '请输入用户名',
						trigger: 'blur'
					}],
					password: [{
						required: true,
						message: '请输入密码',
						trigger: 'blur'
					}],
				},
				phoneLog: {
					mobile: '',
					code: ''
				},
				phoneRules: {
					mobile: [{
						required: true,
						message: '请输入手机号',
						trigger: 'blur'
					}],
					code: [{
						required: true,
						message: '请输入验证码',
						trigger: 'blur'
					}],
				},
				buttonName: "发送验证码",
				time: 10,
				isDisabled: false,
				activeName: 'first',
				innerVisible: false,
				outerVisible: false,
				refreshCode: false,
				catcha: {
					blocky: 0,
					imgurl: '',
					miniimgurl: '',
					text: '向右滑动完成拼图',
					h: 260,
					w: 300,
					sh: 60,
					sw: 75,
				},
				IpInfo:{},
				showLog: false,
				userAvatar: '',
				lastScrollTop: 0,
				fixed: false,
				hidden: false,
				tags: [],
				mobileShow: false
			}
		},
		computed:{
			needInfo(){
				return this.$store.getters.needInfo
			}
		},
		created() {
			this.userAvatar = base.localUrl + getAvatar()
			this.getTags()
		},
		mounted() {
			 
			window.addEventListener('scroll', this.watchScroll)
		},
		beforeDestroy() {
			window.removeEventListener("scroll", this.watchScroll)
		},
		methods: {
			getIpName(){
				getIpName().then(res=>{
					const IpInfo = res.data.IpInfo
					this.IpInfo = res.data.IpInfo
					this.phoneLog = Object.assign(this.phoneLog,IpInfo)
				})
			},
			sendMsg() {
				let me = this;
				if (!this.phoneLog.mobile) {
					Message.error("请先输入手机号！")
					return
				}
				getMobileCodeAPI(this.phoneLog.mobile).then(res => {
					if (res.code === 200) {
						Message.success(res.message)
						me.isDisabled = true;
						let interval = window.setInterval(function() {
							me.buttonName = '(' + me.time + 's)后重新发送';
							--me.time;
							if (me.time < 0) {
								me.buttonName = "重新发送";
								me.time = 10;
								me.isDisabled = false;
								window.clearInterval(interval);
							}
						}, 1000);
					} else
						Message.error(res.message)
				})
			},
			longout() {
				MessageBox.confirm('注销登入?', '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning',
					 center: true
				}).then(() => {
					Message.info("退出成功！")
					removeToken()
					this.$store.dispatch('setNeedInfo',true)
					//location.reload()
				}).catch(() => {
					
				});
			},
			submitLogin() {
				this.$refs.login.validate(valid => {
					if (valid) {
						this.getIpName()
						this.onRefresh()
					} else {
						return false;
					}
				});
			},
			mobileLogin() {
				this.$refs.phonelogin.validate(valid => {
					if (valid) {
						this.mobileCodeLogin()
					} else
						return false;
				})
			},
			setSha256(password) {
				let sha256 = require("js-sha256").sha256 //这里用的是require方法，所以没用import
				return sha256(password)
			},
			onSuccess(left, stddev) {
				let user = {
					username: this.param.username,
					password: this.param.password,
					captchaUUid: this.param.captchaUUid,
					moveX: left
				}
				user = Object.assign(user,this.IpInfo);
				user.password = this.setSha256(user.password);
				this.login(user);
			},
			onFail() {
				this.$refs.dialogopen.handleFail()
				setTimeout(() => {
					this.onRefresh();
				}, 1500)
			},
			onRefresh() {
				this.refreshCode = false
				this.innerVisible = true
				this.getCaptcha()
			},
			login(user) {
				loginAPI(user).then(res => {
					if (res.code === 200) {
						Message.success(res.message)
						this.$refs.dialogopen.handleSuccess()
						this.innerVisible = false
						this.outerVisible = false
						this.userAvatar = base.localUrl + res.data.avatar
						setToken(res.data.token)
						setAvatar(res.data.avatar)
						this.$store.dispatch('setNeedInfo',false)
						this.$nextTick(() => this.$bus.$emit("reload"))
					} else if (res.code === 2003) {
						Message.error(res.message);
						this.onFail();
					} else {
						Message.error(res.message);
						this.innerVisible = false
					}

				})
			},
			mobileCodeLogin() {
				mobileCodeLoginAPI(this.phoneLog).then(res => {
					if (res.code === 200) {
						Message.success(res.message)
						this.outerVisible = false
						this.userAvatar = base.localUrl + res.data.avatar
						setToken(res.data.token)
						setAvatar(res.data.avatar)
						this.$store.dispatch('setNeedInfo',false)
						this.$nextTick(() => this.$bus.$emit("reload"))
					} else if (res.code === 2003) {
						Message.error(res.message);
					} else {
						Message.error(res.message);
						this.outerVisible = false
					}
				})
			},
			getCaptcha() {
				getCaptchaImageAPI().then(res => {
					if (res.code == 200) {
						this.catcha.blocky = res.data.ypos
						this.catcha.imgurl = 'data:image/png;base64,' + res.data.oriImage
						this.catcha.miniimgurl = 'data:image/png;base64,' + res.data.cutImage
						this.catcha.sh = res.data.cutImageHeight
						this.catcha.sw = res.data.cutImageWidth
						this.param.captchaUUid = res.data.captchaUUid
						this.$nextTick(() => this.refreshCode = true)
					}
				})
			},

			toCategory(msg) {
				if (this.$route.path !== "/") {
					this.$store.dispatch('setTag', msg)
					this.$store.dispatch('setContent', '')
					this.$router.replace("/")
				} else {
					this.$bus.$emit("searchTag", msg)
					this.$store.dispatch('setLoading', true)
					setTimeout(() => {
						this.$store.dispatch('setLoading', false)
					}, 500)
				}
			},
			toHome() {
				this.$bus.$emit("reload")
				this.getTags()
				if (this.$route.path !== "/") {
					this.$store.dispatch('setTag', '')
					this.$store.dispatch('setContent', '')
					this.$router.replace("/")
				} else {
					this.$store.dispatch('setLoading', true)
					setTimeout(() => {
						this.$store.dispatch('setLoading', false)
					}, 500)
				}
			},
			getTags() {
				getTagsAPI().then(res => {
					if (res.code == 200) {
						this.tags = res.data.tags
					}
				})
			},
			watchScroll() {
				let scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop
				if (scrollTop === 0) {
					this.fixed = false;
				} else if (scrollTop >= this.lastScrollTop) {
					this.fixed = false;
					this.hidden = true;
				} else {
					this.fixed = true
					this.hidden = false
				}
				this.lastScrollTop = scrollTop
			},
		}
	}
</script>

<style scoped lang="less">
	.ms-content {
		
	}

	.login-btn {
		text-align: center;
	}

	.login-btn button {
		width: 35%;
		height: 36px;
		margin-bottom: 10px;
	}

	.avatar {
		cursor: pointer;
		width: 30px;
		height: 30px;
		margin-top: 15%;
		img {
			width: 30px;
			height: 30px;
			object-fit: cover;
			border-radius: 50%;
			padding: 2px;
			border: 1px solid #DADADA;
			position: relative;
			transition: all 0.2s linear;
			overflow: hidden;

			&:hover {
				transform: scale(1.1, 1.1);
				filter: contrast(130%);
			}
		}
	}

	#layout-header {
		position: fixed;
		top: 0;
		z-index: 9;
		width: 100%;
		height: 80px;
		padding: 0 80px;
		display: flex;
		justify-content: space-between;
		align-items: center;
		transition: .3s all ease;
		-webkit-transition: .3s all ease;
		-moz-transition: .3s all linear;
		-o-transition: .3s all ease;
		-ms-transition: .3s all ease;

		&.hidden {
			top: -100px;
		}

		&.fixed {
			background-color: #FFFFFF;
			box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
		}
	}

	.site-logo {
		text-align: center;
		cursor: pointer;

		img {
			width: 50px;
			height: 50px;
		}

		p.site-name {
			font-size: 15px;
			font-weight: bold;
			position: relative;
			top: -10px;
		}
	}

	.menus-btn {
		display: none;
		visibility: hidden;
	}

	.site-menus {
		display: flex;
		align-items: center;
		
		.menu-item {
			min-width: 60px;
			height: 50px;
			line-height: 50px;
			text-align: center;
			position: relative;
			cursor: pointer;
			&:hover {
				color: #ff6d6d;
			}

			a {
				padding: 12px 10px;
				color: #545454;
				font-weight: 500;
				font-size: 16px;
				cursor: pointer;
				&:hover {
					color: #ff6d6d;
				}
			}

			&:not(:last-child) {
				margin-right: 15px;
			}
			
			&:last-child > span{
				display: none;
			}
			
			&.hasChild:hover .childMenu {
				opacity: 1;
				visibility: visible;
				transform: translateY(-5px);
			}
		}
		.childMenu {
			width: 130px;
			background-color: #FDFDFD;
			border-radius: 3px;
			border: 1px solid #ddd;
			box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
			position: absolute;
			top: 55px;
			z-index: 2;
			opacity: 0;
			visibility: hidden;
			transition: .7s all ease;
			-webkit-transition: .6s all ease;
			-moz-transition: .6s all linear;
			-o-transition: .6s all ease;
			-ms-transition: .6s all ease;

			&:before,
			&:after {
				content: '';
				position: absolute;
				width: 0;
				height: 0;
				border-left: 6px solid transparent;
				border-right: 6px solid transparent;
				border-bottom: 8px solid white;
				top: -8px;
				left: 20px;
			}

			&:before {
				top: -9px;
				border-bottom: 8px solid #ddd;
			}

			.sub-menu {
				height: 40px;
				line-height: 40px;
				position: relative;

				&:not(:last-child):after {
					/*position: absolute;*/
					content: '';
					width: 50%;
					height: 1px;
					color: #ff6d6d;
					bottom: 0;
					left: 25%;
					z-index: 99;
				}
			}
		}
	}

	@media (max-width: 960px) {
		#layout-header {
			padding: 0 20px;
		}
		.avatar{
			display: none;
		}
		
		.menu-item:last-child>span{
			display: block !important;
		}
	}

	@media (max-width: 600px) {
		#layout-header {
			padding: 0 10px;
		}
		.avatar{
			display: none;
		}
		.menus-btn {
			display: block;
			visibility: visible;
		}

		.site-menus {
			position: absolute;
			display: none;
			visibility: hidden;
			background-color: #F9F9F9;
			width: 100%;
			left: 0;
			top: 80px;
			z-index: -9;
			box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

			.menu-item {
				position: relative;
				height: unset;

				&:not(:last-child) {
					margin-right: 0;
				}
				
			}

			.childMenu {
				position: relative;
				width: 100%;
				top: 0;
				background-color: #F3F3F3;
				opacity: 1;
				visibility: visible;
				border: none;
				box-shadow: none;

				&:before,
				&:after {
					content: '';
					position: relative;
					width: 0;
					height: 0;
					border-left: 0;
					border-right: 0;
					border-bottom: 0;
				}
			}
		}

		.site-menus.mobileShow {
			display: inline-block;
			visibility: visible;
			z-index: 99;
		}
	}
</style>
