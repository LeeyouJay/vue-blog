<template>
	<div>
		<div class="container">
			<div class="plugins-tips">
				wangEditor:基于javascript和css开发的 Web富文本编辑器， 轻量、简洁、易用、开源免费
				访问地址：<a href="http://www.wangeditor.com/" target="_blank">wangEditor</a>
			</div>
			<div style="margin: 20px 60px;">
				<el-form :model="article" status-icon :rules="rules" ref="ruleForm" label-width="80px"
					class="demo-ruleForm">
					<el-row :gutter="20">
						<el-col :span="12">
							<el-form-item label="标题:" prop="title">
								<el-input type="text" v-model="article.title" placeholder="请输入文章标题" autocomplete="off"
									maxlength="30" show-word-limit>
								</el-input>
							</el-form-item>
							<el-form-item label="标图:" prop="banner">
								<el-input :clearable="true" type="text" v-model="article.banner" autocomplete="off"
									style="width: 80%;">
								</el-input>
								<el-upload ref="uploadImage" class="upload-demo" action="#"
									:http-request="requestUpload" accept=".jpg, .png, .bmp, .jpeg, .webp"
									:multiple="false" :show-file-list="false" :auto-upload="false"
									:on-change="handleChange">
									<el-button type="primary" :loading="btnLoading">本地上传</el-button>
								</el-upload>
							</el-form-item>
						</el-col>
						<el-col :span="12">
							<el-form-item label="分类:" prop="tag">
								<el-select v-model="article.tag" placeholder="选择分类标签" clearable>
									<el-option v-for="(item,index) in tags" :key="index" :label="item" :value="item">
									</el-option>
								</el-select>
							</el-form-item>
							<el-form-item label="显示:">
								<el-radio-group v-model="article.showType">
									<el-radio label="公开"></el-radio>
									<el-radio label="个人"></el-radio>
								</el-radio-group>
							</el-form-item>
						</el-col>
						<el-col :span="24">
							<el-form-item prop="summary" label-width="20px">
								<div style="width: 100px; display: inline-flex;">
									<img class="banner" :src="article.banner" @error="imgerrorfun()" />
								</div>
								<el-input type="textarea" v-model="article.summary" :autosize="autosize"
									placeholder="请输入内容" autocomplete="off" maxlength="120" show-word-limit
									style="width: calc(100% - 100px)"></el-input>
							</el-form-item>
						</el-col>
					</el-row>
				</el-form>
				<el-button class="editor-btn" :type="articleEdit?'warning':'primary'" :disabled="isDisabled"
					@click="submit">{{articleEdit?'修改':publishBtn}}</el-button>
			</div>
			<div ref="editorEle" class="editor-container" v-loading="loading" />
		</div>
	</div>
</template>

<script>
	import E from 'wangeditor'
	import hljs from 'highlight.js'
	import base from '../../api/base.js'
	export default {
		name: 'markdown',
		data: function() {
			return {
				loading: false,
				isDisabled: false,
				publishBtn: '发布',
				btnLoading: false,
				articleEdit: false,
				isSave: true,
				basicUrl: '',
				autosize: {
					minRows: 3
				},
				placeholder: '请输入内容.....',
				rules: {
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
				article: {
					title: '',
					banner: 'https://img.printf520.com/dist/static/images/avatar/006-avatar.jpg',
					summary: '',
					content: '',
					showType: '公开',
					markdown: false
				}
			}
		},
		computed: {
			tags() {
				return this.$store.state.articleTags
			}
		},
		created() {
			this.basicUrl = base.localUrl
			var v = this.$store.state.articleEdit
			if (v) {
				this.isSave = false
				this.article = this.$store.state.article
				this.articleEdit = v
			} else {
				var article = JSON.parse(localStorage.getItem('ht_article'))
				if (article)
					this.article = article
			}
		},
		mounted() {
			var that = this
			this.editor = new E(this.$refs.editorEle)
			this.editor.config.onchange = (html) => {
				this.article.content = html
			}
			this.editor.config.placeholder = this.placeholder
			this.editor.highlight = hljs
			this.editor.config.height = 550
			this.editor.config.zIndex = 500
			this.editor.config.uploadImgMaxSize = 3 * 1024 * 1024 // 3M
			this.editor.config.uploadImgMaxLength = 3 // 一次最多上传 3 个图片
			this.editor.config.customUploadImg = function(resultFiles, insertImgFn) {
				// resultFiles 是 input 中选中的文件列表
				that.loading = true
				for (var img in resultFiles) {
					var formdata = new FormData();
					formdata.append('files', resultFiles[img]);
					that.imgAdd(formdata, insertImgFn);
				}
			}
			// 创建富文本实例
			this.editor.create()
			this.editor.txt.html(this.article.content)
		},
		methods: {
			submit() {
				this.$refs.ruleForm.validate(valid => {
					if (valid) {
						if (this.articleEdit)
							this.editArticle()
						else
							this.addArticle()
					} else {
						return false;
					}
				});
			},
			imgAdd(formdata, insertImgFn) {
				this.$api.blog.imgAddAPI(formdata).then(res => {
					if (res.code === 200) {
						insertImgFn(this.basicUrl + res.data.url[0])
					} else if (res.code === 401) {
						this.$removeToken()
						setTimeout(() => {
							this.loading = false
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
						this.loading = false
					}, 10)
				})
			},
			addArticle() {
				this.$api.blog.addArticleAPI(this.article).then(res => {
					if (res.code === 200) {
						this.$message.success(res.message)
						this.isSave = false
						this.isDisabled = true
						this.publishBtn = '已发布'
						localStorage.removeItem('ht_article')
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
						this.$message.error(res.message);
				})
			},
			editArticle() {
				this.$api.blog.editArticleAPI(this.article).then(res => {
					if (res.code === 200) {
						this.$message.success(res.message)
						setTimeout(() => {
							this.$router.go(-1);
						}, 2000)
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
						this.$message.error(res.message);
				})
			},
			handleChange(image) {
				this.btnLoading = true
				const rawImage = image.raw
				if (!rawImage) return false
				if (rawImage.type.indexOf("image/") == -1) {
					this.$message.warning('图片只支持.jpg, .png, .bmp, .jpeg, .webp格式!')
					return false
				}
				if (rawImage.size / 1024 / 1024 > 3) {
					this.$message.warning('上传的图片大小不能超过3M!')
					return false
				}
				this.requestUpload(rawImage)
			},
			requestUpload(rawImage) {
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
			imgerrorfun() {
				let img = event.srcElement;
				img.src = this.basicUrl + '/loadImg/noPicture.jpg';
				img.onerror = null;
			}
		},
		beforeDestroy() {
			var article = this.article
			this.$store.dispatch('setArticleEdit', false);
			if (this.isSave) {
				localStorage.setItem('ht_article', JSON.stringify(article))
			}
			this.editor.destroy()
		},
	}
</script>
<style scoped>
	.editor-btn {
		margin-bottom: 20px;
		margin-right: 20px;
	}

	.editor-container {
		min-height: 600px;
	}

	.banner {
		width: 64px;
		height: 64px;
		border-radius: 50%;
	}

	.upload-demo {
		display: inline-flex;
		margin-left: 2%;
		width: 18%;
	}
</style>
