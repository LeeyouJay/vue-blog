<template>
	<section class="main">
		<div class="container">
			<div class="plugins-tips">
				调整移动用户将覆盖原有权限，可在用户列表分别设置</br>
				移动拖拽基于 Sortable.js 的 Vue 拖拽组件。
				访问地址：<a href="https://github.com/SortableJS/Vue.Draggable" target="_blank">Vue.Draggable</a>
			</div>
			<div class="handle-box">
				<el-button plain type="primary" icon="el-icon-plus" @click="handleAdd">新增角色</el-button>
			</div>
			<div class="drag-box">
				<el-row :gutter="20" style="width: 100%;">
					<el-col :span="6">
						<div class="drag-box-item" v-if="refresh">
							<div class="item-title">未分配用户</div>
							<draggable v-model="nonUserList" @remove="removeHandle($event , nonUserList)" v-bind="dragOptions" @change="onChange($event,'non')">
								<transition-group tag="div" id="未分配用户" class="item-ul">
									<div v-for="item in nonUserList" :class="[item.setRight ? 'drag_list cover_set' : 'drag_list']" :key="item.id">
										{{item.username}}
										<span v-if="item.setRight" class="cover-span">已自定义权限</span>
									</div>
								</transition-group>
							</draggable>
						</div>
					</el-col>
					<el-col :span="6" v-for="row in tableData"  :key="row.id">
						<div class="drag-box-item" v-if="refresh">
							<div class="item-title">{{row.roleName}}&nbsp;
								<el-button type="text" icon="el-icon-edit" @click="oppenEdit(row)"></el-button>
								<el-button type="text" icon="el-icon-delete" class="red" @click="oppenDelRole(row)"></el-button>
							</div>
							<draggable  v-model="row.userList" @remove="removeHandle($event,row.userList)" v-bind="dragOptions" @change="onChange($event,row.id)">
								<transition-group tag="div" :id="row.roleName"  class="item-ul">
									<div v-for="item in row.userList"   :class="[item.setRight ? 'drag_list cover_set' : 'drag_list']" :key="item.id">
										{{item.username}}
										<span v-if="item.setRight" class="cover-span">已自定义权限</span>
									</div>
								</transition-group>
							</draggable>
						</div>
					</el-col>
				</el-row>
			</div>
		</div>
		<!-- 编辑弹出框 -->
		<el-dialog  :visible.sync="editVisible" width="30%" center>
			<template slot="title">
				<span class="dialog-title">权限设置</span>
			</template>
			<el-form :model="sysRole" status-icon :rules="ruleForm" ref="ruleForm" label-width="90px" class="demo-ruleForm">
				<el-form-item label="角色名称" prop="roleName">
					<el-input v-model="sysRole.roleName" autocomplete="off"></el-input>
				</el-form-item>
				<el-form-item label="角色标识" prop="role">
					<el-input v-model="sysRole.role" autocomplete="off"></el-input>
				</el-form-item>
			</el-form>
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
				<el-button @click="cancalEdit('ruleForm')">取 消</el-button>
				<el-button type="primary" @click="saveEdit">确 定</el-button>
			</span>
		</el-dialog>
		<!-- 添加弹出框 -->
		<el-dialog  :visible.sync="addVisible" width="30%" center>
			<template slot="title">
				<span class="dialog-title">权限设置</span>
			</template>
			<el-form :model="addRole" status-icon :rules="ruleForm" ref="AddRuleForm" label-width="90px" class="demo-ruleForm">
				<el-form-item label="角色名称" prop="roleName">
					<el-input v-model="addRole.roleName" autocomplete="off"></el-input>
				</el-form-item>
				<el-form-item label="角色标识" prop="role">
					<el-input v-model="addRole.role" autocomplete="off"></el-input>
				</el-form-item>
			</el-form>
			<span slot="footer" class="dialog-footer">
				<el-button @click="cancalAdd('AddRuleForm')">取 消</el-button>
				<el-button type="primary" @click="saveAdd('AddRuleForm')">确 定</el-button>
			</span>
		</el-dialog>
	</section>
</template>

<script>
	import draggable from 'vuedraggable'
	export default {
		name: 'draglist',
		data() {
			return {
				defaultProps: {
					children: 'children',
					label: 'authorityName'
				},
				ruleForm:{
					roleName:[{
						required: true,
						message: '权限名称不能为空',
						target:'blur'
					}],
					role:[{
						required: true,
						message: '权限标识不能为空',
						target:'blur'
					}]
				},
				editVisible: false,
				addVisible: false,
				authorityTree: [],
				tableData:[],
				userList:[],
				nonUserList:[],
				sysRole:{},
				addRole:{
					roleName:'',
					role:''
				},
				refresh:true,
				dragOptions: {
					sort:true,
					animation: 120,
					scroll: true,
					group: 'sortlist',
					ghostClass: 'ghost-style'
				},
			}
		},
		components: {
			draggable
		},
		created(){
			this.getRoleList();
		},
		methods: {
			removeHandle(event,row) {
				this.refresh = false
				this.$nextTick(() => {
					this.refresh = true
				})
				//this.$message.success(`从 ${event.from.id} 移动到 ${event.to.id} `);
			},
			onChange(event,id){
				if(event.added){
					var user = event.added.element
					user.roleId = id;
					this.UserChangeRole(user)
				}
			},
			UserChangeRole(user){
				this.$api.system.UserChangeRoleAPI(user).then(res=>{
					if(res.code === 200){
						user.setRight = false 
						this.$message.success(res.message)
					}else
						this.$message.error(res.message)
				})
			},
			renderContent(h, { node, data, store }) {
			        return (
			          <span style="width:100%">
			            <span style="float: left; line-height: 2;">{node.label}</span>
			            <span style="float: right;">
			              <el-button size="mini" type="text" >{data.authority}</el-button>
			            </span>
			          </span> );
			 },
			 saveEdit() {
			 	var list = this.$refs.tree.getCheckedNodes()
				let auths = list.map(val => val.authority)
				auths.push('dashboard')
				this.sysRole.authorities = auths
				this.$refs.ruleForm.validate(valid => {
					if(valid){
						this.$api.system.setRoleAuthoritiesAPI(this.sysRole).then(res=>{
							if(res.code === 200 ){
								this.editVisible = false
								this.$message.success(res.message)
							}else
								this.$message.error(res.message)
						})
					}else{
						console.log('验证不通过！')
					}
				})
			 },
			 cancalEdit(formName){
				 this.editVisible = false
				 //this.$refs[formName].resetFields();
			 },
			 saveAdd(formName){
				 this.$api.system.addSysRoleAPI(this.addRole).then(res=>{
					 if(res.code === 200){
						  this.addVisible = false
						  this.getRoleList()
						  this.$refs[formName].resetFields();
						 this.$message.success(res.message)
					 }else
						this.$message.error(res.message)
				 })
			 },
			 cancalAdd(formName){
			 	this.addVisible = false
			 	this.$refs[formName].resetFields();
			 },
			 getRoleList(){
				this.$api.system.getRoleListAPI().then(res=>{
					if(res.code === 200){
						this.tableData = res.data.list
						this.getTree();
						this.getRoleUserList();
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
			 			let tree = res.data.authorityTree
			 			this.authorityTree = tree
			 		}else
			 			this.$message.error(res.message)
			 	})
			 },
			 getRoleUserList() {
			 	this.$api.system.getRoleUserListAPI().then(res => {
			 		if(res.code === 200){
						let map = new Map(Object.entries(res.data))
						map.get('non') ? this.nonUserList = map.get('non') : this.nonUserList = []
						this.tableData.map(val=>{
							val.userList = []
							map.forEach((value,key,map)=>{
								if(val.id === key)
									val.userList = value
							})
							return val
						})
			 		}else
			 			this.$message.error(res.message)
			 	})
			 },
			 oppenEdit(row){
				 this.editVisible = true
				 this.sysRole = row
				this.$nextTick(() => {
					this.$refs.tree.setCheckedKeys(row.authorities)
				})
			 },
			 oppenDelRole(row){
				 this.$confirm('即将删除角色：'+row.roleName+'，角色权限将不可恢复是否继续？', '提示', {
				 		type: 'warning'
				 	})
				 	.then(() => {
				 		this.delRole(row)
				 	})
				 	.catch(() => {});
			 },
			 delRole(row){
				 this.$api.system.delRoleAPI(row).then(res=>{
					 if(res.code === 200){
						 this.$message.success(res.message)
						 this.getRoleList();
					 }else
						this.$message.error(res.message)
				 })
			 },
			 handleAdd(){
				 this.addVisible = true
			 }
		}
	}
</script>

<style scoped>
	.drag-box {
		display: flex;
		user-select: none;
	}

	.drag-box-item {
		background-color: #eff1f5;
		border-radius: 6px;
		margin-bottom: 20px;
		border: 1px #e1e4e8 solid;
	}

	.item-title {
		padding: 8px 8px 8px 12px;
		font-size: 14px;
		line-height: 1.5;
		color: #24292e;
		font-weight: 600;
	}

	.item-ul {
		padding: 0 8px 8px;
		height: 500px;
		overflow-y: scroll;
	}

	.item-ul::-webkit-scrollbar {
		width: 0;
	}

	.drag_list {
		border: 1px #e1e4e8 solid;
		padding: 10px;
		margin: 5px 0 10px;
		list-style: none;
		background-color: #fff;
		border-radius: 6px;
		cursor: pointer;
		-webkit-transition: border .3s ease-in;
		transition: border .3s ease-in;
	}

	.drag_list:hover {
		border: 1px solid #20a0ff;
	}
	.cover_set:hover{
		border: 1px solid #E6A23C;
	}
	.cover-span{
		float: right;
		font-size: 10px;
		color: #E6A23C;
	}
	.drag-title {
		font-weight: 400;
		line-height: 25px;
		margin: 10px 0;
		font-size: 22px;
		color: #1f2f3d;
	}
	.red {
	    color: #F56C6C;
	}
	.handle-box {
		margin-bottom: 20px;
	}
	.ghost-style {
		display: block;
		color: transparent;
		border-style: dashed
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
