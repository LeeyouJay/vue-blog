<template>
	<div class="container">
		<el-tabs v-model="message" @tab-click="handleClick">
			<el-tab-pane :label="'正在申请('+applyTotal+')'" name="first">
				<!-- <div class="handle-row">
					<el-button type="primary">全部通过</el-button>
				</div> -->
				<el-table :data="unread" class="table" ref="multipleTable" header-cell-class-name="table-header"
					height="440">
					<el-table-column label="图标" width="100" align="center">
						<template slot-scope="scope">
							<el-image class="table-td-thumb" :src="scope.row.icon"></el-image>
						</template>
					</el-table-column>
					<el-table-column prop="siteName" label="名称"></el-table-column>
					<el-table-column prop="siteLink" label="链接">
						<template slot-scope="scope">
							<el-link :href="scope.row.siteLink" type="primary" target="_blank">{{scope.row.siteLink}}
							</el-link>
						</template>
					</el-table-column>
					<el-table-column prop="email" label="邮箱">
						<template slot-scope="scope">
							<span class="message-title">{{scope.row.email}}</span>
						</template>
					</el-table-column>
					<el-table-column prop="description" label="描述" width="300"></el-table-column>
					<el-table-column prop="createTime" label="申请时间"></el-table-column>
					<el-table-column label="操作" width="180" align="center">
						<template slot-scope="scope">
							<el-button type="text" icon="el-icon-check" :disabled="loading" @click="toRead(scope.row,scope.$index)">通过
							</el-button>
							<el-button type="text" icon="el-icon-close" :disabled="loading" style="color: #F56C6C;" @click="toReject(scope.row,scope.$index)">拒绝</el-button>
						</template>
					</el-table-column>
				</el-table>
				<div class="pagination">
					<el-pagination background layout="total, prev, pager, next" :current-page="applyQuery.pageIndex"
						:page-size="applyQuery.pageSize" :total="applyTotal" @current-change="handlePageChange1">
					</el-pagination>
				</div>
			</el-tab-pane>
			<el-tab-pane :label="'已通过('+friendsTotal+')'" name="second">
				<template v-if="message === 'second'">
					<el-table :data="read" class="table" ref="secondTable" header-cell-class-name="table-header"
						height="440">
						<el-table-column label="图标" width="100" align="center">
							<template slot-scope="scope">
								<el-image class="table-td-thumb" :src="scope.row.icon"></el-image>
							</template>
						</el-table-column>
						<el-table-column prop="siteName" label="名称"></el-table-column>
						<el-table-column prop="siteLink" label="链接"></el-table-column>
						<el-table-column prop="email" label="邮箱"></el-table-column>
						<el-table-column prop="description" label="描述" width="300"></el-table-column>
						<el-table-column prop="updateTime" label="通过时间"></el-table-column>
						<el-table-column label="操作" width="180" align="center">
							<template slot-scope="scope">
								<el-button type="text" icon="el-icon-delete" :disabled="loading" style="color: #F56C6C;" @click="toDelete(scope.row,scope.$index)">删除</el-button>
							</template>
						</el-table-column>
					</el-table>
					<div class="pagination">
						<el-pagination background layout="total, prev, pager, next" :current-page="applyQuery.pageIndex"
							:page-size="applyQuery.pageSize" :total="friendsTotal" @current-change="handlePageChange2">
						</el-pagination>
					</div>
				</template>
			</el-tab-pane>
		</el-tabs>
		
		<!-- 编辑弹出窗-->
		<el-dialog title="拒绝回复"  :visible.sync="centerDialogVisible" width="30%" center>
		  <el-input type="textarea" v-model="desc"  :rows="3" placeholder="请输入内容" >
		  </el-input>
		  <div class="dialog-footer">
			  <el-button @click="centerDialogVisible = false">取 消</el-button>
			  <el-button type="primary" @click="doReject">确 定</el-button>
		  </div>
		</el-dialog>
	</div>
</template>

<script>
	export default {
		name: 'tabs',
		data() {
			return {
				message: 'first',
				loading:false,
				centerDialogVisible:false,
				applyTotal: 0,
				friendsTotal: 0,
				applyQuery: {
					state: 'first',
					searchName: '',
					pageIndex: 1,
					pageSize: 7
				},
				friendQuery: {
					state: 'second',
					searchName: '',
					pageIndex: 1,
					pageSize: 7
				},
				desc:'',
				unread: [],
				read: [],
				tempFriend:{},
				tempIndex:0
			}
		},
		mounted() {
			this.getFriends(this.applyQuery)
		},
		methods: {
			handleClick(tab, event) {
				if (tab.index == 0) {
					this.applyQuery.pageIndex = 1
					this.getFriends(this.applyQuery)
				} else {
					this.friendQuery.pageIndex = 1
					this.getFriends(this.friendQuery)
				}

			},
			handlePageChange1(val) {
				this.$set(this.applyQuery, 'pageIndex', val);
				this.getFriends(this.applyQuery)
			},
			handlePageChange2(val) {
				this.$set(this.friendQuery, 'pageIndex', val);
				this.getFriends(this.friendQuery)
			},
			toRead(row, index){
				this.$confirm('确认通过?', '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning',
					 center: true
				}).then(() => {
					this.handleRead(row, index)
				}).catch((e) => {
					console.log(e)
				});
			},
			handleRead(row, index) {
				row.hasCheck = true
				this.loading = true 
				this.$api.blog.passApplyAPI(row).then(res => {
					if (res.code === 200) {
						this.$message.success(res.message)
						setTimeout(() => {
							if(this.applyTotal != 0)
								this.applyTotal--
							this.friendsTotal++
							this.unread.splice(index, 1)
							this.loading = false
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
			toReject(row, index){
				this.tempFriend = row
				this.tempIndex = index
				this.centerDialogVisible = true
			},
			doReject(){
				this.tempFriend.tempDesc = this.desc
				this.handleDel(this.tempFriend,this.tempIndex)
			},
			toDelete(row,index){
				this.$confirm('确认删除此友链么?', '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning',
					 center: true
				}).then(() => {
					this.handleDel(row,index)
				}).catch((e) => {
					console.log(e)
				});
			},
			handleDel(row,index) {
				this.$api.blog.delFriendAPI(row).then(res=>{
					if (res.code === 200) {
						this.$message.success(res.message)
						setTimeout(() => {
							if( this.applyTotal !== 0)
								this.applyTotal--
							this.unread.splice(index, 1)
							this.loading = false
							this.centerDialogVisible = false
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
			getFriends(query) {
				this.$api.blog.getFriendsAPI(query).then(res => {
					if (res.code === 200) {
						this.unread = res.data.applyFriends
						this.read = res.data.friends
						this.applyTotal = res.data.applyTotal
						this.friendsTotal = res.data.friendsTotal
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
			}

		}
	}
</script>

<style scoped>
	.message-title {
		cursor: pointer;
	}

	.handle-row {
		margin-bottom: 20px;
	}

	.table {
		width: 100%;
		font-size: 14px;
	}
	.table-td-thumb {
		display: block;
		margin: auto;
		width: 40px;
		height: 40px;
		border-radius: 50%;
	}
	.dialog-footer {
		margin-top: 20px;
		text-align-last: center;
	}
</style>
