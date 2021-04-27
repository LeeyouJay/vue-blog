<template>
	<div style="width: 100%; height: 35px; position: relative; display: inline-flex;">
	<el-button type="text" class="btn-con" @click="handleMove(240)">
	  <i class="icon el-icon-arrow-left" />
	</el-button>
    <div class="tags-views" ref="tagsViews" @DOMMouseScroll="handlescroll" @mousewheel="handlescroll"  v-if="showTags">
        <div class="tags-cont" ref="tagsCont" :style="{left: tagsContLeft + 'px',width: wholeWidth+'px'}">
            <div class="tags-li" v-for="(item,index) in tagsList" ref="tagsItem" :class="{'active': isActive(item.path)}" :key="index">
                <router-link :to="item.path" class="tags-li-title">
                    {{item.title}}
                </router-link>
                <span class="tags-li-icon"  v-if="isShow" @click="closeTags(index)"><i class="el-icon-close"></i></span>
            </div>
        </div>
    </div>
	<el-button type="text" class="btn-con" @click="handleMove(-240)">
	  <i class="icon el-icon-arrow-right" />
	</el-button>
	<div class="tags-close-box">
	    <el-dropdown @command="handleTags">
	        <el-button size="mini" type="primary">
	            标签选项<i class="el-icon-arrow-down el-icon--right"></i>
	        </el-button>
	        <el-dropdown-menu size="small" slot="dropdown">
	            <el-dropdown-item command="other">关闭其他</el-dropdown-item>
	            <el-dropdown-item command="all">关闭所有</el-dropdown-item>
	        </el-dropdown-menu>
	    </el-dropdown>
	</div>
	</div>
</template>

<script>
    export default {
        data() {
            return {
				isShow:true,
                tagsList: [],
				tagsContLeft:0,
				contPadding: 0,
				wholeWidth:200
            }
        },
        methods: {
            isActive(path) {
                return path === this.$route.fullPath;
            },
            // 关闭单个标签
            closeTags(index) {
                const delItem = this.tagsList.splice(index, 1)[0];
                const item = this.tagsList[index] ? this.tagsList[index] : this.tagsList[index - 1];
                if (item) {
                    delItem.path === this.$route.fullPath && this.$router.push(item.path);
					this.isShow = this.tagsList.length === 1 ? false :  true
                }else{
					this.isShow = false
					this.$router.push('/');
                }
            },
            // 关闭全部标签
            closeAll(){
				if(this.tagsList.length == 1 && this.tagsList[0].title == '系统首页') return
				this.$nextTick(()=>{
					this.tagsList = [{
						name: "dashboard",
						path: "/dashboard",
						title: "系统首页"
					}];
					this.isShow = false;
				})
				
                this.$router.push('/');
            },
            // 关闭其他标签
            closeOther(){
                const curItem = this.tagsList.filter(item => {
                    return item.path === this.$route.fullPath;
                })
                this.tagsList = curItem;
				this.isShow = false;
            },
            // 设置标签
            setTags(route){
                const isExist = this.tagsList.some(item => {
                    return item.path === route.fullPath;
                })
                if(!isExist){
                    this.tagsList.push({
                        title: route.meta.title,
                        path: route.fullPath,
                        name: route.matched[1].components.default.name
                    })
                }
				this.isShow = this.tagsList.length>1;
                //this.$bus.$emit('tags-cont', this.tagsList);
            },
            handleTags(command){
                command === 'other' ? this.closeOther() : this.closeAll();
            },
			getTagElement(route) {
				const tagsItemArr = this.$refs.tagsItem
			  this.$nextTick(() => {
				const index = tagsItemArr.findIndex(item => {
					return item.className === "tags-li active"
					})
				const tag = tagsItemArr[index]
				this.moveToCurrentTag(tag)
			  })
			this.wholeWidth =  tagsItemArr.reduce((accumulator, cur) => accumulator + cur.offsetWidth, 300);
			},
			moveToCurrentTag(tag) {
			  const viewWidth = this.$refs.tagsViews.offsetWidth
			  const contWidth = this.$refs.tagsCont.offsetWidth
			  if (contWidth < viewWidth) {
			    this.tagsContLeft = 0
			  } else if (tag.offsetLeft < -this.tagsContLeft) {
			    // 标签在可视区域左侧
			    this.tagsContLeft = -tag.offsetLeft + this.contPadding
			  } else if (tag.offsetLeft > -this.tagsContLeft && tag.offsetLeft + tag.offsetWidth < -this.tagsContLeft + viewWidth) {
			    // 标签在可视区域
			    this.tagsContLeft = Math.min(0, viewWidth - tag.offsetWidth - tag.offsetLeft - this.contPadding)
			  } else {
			    // 标签在可视区域右侧
			    this.tagsContLeft = -(tag.offsetLeft - (viewWidth - this.contPadding - tag.offsetWidth))
			  }
			},
			handlescroll(e) {
			  const type = e.type
			  let distance = 0
			  // mousewheel非火狐浏览器鼠标滚动事件; DOMMouseScroll火狐浏览器鼠标滚动事件
			  if (type === 'mousewheel' || type === 'DOMMouseScroll') {
			    // mousewheel 事件中的 event.wheelDelta 属性值：若滚轮是向上滚动，返回值为正，反之为负；且返回的值，均为 120 的倍数，即：幅度大小 = 返回的值 / 120
			    // DOMMouseScroll 事件中的 event.detail 属性值：返回的值，与 event.wheelDelta 正好相反，即滚轮是向上滚动，返回值为负，反之为正；返回的值，均为 3 的倍数，即：幅度大小 = 返回的值 / 3
			    distance = (e.wheelDelta) ? e.wheelDelta : -(e.detail || 0) * 40
			  }
			  this.handleMove(distance)
			},
			handleMove(offset) {
			  const viewWidth = this.$refs.tagsViews.offsetWidth
			  const contWidth = this.$refs.tagsCont.offsetWidth
			  if (offset > 0) {
			    this.tagsContLeft = Math.min(0, this.tagsContLeft + offset)
			  } else {
			    if (viewWidth < contWidth) {
			      if (this.tagsContLeft < -(contWidth - viewWidth)) {
			        // this.tagsContLeft = this.tagsContLeft
			      } else {
			        this.tagsContLeft = Math.max(this.tagsContLeft + offset, viewWidth - contWidth)
			      }
			    } else {
			      this.tagsContLeft = 0
			    }
			  }
			},
        },
        computed: {
            showTags() {
                return this.tagsList.length > 0;
            }
        },
        watch:{
            $route(newValue, oldValue){
                this.setTags(newValue);
				this.getTagElement(newValue);
            }
        },
        created(){
            this.setTags(this.$route);
            // 监听关闭当前页面的标签页
            this.$bus.$on('close_current_tags', () => {
				console.log('监听关闭当前页面的标签页')
                for (let i = 0, len = this.tagsList.length; i < len; i++) {
                    const item = this.tagsList[i];
                    if(item.path === this.$route.fullPath){
                        if(i < len - 1){
                            this.$router.push(this.tagsList[i+1].path);
                        }else if(i > 0){
                            this.$router.push(this.tagsList[i-1].path);
                        }else{
                            this.$router.push('/');
                        }
                        this.tagsList.splice(i, 1);
                        break;
                    }
                }
            })
        }
    }

</script>


<style >
    .tags {
        position: relative;
        height: 35px;
        overflow: hidden;
        background: #fff;
        padding-right: 120px;
        box-shadow: 0 5px 10px #ddd;
    }

    .tags ul {
        box-sizing: border-box;
        width: 100%;
        height: 100%;
    }

    .tags-li {
        float: left;
        margin: 3px 5px 2px 3px;
        border-radius: 3px;
        font-size: 12px;
        overflow: hidden;
        cursor: pointer;
        height: 25px;
        line-height: 25px;
        border: 1px solid #e9eaec;
        background: #fff;
        padding: 0 5px 0 12px;
        vertical-align: middle;
        color: #666;
        -webkit-transition: all .3s ease-in;
        -moz-transition: all .3s ease-in;
        transition: all .3s ease-in;
    }

    .tags-li:not(.active):hover {
        background: #f8f8f8;
    }

    .tags-li.active {
        color: #fff;
    }

    .tags-li-title {
        float: left;
        max-width: 110px;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        margin-right: 5px;
        color: #666;
    }

    .tags-li.active .tags-li-title {
        color: #fff;
    }

    .tags-close-box {
        position: absolute;
        right: 14px;
        top: 0;
        box-sizing: border-box;
        padding-top: 3px;
        text-align: center;
        width: 110px;
        height: 30px;
        background: #fff;
        /* box-shadow: -3px 0 15px 3px rgba(0, 0, 0, .1); */
        z-index: 10;
    }
	.tags-views {
		height: 100%;
		position: relative;
		width: calc(100% - 195px);
		background: #f0f0f0;
		box-shadow: inset 0 0 3px 2px #6464641a;
		overflow: hidden;
	}
	.tags-cont {
	  position: absolute;
	  padding: 0 4px;
	  overflow: visible;
	  white-space: nowrap;
	  transition: left .5s ease;
	}
	.btn-con {
	  float: left;
	  width: 35px;
	  height: 35px;
	  padding: 8px 7px 8px 3px;
	  border-top: solid 1px #f0f0f0;
	  border-bottom: solid 1px #f0f0f0;
	  box-sizing: border-box;
	}
</style>
