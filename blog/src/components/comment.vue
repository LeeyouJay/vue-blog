<template>
    <div class="comment" :id="`comment${comment.id}`">
        <div class="comment-head">
            <div class="user-avatar"><img :src="comment.fromUserAvatar" alt=""></div>
            <div class="head-right">
                <section-title>
                    <div style="display: flex;align-items: center;justify-content: space-between;">
                        <div>
                            <span :class="[comment.fromUserId ? 'owner-name' : 'user-name']">{{comment.fromUserName}}</span>
                            <span class="to-user" v-if="comment.parentId"><span style="margin: 0 5px;">@</span><span :class="[comment.toUserId ? 'owner-name' : 'user-name']">{{comment.toUserName}}</span></span>
							<div class="comment-time">{{updateTime}}</div>
						</div>
                        <div>
                            <span class="system-info">{{comment.browser}} ,{{comment.systemName}}</span>
                            <span v-if="showReply" @click.stop="oppenComment(comment)" style="cursor: pointer;">回复</span>
                        </div>
                    </div>
                </section-title>
            </div>
        </div>
        <div class="comment-body">
            <div class="content-text">
                <p v-html="comment.content"></p>
            </div>
			<comment-editor :show="showReport" @onSubmit="submitReply"></comment-editor>
            <slot></slot>
        </div>
    </div>
</template>

<script>
    import sectionTitle from '@/components/section-title'
	import commentEditor from '@/components/comment-editor.vue'
    export default {
        name: "comment",
        props: {
          comment: {
              type: Object
          },
		  showReport:{
			  type:Boolean,
			  default:false
		  },
		  showReply:{
			  type:Boolean,
			  default:true
		  }
        },
		computed:{
			updateTime(){
				var d =  this.$moment(new Date()).diff(this.$moment(this.comment.createTime), 'days')
				if(d>90)
				return this.comment.createTime
				else
					return this.$moment(this.comment.createTime).fromNow()
			}
		},
        components: {
            sectionTitle,
			commentEditor
        },
        methods: {
            reply(id){
                const ref = `comment${id}`
            },
            submitReply(v){
				this.$emit('reply',v);
            },
			oppenComment(comment){
				this.$emit('oppenComment',comment);
			}
        }
    }
</script>

<style scoped lang="less">
    .comment{
		animation: main 0.7s;
        margin: 20px 0;
    }
    .comment-head{
        display: flex;
        .head-right{
            flex: 1;
        }
        .user-name{
            color: #8fd0cc;
        }
		.owner-name {
			color: #ff6d6d;
		}
    }
    .comment-body{
        padding-left: 80px;
        .content-text{
            /*padding-bottom: 30px;*/
            margin-bottom: 30px;
            font-size: 14px;
            color: #9c9c9c;
            line-height: 1.3rem;
        }
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
        img{
            width: 100%;
            height: 100%;
            border-radius: 100%;
        }
    }
    @media (max-width: 600px){
        .comment-body{
            padding-left: 15px;
            .content-text{
                margin-top: 10px;
            }
        }
		.system-info {
			display: none;
		}
        .user-avatar{
            margin-right: 10px;
        }
    }
</style>
