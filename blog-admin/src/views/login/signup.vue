<template>
	<el-dialog title="注册" center :visible.sync="openStatus" width="30%" :before-close="handleClose">
		<el-form :model="ruleForm" status-icon :rules="registered" ref="ruleForm" label-width="100px" class="demo-ruleForm">
			<el-form-item label="登入账号" prop="username">
				<el-input v-model="ruleForm.username" autocomplete="off"></el-input>
			</el-form-item>
			<el-form-item label="密码" prop="password">
				<el-input type="password" v-model="ruleForm.password" autocomplete="off"></el-input>
			</el-form-item>
			<el-form-item label="确认密码" prop="checkPass">
				<el-input type="password" v-model="ruleForm.checkPass" autocomplete="off"></el-input>
			</el-form-item>
			<el-form-item label="邮箱" prop="email">
				<el-input v-model="ruleForm.email"></el-input>
			</el-form-item>
			
			<el-form-item label="验证码" prop="verifyCode">
				<el-row>
					<el-col :span="9">
						<el-input v-model="ruleForm.verifyCode"></el-input>
					</el-col>
					<el-col :span="9">
						<el-button style="margin-left: 20px;" @click="toSendCode" type="primary" :disabled="isDisabled">{{buttonName}}
						</el-button>
					</el-col>
				</el-row>
			</el-form-item>
		</el-form>
		<span slot="footer" class="dialog-footer">
			<el-button type="primary" @click="resetForm('ruleForm')">取 消</el-button>
			<el-button type="primary" @click="submitRegistered('ruleForm')">提 交</el-button>
		</span>
	</el-dialog>
</template>

<script>
	export default {
		props: {
			visible: {
				type: Boolean,
				default: false
			}
		},
		watch: {
			visible(val) {
				this.openStatus = val
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
			var checkEmail = (rule, value, callback) => {
				const mailReg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/
				if (!value) {
					return callback(new Error('邮箱不能为空'))
				}
				if (mailReg.test(value)) {
					callback()
				} else {
					callback(new Error('请输入正确的邮箱格式'))
				}
			}
			var validatePass = (rule, value, callback) => {
				if (value === "") {
					callback(new Error('请输入密码'));
				} else {
					if (this.ruleForm.checkPass !== '') {
						this.$refs.ruleForm.validateField('checkPass');
					}
					callback();
				}
			};
			var validatePass2 = (rule, value, callback) => {
				if (value === '') {
					callback(new Error('请再次输入密码'));
				} else if (value !== this.ruleForm.password) {
					callback(new Error('两次输入密码不一致!'));
				} else {
					callback();
				}
			};
			return {
				openStatus: this.visible,
				captchaImage: '',
				isDisabled:false,
				buttonName: "发送验证码",
				time: 10,
				ruleForm: {
					id: '',
					username: '',
					password: '',
					checkPass: '',
					email:'',
					phone: '',
				},
				registered: {
					username: [{
							required: true,
							message: '请输入登入账号'
						},
						{
							max: 10,
							message: '长度在 10字符以内',
							trigger: 'blur'
						},
					],
					
					password: [{
						required: true,
						validator: validatePass,
						trigger: 'blur'
					}],
					checkPass: [{
						required: true,
						validator: validatePass2,
						trigger: 'blur'
					}],
					email: [{
						required: true,
						validator: checkEmail,
						trigger: 'blur'
					}],
					phone: [{
						validator: checkPhone,
						trigger: 'blur'
					}],
					verifyCode: [{
						required: true,
						message: '请输入验证码'
					}]
				},
			};
		},
		methods: {
			submitRegistered(formName) {
				this.$refs.ruleForm.validate(valid => {
					if (valid) {
						let signForm = Object.assign({}, this.ruleForm)
						signForm.password = this.setSha256(signForm.password);
						//先将验证码绑定到id
						signForm.id = signForm.verifyCode
						this.signup(signForm);
					} else {
						return false;
					}
				});
			},
			signup(signForm) {
				this.$api.login.signupApi(signForm).then(res => {
					if (res.code === 200) {
						this.$emit('dialogData', false)
						this.$refs['ruleForm'].resetFields()
						this.$message.success(res.message);
					} else {
						this.ruleForm.password = ""
						this.ruleForm.checkPass = ""
						this.$message.error(res.message);
					}
				})
			},
			toSendCode(){
				this.$refs.ruleForm.validateField('email',valid => {
						this.sendMsg(this.ruleForm.email)
				});
			},
			sendMsg(email) {
				let me = this;
				this.$api.login.getVerifyCode(email).then(res => {
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
			setSha256(password) {
				let sha256 = require("js-sha256").sha256
				return sha256(password)
			},
			resetForm(formName) {
				this.$emit('dialogData', false)
				this.$refs[formName].resetFields();
			},
			handleClose(done) {
				this.$emit('dialogData', false)
			}
		},
	};
</script>

<style scoped>

</style>
