<template>
	<div>
		<div class="container">
			<div class="plugins-tips">
				mavonEditor：基于Vue的markdown编辑器。
				访问地址：<a href="https://github.com/hinesboy/mavonEditor" target="_blank">mavonEditor</a>
			</div>
			<div style="margin: 20px 60px;">
				<el-form :model="article" status-icon :rules="rules" ref="ruleForm" label-width="80px" 
					class="demo-ruleForm">
					<el-row :gutter="20">
						<el-col :span="12">
							<el-form-item label="标题:" prop="title">
								<el-input type="text" v-model="article.title" placeholder="请输入文章标题" autocomplete="off">
								</el-input>
							</el-form-item>
							<el-form-item label="标图:" prop="banner">
								<el-input :clearable="true" type="text" v-model="article.banner" autocomplete="off" style="width: 80%;">
								</el-input>
								<el-upload ref="uploadImage" class="upload-demo" action="#"  :http-request="requestUpload" accept=".jpg, .png, .bmp, .jpeg, .webp"
								 :multiple="false" :show-file-list="false" :auto-upload="false" :disabled="btnLoading" :on-change="handleChange">
									<el-button type="primary" :loading="btnLoading" >本地上传</el-button>
								</el-upload>
							</el-form-item>
						</el-col>
						<el-col :span="12">
							<el-form-item label="分类:" prop="tag">
								<el-select v-model="article.tag" placeholder="选择分类标签" clearable >
								      <el-option label="JAVA" value="JAVA"></el-option>
								      <el-option label="SpringBoot" value="SpringBoot"></el-option>
									  <el-option label="Vue" value="Vue"></el-option>
									  <el-option label="随笔" value="随笔"></el-option>
								</el-select>
							</el-form-item>
							<el-form-item label="状态:">
								<el-radio-group v-model="article.showType">
									<el-radio label="公开"></el-radio>
									<el-radio label="个人"></el-radio>
								</el-radio-group>
							</el-form-item>
						</el-col>
						<el-col :span="24">
							<el-form-item prop="summary" label-width="20px"	>
								<div style="width: 100px; display: inline-flex;">
									<img class="banner" :src="article.banner" @error="imgerrorfun()" />
								</div>
								<el-input type="textarea" v-model="article.summary" :autosize="autosize"
									placeholder="请输入内容" autocomplete="off" maxlength="100" show-word-limit
									style="width: calc(100% - 100px)"></el-input>
							</el-form-item>
						</el-col>
					</el-row>
				</el-form>
				<el-button class="editor-btn" :type="articleEdit?'warning':'primary'" :disabled="isDisabled" @click="submit">{{articleEdit?'修改':publishBtn}}</el-button>
			</div>
			<div v-loading="loading">
				<mavon-editor v-model="article.content" ref="md"   :imageClick="imageClick"   @imgAdd="imgAdd"  @save="save" @change="change" style="min-height: 600px" />
			</div>
		</div>
	</div>
</template>

<script>
	import {
		mavonEditor
	} from 'mavon-editor'
	import 'mavon-editor/dist/css/index.css'
	import base from '../../api/base.js'
	export default {
		name: 'markdown',
		data: function() {
			return {
				basicUrl:'',
				loading: false,
				isDisabled:false,
				publishBtn:'发布',
				btnLoading:false,
				articleEdit:false,
				isSave:true,
				autosize: {
					minRows: 3
				},
				rules:{
					title: [{
						required: true,
						trigger: 'blur',
						message: '文章标题不能为空'
					}],
					summary: [{
						required: true,
						trigger: 'blur',
						message: '简介不能为空'
					}],
					banner: [{
						required: true,
						trigger: 'blur',
						message: '图片链接不能为空'
					}],
					tag: [{
						required: true,
						trigger: 'blur',
						message: '请选择文章标签'
					}]
					},
				article:{
					title: '',
					banner: 'https://img.printf520.com/dist/static/images/avatar/006-avatar.jpg',
					summary: '',
					content: '',
					showType:'公开',
					markdown: true
				}
			}
		},
		components: {
			mavonEditor
		},
		created() {
			this.basicUrl = base.localUrl
			var v = this.$store.state.articleEdit
			if(v){
				this.isSave = false
				this.article = this.$store.state.article
				this.articleEdit = v
			}else{
				var article = JSON.parse(localStorage.getItem('md_article'))
				if(article)
					this.article = article
			}
		},
		mounted() {
			
		},
		methods: {
			// 将图片上传到服务器，返回地址替换到md中
			imgAdd(filename, file) {
				if (file.type.indexOf("image/") == -1) {
					this.$message.warning('图片只支持.jpg, .png, .bmp, .jpeg, .webp格式!')
					this.$nextTick(()=>this.$refs.md.$refs.toolbar_left.$imgDelByFilename(file._name))
					return false
				}
				if (file.size / 1024 / 1024 > 3) {
					this.$message.warning('上传的图片大小不能超过3M!')
					this.$nextTick(()=>this.$refs.md.$refs.toolbar_left.$imgDelByFilename(file._name))
					return false
				}
				var formdata = new FormData();
				formdata.append('files', file);
				this.loading = true
				this.$api.blog.imgAddAPI(formdata).then(res=>{
					if(res.code === 200){
						this.$refs.md.$img2Url(filename, this.basicUrl+res.data.url[0]);
					}else if(res.code === 401){
						setTimeout(()=>{
							this.loading = false
						},10)
						this.$nextTick(()=>this.$refs.md.$refs.toolbar_left.$imgDelByFilename(file._name))
						this.$removeToken()
						this.$alert(res.message, "提示", {
							type: 'warning',
							 center: true,
							 callback: action => {
								 this.$router.replace('/login')
							 }
						})
					}else
						this.$message.error(res.message); 
					setTimeout(()=>{
						this.loading = false
					},10)
				})
			},
			imageClick(ele){
				var  src = ele.src
				if(src.includes('-thumbnail')) 
					src = src.substr(0,src.lastIndexOf('-'))+'.jpg'
				this.$refs.md.d_preview_imgsrc = src
			},
			submit() {
				console.log(this.article)
				this.$refs.ruleForm.validate(valid => {
					if (valid) {
						if(this.articleEdit)
							this.editArticle()
						else
							this.addArticle()
					} else {
						return false;
					}
				});
			},
			handleChange(image) {
				this.btnLoading = true
				const rawImage = image.raw
				if (!rawImage) return false
				if (rawImage.type.indexOf("image/") == -1) {
					this.$message.warning('图片只支持.jpg, .png, .bmp, .jpeg, .webp格式!')
					this.btnLoading = false
					return false
				}
				if (rawImage.size / 1024 / 1024 > 3) {
					this.$message.warning('上传的图片大小不能超过3M!')
					this.btnLoading = false
					return false
				}
				this.requestUpload(rawImage)
			},
			requestUpload(rawImage){
				var formdata = new FormData();
				formdata.append('files', rawImage);
				this.$api.blog.imgAddAPI(formdata).then(res => {
					if (res.code === 200) {
						this.btnLoading = false
						this.article.banner = this.basicUrl + res.data.url[0]
					} else if (res.code === 401) {
						this.$removeToken()
						setTimeout(() => {
							this.btnLoading = false
						}, 10)
						this.$alert(res.message, "提示", {
							type: 'warning',
							center: true,
							callback: action => {
								this.$router.replace('/login')
							}
						})
					} else
						this.$message.error(res.message);
					setTimeout(() => {
						this.btnLoading = false
					}, 10)
				})
			},
			addArticle(){
				this.$api.blog.addArticleAPI(this.article).then(res=>{
					if(res.code === 200){
						this.$message.success(res.message)
						this.isDisabled = true
						this.publishBtn = '已发布'
						localStorage.removeItem('md_article')
						this.isSave = false
					}else if(res.code === 401){
						this.$removeToken()
						this.$alert(res.message, "提示", {
							type: 'warning',
							 center: true,
							 callback: action => {
								 this.$router.replace('/login')
							 }
						})
					}else
						this.$message.error(res.message);
				})
			},
			editArticle(){
				this.$api.blog.editArticleAPI(this.article).then(res=>{
					if(res.code === 200){
						this.$message.success(res.message)
						setTimeout(()=>{
							 this.$router.go(-1);
						},2000)
					}else if(res.code === 401){
						this.$removeToken()
						this.$alert(res.message, "提示", {
							type: 'warning',
							 center: true,
							 callback: action => {
								 this.$router.replace('/login')
							 }
						})
					}else
						this.$message.error(res.message);
				})
			},
			
			save(value, html) {
				
			},
			change(value, html) {
				this.article.content = value
				
			},
			imgerrorfun(){
				let img = event.srcElement;
					 img.src = this.basicUrl +'/loadImg/noPicture.jpg';
					 img.onerror = null; 
			}
		},
		beforeDestroy() {
			var article = this.article
			this.$store.dispatch('setArticleEdit', false);
			if(this.isSave){
				localStorage.setItem('md_article', JSON.stringify(article))
			}
		},
	}
</script>
<style scoped>
	.editor-btn {
		margin-bottom: 20px;
		margin-right: 20px;
	}
	.banner {
		width: 64px;
		height: 64px;
		border-radius: 50%;
	}
	.upload-demo{
		display: inline-flex;
		margin-left: 2%; 
		width: 18%;
	}
</style>
