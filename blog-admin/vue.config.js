module.exports = {
	//assetsDir: 'static',
	outputDir: 'dist',
	productionSourceMap: false,
	publicPath: '/manage/',
	devServer: {
		port: 8100,
		open: true,
		//disableHostCheck: true,//解决Invalid Host header问题
		overlay: { // 是否在浏览器上显示编译的 errors 或 warnings
			warnings: false,
			errors: true
		},
		// proxy: {
		// 	'/getIpName': {
		// 		target: 'https://192.168.199.155:8900/getIp', 
		// 		ws: true,
		// 		changOrigin: true, //允许跨域
		// 		pathRewrite: {
		// 			'^/getIpName': '' //请求的时候使用这个api就可以
		// 		}
		// 	},
		// },
	}
}
