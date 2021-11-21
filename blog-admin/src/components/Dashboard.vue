<template>
	<div>
		<el-row :gutter="20">
			<el-col :span="8">
				<el-card shadow="always" class="mgb20" style="height:252px;">
					<div class="user-info">
						<router-link to="/userInfo">
							<img :src="userInfo.avatar" class="user-avator" alt />
						</router-link>
						<div class="user-info-cont">
							<div class="user-info-name">{{userInfo.nickName}}</div>
							<!-- <div>{{role}}</div> -->
						</div>
					</div>
					<div class="user-info-list">
						上次登录时间：
						<span>{{sysLog.createTime}}</span>
					</div>
					<div class="user-info-list">
						上次登录地点：
						<span>{{sysLog.ipSource}}</span>
					</div>
				</el-card>
				<el-card shadow="always">
					<div slot="header" class="clearfix">
						<span>文章类型</span>
					</div>
					<div class="content scrollbar" style="height:251px;">
						<div v-for="(item, index) in tags" :key="index">
							<label>{{item.tag}}</label>
							<el-progress :percentage="persent(item.count,articleCount)" :color="getColor"></el-progress>
						</div>
					</div>
				</el-card>
			</el-col>
			<el-col :span="16">
				<el-row :gutter="20" class="mgb20">
					<el-col :span="8">
						<el-card shadow="always" :body-style="{padding: '0px'}">
							<div class="grid-content grid-con-1">
								<i class="el-icon-lx-people grid-con-icon"></i>
								<div class="grid-cont-right">
									<div class="grid-num">{{viewCount}}</div>
									<div>文章访问量</div>
								</div>
							</div>
						</el-card>
					</el-col>
					<el-col :span="8">
						<el-card shadow="always" :body-style="{padding: '0px'}">
							<div class="grid-content grid-con-2">
								<i class="el-icon-lx-notice grid-con-icon"></i>
								<div class="grid-cont-right">
									<div class="grid-num">{{articleCount}}</div>
									<div>文章数量</div>
								</div>
							</div>
						</el-card>
					</el-col>
					<el-col :span="8">
						<el-card shadow="always" :body-style="{padding: '0px'}">
							<div class="grid-content grid-con-3">
								<i class="el-icon-lx-goods grid-con-icon"></i>
								<div class="grid-cont-right">
									<div class="grid-num">{{commentsCount}}</div>
									<div>评论消息</div>
								</div>
							</div>
						</el-card>
					</el-col>
				</el-row>
				<el-card shadow="always" style="height:500px;">
					<div slot="header" class="clearfix">
						<span>评论</span>
						<el-badge is-dot style="float: right;">
							<el-button type="text" @click="toMessage">留言</el-button>
						</el-badge>
					</div>
					<el-table :data="commentList" height="357">
						<el-table-column prop="title" label="文章" width="260">
							<template slot-scope="scope">
								<el-badge value="new" :hidden="!scope.row.hasNew" class="item">
									<span>{{ scope.row.title}}</span>
								</el-badge>
							</template>
						</el-table-column>
						<el-table-column prop="fromUserName" label="昵称" width="120">
						</el-table-column>
						<el-table-column prop="fromUserEmail" label="联系邮箱">
						</el-table-column>
						<el-table-column prop="createTime" label="留言时间">
						</el-table-column>
						<el-table-column align="center">
							<template slot-scope="scope">
								<el-button type="text" icon="el-icon-search" @click="handleClick(scope.row)">查看
								</el-button>
							</template>
						</el-table-column>
					</el-table>
					<div class="pagination">
						<el-pagination background layout="total, prev, pager, next" :current-page="query.pageIndex"
							:page-size="query.pageSize" :total="pageTotal" @current-change="handlePageChange">
						</el-pagination>
					</div>
				</el-card>
			</el-col>
		</el-row>

		<!-- 编辑弹出框 -->
		<el-dialog :visible.sync="editVisible" center>
			<template slot="title">
				<span class="dialog-title">{{dialogTitle}}</span>
			</template>
			<div class="content scrollbar" style="padding-right: 25px;">
				<comment v-for="item in comments" :key="item.id" :comment="item" @oppenComment="oppenComment"
					@reply="toReply" @delComment="showDel">
					<template v-if="item.reply.length">
						<comment v-for="reply in item.reply" :key="reply.id" :comment="reply"
							@oppenComment="oppenComment" @reply="toReply" @delComment="showDel"></comment>
					</template>
				</comment>
			</div>
			</el-card>
			<div slot="footer" class="pagination" v-if="showMsgPage">
				<el-pagination background layout="total, prev, pager, next" :current-page="msgQuery.pageIndex"
					:page-size="msgQuery.pageSize" :total="msgTotal" @current-change="msgPageChange"></el-pagination>
			</div>
		</el-dialog>
	</div>
</template>

<script>
	import base from '../api/base.js'
	import comment from './comment.vue'
	export default {
		name: 'dashboard',
		components: {
			comment
		},
		data() {
			return {
				editVisible: false,
				commentList: [],
				userInfo: {},
				sysLog: {},
				msgQuery: {
					pageIndex: 1,
					pageSize: 5
				},
				dialogTitle: "文章评论",
				query: {
					state: '',
					searchName: '',
					pageIndex: 1,
					pageSize: 5
				},
				msgTotal: 0,
				pageTotal: 3,
				comments: [],
				tempCom: {},
				showMsgPage: false,
				color: ['#2d8cf0', '#19be6b', '#ff9900', '#e46cbb', '#9a66e4'],
				viewCount: 0,
				commentsCount: 0,
				articleCount: 0,
				tags: []
			};
		},
		computed: {

		},
		created() {
			this.getUserInfo()
		},
		methods: {
			getUserInfo() {
				this.$api.system.getUserInfoAPI().then(res => {
					if (res.code === 200) {
						this.userInfo = res.data.userInfo;
						if(this.userInfo.avatar.indexOf("-thumbnail.jpg") != -1){
							this.userInfo.avatar = base.localUrl +this.userInfo.avatar
						}
						this.getLatestLogByUser(this.userInfo.username)
						this.getCommentList()
						this.getStatistics(this.userInfo.id)
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
			getLatestLogByUser(username) {
				this.$api.system.getLatestLogAPI(username).then(res => {
					if (res.data.sysLog)
						this.sysLog = res.data.sysLog
				})
			},
			getCommentList() {
				this.$api.blog.getCommentListAPI(this.userInfo.id, this.query).then(res => {
					if (res.data.commentList) {
						this.commentList = res.data.commentList
						this.pageTotal = res.data.pageTotal
						
					}
				})
			},
			getStatistics(userId) {
				this.$api.blog.getStatisticsAPI(userId).then(res => {
					if (res.code === 200) {
						this.viewCount = res.data.viewCount
						this.commentsCount = res.data.commentsCount
						this.articleCount = res.data.articleCount
						this.tags = res.data.tags
					}
				})
			},
			handlePageChange(val) {
				this.$set(this.query, 'pageIndex', val);
				this.getCommentList()
			},
			msgPageChange(val) {
				this.$set(this.msgQuery, 'pageIndex', val);
				this.handleComment('about', this.msgQuery)
			},
			handleClick(row) {
				row.hasNew = false
				this.editVisible = true
				this.showMsgPage = false
				this.dialogTitle = '文章评论'
				this.handleComment(row.articleId, {
					searchName: row.id
				})

			},
			toMessage() {
				this.editVisible = true
				this.showMsgPage = true
				this.dialogTitle = '留言'
				this.handleComment('about', this.msgQuery)

			},

			handleComment(articleId, object) {
				this.$api.blog.getCommentAPI(articleId, object).then(res => {
					if (res.code === 200) {
						const comments = res.data.comments
						comments.map(item => {
							item.oppen = false
							if (item.fromUserId && item.fromUserAvatar.indexOf('-thumbnail') != -1) {
								item.fromUserAvatar = base.localUrl + item.fromUserAvatar
							}
							if (item.reply.length != 0) {
								item.reply.map(val => {
									val.oppen = false
									if (val.fromUserId && val.fromUserAvatar.indexOf('-thumbnail') != -1)
										val.fromUserAvatar = base.localUrl + val.fromUserAvatar
									return val
								})
							}
							return item
						})
						this.comments = comments
						if (comments.length == 0) {
							this.$message.info("暂无消息！")
							setTimeout(()=>{
								this.editVisible = false
								this.getCommentList()
							},1000)
						}
						if (res.data.pageTotal)
							this.msgTotal = res.data.pageTotal
					}
				})
			},
			oppenComment(v) {
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
					toUserId: v.fromUserId,
					toUserName: v.fromUserName,
					toUserAvatar: v.fromUserAvatar,
					toUserEmail: v.fromUserEmail,
					parentId: v.id
				}
			},
			toReply(v) {
				var com = Object.assign(this.tempCom, v);
				this.addComment(com)
			},
			addComment(com) {
				this.$api.blog.addCommentAPI(com).then(res => {
					if (res.code === 200) {
						this.$message.success(res.message)
						setTimeout(() => {
							if (com.articleId === 'about')
								this.handleComment('about', this.msgQuery)
							else
								this.handleComment(com.articleId, {
									searchName: com.parentId
								})
								this.commentsCount++
						}, 1000)
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
			showDel(com) {
				this.$confirm('评论删除后将不可恢复，是否继续？', '提示', {
					type: 'warning'
				}).then(() => {
					this.delComment(com)
				}).catch((e) => {
					console.log(e)
				});
			},
			delComment(com) {
				this.$api.blog.delCommentAPI(com.id,com.articleId).then(res => {
					if (res.code === 200) {
						this.$message.success(res.message)
						setTimeout(() => {
							if (com.articleId === 'about')
								this.handleComment('about', this.msgQuery)
							else
								this.handleComment(com.articleId, {
									searchName: com.parentId
								})
								this.commentsCount--
						}, 700)

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
			persent(arg1, arg2) {
				var result = (100* (arg1/arg2)).toFixed(2)
				return Number(result)
			},
			getColor() {
				let count = ~~(Math.random() * this.color.length)
				return this.color[count]
			}
		}
	};
</script>


<style scoped>
	.item {
		margin-top: 10px;
		margin-right: 30px;
	}

	.el-row {
		margin-bottom: 20px;
	}

	.grid-content {
		display: flex;
		align-items: center;
		height: 100px;
	}

	.content {
		position: relative;
		height: 360px;
		padding-left: 10px;
		overflow-x: hidden;
		overflow-y: scroll;
	}

	.articleTag {
		overflow-y: scroll;
	}

	.inner-content {
		position: absolute;
		width: 375px;
	}

	.scrollbar::-webkit-scrollbar {
		width: 5px;
	}

	.scrollbar::-webkit-scrollbar-track-piece {
		background-color: #fff;
	}

	/* 滚动条的内层滑轨背景颜色 */

	.scrollbar::-webkit-scrollbar-track {
		background-color: #fff;
	}

	/* 滚动条的外层滑轨背景颜色 */

	.scrollbar::-webkit-scrollbar-thumb {
		background-color: #d4d8e2;
	}

	/* 滚动条的内层滑块颜色 */

	.scrollbar::-webkit-scrollbar-button {
		background-color: #fff;
		display: none;
	}

	/* 滑轨两头的监听按钮颜色 */
	.grid-cont-right {
		flex: 1;
		text-align: center;
		font-size: 14px;
		color: #999;
	}

	.grid-num {
		font-size: 30px;
		font-weight: bold;
	}

	.grid-con-icon {
		font-size: 50px;
		width: 100px;
		height: 100px;
		text-align: center;
		line-height: 100px;
		color: #fff;
	}

	.grid-con-1 .grid-con-icon {
		background: rgb(45, 140, 240);
	}

	.grid-con-1 .grid-num {
		color: rgb(45, 140, 240);
	}

	.grid-con-2 .grid-con-icon {
		background: rgb(100, 213, 114);
	}

	.grid-con-2 .grid-num {
		color: rgb(45, 140, 240);
	}

	.grid-con-3 .grid-con-icon {
		background: rgb(242, 94, 67);
	}

	.grid-con-3 .grid-num {
		color: rgb(242, 94, 67);
	}

	.user-info {
		display: flex;
		align-items: center;
		padding-bottom: 20px;
		border-bottom: 2px solid #ccc;
		margin-bottom: 20px;
	}

	.user-avator {
		width: 120px;
		height: 120px;
		border-radius: 50%;
	}

	.user-info-cont {
		padding-left: 50px;
		flex: 1;
		font-size: 14px;
		color: #999;
	}

	.user-info-cont div:first-child {
		font-size: 30px;
		color: #222;
	}

	.user-info-list {
		font-size: 14px;
		color: #999;
		line-height: 25px;
	}

	.user-info-list span {
		margin-left: 70px;
	}

	.mgb20 {
		margin-bottom: 20px;
	}

	.todo-item {
		font-size: 14px;
	}

	.todo-item-del {
		text-decoration: line-through;
		color: #999;
	}

	.schart {
		width: 100%;
		height: 300px;
	}
</style>
