<template>
	<div class="home">
		<banner isHome="true"></banner>
		<div class="site-content">
			<!--通知栏-->
			<div class="notify">
				<div class="search-result" v-if="hideSlogan">
					<span v-if="searchWords">搜索结果："{{searchWords}}" 相关文章</span>
					<span v-else-if="category">分类 "{{category}}" 相关文章</span>
				</div>
				<quote v-else>面向自由和真理的无名献身。</quote>
			</div>

			<!--焦点图-->
			<div class="top-feature animate" v-if="!hideSlogan">
				<section-title>
					<div style="display: flex;align-items: flex-end;">聚焦<small-ico></small-ico>
					</div>
				</section-title>
				<div class="feature-content">
					<div class="feature-item" v-for="item in features" :key="item.title">
						<Feature :data="item"></Feature>
					</div>
				</div>
			</div>
			<!--文章列表-->
			<main class="site-main" :class="{'search':hideSlogan}">
				<section-title v-if="!hideSlogan">近况</section-title>
				<template v-for="item in postList">
					<post :post="item" :banner="item.banner" :key="item.id"></post>
				</template>
			</main>

			<!--加载更多-->
			<div class="more" v-show="hasNextPage">
				<div class="more-btn" @click="loadMore">More</div>
			</div>
		</div>
	</div>
</template>

<script>
	import Banner from '@/components/banner'
	import Feature from '@/components/feature'
	import sectionTitle from '@/components/section-title'
	import Post from '@/components/post'
	import SmallIco from '@/components/small-ico'
	import Quote from '@/components/quote'
	import { removeToken } from '@/utils/cookie.js'
	import { articleListAPI } from '../api'

	export default {
		name: 'Home',
		props: ['cate', 'words'],
		components: {
			Banner,
			Feature,
			sectionTitle,
			Post,
			SmallIco,
			Quote
		},
		data() {
			return {
				features: [{
						id: 1,
						url:'meaning',
						title: '个人博客，意义何在',
						img: 'https://c.pxhere.com/photos/c1/1d/cloud_sky_fluffy_atmosphere_heaven-1625.jpg!d'
					},
					{
						id: 2,
						url:'article/about',
						title: '关于博客',
						img: 'https://c.pxhere.com/photos/a2/e4/mist_fog_forest_evergreen_nature-100653.jpg!d'
					},
					{
						id: 3,
						url:'Archives',
						title: '文章归档',
						img: 'https://c.pxhere.com/photos/9b/63/runner_sunset_route_run_road-2492.jpg!d'
					},
				],
				postList: [],
				query: {
					content: '',
					tag:'',
					pageIndex: 1,
					pageSize: 8
				},
				pageTotal:0,
				hasNextPage: false
			}
		},
		computed: {
			searchWords() {
				//return this.$route.params.words
				return this.query.content
			},
			category() {
				//return this.$route.params.cate
				return this.query.tag
			},
			hideSlogan() {
				return this.category || this.searchWords
			},
		},
		methods: {
			loadMore() {
				this.query.pageIndex++
				this.getArticleList() 
			},
			getArticleList() {
				articleListAPI(this.query).then(res=>{
					if(res.code === 401){
						removeToken()
						this.$store.dispatch('setNeedInfo',true)
					}else
						this.$store.dispatch('setNeedInfo',false)
					
					this.postList = this.postList.concat(res.data.articleList)
					this.pageTotal = res.data.pageTotal
					
					var query = this.query
					if(query.pageIndex*query.pageSize<this.pageTotal){
						this.hasNextPage = true;
					}else{
						this.hasNextPage = false;
					}
				})
			},
		},
		created() {
			this.$bus.$off('reload')
			this.$bus.$off('searchTag')
			this.$bus.$off('searchContent')
			this.query.content = this.$store.state.content
			this.query.tag = this.$store.state.tag
			this.getArticleList()
		},
		mounted() {
			this.$bus.$on('searchTag',msg=>{
				this.query.tag = msg
				this.query.content = ''
				this.query.pageIndex =1
				this.postList = []
				this.getArticleList()
			})
			this.$bus.$on('searchContent',msg=>{
				this.query.tag = ''
				this.query.content = msg
				this.query.pageIndex =1
				this.postList = []
				this.getArticleList()
			})
			this.$bus.$on('reload',msg=>{
				this.query.tag = ''
				this.query.content = ''
				this.query.pageIndex =1
				this.postList = []
				this.getArticleList()
			})
		},
	}
</script>
<style scoped lang="less">
	.site-content {
		.notify {
			margin: 60px 0;
			border-radius: 3px;

			// &>div {
			// 	padding: 20px;
			// }
		}


		.search-result {
			padding: 15px 20px;
			text-align: center;
			font-size: 20px;
			font-weight: 400;
			border: 1px dashed #ddd;
			color: #828282;
		}
	}

	.top-feature {
		width: 100%;
		height: auto;
		margin-top: 30px;

		.feature-content {
			margin-top: 10px;
			display: flex;
			justify-content: space-between;
			position: relative;

			.feature-item {
				width: 32.9%;
			}
		}
	}

	.site-main {
		padding-top: 80px;

		&.search {
			padding-top: 0;
		}
	}

	.more {
		margin: 50px 0;

		.more-btn {
			width: 100px;
			height: 40px;
			line-height: 40px;
			text-align: center;
			color: #ADADAD;
			border: 1px solid #ADADAD;
			border-radius: 20px;
			margin: 0 auto;
			cursor: pointer;

			&:hover {
				color: #8fd0cc;
				border: 1px dashed #8fd0cc;
			}
		}
	}

	/******/
	@media (max-width: 800px) {
		.top-feature {
			display: none;
		}

		.site-main {
			padding-top: 40px;
		}
		.site-content{
			padding: 5px 4% 0 4%;
		}
		.site-content {
			.notify {
				margin: 30px 0 0 0;
			}

			.search-result {
				margin-bottom: 20px;
				font-size: 16px;
			}
		}
	}

	/******/
</style>
