<template>
	<div>
		<div class="container">
			<div class="handle-box">
				<el-select v-model="query.tag" placeholder="分类" clearable class="handle-select mr10">
					<el-option v-for="(item,index) in dynamicTags" :key="index" :label="item" :value="item"></el-option>
				</el-select>
				<el-input v-model="query.content" placeholder="标题/内容" class="handle-input mr10"></el-input>
				<el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
			</div>

			<el-button type="danger" icon="el-icon-delete" class="handle-del" @click="delAllSelection">批量删除
			</el-button>
			<div>
				分类管理：
				<el-tag :key="tag" v-for="tag in dynamicTags" closable :disable-transitions="false"
					@close="handleClose(tag)">
					{{tag}}
				</el-tag>
				<el-input class="input-new-tag" v-if="inputVisible" v-model="inputValue" ref="saveTagInput" size="small"
					@keyup.enter.native="handleInputConfirm" @blur="handleInputConfirm">
				</el-input>
				<el-button v-else class="button-new-tag" size="small" @click="showInput">+ New Tag</el-button>
			</div>
			<el-table :data="tableData" border class="table" ref="multipleTable" header-cell-class-name="table-header" height="400"
				@selection-change="handleSelectionChange">
				<el-table-column type="selection" width="55" align="center"></el-table-column>
				<el-table-column label="标图" align="center">
					<template slot-scope="scope">
						<el-image class="table-td-thumb" :src="scope.row.banner"
							:preview-src-list="[scope.row.banner.includes('-thumbnail') ? scope.row.banner.substr(0, scope.row.banner.lastIndexOf('-')) + '.jpg' : scope.row.banner]">
						</el-image>
					</template>
				</el-table-column>
				<el-table-column prop="title" label="标题" width="155"></el-table-column>
				<el-table-column label="分类" width="155">
					<template slot-scope="scope">
						<el-select v-model="scope.row.tag" @change="tagChange(scope.row)">
							<el-option v-for="(item,index) in dynamicTags" :key="index" :label="item" :value="item"></el-option>
						</el-select>
					</template>
				</el-table-column>
				<el-table-column label="状态" width="100">
					<template slot-scope="scope">
						<el-select v-model="scope.row.showType" @change="typeChange(scope.row)">
							<el-option label="公开" value="公开"></el-option>
							<el-option label="个人" value="个人"></el-option>
						</el-select>
					</template>
				</el-table-column>
				<el-table-column label="是否置顶">
					<template slot-scope="scope">
						<el-switch v-model="scope.row.top" @change="stateChange(scope.row)">
						</el-switch>
					</template>
				</el-table-column>
				<el-table-column label="文章格式" width="140">
					<template slot-scope="scope">
						<el-tag size='medium' :type="scope.row.markdown ? 'info' : ''" effect="plain">
							{{scope.row.markdown ? 'Markdown格式' : 'HTML格式'}}
						</el-tag>
						</el-switch>
					</template>
				</el-table-column>
				<el-table-column prop="commentsCount" label="评论"></el-table-column>
				<el-table-column prop="viewsCount" label="热度"></el-table-column>
				<el-table-column prop="createTime" label="发布时间" width="155"></el-table-column>
				<el-table-column prop="updateTime" label="最后编辑时间" width="155"></el-table-column>

				<el-table-column label="操作" width="90" align="center" fixed="right">
					<template slot-scope="scope">
						<el-button type="text" icon="el-icon-edit" @click="handleEdit(scope.$index, scope.row)">修改
						</el-button>
					</template>
				</el-table-column>
			</el-table>

			<div class="pagination">
				<el-pagination background layout="total, prev, pager, next" :current-page="query.pageIndex"
					:page-size="query.pageSize" :total="pageTotal" @current-change="handlePageChange"></el-pagination>
			</div>
		</div>

	</div>
</template>

<script>
	export default {
		name: 'basetable',
		data() {
			return {
				dynamicTags: [],
				inputVisible: false,
				inputValue: '',
				query: {
					content: '',
					author: '',
					tag: '',
					pageIndex: 1,
					pageSize: 10
				},
				tableData: [],
				multipleSelection: [],
				delIds: [],
				pageTotal: 10,
			};
		},
		created() {
			this.getArticleTags()
			
		},
		methods: {
			getArticleTags(){
				this.$api.blog.getArticleTagsAPI().then(res=>{
					if(res.code === 200){
						if(res.data.tags)
							this.$store.dispatch('setArticleTags',res.data.tags)
						this.getData()
						this.dynamicTags = this.$store.state.articleTags
					}else if (res.code === 401) {
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
			getData() {
				this.$api.blog.articleListAPI(this.query).then(res => {
					if (res.code === 200) {
						this.tableData = res.data.articleList
						this.pageTotal = res.data.pageTotal
					} else
						this.$message.error(res.message);
				})
			},
			handleClose(tag) {
				var tags = this.$store.getters.getArticleTags
				tags.splice(tags.indexOf(tag), 1)
				this.saveTags(tags)
			},
			showInput() {
				this.inputVisible = true;
				this.$nextTick(_ => {
					this.$refs.saveTagInput.$refs.input.focus();
				});
			},
			handleInputConfirm() {
				let inputValue = this.inputValue;
				if (inputValue) {
					var tags = this.$store.getters.getArticleTags
					tags.push(inputValue)
					this.saveTags(tags)
				}
				this.inputVisible = false;
				this.inputValue = '';
			},
			saveTags(tags){
				this.$api.blog.saveTagsAPI(tags).then(res=>{
					if(res.code === 200){
						this.dynamicTags = tags
						this.$store.dispatch('setArticleTags',tags)
						this.$message.success(res.message)
					}else if (res.code === 401) {
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
			// 触发搜索按钮
			handleSearch() {
				var pattern = /[`~!@#_$%^&*()=|{}':;',\\\[\\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？\s]/g
				this.query.content = this.query.content.replace(pattern, '')
				this.$set(this.query, 'pageIndex', 1);
				this.getData();
			},
			// 多选操作
			handleSelectionChange(val) {
				this.multipleSelection = val;
			},
			delAllSelection() {
				if(!this.multipleSelection.length){
					this.$message.warning('请先选择数据！')
					return
				}
				this.$confirm('删除文章将不可恢复，确定要删除么？', '提示', {
					type: 'warning'
				}).then(() => {
					this.delArticles()
				}).catch((e) => {
					console.log(e)
				});
			},
			delArticles() {
				const length = this.multipleSelection.length;
				this.delIds = [];
				let msg = '';
				for (let i = 0; i < length; i++) {
					msg += '《' + this.multipleSelection[i].title + '》 ';
					this.delIds.push(this.multipleSelection[i].id)
				}
				this.$api.blog.delArticleAPI(this.delIds).then(res => {
					if (res.code === 200) {
						this.$message.success(res.message)
						this.multipleSelection = [];
						this.getData()
					} else if (code === 401) {
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
			// 编辑操作
			handleEdit(index, row) {
				this.$store.dispatch('setArticleEdit', true);
				this.$store.dispatch('setArticle', row);
				if (row.markdown) {
					this.$router.push('/write_markdown')
				} else {
					this.$router.push('/write_html')
				}
			},
			stateChange(row) {
				this.saveEdit(row)
			},
			tagChange(row) {
				this.saveEdit(row)
			},
			typeChange(row) {
				this.saveEdit(row)
			},
			// 保存编辑
			saveEdit(row) {
				this.$api.blog.editArticleAPI(row).then(res => {
					if (res.code === 200) {
						this.$message.success(res.message)
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
			// 分页导航
			handlePageChange(val) {
				this.$set(this.query, 'pageIndex', val);
				this.getData();
			}
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

	.handle-input-author {
		width: 120px;
		display: inline-block;
	}

	.handle-del {
		margin-bottom: 20px;
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

	.el-tag+.el-tag {
		margin-left: 12px;
		margin-bottom: 12px;
	}

	.button-new-tag {
		margin-left: 10px;
		height: 32px;
		line-height: 30px;
		padding-top: 0;
		padding-bottom: 0;
	}

	.input-new-tag {
		width: 90px;
		margin-left: 10px;
		vertical-align: bottom;
	}
</style>
