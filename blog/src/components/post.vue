<template>
	<article class="post post-list">
		<div class="post-entry">
			<div class="feature">
				<router-link :to="`/article/${post.id}`">
					<img :src="banner" />
				</router-link>
			</div>
			<h1 class="entry-title">
				<router-link :to="`/article/${post.id}`"><span v-if="post.top"
						style="color:#ff6d6d;font-weight:600">[置顶] </span>{{strTitle(post.title)}}</router-link>
			</h1>
			<div class="p-time">
				<i class="iconfont icon-shijian"></i>
				<span>发布于 {{post.createTime.substr(0,10)}}</span>
				<i v-if="post.hot" class="iconfont icon-icon-test" style="margin-left: 5px;color: #ff6d6d;"></i>
			</div>
			<p class="summary">{{summary(post.summary)}}</p>
			<footer class="entry-footer">
				<div class="post-more">
					<div  style="display: inline;float: left;color: #989898;font-size: 14px;" >
						by&emsp;{{post.author}}
					</div>
					<div style="display: inline;float: right;" :style="post.showType == '个人' ? 'color:#d81e06;' :''">
						<router-link :to="`/article/${post.id}`"><i class="iconfont icon-zhifeiji"
								style="font-size: 25px;"></i></router-link>
					</div>
				</div>
				<div class="info-meta">
					<div class="comnum">
						<span><i class="iconfont icon-pinglun"></i>{{post.commentsCount}}条评论</span>
					</div>
					<div class="views">
						<span><i class="iconfont icon-yanjing"></i>{{post.viewsCount}} 热度</span>
					</div>
				</div>
			</footer>
		</div>
		<hr />
	</article>
</template>

<script>
	export default {
		name: "post",
		props: {
			post: {
				type: Object
			},
			banner: {
				type: String,
				default: 'https://s1.ax1x.com/2020/05/14/YD4FR1.jpg'
			}
		},
		methods:{
			summary(text){
				var len = text.length;  
				   if(len >= 100)
						return text+"..."
					else
						return text
			},
			strTitle(text){
				if(text.length>17)
					return text.substr(0,17)+"..."
				else
					return text
			}
		}
	}
</script>

<style scoped lang="less">
	.post {
		margin: 0 0 4% 0;
		position: relative;
		animation: main 1s;
	}

	.post-entry {
		.feature {
			position: absolute;
			margin-top: 10px;
			
			img {
				width: 100px;
				height: 100px;
				object-fit: cover;
				border-radius: 50%;
				padding: 2px;
				border: 1px solid #DADADA;
				position: relative;
				transition: all 0.2s linear;
				overflow: hidden;

				&:hover {
					transform: scale(1.1, 1.1);
					filter: contrast(130%);
				}
			}
		}

		.entry-title {
			font-size: 21px;
			font-weight: 600;
			line-height: 50px;
			margin: 0 0 0 17%;
			position: relative;
			z-index: 1;
			white-space: nowrap;
			overflow: hidden;
			text-overflow: ellipsis;
			width: 70%;
			color: #737373;

			& a:hover {
				color: #00ba9a;
			}
		}

		.p-time {
			position: absolute;
			right: 0;
			top: 16px;
			font-size: 12px;
			color: #989898;
			letter-spacing: 1px;
			font-family: din, 'Hiragino Sans GB', 'Microsoft Yahei', Arial, sans-serif;
			display: flex;
			align-items: center;
			 i {
				margin-right: 5px;
			}
		}

		p.summary {
			min-height: 60px;
			margin: 0 0 0 17%;
			font-size: 15px;
			color: #6f6f6f;
			letter-spacing: 1px;
			line-height: 30px;
		}

		footer.entry-footer {
			margin: 0 0 0 17%;
			list-style: none;
			display: flow-root;
			.post-more {
				margin-top: 10px;
				text-align: right;
				color: #A0DAD0;

				a:hover {
					color: #00ba9a;
				}
			}

			.info-meta {
				margin-top: 10px;
				font-family: din, 'Hiragino Sans GB', 'Microsoft Yahei', Arial, sans-serif;
				position: absolute;
				top: 20px;
				opacity: 0;
				padding-top: 8px;
				border-top: 1px solid #ddd;
				-webkit-transform: translate3d(-150px, 0, 0);
				transform: translate3d(-150px, 0, 0);
				visibility: hidden;
				transition: .7s all ease;
				-webkit-transition: .6s all ease;
				-moz-transition: .6s all linear;
				-o-transition: .6s all ease;
				-ms-transition: .6s all ease;

				span,
				a {
					color: #B3B3B3;
					font-size: 12px;
				}

				i {
					margin-top: 3px;
					margin-right: 10px;
					float: left
				}
			}

			.comnum {
				float: left;
			}
		}

	}

	.post-entry:hover footer.entry-footer .info-meta {
		-webkit-transform: translate3d(-230px, 0, 0);
		transform: translate3d(-230px, 0, 0);
		opacity: 1;
		visibility: visible;
	}

	.post hr {
		width: 30%;
		height: 1px;
		margin: 0 auto;
		border: 0;
		background: #EFEFEF;
	}

	/*******/
	@media (max-width: 1060px) {
		.info-meta {
			display: none;
		}
	}

	@media (max-width: 800px) {
		.post-entry {
			.feature img {
				width: 50px;
				height: 50px;
			}

			.entry-title {
				font-size: 16px;
				line-height: 30px;
				margin: 0 0 0 65px;
			}

			.p-time {
				position: relative;
				margin: -15px 0 0 65px;
			}

			p.summary {
				margin: 20px 0 0 65px;
				font-size: 14px;
				height: 30px;
				overflow: hidden;
			}

			.post-more {
				display: none;
			}
		}

		.post-list hr {
			margin-top: 20px;
		}
	}

	/******/
</style>
