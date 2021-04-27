<template>
	<div id="loading" v-show="loading">
		<div class="container">
			<div class="point"></div>
			<div class="point"></div>
			<div class="point"></div>
			<div class="point"></div>
			<div class="point"></div>
		</div>
	</div>
</template>

<script>
	export default {
		computed: {
			loading() {
				return this.$store.state.loading
			}
		},
		watch: {
			loading: {
				immediate: true,
				handler: (n, o) => {
					let htmlBodyElement = document.querySelector('body');
					htmlBodyElement.style.overflowY = n ? 'hidden' : 'auto';
				}
			}
		}
	}
</script>

<style lang="less" scoped>
	#loading {
		position: fixed;
		z-index: 999;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background-color: #F9F9F9;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.container {
		box-sizing: border-box;
		display: flex;
		flex: 0 1 auto;
		flex-grow: 1;
		flex-shrink: 0;
		flex-basis: 25%;
		max-width: 25%;
		height: 200px;
		align-items: center;
		justify-content: center;
	}

	.container .point {
		width: 1em;
		height: 1em;
		border-radius: 50%;
		/* 实现圆形 */
		background: #fff;
		margin: 1em;
		user-select: none;
		position: relative;
	}

	.container .point::before {
		position: absolute;
		content: '';
		width: 100%;
		height: 100%;
		background: inherit;
		/* 从父元素继承背景颜色 */
		border-radius: inherit;
		/* 从父元素继承圆角 */
		animation: wave 0.8s ease-out infinite;
		/* 对before元素定义动画，完成时间2s, 采用ease-out过渡，重复循环 */
	}

	@keyframes wave {

		50%,
		70% {
			transform: scale(2.5);
		}

		80%,
		100% {
			opacity: 0;
		}
	}

	/* 设置背景颜色,自行选取五个颜色即可 */
	.container .point:nth-child(1) {
		background: #f2c6c0;
	}

	.container .point:nth-child(2) {
		background: #f49c91;
	}

	.container .point:nth-child(3) {
		background: #f06a59;
	}

	.container .point:nth-child(4) {
		background: #e12d15;
	}

	.container .point:nth-child(5) {
		background: #d81e06;
	}

	/*
	* 现在动画都是同时出现的，而我们需要的是一种逐渐出现的效果
	* 所以需要对每个元素的动画加一个延迟
	* 最后一个与第一个之间的间隔太短
	* 所以需要把整体的动画时间延长一下
	*/
	.container .point:nth-child(1)::before {
		animation-delay: 0s;
		/* 第一个不需要延迟 */
	}

	.container .point:nth-child(2)::before {
		animation-delay: 0.1s;
		/* 依次延迟0.2s */
	}

	.container .point:nth-child(3)::before {
		animation-delay: 0.2s;
	}

	.container .point:nth-child(4)::before {
		animation-delay: 0.3s;
	}

	.container .point:nth-child(5)::before {
		animation-delay: 0.4s;
	}
</style>
