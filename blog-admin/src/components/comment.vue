<template>
    <div class="comment" :id="`comment${comment.id}`">
        <div class="comment-head">
            <div class="user-avatar"><img :src="comment.fromUserAvatar" alt=""></div>
            <div class="head-right">
                    <div style="display: flex;align-items: center;justify-content: space-between;">
                        <div>
							<el-popover v-if="comment.fromUserEmail"
							    placement="top-start"
							    width="200"
							    trigger="hover"
							    :content="comment.fromUserEmail">
								<span slot="reference" :class="[comment.fromUserId ? 'owner-name' : 'user-name']">{{comment.fromUserName}}</span>
							  </el-popover>
							  <span v-else :class="[comment.fromUserId ? 'owner-name' : 'user-name']">{{comment.fromUserName}}</span>
                            <span class="to-user" v-if="comment.parentId">
								<span style="margin: 0 5px;">@</span>
								<span  :class="[comment.toUserId ? 'owner-name' : 'user-name']">{{comment.toUserName}}</span>
							</span>
							<div class="comment-time">{{comment.createTime}}</div>
						</div>
                        <div>
                            <span class="system-info">{{comment.browser}} ,{{comment.systemName}}</span>
							<el-button type="text" icon="el-icon-edit" @click="oppenComment(comment)" >回复</el-button>
							<el-button type="text" style="color: #F56C6C;" v-if="!comment.reply.length" @click="delComment(comment)" icon="el-icon-delete" >删除</el-button>
                        </div>
                    </div>
            </div>
        </div>
        <div class="comment-body">
            <div class="content-text">
                <p v-html="comment.content"></p>
            </div>
			<comment-message-editor ref="editor" v-if="comment.oppen" @submit="submitReply"></comment-message-editor>
            <slot></slot>
        </div>
    </div>
</template>

<script>
	import commentMessageEditor from './message-editor'
    export default {
        name: "comment",
        props: {
          comment: {
              type: Object
          },
		  showReport:{
			  type:Boolean,
			  default:true
		  },
		  showReply:{
			  type:Boolean,
			  default:true
		  }
        },
		computed:{
			
		},
        components: {
			commentMessageEditor
        },
        methods: {
            reply(id){
                const ref = `comment${id}`
            },
            submitReply(v){
				this.$emit('reply',{content:v});
            },
			oppenComment(comment){
				this.$emit('oppenComment',comment);
			},
			delComment(comment){
				this.$emit('delComment',comment);
			},
			clearContent() {
				this.$refs.editor.clearContent()
			}
        }
    }
</script>

<style scoped >
    .comment{
		animation: main 0.7s;
        margin: 20px 0;
    }
    .comment-head{
        display: flex;
    }
	.head-right{
	    flex: 1;
	}
	.user-name{
	    color: #8fd0cc;
	}
	.owner-name {
		color: #ff6d6d;
	}
    .comment-body{
        padding-left: 80px;
    }
	.content-text{
	    /*padding-bottom: 30px;*/
	    margin-bottom: 30px;
	    font-size: 14px;
	    color: #9c9c9c;
	    line-height: 1.3rem;
	}
	.comment-time {
		font-size: 10px;
		margin-top: 7px;
		color: rgb(156, 156, 156);
	}
	.system-info{
		font-size: 10px;
		margin-right: 20px;
		color: rgb(156, 156, 156);
	}
    .user-avatar{
        width: 50px;
        height: 50px;
        margin-right: 30px;
    }
	.user-avatar>img{
	    width: 100%;
	    height: 100%;
	    border-radius: 100%;
	}
</style>
