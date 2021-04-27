<template>
	<div class="articles">
		<banner :src=" article.banner"></banner>
		<div class="site-content animate">
			<!-- 文章目录 -->
			<div id="article-menus">
				<div v-if="menus.length">目录：</div>
				<menu-tree :menus="menus" child-label="child"></menu-tree>
			</div>
			<main class="site-main" id="main">
				<article class="hentry">
					<!-- 文章头部 -->
					<header class="entry-header">
						<!-- 标题输出 -->
						<h1 class="entry-title">{{article.title}}</h1>
						<hr>
						<div class="breadcrumbs">
							<div id="crumbs">最后更新时间：{{article.updateTime?article.updateTime:article.createTime}}</div>
						</div>
						<div class="breadcrumbs">
							<div>作者：{{article.author}}</div>
						</div>
					</header>
					<!-- 正文输出 -->
					<div class="entry-content" v-if="!article.markdown" @click="showImg($event)"
						v-html="article.content">
					</div>
					<mavon-editor v-if="article.markdown" ref="md" :value="article.content" :subfield="false"
						:defaultOpen="'preview'" :toolbarsFlag="false" :editable="false" :scrollStyle="true"
						:ishljs="true" :boxShadow="false" previewBackground="#ffffff" :imageClick="imageClick" />
					<!-- 文章底部 -->
					<section-title>
						<footer class="post-footer">
							<!-- 阅读次数 -->
							<div class="post-like">
								<i class="iconfont icon-yanjing"></i>
								<span class="count">{{article.viewsCount}}</span>
							</div>

							<!-- 赞助按钮 -->
							<div class="donate" @click="showDonate=!showDonate">
								<span>赏</span>
								<ul class="donate_inner" :class="{'show':showDonate}">
									<li class="wedonate"><img src="@/assets/wechat.jpg">
										<p>微信</p>
									</li>
									<li class="alidonate"><img src="@/assets/alipay.jpg">
										<p>支付宝</p>
									</li>
								</ul>
							</div>
							<!-- 文章标签 -->
							<div class="post-tags">
								<i class="iconfont icon-biaoqian"></i>
								<a style="cursor: pointer;" @click="toCategory(article.tag)">{{article.tag}}</a>
							</div>
						</footer>
					</section-title>
					<!--声明-->
					<div class="open-message">
						<p>声明：Arslinth博客|版权所有，违者必究|如未注明，均为原创|本网站采用<a
								href="https://creativecommons.org/licenses/by-nc-sa/3.0/"
								target="_blank">BY-NC-SA</a>协议进行授权</p>
						<p>转载：转载请注明原文链接 - <a :href="'/article/'+article.id">{{article.title}}</a></p>
					</div>
					<!--评论-->
					<div style="padding-top: 30px;margin-bottom: 30px;">
						<section-title>
							<div style="display: flex;align-items: flex-end;">
								Comments | {{pageTotal}} 条评论
							</div>
						</section-title>
					</div>
					<comment-editor ref="mainEditor" :show="showComment" @onSubmit="submitComment"></comment-editor>
					<div class="comments">
						<comment v-for="item in comments" :key="item.id" :comment="item" :showReport="item.oppen"
							@oppenComment="oppenComment" @reply="toReply">
							<template v-if="item.reply.length">
								<comment v-for="reply in item.reply" :key="reply.id" :comment="reply"
									:showReport="reply.oppen" @oppenComment="oppenComment" @reply="toReply"></comment>
							</template>
						</comment>
					</div>
					<div class="pagination">
						<el-pagination background layout="prev, pager, next" :current-page="query.pageIndex"
							:hide-on-single-page="pageTotal<7" :page-size="query.pageSize" :total="pageTotal"
							@current-change="handlePageChange"></el-pagination>
					</div>
				</article>
			</main>
		</div>
		<div class="imgDolg" v-show="imgPreview.show" @click.stop="imgPreview.show = false">

			<img @click.stop="imgPreview.show = false" :src="imgPreview.img" />
		</div>
	</div>
</template>

<script>
	import Banner from '@/components/banner'
	import sectionTitle from '@/components/section-title'
	import comment from '@/components/comment'
	import menuTree from '@/components/menu-tree'
	import commentEditor from '@/components/comment-editor.vue'
	import {
		Message
	} from 'element-ui';
	import {
		removeToken
	} from '@/utils/cookie.js'
	import base from '@/api/base.js'
	import 'mavon-editor/dist/css/index.css'
	import {
		mavonEditor
	} from 'mavon-editor'
	import {
		articleAPI,
		addCommentAPI,
		getCommentsAPI
	} from '../api'

	export default {
		name: 'articles',
		data() {
			return {
				query: {
					state: '',
					searchName: '',
					pageIndex: 1,
					pageSize: 7
				},
				pageTotal: 100,
				imgPreview: {
					img: "",
					show: false
				},
				showComment: true,
				showDonate: false,
				comments: [],
				menus: [],
				article: {},
				tempCom: {}
			}
		},
		components: {
			Banner,
			sectionTitle,
			comment,
			menuTree,
			mavonEditor,
			commentEditor
		},
		created() {

		},
		mounted() {
			this.getArticle()
		},
		methods: {
			toCategory(value) {
				this.$store.dispatch('setTag', value)
				this.$store.dispatch('setContent', '')
				this.$router.push("/")
			},
			oppenComment(v) {
				this.showComment = v.oppen ? true : false
				v.oppen = !v.oppen
				this.comments.map(item => {
					if (item.id != v.id) {
						item.oppen = false
					}
					if (item.reply.length != 0) {
						item.reply.map(value => {
							if (value.id != v.id) {
								value.oppen = false
							}
						})
					}
					return item
				})
				this.tempCom = {
					articleId: v.articleId,
					parentId: v.id,
					toUserId: v.fromUserId,
					toUserName: v.fromUserName,
					toUserEmail: v.fromUserEmail,
					toUserAvatar: v.fromUserAvatar
				}
				if (v.parentId)
					this.tempCom.parentId = v.parentId
			},
			toReply(v) {
				!v.fromUserAvatar && (v.fromUserAvatar =
					'https://gravatar.loli.net/avatar/baee522214bec0d6d3664fa8a6640a1c?s=80&r=X&d=mm')
				var com = Object.assign(this.tempCom, v);
				this.addComment(com)
			},
			// 图片点击放大
			showImg(e) {
				if (e.target.tagName == 'IMG') {
					this.previewImg(e.target.src)
				}
			},
			submitComment(v) {
				const path = this.$route.path
				v.articleId = path.substring(path.lastIndexOf('/') + 1)
				v.hasNew = true 
				!v.fromUserAvatar && (v.fromUserAvatar = 'https://gravatar.loli.net/avatar/baee522214bec0d6d3664fa8a6640a1c?s=80&r=X&d=mm')
				this.addComment(v)
			},
			addComment(com) {
				addCommentAPI(com).then(res => {
					if (res.code === 200) {
						this.$message.success(res.message)
						if (this.showComment)
							this.$refs.mainEditor.clearContent()
						setTimeout(() => {
							this.getComment(this.query)
							this.showComment = true
						}, 1000)
					} else if (res.code === 401) {
						removeToken()
						this.$store.dispatch('setNeedInfo', true)
						this.$message.error(res.message)
					} else
						this.$message.error(res.message)
				})
			},
			handlePageChange(val) {
				this.$set(this.query, 'pageIndex', val);
				this.getComment(this.query)
			},
			getComment(page) {
				const path = this.$route.path
				var articleId = path.substring(path.lastIndexOf('/') + 1)
				getCommentsAPI(articleId, page).then(res => {
					if (res.code === 200) {
						const comments = res.data.comments
						this.pageTotal = res.data.pageTotal
						comments.map(item => {
							item.oppen = false
							if (item.fromUserId) {
								item.fromUserAvatar = base.localUrl + item.fromUserAvatar
							}
							if (item.reply.length != 0) {
								item.reply.map(val => {
									val.oppen = false
									if (val.fromUserId)
										val.fromUserAvatar = base.localUrl + val.fromUserAvatar
									return val
								})
							}
							return item
						})
						this.comments = comments
					}
				})
			},
			getArticle() {
				articleAPI(this.$route.path).then(res => {
					if (!res.data.article) {
						this.$router.push('/404')
						return
					}
					if (res.code === 401) {
						removeToken()
						this.$store.dispatch('setNeedInfo', true)
					} else
						this.$store.dispatch('setNeedInfo', false)
					this.article = res.data.article
					this.article.banner = this.article.banner.includes('-thumbnail') ? this.article.banner.substr(
						0, this.article.banner.lastIndexOf('-')) + '.jpg' : this.article.banner
					setTimeout(() => {
						this.getComment(this.query)
						this.createMenus()
					}, 500)
				})
			},
			imageClick(ele) {
				this.previewImg(ele.src)
				//Markdown内置图片预览
				//this.$refs.md.d_preview_imgsrc = src
			},
			//点击预览图片
			previewImg(src) {
				if (src.includes('-thumbnail'))
					src = src.substr(0, src.lastIndexOf('-')) + '.jpg'
				this.imgPreview.img = src
				this.imgPreview.show = true
			},
			fetchH(arr, left, right) {
				if (right) {
					return arr.filter(item => item.offsetTop > left && item.offsetTop < right)
				} else {
					return arr.filter(item => item.offsetTop > left)
				}
			},
			// 生成目录
			createMenus() {
				var that = this
				let arr = []
				for (let i = 6; i > 0; i--) {
					let temp = []
					let e = []
					if (this.article.markdown)
						e = document.querySelector(".v-show-content").querySelectorAll(`h${i}`)
					else
						e = document.querySelector(".entry-content").querySelectorAll(`h${i}`)

					for (let j = 0; j < e.length; j++) {
						var itemId = that.generateRdStr()
						e[j].setAttribute('id', itemId);
						let child = this.fetchH(arr, e[j].offsetTop, (j + 1 === e.length) ? undefined : e[j + 1].offsetTop)
						temp.push({
							h: i,
							title: e[j].innerText,
							id: itemId,
							offsetTop: e[j].offsetTop,
							child
						})
					}
					if (temp.length) {
						arr = temp
					}
				}
				this.menus = arr
				setTimeout(() => {
					var h = document.getElementById("article-menus").offsetHeight
					let m = document.getElementById("main");
					m.setAttribute('style', 'transform: translateY(-' + h + 'px);')
				}, 50)



			},
			//生成随机字符串
			generateRdStr() {
				var text = "";
				var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
				for (var i = 0; i < 6; i++)
					text += possible.charAt(Math.floor(Math.random() * possible.length));
				return text;
			}
		},
	}
</script>
<style scoped lang="less">
	.site-content {
		position: relative;

		.site-main {
			padding: 80px 0 0 0;
		}
	}

	.pagination {
		text-align: center;
	}

	#article-menus {
		position: sticky;
		top: 0;
		box-shadow: 0 2px 6px rgba(0, 0, 0, .1);
		border-radius: 3px;
		padding: 15px;
		width: 300px;
		transform: translateX(-120%) translateY(150px);
		font-size: 14px;
	}

	article.hentry {
		.entry-header {
			.entry-title {
				font-size: 23px;
				font-weight: 600;
				color: #737373;
				margin: 0.67em 0;

				&:before {
					content: "#";
					margin-right: 6px;
					color: #d82e16;
					font-size: 20px;
					font-weight: 600;
				}
			}

			hr {
				height: 1px;
				border: 0;
				background: #EFEFEF;
				margin: 15px 0;
			}

			.breadcrumbs {
				font-size: 14px;
				color: #a9a9a9;
				text-decoration: none;
				margin-bottom: 15px;
			}
		}

		.entry-content {
			font-family: 'Merriweather Sans', Helvetica, Tahoma, Arial, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft Yahei', 'WenQuanYi Micro Hei', sans-serif;

			// font-family: Microsoft Yahei;
		}

		footer.post-footer {
			width: 100%;
			padding: 20px 10px;
			margin-top: 30px;
			height: 65px;
			position: relative;

			i {
				font-size: 18px;
				margin-right: 5px;
			}

			.post-like {
				float: right;
				margin: 7px 0 0 20px;
			}

			.post-share {
				float: right;
				list-style: none;
				margin-right: 20px;
			}

			.donate {
				float: left;
				line-height: 36px;
				border-radius: 100%;
				-webkit-border-radius: 100%;
				-moz-border-radius: 100%;
				border: 1px solid #2B2B2B;

				&:hover {
					border: 1px solid goldenrod;

					span {
						color: goldenrod;
					}
				}

				span {
					color: #2B2B2B;
					padding: 10px;
					position: relative;
					cursor: pointer;
				}

				.donate_inner {
					display: none;
					margin: 0;
					list-style: none;
					position: absolute;
					left: 80px;
					top: -40px;
					background: #FFF;
					padding: 10px;
					border: 1px solid #ddd;
					box-shadow: 0 2px 6px rgba(0, 0, 0, .08);
					border-radius: 3px;

					&.show {
						display: block;
					}

					li {
						float: left;
					}

					img {
						width: 100px;
					}

					p {
						text-align: center;
						font-size: 15px;
						color: #D2D2D2;
						line-height: 1rem;
					}
				}

				.donate_inner:after,
				.donate_inner:before {
					content: "";
					position: absolute;
					left: 0;
					bottom: 45%;
					margin-left: -8px;
					border-top: 8px solid transparent;
					border-bottom: 8px solid transparent;
					border-right: 8px solid #fff;
				}

				.donate_inner:before {
					left: -1px;
					border-right: 8px solid #ddd;
				}

			}

			.post-tags {
				margin: 7px 0 0 20px;
				float: left;
				text-transform: uppercase;

				a:hover {
					color: #ff6d6d;
				}
			}
		}

		.open-message {
			margin: 20px 0;
			position: relative;
			background: #2B2B2B;
			padding: 10px 30px;
			border-radius: 3px;
			font-size: 14px;
			color: #fff;

			&:after {
				content: "";
				border-left: 10px solid transparent;
				border-right: 10px solid transparent;
				border-bottom: 10px solid #2B2B2B;
				position: absolute;
				top: -8px;
				left: 48%;
			}

			p {
				margin: 10px 0;
				overflow: hidden;
				text-overflow: ellipsis;
				white-space: nowrap;
			}

			a {
				color: #A0DAD0;
				padding: 0 5px;
			}
		}
	}

	//富文本图片放大
	.imgDolg {
		width: 100vw;
		height: 100vh;
		position: fixed;
		z-index: 900;
		background: rgba(0, 0, 0, 0.7);
		top: 0;
		left: 0;
		display: flex;
		align-items: center;
		justify-content: center;
		transition: all 0.1s linear 0s;

		img {
			width: 100%;
		}
	}

	@media (max-width: 600px) {
		.open-message {
			display: none;
		}
	}
</style>
