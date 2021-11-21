'use strict'
module.exports = {
	//publicPath: './', // 部署应用包时的基本 url
	outputDir: 'dist', // build 构建文件
	productionSourceMap: false,
	devServer: {
		port: 8088,
		open: true
	},
	lintOnSave: false,
}
