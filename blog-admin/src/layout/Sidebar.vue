<template>
	<div class="sidebar">
		<div class="logo">
				<a href="https://github.com/LeeyouJay/vue-blog" target="_blank">
					<img src="../assets/img/github.png" :width="[collapse ? 45 : 70]"/>
				</a>
		</div>
		<el-menu class="sidebar-el-menu" :default-active="onRoutes" :collapse="collapse" background-color="#263238"
		 text-color="#bfcbd9" active-text-color="#20a0ff" :unique-opened="true" :router="true" >
					<el-menu-item  index="dashboard" >
						<i class="el-icon-s-promotion"></i>
					<span slot="title">系统首页</span>
					</el-menu-item>
					<el-menu-item v-for="menu in singleMenu" :index="menu.authority" >
						<i :class="menu.icon"></i>
					<span slot="title">{{menu.authorityName}}</span>
					</el-menu-item>
					<el-submenu v-for="item in menuList" :index="item.authority">
						<template slot="title">
							<i :class="item.icon"></i>
							<span slot="title">{{ item.authorityName }}</span>
						</template>
							<el-menu-item v-for="child in item.children" :index="child.authority">
								<template slot="title">
									<i :class="child.icon"></i>
									<span slot="title">{{ child.authorityName }}</span>
								</template>
							</el-menu-item>
					</el-submenu>
				
		</el-menu>
	</div>
</template>

<script>
	export default {
		data() {
			return {
				avatar: 'https://img.printf520.com/dist/static/images/avatar/006-avatar.jpg',
				collapse: false,
				singleMenu:[],
				menuList: [],
				testData:'',
				nickName:''
			};
		},
		computed: {
			onRoutes() {
				return this.$route.path.replace('/', '');
			}
		},
		mounted() {
			this.$bus.$on("reload",(msg)=>{
				this.getMenuList()
			})
		},
		created() {
			this.$bus.$off('collapse')
			this.$bus.$off('reload')
			this.getMenuList();
			this.$bus.$on('collapse', msg => {
				this.collapse = msg;
				this.$bus.$emit('collapse-content', msg);
			});
			this.nickName = localStorage.getItem('ms_username')
		},
		methods:{
			getMenuList(){
				this.$api.system.menuListAPI().then(res=>{
					if(res.code === 200){
						this.singleMenu = res.data.singleMenu
						this.menuList = res.data.menuList
					}else
						this.$message.error(res.message)
				})
			}
		}
	};
</script>

<style scoped>
	.sidebar {
		display: block;
		position: absolute;
		left: 0;
		top: 0px;
		bottom: 0;
		overflow-y: scroll;
	}

	.sidebar::-webkit-scrollbar {
		width: 0;
	}

	.sidebar-el-menu:not(.el-menu--collapse) {
		width: 250px;
	}

	.sidebar>ul {
		height: 100%;
	}

	.logo {
		text-align: center;

		height: 100px;
		padding: 10px;
		box-sizing: border-box;
		background-color: #263238;
	}
</style>
