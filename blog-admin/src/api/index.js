/** 
 * api接口的统一出口
 */
// 登入模块接口
import login from './login.js';
import system from './system.js';
import blog from './blog.js'
// 导出接口
export default {
    login,
	system,
	blog
}

