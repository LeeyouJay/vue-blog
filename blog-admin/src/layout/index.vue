<template>
	<div class="wrapper">
		<v-sidebar></v-sidebar>
		<div class="content-box" :class="{'content-collapse':collapse}">
			<v-head ref="head"></v-head>
			<v-tags></v-tags>
			<el-scrollbar wrap-class="scrollbar">
				<div class="content">
					<transition name="move" mode="out-in">
						<keep-alive :include="tagsList">
							<router-view></router-view>
						</keep-alive>
					</transition>
					<el-backtop target=".content"></el-backtop>
				</div>
			</el-scrollbar>
		</div>
	</div>
</template>

<script>
	import vHead from './Header.vue';
	import vSidebar from './Sidebar.vue';
	import vTags from './Tags.vue';
	export default {
		data() {
			return {
				tagsList: [],
				collapse: false
			};
		},
		components: {
			vHead,
			vSidebar,
			vTags
		},
		created() {
			this.$bus.$off('collapse-content')
			this.$bus.$on('collapse-content', msg => {
				this.collapse = msg;
			});

			// 只有在标签页列表里的页面才使用keep-alive，即关闭标签之后就不保存到内存中了。
			this.$bus.$on('tags', msg => {
				console.log('在标签页列表')
				let arr = [];
				for (let i = 0, len = msg.length; i < len; i++) {
					msg[i].name && arr.push(msg[i].name);
				}
				this.tagsList = arr;
			});
		}
	};
</script>
<style scoped>
	.content {
		padding: 15px 25px;
		box-sizing: border-box;
		width: 100%;
	}

	.el-scrollbar {
		height: calc(100vh - 64px - 14px);
	}
	 .scrollbar {
		height: 100%;
	}
</style>
