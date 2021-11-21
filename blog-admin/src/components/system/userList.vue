<template>
	<div>
		<div class="container">
			<div class="handle-box">
				<el-select v-model="query.state" placeholder="状态" clearable class="handle-select mr10">
					<el-option :value="1" label="正常" ></el-option>
					<el-option :value="0" label="停用" ></el-option>
				</el-select>
				<el-input v-model="query.searchName" placeholder="昵称/账号/手机号" class="handle-input mr10"></el-input>
				<el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
			</div>

			<el-table :data="tableData" class="table" ref="multipleTable" header-cell-class-name="table-header">
				
				<el-table-column label="头像" align="center">
					<template slot-scope="scope">
						<el-image class="table-td-thumb" :src="preAvatar(scope.row.avatar)" :preview-src-list="preview(scope.row.avatar)"></el-image>
					</template>
				</el-table-column>
				<el-table-column prop="username" label="账号" align="center"></el-table-column>
				<el-table-column prop="nickName" label="昵称" align="center"></el-table-column>
				<el-table-column prop="sex" label="性别" align="center"></el-table-column>
				<el-table-column prop="phone" label="手机号码" min-width="100"></el-table-column>
				<el-table-column prop="email" label="邮箱" min-width="100"></el-table-column>
				<el-table-column  label="启用状态" align="center">
					<template slot-scope="scope">
						<el-switch v-model="scope.row.state" @change="stateChange(scope.row)">
						</el-switch>
					</template>
				</el-table-column>
				<el-table-column prop="createTime" min-width="120" label="注册时间" sortable></el-table-column>
				<el-table-column label="操作" width="180" align="center">
					<template slot-scope="scope">
						<el-button type="text" icon="el-icon-user"  @click="handleEdit(scope.$index, scope.row)">权限</el-button>
						<el-button type="text" icon="el-icon-key"  @click="handleDelete(scope.$index, scope.row)">重置</el-button>
					</template>
				</el-table-column>
			</el-table>
			<div class="pagination">
				<el-pagination background layout="total, prev, pager, next" :current-page="query.pageIndex" :page-size="query.pageSize"
				 :total="pageTotal" @current-change="handlePageChange"></el-pagination>
			</div>
		</div>

		<!-- 编辑弹出框 -->
		<el-dialog  :visible.sync="editVisible" width="30%" center>
			<template slot="title">
				<span class="dialog-title">权限设置</span>
			</template>
			<el-row :gutter="20">
				<el-col :span="24">
					<el-card shadow="always">
						<div class="content scrollbar">
							<div class="inner-content">
								<el-tree :data="authorityTree" node-key="authority" ref="tree" show-checkbox check-strictly default-expand-all
								 :props="defaultProps"  :render-content="renderContent" >
								 </el-tree>
							</div>
						</div>
					</el-card>
				</el-col>
			</el-row>
			<span slot="footer" class="dialog-footer">
				<el-button @click="editVisible = false">取 消</el-button>
				<el-button type="primary" @click="saveEdit">确 定</el-button>
			</span>
		</el-dialog>
	</div>
</template>

<script>
	import base from '../../api/base.js'
	export default {
		name: 'basetable',
		data() {
			return {
				basicUrl:'',
				authorityTree: [],
				defaultProps: {
					children: 'children',
					label: 'authorityName'
				},
				query: {
					state: '',
					searchName: '',
					pageIndex: 1,
					pageSize: 10
				},
				tableData: [],
				editVisible: false,
				pageTotal: 10,
				userForm: {},
			};
		},
		created() {
			this.basicUrl = base.localUrl
			this.getData();
		},
		methods: {
			getData() {
				this.$api.system.userListAPI(this.query).then(res => {
					if(res.code === 200){
						this.tableData = res.data.list
						this.pageTotal = res.data.pageTotal
						this.getTree();
					}else if (res.code === 401){
						this.$removeToken()
						this.$alert(res.message, "提示", {
							type: 'warning',
							 center: true,
							 callback: action => {
								 this.$router.replace('/login')
							 }
						})
					}else
						this.$message.error(res.message)
				})
			},
			getTree() {
				this.$api.system.authoritiesAPI().then(res => {
					if(res.code === 200){
						let state = ['dashboard','system','user_list',
						'menu','sysLog','setAuthority','resetPassword',
						'addMenu','delMenu','editMenu',
						'changState','sysRoles','delete_sysRole',
						'change_userAuths','set_roleAuths','add_sysRole']
						let tree = res.data.authorityTree
						tree.map(val => {
							state.forEach(item=>{
								if(val.authority === item)
									val.disabled = true 
									val.children.map(v=>{
										if(v.authority === item)
											v.disabled = true
											v.children.map(m=>{
												if(m.authority === item)
													m.disabled = true 
											})
									})
							})
							return val
						})
						this.authorityTree = tree
					}else
						this.$message.error(res.message)
				})
			},
			preAvatar(avatar){
				return ( avatar.indexOf('-thumbnail') == -1) ? avatar : this.basicUrl + avatar
			},
			preview(avatar){
				return ( avatar.indexOf('-thumbnail') == -1) ? [avatar] : [this.basicUrl+avatar.substr(0,avatar.lastIndexOf('-'))+'.jpg']
			},
			// 触发搜索按钮
			handleSearch() {
				this.$set(this.query, 'pageIndex', 1);
				this.getData();
			},
			// 删除操作
			handleDelete(index, row) {
				this.$confirm('密码将重置为123456，是否继续？', '提示', {
						type: 'warning'
					})
					.then(() => {
						this.resetPassword(row)
					})
					.catch(() => {});
			},
			resetPassword(row){
				this.$api.system.resetAPI(row).then(res=>{
					if(res.code === 200)
						this.$message.success(res.message);
					else if(code === 401){
						this.$removeToken()
						this.$alert(res.message, "提示", {
							type: 'warning',
							 center: true,
							 callback: action => {
								 this.$router.replace('/login')
							 }
						})
					}else
						this.$message.success(res.message);
				})
			},
			handleEdit(index, row) {
				this.userForm = row;
				this.editVisible = true;
				this.$nextTick(() => {
					this.$refs.tree.setCheckedKeys(row.sysAuthorities)
				})
			},
			stateChange(row) {
				this.$api.system.setStateAPI(row).then(res=>{
					if(res.code === 200)
						row.state ? this.$message.success("账号已启用！") : this.$message.info("账号已禁用！")
					else if(code === 401){
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
			saveEdit() {
				var list = this.$refs.tree.getCheckedNodes()
				let auths = list.map(val => val.authority);
				this.userForm.sysAuthorities = auths;
				this.$api.system.setAuthorityAPI(this.userForm).then(res => {
					if (res.code === 200) {
						this.$message.success(res.message)
						this.$bus.$emit("reload",'重新加载左边栏')
						this.editVisible = false;
					} else if(code === 401){
						this.$removeToken()
						this.$alert(res.message, "提示", {
							type: 'warning',
							 center: true,
							 callback: action => {
								 this.$router.replace('/login')
							 }
						})
					}else
						this.$message.error(res.message)
				})

			},
			// 分页导航
			handlePageChange(val) {
				this.$set(this.query, 'pageIndex', val);
				this.getData();
			},
			renderContent(h, { node, data, store }) {
			        return (
			          <span style="width:100%">
			            <span style="float: left; line-height: 2;">{node.label}</span>
			            <span style="float: right;">
			              <el-button size="mini" type="text" >{data.authority}</el-button>
			            </span>
			          </span> );
			      }
		}
	};
</script>

<style scoped>
	.dialog-title {
		line-height: 24px;
		font-size: 18px;
		color: #303133;
		font-weight: bold;
	}
	.handle-box {
		margin-bottom: 20px;
	}
	.custom-tree-node {
		width: 100%;
	    flex: 1;
	    display: flex;
	    align-items: center;
	    justify-content: space-between;
	    font-size: 14px;
	    padding-right: 8px;
	  }
	.handle-select {
		width: 120px;
	}

	.handle-input {
		width: 300px;
		display: inline-block;
	}

	.table {
		width: 100%;
		font-size: 14px;
	}

	.red {
		color: #ff0000;
	}
	.blue{
		color: #409EFF;
	}
	.yellow {
		color: #ffaa00;
	}

	.mr10 {
		margin-right: 10px;
	}

	.title {
		text-align: center;
		font-size: 14px;
		font-weight: 700;
	}

	.table-td-thumb {
		display: block;
		margin: auto;
		width: 40px;
		height: 40px;
		border-radius: 50%;
	}
	  .content{
		position: relative;
	    height: 360px;
	    padding-left: 10px;
		overflow-x: hidden;
	    overflow-y: hidden;
	  }
	  .inner-content{
		  position: absolute; 
		  width: 375px;
	  }

	  .content:hover{
	    overflow-y: auto;
	  }
	  .scrollbar::-webkit-scrollbar {
	    width: 5px;
	  }
	
	  .scrollbar::-webkit-scrollbar-track-piece {
	    background-color: #fff;
	  } /* 滚动条的内层滑轨背景颜色 */
	
	  .scrollbar::-webkit-scrollbar-track {
	    background-color: #fff;
	  } /* 滚动条的外层滑轨背景颜色 */
	
	  .scrollbar::-webkit-scrollbar-thumb {
	    background-color: #d4d8e2;
	  } /* 滚动条的内层滑块颜色 */
	
	  .scrollbar::-webkit-scrollbar-button {
	    background-color: #fff;
	    display: none;
	  } /* 滑轨两头的监听按钮颜色 */
</style>
