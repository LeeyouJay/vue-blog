<template>
	<div class="friend" id="main">
		<span class="linkss-title">友情链接</span>
		<div class="links">
			<ul class="link-items">
				
				<li class="link-items" v-for="(item,index) in friends" :key="item.id">
					<a class="link-item-inner" :href="item.siteLink" target="_blank">
						<img style="border-radius: 50%;" height="55px" width="55px"
							:src="item.icon" @error="imgerrorfun()" />
						<span class="sitename">{{item.siteName}}</span>
						<el-tooltip  effect="light" :open-delay="1000" :content="item.description" placement="top-start">
							<div class="linkdes">{{item.description}}</div>
						</el-tooltip>
					</a>
				</li>
			</ul>
		</div>
		站点信息：
		<div class="info-card">
		   <p>
			   站名：Arslinth<br />
			   链接：<a href="https://chenyi.ink" target="_blank">https://chenyi.ink</a> <br />
			   邮箱：752279593@qq.com<br />
			   描述：面向自由和真理的无名献身。<br />
			   图标链接：<a href="https://avatars.githubusercontent.com/u/40257629?v=4" target="_blank">https://avatars.githubusercontent.com/u/40257629?v=4</a><br />
		   </p>
		</div>
		<div style="line-height: 2rem;" class="info">
			注意啦！不定时清理打不开的友链！
			暂只接受科技代码类博客，
			交换友链请提供以下信息：
		</div>
		<div style="margin-top: 30px;" class="info">
			<el-form :model="friend" :rules="rules" ref="linkApply" label-width="0px">
				<el-row>
					<el-col :span="16">
						<el-form-item prop="icon">
							<div class="item-align">
								<img style="border-radius: 50%;" height="55px" width="55px"
									:src="icon" @error="imgerrorfun()"/>
							</div>
							<el-input :clearable="true" class="item-align" style="width: calc(100% - 100px);margin-left: 10px;" size="small" placeholder="网站头像url" v-model="friend.icon">
							</el-input>
						</el-form-item>
					</el-col>
				</el-row>
				<el-row :gutter="10">
					<el-col :span="8">
						<el-form-item prop="siteName">
							<el-input size="small" placeholder="站名" maxlength="20" v-model="friend.siteName">
							</el-input>
						</el-form-item>
					</el-col>
					<el-col :span="8">
						<el-form-item prop="email">
							<el-input size="small" placeholder="邮箱" maxlength="100" type="email" v-model="friend.email">
							</el-input>
						</el-form-item>
					</el-col>
					<el-col :span="8">
						<el-form-item prop="siteLink">
							<el-input size="small" placeholder="地址(http://)" v-model="friend.siteLink">
							</el-input>
						</el-form-item>
					</el-col>
				</el-row>
				<el-row>
					<el-col :span="24">
						<el-form-item prop="description">
							<el-input type="textarea" :rows="3" placeholder="简单描述一下您的网站吧..." v-model="friend.description">
							</el-input>
						</el-form-item>
						<el-form-item>
							<el-button type="primary" size="mini" @click="applyFriend">提交</el-button>
						</el-form-item>
					</el-col>
				</el-row>
			</el-form>
		</div>
	</div>
</template>

<script>
	import {
		applyFriendAPI,
		getFriends
	} from '../api'
	export default {
		name: "Friend",
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
				friends:[],
				friend: {
					icon:'',
					siteName: '',
					siteLink: '',
					email: '',
					description: ''
				},
				rules: {
					icon: [{
						required: true,
						message: '请输入图标链接',
						trigger: 'blur'
					}],
					siteName: [{
						required: true,
						message: '请输入站点名称',
						trigger: 'blur'
					}],
					siteLink: [{
						required: true,
						message: '请输入站点链接',
						trigger: 'blur'
					}],
					email: [{
						validator: checkEmail,
						trigger: 'blur'
					}]
				}
			}
		},
		computed:{
			icon(){
				if(this.friend.icon == "")
					return "https://gravatar.loli.net/avatar/baee522214bec0d6d3664fa8a6640a1c?s=80&r=X&d=mm"
				else
					return this.friend.icon
			}
		},
		methods: {
			imgerrorfun(){
				let img = event.srcElement;
					 img.src = 'https://gravatar.loli.net/avatar/baee522214bec0d6d3664fa8a6640a1c?s=80&r=X&d=mm';
					 img.onerror = null; 
			},
			applyFriend(){
				this.$refs['linkApply'].validate((valid) => {
					if (valid) {
						applyFriendAPI(this.friend).then(res=>{
							if(res.code === 200){
								this.$message.success(res.message)
								setTimeout(()=>{
									this.$refs['linkApply'].resetFields();
								},1000)
							}else
								this.$message.error(res.message)
						})
					} else {
						console.log('验证失败！');
						return false;
					}
				});
			},
			getFriends(){
				getFriends().then(res=>{
					if(res.code === 200){
						this.friends = res.data.friends
						console.log(this.friends)
					}
				})
			}
		},
		mounted() {
			this.getFriends()
		}
	}
</script>

<style scoped lang="less">
	#main {
		animation: main 1.5s;
	}

	.friend {
		margin-top: 160px;
		max-width: 800px;
		padding: 0 10px;
		margin-left: auto;
		margin-right: auto;
	}

	.link-apply {
		width: 100%;
		height: 100px;
		border-top: 1px solid #ddd;
		border-left: 1px solid #ddd;
		box-shadow: 1px 1px 1px rgba(0, 0, 0, 0.2);
	}

	.links {
		font-family: miranafont, "Hiragino Sans GB", STXihei, "Microsoft YaHei", SimSun, sans-serif;
		margin-bottom: 80px
	}

	.links ul {
		margin: 0;
		list-style: none;
		padding: 0;
		width: 100%;
		display: inline-block;
	}

	.links ul li {
		width: 30%;
		float: left;
		border: 1px solid #ECECEC;
		border-radius: 3px;
		padding: 10px 30px;
		margin: 10px 10px;
		position: relative;
		overflow: hidden;
		-webkit-transition: all .3s;
		transition: all .3s;
		box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
	}

	span.sitename {
		font-size: 14px;
		margin-top: 10px;
		color: #34403E;
		padding-bottom: 10px;
		display: block;
		-webkit-transition: all .3s;
		transition: all .3s;
	}

	.linkdes {
		color: #949494;
		font-size: 13px;
		padding: 10px 0;
		border-top: 1px dashed #ddd;
		min-height: 46px;
		text-overflow: ellipsis;
		overflow: hidden;
		white-space: nowrap;
		line-height: 25px;
		-webkit-transition: all .5s;
		transition: all .5s;
	}

	.link-title {
		font-weight: 400;
		color: #6D6D6D;
		padding-left: 10px;
		border-left: 3px solid #34403E;
		margin: 50px 0 20px;
	}

	.links ul li:before {
		content: "";
		background-color: #34403E;
		;
		-webkit-transform: skew(45deg, 0);
		transform: skew(45deg, 0);
		width: 0;
		height: 100%;
		position: absolute;
		top: 0px;
		left: -60px;
		z-index: -1;
		-webkit-transition: all .5s;
		transition: all .5s;
	}

	.links ul li:hover {
		color: #27323A;
		border: 1px solid #34403E;
	}

	.links ul li:hover:before {
		width: 180%;
	}

	.links ul li:hover .sitename {
		color: #fff;
	}

	.links ul li:hover .linkdes {
		color: #fff;
		border-top: 1px dashed #fff;
	}

	span.linkss-title {
		font-size: 25px;
		/* margin: 0 auto; */
		text-align: center;
		display: block;
		margin: 80px 0 50px;
		font-family: miranafont, "Hiragino Sans GB", STXihei, "Microsoft YaHei", SimSun, sans-serif;
		letter-spacing: 5px;
	}
	.info-card {
	    min-height: 100px;
	    padding: 30px;
	    border-radius: 3px;
	    margin: 30px 0 50px 0;
	    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
	    p{
	        line-height: 2rem;
			a{
				color: #ff6d6d;
			}
	    }
	}
	.item-align{
		display: inline-flex;
		vertical-align: -webkit-baseline-middle;
	}
	@media (max-width: 600px) {
		.links ul li {
			width: 94%;
		}
		.info {
			display: none;
		}
	}
</style>
