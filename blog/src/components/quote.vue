<template>
	<div :style="[getStyle]">
		<div class="arrow-left-ar"></div>
		<div class="notice">
			<slot></slot>
		</div>
	</div>

</template>

<script>
	export default {
		name: "quote",
		props:{
			customStyle: {
				type: Object,
				default() {
					return {};
				}
			},
		},
		computed:{
			getStyle() {
				let style = {};
				style = Object.assign(style, this.customStyle);
				return style;
			},
		}
	}
</script>

<style scoped>
	.quote {
		border-left: 3px solid #ff6d6d;
		background-color: #FBFBFB;
		padding: 15px 20px;
		border-radius: 3px;
	}

	.arrow-left-ar {
		animation: main 1s;
		border-left-color: transparent;
		border-bottom-color: rgb(61, 66, 68);
		width: 0;
		height: 0;
		display: block;
		float: left;
		margin-top: 10px;
		border-left: 20px solid transparent;
		border-bottom: 20px solid #F5F5F5;
		
	}


	.notice {
		animation: main 1s;
		display: flex;
		border-left: 3px solid #F5F5F5;
		color: rgb(217, 210, 199);
		background-image: initial;
		background-color: rgb(31, 34, 36);
		padding: 15px;
		font-family: miranafont, "Hiragino Sans GB", STXihei, "Microsoft YaHei", SimSun, sans-serif;
		color: #505050;
		position: relative;
		margin-top: 5px;
		margin-left: 20px;
		background: #F5F5F5;
		border-radius: 5px;
	}

</style>
