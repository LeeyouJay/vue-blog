<template>
	<div>
		<div class="container">
			<div class="handle-box">
				<el-select v-model="query.state" placeholder="状态" clearable class="handle-select mr10">
					<el-option :value="1" label="成功" ></el-option>
					<el-option :value="0" label="失败" ></el-option>
				</el-select>
				<el-input v-model="query.searchName" placeholder="账号/地址" class="handle-input mr10"></el-input>
				<el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
			</div>

			<el-table :data="tableData" class="table" ref="multipleTable" header-cell-class-name="table-header">
				<el-table-column prop="username" label="登入账号" ></el-table-column>
				<el-table-column prop="type" label="登入方式" ></el-table-column>
				<el-table-column prop="ipAddress" label="登入IP" ></el-table-column>
				<el-table-column prop="ipSource" label="IP地址" ></el-table-column>
				<el-table-column prop="message" label="日志信息"></el-table-column>
				<el-table-column prop="browserName" label="浏览器名称" ></el-table-column>
				<el-table-column prop="systemName" label="系统名称" ></el-table-column>
				<el-table-column prop="createTime"  label="登入时间" sortable></el-table-column>
			</el-table>
			<div class="pagination">
				<el-pagination background layout="total, prev, pager, next" :current-page="query.pageIndex" :page-size="query.pageSize"
				 :total="pageTotal" @current-change="handlePageChange"></el-pagination>
			</div>
		</div>
	</div>
</template>

<script>
	export default {
		name: 'sysLog',
		data() {
			return {
				query: {
					state:'',
					searchName:'',
					pageIndex: 1,
					pageSize: 10
				},
				tableData: [],
				pageTotal: 10,
			};
		},
		created() {
			this.getData();
		},
		methods: {
			getData() {
				this.$api.system.getLogListAPI(this.query).then(res => {
					if(res.code === 200){
						this.tableData = res.data.syslogList
						this.pageTotal = res.data.pageTotal
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
			// 触发搜索按钮
			handleSearch() {
				this.$set(this.query, 'pageIndex', 1);
				this.getData();
			},
			// 分页导航
			handlePageChange(val) {
				this.$set(this.query, 'pageIndex', val);
				this.getData();
			},
		}
	};
</script>

<style scoped>

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
	.blue{
		color: #409EFF;
	}
	.yellow {
		color: #ffaa00;
	}

	.mr10 {
		margin-right: 10px;
	}






</style>
