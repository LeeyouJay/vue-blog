<template>
	<div class="main-editor" v-if="show">
		<el-form :model="param" :rules="rules" ref="comment" label-width="0px" >
			<el-row :gutter="10" v-if="needInfo">
				<el-col :span="8">
					<el-form-item prop="fromUserName">
						<el-popover
						    ref="popover"
						    placement="top-start"
						    width="200"
						    trigger="focus"
						    content="输入QQ号自动获取昵称头像">
						  </el-popover>
						<el-input size="small" placeholder="(必填)昵称" :disabled="lockInput" v-popover:popover v-model="param.fromUserName" @blur="onBlur(param.fromUserName)">
						</el-input>
					</el-form-item>
				</el-col>
				<el-col :span="8">
					<el-form-item prop="fromUserEmail">
						<el-input size="small" placeholder="(必填)邮箱" :disabled="lockInput" type="email" v-model="param.fromUserEmail">
						</el-input>
					</el-form-item>
				</el-col>
				<el-col :span="8">
					<el-form-item>
						<el-input size="small" placeholder="(选填)http://" v-model="param.fromUserSite">
						</el-input>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row>
				<el-col :span="24">
					<el-form-item prop="content">
						<comment-message-editor ref="editor" @submit="submitReply"></comment-message-editor>
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>

	</div>
</template>

<script>
	import commentMessageEditor from '@/components/message-editor'

	import {
		getToken
	} from '@/utils/cookie.js'
import {
		getQQUser
	} from '../api'
	export default {
		name: "commentEditor",
		props: {
			comment: {
				type: Object
			},
			show: {
				type: Boolean,
				default: false
			}
		},
		data() {
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
			return {
				lockInput:false,
				rules: {
					fromUserName: [{
						required: true, 
						message: '请输入昵称',
						trigger: 'blur'
					}],
					fromUserEmail: [{
						validator: checkEmail,
						trigger: ['blur', 'change']
					}]
				}
			}
		},
		components: {
			commentMessageEditor,
		},
		computed: {
			needInfo() {
				return this.$store.getters.needInfo
			},
			param(){
				return this.$store.getters.param
			}
		},
		mounted() {

		},
		methods: {
			submitReply(v) {
				this.param.content = v
				this.$refs['comment'].validate((valid) => {
					if (valid) {
						this.$store.dispatch('setParam' ,this.param)
						this.$emit('onSubmit',this.param)
					} else {
						console.log('验证失败！');
						return false;
					}
				});
			},
			onBlur(value){
				const qqReg=/^[1-9]\d{4,10}$/;
				if (qqReg.test(value)) {
					this.lockInput = true
					setTimeout(()=>{
						getQQUser(value).then(res=>{
							this.param.fromUserName = res.data.nickName
							this.param.fromUserEmail = value+'@qq.com'
							//this.param.fromUserAvatar =  res.data.avatar
							this.param.fromUserAvatar = 'http://q1.qlogo.cn/g?b=qq&nk='+value+'&s=100'
						})
						this.lockInput = false
					},1200)
				}
			},
			clearContent() {
				this.$refs.editor.clearContent()
			}

		}
	}
</script>
<style scoped lang="less">
.main-editor {
	animation: main 0.7s;
}
</style>
