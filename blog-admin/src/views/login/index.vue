<template>
	<div class="login-wrap">
		<div class="ms-login">
			<div class="ms-title">
				<div style="width: 40%;  height: 60px; text-align: right;">
					<img src="../../assets/img/github.png" width="45" />
				</div>
				<div style="padding-left: 10px;">ArslinthBoot</div>
			</div>
			<el-tabs v-model="activeName" @tab-click="handleClick">
				<el-tab-pane label="账号密码登入" name="first">
					<el-form :model="param" :rules="rules" ref="login" label-width="0px" class="ms-content">
						<el-form-item prop="username">
							<el-input placeholder="账号" v-model="param.username">
								<el-button slot="prepend" icon="el-icon-user"></el-button>
							</el-input>
						</el-form-item>
						<el-form-item prop="password">
							<el-input placeholder="密码" type="password" v-model="param.password"
								@keyup.enter.native="submitLogin()">
								<el-button slot="prepend" icon="el-icon-lock"></el-button>
							</el-input>
						</el-form-item>
						<div class="login-btn">
							<el-button type="primary" @click="submitLogin()">登录</el-button>
						</div>
						<div>
							<p class="login-tips">Tips : 用户名和密码随便注册。</p>
							<el-link type="primary" @click="visible = true">去注册</el-link>
						</div>
					</el-form>
				</el-tab-pane>
				<el-tab-pane label="验证码登入" name="second">
					<el-form :model="phoneLog" :rules="phoneRules" ref="phonelogin" label-width="0px"
						class="ms-content">
						<el-form-item prop="mobile">
							<el-input placeholder="手机号/邮箱" v-model="phoneLog.mobile">
								<el-button slot="prepend" icon="el-icon-phone"></el-button>
							</el-input>
						</el-form-item>
						<el-form-item prop="code">
							<el-row :gutter="20">
								<el-col :span="15">
									<el-input placeholder="验证码" v-model="phoneLog.code">
										<el-button slot="prepend" icon="el-icon-key"></el-button>
									</el-input>
								</el-col>
								<el-col :span="9">
									<el-button @click="sendMsg" type="primary" :disabled="isDisabled">{{buttonName}}
									</el-button>
								</el-col>
							</el-row>
						</el-form-item>
						<div class="login-btn">
							<el-button type="primary" @click="mobileLogin()">登录</el-button>
						</div>
						<div>
							<p class="login-tips">Tips : 手机短信功能暂不支持。</p>
						</div>
					</el-form>
				</el-tab-pane>
			</el-tabs>
		</div>
		<Signup ref="signupDialog" :visible="visible" @dialogData="dialogData" />
		<!-- 验证码弹框 -->
		<el-dialog width="340px" append-to-body :visible.sync="dialogVisible" :show-close="false"
			:close-on-click-modal="true">
			<template slot="title">
				<span class="dialog-title">请完成图片验证</span>
			</template>
			<Captcha v-if="refreshCode" ref="dialogopen" :l="42" :r="10" :w="catcha.w" :h="catcha.h" :sw="catcha.sw"
				:sh="catcha.sh" :blocky="catcha.blocky" :imgurl="catcha.imgurl" :miniimgurl="catcha.miniimgurl"
				:slider-text="catcha.text" @success="onSuccess" @fail="onFail" @refresh="onRefresh" />
		</el-dialog>

	</div>
</template>

<script>
	import {
		setToken,
		setAvatar
	} from '../../utils/cookie.js';
	import Signup from './signup.vue'
	import Captcha from './captcha.vue'
	export default {
		components: {
			Signup,
			Captcha
		},
		data() {
			return {
				buttonName: "发送验证码",
				isDisabled: false,
				time: 10,
				activeName: 'first',
				dialogVisible: false,
				visible: false,
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
				//登入表单
				param: {
					username: '',
					password: '',
					captchaUUid: '',
					moveX: '',
				},
				IpInfo:{},
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
					code: '',
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
				}
			};
		},
		created() {
			this.getIpName()
		},
		methods: {
			getIpName(){
				this.$api.login.getIpName().then(res=>{
					const IpInfo = res.data.IpInfo
					this.IpInfo = res.data.IpInfo
					this.phoneLog = Object.assign(this.phoneLog,IpInfo);
				})
			},
			sendMsg() {
				let me = this;
				if (!this.phoneLog.mobile) {
					this.$message.error("请先输入手机号或邮箱！")
					return
				}
				this.$api.login.getMobileCode(this.phoneLog.mobile).then(res => {
					if (res.code === 200) {
						this.$message.success(res.message)
						me.isDisabled = true;
						let interval = window.setInterval(function() {
							me.buttonName = '(' + me.time + '秒)后重新发送';
							--me.time;
							if (me.time < 0) {
								me.buttonName = "重新发送";
								me.time = 10;
								me.isDisabled = false;
								window.clearInterval(interval);
							}
						}, 1000);
					} else
						this.$message.error(res.message)
				})
			},

			submitLogin() {
				this.$refs.login.validate(valid => {
					if (valid) {
						this.onRefresh()
					} else {
						return false;
					}
				});
			},
			login(user) {
				this.$api.login.loginApi(user).then(res => {
					if (res.code === 200) {
						this.$message.success(res.message)
						this.$refs.dialogopen.handleSuccess()
						setToken(res.data.token)
						setAvatar(res.data.avatar)
						localStorage.setItem('ms_username', res.data.nickName)
						setTimeout(() => {
							this.$router.push('/');
						}, 1000)
					} else if (res.code === 2003) {
						this.$message.error(res.message);
						this.onFail();
					} else {
						this.$message.error(res.message);
						this.dialogVisible = false
					}

				})
			},
			mobileLogin() {
				this.$refs.phonelogin.validate(valid => {
					if (valid) {
						this.mobileCodeLogin()
					} else
						return false;
				})
			},

			mobileCodeLogin() {
				this.$api.login.mobileCodeLoginApi(this.phoneLog).then(res => {
					if (res.code === 200) {
						this.$message.success(res.message)
						setToken(res.data.token)
						setAvatar(res.data.avatar)
						localStorage.setItem('ms_username', res.data.nickName)
						setTimeout(() => {
							this.$router.push('/');
						}, 1000)
					} else if (res.code === 2003) {
						this.$message.error(res.message);
						this.onFail();
					} else {
						this.$message.error(res.message);
						this.dialogVisible = false
					}
				})
			},
			setSha256(password) {
				let sha256 = require("js-sha256").sha256 //这里用的是require方法，所以没用import
				return sha256(password)
			},
			dialogData(data) {
				this.visible = data
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
				this.dialogVisible = true
				this.getCaptcha()
			},
			getCaptcha() {
				this.$api.login.getCaptcha().then(res => {
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
			handleClick(tab, event) {
				//console.log(tab, event);
			}
		},
	};
</script>

<style scoped>
	.dialog-title {
		line-height: 24px;
		font-size: 18px;
		color: #303133;
		font-weight: bold;
	}

	.login-wrap {
		position: relative;
		width: 100%;
		height: 100%;
		background-image: url(https://images.pexels.com/photos/1323550/pexels-photo-1323550.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260);
		background-size: 100%;
	}

	.ms-title {
		padding-top: 10px;
		width: 100%;
		line-height: 50px;
		display: inline-flex;
		font-size: 20px;
		color: #000;
	}

	.ms-login {
		position: absolute;
		left: 36%;
		top: 25%;
		width: 24%;
		padding: 20px 30px;
		border-radius: 5px;
		background: rgba(255, 255, 255, 30%);
		overflow: hidden;
	}

	.ms-content {
		padding: 10px 10px;
	}

	.login-btn {
		text-align: center;
	}

	.login-btn button {
		width: 35%;
		height: 36px;
		margin-bottom: 10px;
	}

	.login-tips {
		font-size: 12px;
		line-height: 30px;
		color: #000;
	}
</style>
