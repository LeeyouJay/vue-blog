<template>
	<div class="about">
		<banner src="https://images.pexels.com/photos/370717/pexels-photo-370717.jpeg"></banner>
		<div class="site-content">
			<div class="content-warp">
				<div class="entry-content">
					<div class="about-title">关于博客</div>
					<div class="about-content">
						<p>没有想过懒散的自己会想去写博客，但毕竟准程序员一枚怎么可以会没有自己的博客？</p>
						<p>闲暇之余看到<a href="https://zhebk.cn/Web/Akina.html" target="_blank">Akina For Typecho</a>的主题，于是决定用VUE模仿一下写写自己的博客系统，在其中引入了 <a target="_blank" href="https://element.eleme.cn/#/zh-CN/component/installation">ElementUI</a>的元素</p>
						<p>后端基于SpringBoot框架搭建，目前处于瞎折腾，代码是复制粘贴一通乱搞的状态，不嫌弃的话star一个吧：<a target="_blank" href="https://github.com/LeeyouJay/vue-blog">github</a></p>
					</div>
					<hr style="border: 1px dashed #ECECEC;"/>
					<div class="about-title">关于我</div>
					<div class="about-content">
						<p>-本人男，死肥宅一枚</p>
						<p>-极简主义，对花里胡哨的东西无感而且还有一定的强迫症</p>
						<p>-交流的角色中只喜欢当其中的聆听者</p>
						<p>-话少情商低得可怜，不过却很羡慕会说话的人</p>
						<p>-走出去和陌生人说话是一件很恐怖的事情，不过自己也在不断尝试改变</p>
						<p>-悲观主义者，对事件的看法先会顾及后果而不是收益</p>
						<p>-渴望优秀，但时常伴随孤独</p>
						<p>-对芹菜有绝的厌恶</p>
						<p>-喜欢天文物理电子科技类，对一切未知的东西充满好奇</p>
						<p>-写代码也是享受孤独的一种方式</p>
						<p>-闲暇时间会打DOTA，也在努力培养玩游戏的爱好，不过对快餐式的游戏嗤之以鼻</p>
						<p>-独处时喜欢幻想的空想主义者</p>
						<p>-对音乐欣赏的点和周围大多数的人不同(或许是朋友太少？)</p>
						<p>-喜欢黑色，因为它代表很酷的感觉</p>
					</div>
					<div style="margin: 30px 0;">
						<aplayer :music="song" />
					</div>
					<hr style="border: 1px dashed #ECECEC;"/>
				</div>
				
				<div class="about-me about-info">
					<div class="about-title">给我留言吧 </div>
					<comment-editor ref="mainEditor" :show="showComment" @onSubmit="submitComment"></comment-editor>
				</div>
				<div style="padding-top: 30px;margin-bottom: 30px;">
					<section-title>
						<div style="display: flex;align-items: flex-end;">
							Comments | {{pageTotal}} 条留言
						</div>
					</section-title>
				</div>
				<div class="comments">
					<comment v-for="item in comments" :key="item.id" :comment="item" :showReport="item.oppen"
						:showReply="canReply" @oppenComment="oppenComment" @reply="toReply">
						<template v-if="item.reply.length">
							<comment v-for="reply in item.reply" :key="reply.id" :comment="reply"
								:showReport="reply.oppen" :showReply="canReply" @oppenComment="oppenComment"
								@reply="toReply"></comment>
						</template>
					</comment>
				</div>
				<div class="pagination">
					<el-pagination background layout="prev, pager, next" :current-page="query.pageIndex"
						:hide-on-single-page="pageTotal<7" :page-size="query.pageSize" :total="pageTotal"
						@current-change="handlePageChange"></el-pagination>
				</div>
			</div>
		</div>
	</div>
</template>
<script>
	import sectionTitle from '@/components/section-title'
	import commentEditor from '@/components/comment-editor.vue'
	import Banner from '@/components/banner'
	import comment from '@/components/comment'
	import base from '@/api/base.js'
	import Aplayer from 'vue-aplayer'
	import {
		getCommentsAPI,
		addCommentAPI
	} from '../api'
	export default {
		name: "About",
		data() {
			return {
				list: [],
				showComment: true,
				pageTotal: 0,
				comments: [],
				song: {
					title: 'Das zweite Kapitel',
					artist: 'やまだ豊',
					src: require('@/assets/mp3/Das zweite Kapitel.mp3'),
					//pic: 'http://p1.music.126.net/lxvZ17eYxHi13fRzOF2zTw==/109951163023641953.jpg?param=300y300'
					pic: 'http://p2.music.126.net/Gg560eWcSoqhbmKpYnRfZQ==/109951164659464931.jpg?param=300y300'
				},
				query: {
					state: '',
					searchName: '',
					pageIndex: 1,
					pageSize: 7
				},
			}
		},
		computed: {
			canReply() {
				return !this.$store.getters.needInfo
			}
		},
		components: {
			commentEditor,
			Banner,
			sectionTitle,
			comment,
			Aplayer
		},
		mounted() {
			this.getComment(this.query)
		},
		methods: {
			submitComment(v) {
				const path = this.$route.path
				v.articleId = path.substring(path.lastIndexOf('/') + 1);
				!v.fromUserAvatar && (v.fromUserAvatar =
					'https://gravatar.loli.net/avatar/baee522214bec0d6d3664fa8a6640a1c?s=80&r=X&d=mm')
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
		}
	}
</script>
<style scoped lang="less">
	.about {
		padding-top: 0px;
	}

	.about-title {
		font-size: 20px;
		margin: 30px 0;
		&:before {
			left: -28px;
			content: '「 ';
			color: #ff6d6d;
		}
		&:after {
			content: ' 」';
			color: #ff6d6d;
		}
	}

	.about-content {
		padding-left: 20px;
		a{
			color: #ff6d6d;
		}
	}

	.content-warp {
		margin-top: 80px;
		.about-info {
			margin: 30px 0;

			span {
				color: red;
				margin-right: 10px;
			}

			.info-card {
				min-height: 100px;
				padding: 20px;
				border-radius: 3px;
				margin: 30px 0 50px 0;
				box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

				p {
					line-height: 1.7rem;
				}
			}
		}

		.contactForm {
			width: 100%;
			padding: 20px;

			.form-item {
				align-items: center;
				display: flex;

				&:not(:last-child) {
					margin-bottom: 20px;
				}

				label {
					width: 80px;
				}

				.v {
					min-height: 40px;
					line-height: 20px;
					border-radius: 3px;
					padding: 2px 10px;
					outline: none;
					border: 1px solid #8fd0cc;
					width: 100%;
					resize: vertical;
				}

				button {
					width: 100px;
					height: 40px;
					border-radius: 3px;
					outline: 0;
					border-style: none;
					cursor: pointer;
					background-color: #409eff;
					color: white;

					&:hover {
						opacity: 0.8;
					}
				}
			}
		}
	}

	/*******/
	@media (max-width: 800px) {
		.content-warp {
			margin-top: 0;
		}
	}
</style>
