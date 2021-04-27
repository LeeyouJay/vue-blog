<template>
    <ul class="menus">
        <li v-for="item in menus">
			<div class="text" @click="turnTo(item.id)">{{item.title}}</div>
            <ul v-if="item[childLabel]" class="child">
                <menu-tree :menus="item[childLabel]" :child-label="childLabel"></menu-tree>
            </ul>
        </li>
    </ul>
</template>

<script>
    export default {
        name: "menuTree",
        props: {
            menus: {
                type: [Object,Array],
                required: true
            },
            childLabel: {
                type: String,
                default: 'child'
            }
        },
        data() {
            return{
            }
        },
        methods: {
			turnTo(id){
				document.querySelector("#"+id+"").scrollIntoView({
					block: 'center',
					inline: 'start',
					behavior: 'smooth'
				});
			}
        }
    }
</script>

<style scoped lang="less">
    .menus{
        line-height: 1.7em;
		cursor: pointer;
    }
	.text:hover{
		color: #ff6d6d;
	}
    .child{
        padding-left: 15px;
		cursor: pointer;
    }
</style>
