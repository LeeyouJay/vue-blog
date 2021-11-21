<template>
	<div>
		<el-row :gutter="20">
			<el-col :span="8">
				<el-card shadow="always">
					<div slot="header" class="user-head">个人信息</div>
					<div class="user-info">
						<div style="margin-bottom: 20px;">
							<img :src="userInfo.avatar" class="user-avator" v-if="reloadImg" alt />
						</div>
						
						<el-popover placement="top" width="200" v-model="visible">
							<p>选择更换的方式：</p>
							<div style="text-align: right; margin: 0">
								<el-button size="mini" type="text" v-for="(item,index) in authSocial" :key="index" v-if="item.bind" @click="avatarTo(item.platform)">{{item.platform}}头像</el-button>
								<el-button size="mini" type="text" @click="editVisible = true" >本地上传</el-button>
							</div>
							<el-button type="primary" slot="reference" size="mini" round>更换头像</el-button>
						</el-popover>

					</div>
					<ul class="list-group">
						<li class="list-group-item">
							<i class="el-icon-star-off"></i>
							登入账号：
							<div style="float: right;">{{userInfo.username}}</div>
						</li>
						<li class="list-group-item">
							<i class="el-icon-star-off"></i>
							手机号码：
							<div style="float: right;">{{userInfo.phone}}</div>
						</li>
						<li class="list-group-item">
							<i class="el-icon-star-off"></i>
							用户邮箱：
							<div style="float: right;">{{userInfo.email}}</div>
						</li>
						<li class="list-group-item">
							<i class="el-icon-star-off"></i>
							创建日期：
							<div style="float: right;">{{userInfo.createTime}}</div>
						</li>
						<li class="list-group-item">
							<i class="el-icon-star-off"></i>
							绑定社交账号：
							<div style="float: right;">
								<SocialAuth :userInfo="userInfo"></SocialAuth>
							</div>
						</li>
					</ul>
				</el-card>
			</el-col>
			<el-col :span="16">
				<el-card shadow="always">
					<div slot="header" class="user-head">基本信息</div>
					<el-tabs v-model="activeName">
						<el-tab-pane label="基本资料" name="first">
							<el-form :model="userInfo" status-icon :rules="rules" ref="ruleForm1" label-width="100px"
								class="demo-ruleForm">
								<el-form-item label="用户昵称" prop="nickName">
									<el-input type="text" v-model="userInfo.nickName" autocomplete="off"></el-input>
								</el-form-item>
								<el-form-item label="邮箱" prop="email">
									<el-input type="text" v-model="userInfo.email" autocomplete="off"></el-input>
								</el-form-item>
								<el-form-item label="手机号码" prop="phone">
									<el-input type="text" v-model="userInfo.phone" autocomplete="off"></el-input>
								</el-form-item>
								<el-form-item label="性别" prop="sex">
									<el-radio-group v-model="userInfo.sex">
										<el-radio label="男"></el-radio>
										<el-radio label="女"></el-radio>
										<el-radio label="保密"></el-radio>
									</el-radio-group>
								</el-form-item>
								<el-form-item>
									<el-button type="primary" @click="submitForm('ruleForm1')">提交</el-button>
									<el-button @click="resetForm('ruleForm1')">重置</el-button>
								</el-form-item>
							</el-form>
						</el-tab-pane>
						<el-tab-pane label="修改密码" name="second">
							<el-form :model="ruleForm" status-icon :rules="rules" ref="ruleForm2" label-width="100px"
								class="demo-ruleForm">
								<el-form-item label="旧密码" prop="oldPass">
									<el-input type="password" placeholder="请输入旧密码" v-model="ruleForm.oldPass"
										autocomplete="off"></el-input>
								</el-form-item>
								<el-form-item label="新密码" prop="newPass">
									<el-input type="password" placeholder="请输入新密码" v-model="ruleForm.newPass"
										autocomplete="off"></el-input>
								</el-form-item>
								<el-form-item label="确认密码" prop="checkPass">
									<el-input type="password" placeholder="请确认密码" v-model="ruleForm.checkPass"
										autocomplete="off"></el-input>
								</el-form-item>
								<el-form-item>
									<el-button type="primary" @click="submitForm('ruleForm2')">提交</el-button>
									<el-button @click="resetForm('ruleForm2')">重置</el-button>
								</el-form-item>
							</el-form>
						</el-tab-pane>
					</el-tabs>
				</el-card>
			</el-col>
		</el-row>

		<!-- 编辑弹出框 -->
		<el-dialog :visible.sync="editVisible" width="50%" center>
			<template slot="title">
				<span class="dialog-title">头像设置</span>
			</template>
			<el-card shadow="always">
				<div class="cropper-content">
					<el-row :gutter="20">
						<el-col :span="12">
							<div style="height: 400px;">
								<VueCropper ref="cropper" :img="configObj.imgSrc" :can-move="true" :auto-crop="true"
									:center-box="false" :info-true="true" :full="false" :fixedBox="true"
									:autoCropWidth="200" :autoCropHeight="200" output-type="png" @realTime="realTime" />
							</div>
							<div style="margin-top: 20px;">
								<el-row :gutter="20">
									<el-col :span="8">
										<el-upload ref="uploadImage" class="upload-demo" action="#"
											:http-request="requestUpload" accept=".jpg, .png, .bmp, .jpeg, .webp"
											:multiple="false" :show-file-list="false" :auto-upload="false"
											:on-change="handleChange">
											<el-button size="small" plain type="primary">选择照片</el-button>
										</el-upload>
									</el-col>
									<el-col :span="16">
										<el-button size="small" icon="el-icon-plus" plain type="primary"
											@click="changeScale(1)"></el-button>
										<el-button size="small" icon="el-icon-minus" plain type="primary"
											@click="changeScale(-1)"></el-button>
										<el-button size="small" icon="el-icon-refresh-left" plain type="primary"
											@click="rotateLeft"></el-button>
										<el-button size="small" icon="el-icon-refresh-right" plain type="primary"
											@click="rotateRight"></el-button>
									</el-col>
								</el-row>
							</div>
						</el-col>
						<el-col :span="12">
							<div class="avatar-upload-preview">
								<div
									:style="{'width': previews.w + 'px', 'height': previews.h + 'px', 'zoom': (200 / previews.h)}">
									<el-image :src="previews.url" :style="previews.img" alt="预览"></el-image>
								</div>
							</div>
							<div class="avatar-upload-button">
								<el-button type="primary" :loading="loading" @click="saveEdit">提 交</el-button>
							</div>
						</el-col>
					</el-row>
				</div>
			</el-card>
		</el-dialog>
	</div>

</template>

<script>
	import {
		VueCropper
	} from 'vue-cropper'
	import SocialAuth from './socialAuth.vue'
	import base from '../../api/base.js'
	export default {
		components: {
			VueCropper,
			SocialAuth
		},
		computed:{
			authSocial(){
				const authSocial =  this.$store.state.authSocial
				authSocial.map(val=>{
					if(this.userInfo.qq)
						if(val.platform === 'QQ') val.bind = true
					if(this.userInfo.github)
						if(val.platform === 'github') val.bind = true
					if(this.userInfo.gitee)
						if(val.platform === 'gitee') val.bind = true
					if(this.userInfo.baidu)
						if(val.platform === 'baidu') val.bind = true
				})
				return authSocial
			}
		},
		data() {
			var checkPhone = (rule, value, callback) => {
				const reg = /^1[3|4|5|7|8][0-9]\d{8}$/
				if (value !== "") {
					if (!reg.test(value)) {
						callback(new Error('请输入正确的手机号'));
					} else {
						callback();
					}
				} else {
					callback();
				}

			};
			var validatePass = (rule, value, callback) => {
				if (value === '') {
					callback(new Error('请输入密码'));
				} else {
					if (this.ruleForm.checkPass !== '') {
						this.$refs.ruleForm2.validateField('checkPass');
					}
					callback();
				}
			};
			var validatePass2 = (rule, value, callback) => {
				if (value === '') {
					callback(new Error('确认密码不能为空'));
				} else if (value !== this.ruleForm.newPass) {
					callback(new Error('两次输入密码不一致!'));
				} else {
					callback();
				}
			};
			return {
				reloadImg: true,
				basicUrl: '',
				loading: false,
				editVisible: false,
				visible: false,
				activeName: 'first',
				userInfo: {},
				ruleForm: {
					oldPass: '',
					newPass: '',
					checkPass: '',
				},
				rules: {
					oldPass: [{
						required: true,
						validator: validatePass,
						trigger: 'blur',
						message: '旧密码不能为空'
					}],
					newPass: [{
						required: true,
						validator: validatePass,
						trigger: 'blur',
						message: '新密码不能为空'
					}],
					checkPass: [{
						required: true,
						validator: validatePass2,
						trigger: 'blur'
					}],
					nickName: [{
						required: true,
						trigger: 'blur',
						message: '昵称不能为空'
					}],
					phone: [{
						validator: checkPhone,
						trigger: 'blur',
					}],
					email: [{
						required: true,
						trigger: 'blur',
						message: '邮箱不能为空'
					}],
				},
				configObj: {
					imgSrc: '',
					canMove: false,
					autoCrop: true,
					centerBox: true,
				},
				previews: {}
			}
		},
		created() {
			this.basicUrl = base.localUrl
			this.getUserInfo();
		},
		methods: {
			getUserInfo() {
				this.$api.system.getUserInfoAPI().then(res => {
					if (res.code === 200) {
						this.userInfo = res.data.userInfo;
						if (this.userInfo.avatar.indexOf('-thumbnail') != -1) {
							this.userInfo.avatar = this.basicUrl + this.userInfo.avatar
							this.configObj.imgSrc = this.rename(this.userInfo.avatar)
						} else
							this.configObj.imgSrc = this.userInfo.avatar
					} else if (res.code === 401) {
						this.$removeToken()
						this.$alert(res.message, "提示", {
							type: 'warning',
							center: true,
							callback: action => {
								this.$router.replace('/login')
							}
						})
					} else
						this.$message.error(res.message)
				})
			},
			rename(imgUrl) {
				return imgUrl.substr(0, imgUrl.lastIndexOf('-')) + '.jpg'
			},
			changePassword(temp) {
				this.$api.system.changePasswordAPI(temp).then(res => {
					if (res.code === 200) {
						this.resetForm('ruleForm2')
						this.$message.success(res.message)
					} else if (res.code === 401) {
						this.$removeToken()
						this.$alert(res.message, "提示", {
							type: 'warning',
							center: true,
							callback: action => {
								this.$router.replace('/login')
							}
						})
					} else
						this.$message.error(res.message)
				})
			},
			changeUserInfo() {
				this.$api.system.changeUserInfoAPI(this.userInfo).then(res => {
					if (res.code === 200) {
						
						this.$message.success(res.message)
					} else if (res.code === 401) {
						this.$removeToken()
						this.$alert(res.message, "提示", {
							type: 'warning',
							center: true,
							callback: action => {
								this.$router.replace('/login')
							}
						})
					} else
						this.$message.error(res.message)
				})
			},
			submitForm(formName) {
				this.$refs[formName].validate((valid) => {
					if (valid) {
						if (formName === 'ruleForm1') {
							this.changeUserInfo();
						} else {
							let temp = {
								username: this.userInfo.username,
								oldPassword: this.ruleForm.oldPass,
								newPassword: this.ruleForm.newPass
							}
							temp.newPassword = this.setSha256(temp.newPassword)
							temp.oldPassword = this.setSha256(temp.oldPassword)
							this.changePassword(temp)
						}
					} else {
						console.log("验证不通过！")
						return false;
					}
				});
			},
			changeScale(size) {
				this.$refs.cropper.changeScale(size)
			},
			rotateLeft() {
				this.$refs.cropper.rotateLeft()
			},
			rotateRight() {
				this.$refs.cropper.rotateRight()
			},
			//头像提交按钮
			saveEdit() {
				this.$refs.cropper.getCropBlob(data => {
					this.loading = true
					let formData = new FormData();
					formData.append("avatarfile", data);
					this.$api.system.changeAvatarAPI(formData).then(res => {
						if (res.code === 200) {
							this.$message.success(res.message)
							this.reloadImg = false
							this.editVisible = false
							this.loading = false
							this.userInfo.avatar = this.basicUrl + res.data.imgUrl
						} else if (res.code === 401) {
							this.$removeToken()
							this.$alert(res.message, "提示", {
								type: 'warning',
								center: true,
								callback: action => {
									this.$router.replace('/login')
								}
							})
						} else
							this.$message.error(res.message)
						this.$nextTick(() => this.reloadImg = true)
					})
				});
			},
			resetForm(formName) {
				this.$refs[formName].resetFields();
			},
			avatarTo(platform){
				this.$api.system.changeAvatarFromPlatformAPI(platform).then(res=>{
					if(res.code === 200){
						this.$message.success(res.message)
						this.visible = false
						setTimeout(()=>{
							this.getUserInfo()
						},500)
					}else
						this.$message.error(res.message)
					
				})
			},
			handleChange(image) {
				const rawImage = image.raw
				if (!rawImage) return false
				if (rawImage.type.indexOf("image/") == -1) {
					this.$message.warning('图片只支持.jpg, .png 格式!')
					return false
				}
				if (this.isLimit3M(rawImage)) {
					this.readImage(rawImage)
				}
			},
			isLimit3M(image) {
				const isLimit3M = image.size / 1024 / 1024 < 3
				if (isLimit3M) {
					return true
				} else {
					this.$message.warning('上传的图片大小不能超过3M!')
					return false
				}
			},
			isImage(image) {
				return /\.(jpg|png|bmp|jpeg|webp)$/.test(image.name)
			},
			readImage(image) {
				const reader = new FileReader()
				reader.onload = e => {
					let data
					if (typeof e.target.result === 'object') {
						// 把Array Buffer转化为blob 如果是base64不需要
						data = window.URL.createObjectURL(new Blob([e.target.result]))
					} else {
						data = e.target.result
					}
					this.configObj.imgSrc = data
				}
				// 转化为base64
				reader.readAsDataURL(image)
				// 转化为blob
				// reader.readAsArrayBuffer(image)
				reader.onerror = e => {
					this.$message.error('图片读取出错!')
				}
			},
			setSha256(password) {
				let sha256 = require("js-sha256").sha256
				return sha256(password)
			},
			realTime(data) {
				this.previews = data
			},
			requestUpload() {

			}
		}

	}
</script>

<style scoped>
	.avatar-upload-button {
		text-align-last: center;
		top: 90%;
		left: 75%;
		position: absolute;
	}

	.cropper-content {
		/* height: 360px;
		width: 360px;
		padding-left: 10px; */
	}

	.user-info {
		text-align: center;
		/* padding-bottom: 20px; */
		margin-bottom: 20px;
	}

	.user-avator {
		width: 120px;
		height: 120px;
		border-radius: 50%;
	}

	.avatar-upload-preview {
		position: absolute;
		overflow: hidden;
		top: 40%;
		-webkit-transform: translate(50%, -50%);
		transform: translate(50%, -50%);
		width: 200px;
		height: 200px;
		border-radius: 50%;
	}

	.user-head {}

	.list-group-item {
		border-bottom: 1px solid #e7eaec;
		border-top: 1px solid #e7eaec;
		margin-bottom: -1px;
		padding: 11px 0;
		font-size: 15px;
	}

	.list-group {
		padding-left: 0;
		list-style: none;
	}
</style>
