<template>
	<div>
		<div class="container">
			<div class="handle-box">
			</div>
			<div class="handle-box">
				<el-button plain type="primary" icon="el-icon-plus" @click="handleAdd">新增</el-button>
			</div>
			<el-table :data="tableData" class="table" row-key="id" ref="menuTabl" header-cell-class-name="table-header">
				<el-table-column prop="authorityName" align="left" label="权限名称" width="200"></el-table-column>
				<el-table-column align="center" label="图标" width="80">
					<template slot-scope="scope">
						<i :class="scope.row.icon"></i>
					</template>
				</el-table-column>
				<el-table-column prop="sorted" label="排序" width="70"></el-table-column>
				<el-table-column prop="authority" label="权限标识"></el-table-column>
				<el-table-column prop="authorityType" align="center" label="类型" width="100"></el-table-column>
				<el-table-column prop="createTime" align="center" label="创建时间"></el-table-column>

				<el-table-column label="操作" width="180" align="center">
					<template slot-scope="scope">
						<el-button type="text" icon="el-icon-edit" @click="handleEdit(scope.$index, scope.row)">修改</el-button>
						<el-button v-if="scope.row.children.length==0" type="text" icon="el-icon-delete" class="red" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
					</template>
				</el-table-column>
			</el-table>
			<div class="pagination" style="height: 30px;">
				
			</div>
		</div>

		<!-- 编辑弹出框 -->
		<el-dialog :visible.sync="editVisible" width="35%" center>
			<template slot="title">
				<span class="dialog-title">修改</span>
			</template>
			<el-form ref="formTable" :model="form" label-width="80px" :rules="rules" >
				<el-row :gutter="20">
					<el-col :span="24">
						<el-form-item label="上级权限">
							<el-select v-model="slelectLable" placeholder="点击选择页面" ref="selectUpResId">
								<el-option hidden  :value="slelectVale" :label="slelectLable" style="height: auto"></el-option>
									<el-tree :data="tableTree"  node-key="id" ref="tree" highlight-current 
									:props="defaultProps" :expand-on-click-node="false" :check-on-click-node="true" default-expand-all @node-click="handleNodeClick"></el-tree>
							</el-select>
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="权限名称" prop="authorityName">
							<el-input v-model="form.authorityName"></el-input>
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="权限标识" prop="authority">
							<el-input v-model="form.authority"></el-input>
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="类型">
							<el-radio-group v-model="form.authorityType">
								<el-radio label="页面"></el-radio>
								<el-radio label="按钮"></el-radio>
							</el-radio-group>
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item label="显示排序" prop="sorted">
							<el-input-number v-model="form.sorted" :min="0" ></el-input-number>
						</el-form-item>
					</el-col>
					<el-col :span="24">
						<el-form-item label="图标">
							<e-icon-picker v-model="form.icon" />
						</el-form-item>
					</el-col>
				</el-row>
			</el-form>
			<span slot="footer" class="dialog-footer">
				<el-button @click="editVisible = false">取 消</el-button>
				<el-button type="primary" @click="submitData">确 定</el-button>
			</span>
		</el-dialog>
	</div>
</template>

<script>
	var that ;
	export default {
		name: 'basetable',
		data() {
			return {
				isAdd:false,
				slelectLable:'',
				slelectVale: '',
				query: {
					authorityName: '',
					authorityType:''
				},
				defaultProps: {
					children: 'children',
					label: 'authorityName'
				},
				tableTree:[
					{
						authorityName: "主目录",
						level:-1,
						children:[],
						id: "none"
					}
				],
				tableData: [],
				editVisible: false,
				form: {
					id: '',
					sorted: 0,
					authority: '',
					authorityName: '',
					authorityType: '',
				},
				rules:{
					authorityName:[{ required: true, message: '请输入权限名称', trigger: 'blur' }],
					authority:[{ required: true, message: '请输入权限标示', trigger: 'blur' }],
					sorted:[{ required: true, message: '请选择排序大小', trigger: 'blur' }]
				}
			};
		},
		created() {
			this.getData();
		},
		methods: {
			getData() {
				this.$api.system.authoritiesAPI().then(res => {
					if(res.code === 200){
						this.tableData = res.data.authorityTree
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
						this.$message.error(res.message)
				})
			},
			
			handleNodeClick(data) {
				this.slelectVale = data.id
				this.slelectLable = data.authorityName
				this.form.parentId = data.id
				this.form.level = data.level+1;
				this.form.parentName = data.authorityName
				this.$refs.selectUpResId.blur()
			},
			// 触发搜索按钮
			handleSearch() {
				
			},
			// 删除操作
			handleDelete(index, row) {
				// 二次确认删除
				this.$confirm('确定要删除吗？', '提示', {
						type: 'warning'
					})
					.then(() => {
						this.delMenu(row.id,row)
					})
					.catch((e) => {
						console.log(e)
					});
			},
			delMenu(id,row){
					this.$api.system.delMenuAPI(id).then(res=>{
						if(res.code === 200){
							this.$message.success(res.message);
							this.filterData(this.tableData, row);
							this.$bus.$emit("reload",'重新加载左边栏')
						}else
							this.$message.success(res.message);
					})
			},
			filterData(data, row) {
				if (row.parentId == 'none') return data.splice(data.findIndex(item => item.id === row.id), 1);
				var newData = data.filter(x => x.id != row.id)
				newData.forEach(x => x.children && (x.children = this.filterData(x.children, row)))
				return newData
			},
			//添加操作
			handleAdd() {
				this.isAdd = true
				this.slelectLable = "主目录"
				this.slelectVale = "none"
				this.form.authorityName = ""
				this.form.authority=""
				this.form.icon = ""
				this.form.level = 0
				this.tableTree = [{
						authorityName: "主目录",
						level:-1,
						children:[],
						id: "none"
					}]
				this.tableData.forEach(val=>this.tableTree.push(val))
				this.editVisible = true;
			},
			// 编辑操作
			handleEdit(index, row) {
				this.editVisible = true
				this.isAdd = false
				this.slelectLable = row.authorityName;
				this.tableTree = [{
						authorityName: "主目录",
						level:-1,
						children:[],
						id: "none"
					}]
				this.tableData.forEach(val=>this.tableTree.push(val))
				this.$api.system.findByIdAPI(row.parentId).then(res=>{
					if(res.code === 200){
						let parent = res.data.parent
						parent ? this.slelectLable = parent.authorityName : this.slelectLable="主目录"
						parent ? this.slelectVale = parent.id : this.slelectVale = "none"
					}else
						this.$message.error(res.message)
				})
				this.form = Object.assign({}, row)
			},
			submitData(){
				this.isAdd?this.saveAdd() : this.saveEdit()
			},
			saveEdit() {
				this.$api.system.setMenuAPI(this.form).then(data=>{
					if(data.code === 200 ){
						this.$message.success(data.message)
						this.editVisible = false;
						this.getData();
						this.$bus.$emit("reload",'重新加载左边栏')
					}else
						this.$message.error(data.message)
				})
			},
			saveAdd() {
				this.$api.system.addMenuAPI(this.form).then(data=>{
					if(data.code === 200 ){
						this.$message.success(data.message)
						this.getData();
						this.editVisible = false;
					}else
						this.$message.error(data.message)
				})
			},
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

	.mr10 {
		margin-right: 10px;
	}

	.table-td-thumb {
		display: block;
		margin: auto;
		width: 40px;
		height: 40px;
	}
</style>
