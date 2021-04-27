<template>
	<div class="archive">
		<div class="site-content">
			<div class="archive-header">
				<span>文章归档</span>
			</div>
			<el-collapse accordion>
				<el-collapse-item class="col" v-for="(item,index) in archives " :key="index">
					<template slot="title">
						<div class="time-title">
							<div>
								<i class="ar-time iconfont icon-shijian "></i>
							</div>
							<span>{{item.date}}</span>
						</div>
					</template>
					<div class="ar" v-for="val in item.articles " :key="val.id">
						<span class="ar-circle" :style="val.showType == '个人' ? 'background:#d81e06;' :''"></span>
						<div class="ar-line">
							<router-link :to="`/article/${val.id}`">
								<quote :customStyle="customStyle">
									<div class="article-title">{{val.title}}</div>
									<div class="time">
										<i class="iconfont icon-shijian"></i>{{val.createTime.substr(5,5)}}
									</div>
								</quote>
							</router-link>
						</div>
					</div>
				</el-collapse-item>
			</el-collapse>
		</div>
	</div>
</template>

<script>
	import Quote from "@/components/quote";
	import {
		getArchivesAPI
	} from '../api'
	import {
		removeToken
	} from '@/utils/cookie.js'
	export default {
		name: "Friend",
		data() {
			return {
				customStyle: {
					fontSize: '16px',
				},
				archives: []
			}
		},
		components: {
			Quote
		},
		methods: {
			getArticleList() {
				getArchivesAPI().then(res => {
					if (res.code === 401) {
						removeToken()
						this.$store.dispatch('setNeedInfo',true)
					}else
						this.$store.dispatch('setNeedInfo',false)
					let map = new Map(Object.entries(res.data.archives))
					map.forEach((value, key) => {
						this.archives.push({
							date: key,
							articles: value
						})
					})
				})
			},
		},
		mounted() {
			//this.getWebSiteInfo();
			//this.getArticleList()
		},
		created() {
			this.getArticleList()
		}
	}
</script>

<style scoped lang="less">
	.col {
		margin: 20px 0;
	}

	.archive {
		padding-top: 80px;

		.archive-header {
			margin-top: 80px;
			margin-bottom: 20px;
			text-align: center;

			&>span {
				font-size: 24px;
			}
		}
	}

	.time-title {
		display: inherit;

		&>span {
			padding-left: 20px;
			font-size: 20px;
			font-family: microsoft yahei;
			font-style: italic;
			color: #A0DAD0;
		}
	}

	.time {
		color: #CACACA;
		font-size: 16px;
	}

	.ar {
		padding-left: 15px;

		.ar-circle {
			animation: main 1s;
			height: 10px;
			width: 10px;
			background: #A0DAD0;
			display: inline-block;
			position: absolute;
			margin-top: 30px;
			border-radius: 100px;
		}

		.ar-circle:first-child {
			margin-top: 26px;
		}
	}

	.article-title {
		width: 90%;
	}


	.ar-time {
		color: #FFFFFF;
		padding: 10px;
		border-radius: 100px;
		background: #A0DAD0;
	}

	.ar-line {
		border-left: 1px solid #f5efef;
		margin-left: 4px;
		padding-left: 8px;
	}
	.el-collapse {
		border-top: 0px solid #EBEEF5;
		border-bottom: 0px solid #EBEEF5;
		padding-right: 20px;
	}
	
	@media (max-width: 800px){
		.time{
			display: none;
		}
		.archive-header {
			margin-top: 0px !important;
		}
	}
</style>
