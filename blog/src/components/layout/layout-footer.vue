<template>
	<div id="layout-footer">
		<div class="footer-main">
			<div class="footer-item">
				<span v-for="item in socials" :key="item.id" style="margin-right: 10px;">
					<el-tooltip  effect="light"  :content="item.tips" placement="top-start">
						<a target="_blank" class="out-link" :href="item.href">
							<i class="iconfont" :class="item.icon"></i>{{item.title}}
						</a>
					</el-tooltip>
				</span>
			</div>
			<div class="footer-item">
				本站已苟活 {{runTimeInterval}}
			</div>
			<div class="footer-item">
				Copyright © 2021 by <a target="_blank" class="out-link" href="http://www.chenyi.ink">chenyi.ink</a><a
					target="_blank" class="out-link" href="https://beian.miit.gov.cn/"> 桂ICP备20006107号</a>
			</div>
		</div>
	</div>
</template>

<script>
	export default {
		name: "layout-footer",
		data() {
			return {
				socials: [{
						id: 1,
						title: 'QQ',
						tips:'752279593',
						icon: 'icon-qq',
						href: 'http://wpa.qq.com/msgrd?v=3&uin=752279593&site=qq&menu=yes'
					},
					{
						id: 2,
						title: 'GitHub',
						tips:'https://github.com/LeeyouJay',
						icon: 'icon-github',
						href: 'https://github.com/LeeyouJay'
					},
					{
						id: 3,
						title: '后台管理',
						tips:'https:chenyi.ink/manage',
						icon: 'icon-guanli',
						href: 'https:chenyi.ink/manage'
					},
				]
			}
		},
		computed: {
			runTimeInterval() {
				return this.$store.state.runTimeInterval;
			}
		},
		methods: {

		},
		created() {
			this.$store.dispatch('initComputeTime');
		}
	}
</script>

<style scoped lang="less">
	#layout-footer {
		padding: 2%;
		border-top: 1px solid #F7F7F7;
		font-size: 13px;
		color: #9c9c9c;

		a.out-link:hover {
			color: #ff6d6d;
		}

		.footer-main {
			max-width: 800px;
			margin: 0 auto 20px auto;
			text-align: center;
			//display: flex;
			justify-content: space-around;
			align-items: flex-start;

			.footer-item {
				flex: 1;
				margin-bottom: 10px;

				&>div:not(:last-child) {
					margin-bottom: 10px;
				}

				i {
					margin-right: 10px;
				}
			}
		}
	}

	/*****/
	@media (max-width: 800px) {
		#layout-footer {

			.footer-main .footer-item>span:nth-child(3) {
				display: none;
			}
		}
	}

	@media (max-width: 600px) {
		#layout-footer {
			.footer-main {
				display: block;

				.footer-item {
					justify-content: space-around;
					align-items: center;
					flex-wrap: wrap;

					&>div {
						margin-bottom: 10px;
					}
				}
			}
		}
	}

	/*****/
</style>
