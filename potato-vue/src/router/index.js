import { createRouter, createWebHistory } from 'vue-router'
import FrontHome from "@/view/front/FrontHome.vue";
import FrontDiary from "@/view/front/FrontDiary.vue";
import FrontNote from "@/view/front/FrontNote.vue";
import FrontLove from "@/view/front/FrontLove.vue";
import FrontTravel from "@/view/front/FrontTravel.vue";
import FrontCircle from "@/view/front/FrontCircle.vue";
import FrontWelcome from "@/view/front/FrontWelcome.vue";
import InvalidPage from "@/components/InvalidPage.vue";
import FrontMessage from "@/view/front/FrontMessage.vue";
import FrontConcern from "@/view/front/FrontConcern.vue";
import FrontBlog from "@/view/front/FrontBlog.vue";
import FrontLayout from "@/components/FrontLayout.vue";
import BackHome from "@/view/back/BackHome.vue";
import BackBlog from "@/view/back/BackBlog.vue";
import BackLayout from "@/components/BackLayout.vue";
import BackNote from "@/view/back/BackNote.vue";
import BackCircle from "@/view/back/BackCircle.vue";
import BackLove from "@/view/back/BackLove.vue";
import BackTravel from "@/view/back/BackTravel.vue";
import BackMessage from "@/view/back/BackMessage.vue";
import BackConcern from "@/view/back/BackConcern.vue";
import BackDiary from "@/view/back/BackDiary.vue";
import FrontMusic from "@/view/front/FrontMusic.vue";
import BackMusic from "@/view/back/BackMusic.vue";
import BackApply from "@/view/back/BackApply.vue";
import BackBgImage from "@/view/back/BackBgImage.vue";
import FrontDetail from "@/view/front/FrontDetail.vue";
import FrontLogin from "@/components/FrontLogin.vue";
import FrontTools from "@/view/front/FrontTools.vue";
import RandomMeals from "@/view/front/toolCatalogue/RandomMeals.vue";


const commonRoutes = [
    {
        path: '/',
        redirect: '/welcome'
    },
    {
        path: '/login',
        component: FrontLogin,
        name: 'Login',
        meta: { title: '登录'}
    },
    {
        path: '/welcome',
        component: FrontWelcome,
        name: 'FrontWelcome',
        meta: { title: '欢迎页' }
    },
    {
        path: '/music',
        component: FrontMusic,
        name: 'FrontMusic',
        meta: { title: '音乐界面' }
    },
    {
        path: '/concern',
        component: FrontConcern,
        name: 'FrontConcern',
        meta: { title: '我的界面'}
    },
    {
        path: "/404",
        component: InvalidPage,
        name: 'InvalidPage',
        meta: { title: '页面不存在' }
    },
]
const frontRoutes = [
    {
        path: '/front',
        component: FrontLayout,
        redirect: '/front/home',
        meta: { title: '前台布局' },
        children: [
            { path: "home", component: FrontHome},
            { path: "blog", component: FrontBlog, name: 'FrontBlog', meta: { title: '博客列表' }},
            {  path: '/detail/:type/:id', component: FrontDetail, name: 'FrontDetail', meta: { title: '详情页面' }},
            { path: "diary", component: FrontDiary, name: 'FrontDiary', meta: { title: '日记列表' }},
            { path: "note", component: FrontNote, name: 'FrontNote', meta: { title: '笔记列表' }},
            { path: "circle", component: FrontCircle, name: 'FrontCircle', meta: { title: '朋友圈'}},
            { path: "love", component: FrontLove, name: 'FrontLove', meta: { title: '爱情记录'}},
            { path: "travel", component: FrontTravel, name: 'FrontTravel', meta: { title: '旅游相册'}},
            { path: "message", component: FrontMessage, name: 'FrontMessage', meta: { title: '反馈消息'}},
            { path: "tools", component: FrontTools, name: 'FrontTools', meta: { title: '小工具'}},
            // 工具类下的小工具
            { path: "randomMeals", component: RandomMeals, name: 'RandomMeals', meta: { title: '随机选餐'}},
        ]
    },

]
const backRoutes = [
    {
        path: '/back',
        component: BackLayout,
        redirect: '/back/home',
        meta: { title: '后台布局', requiresAuth: true },
        children: [
            { path: "home", component: BackHome},
            { path: "blog", component: BackBlog},
            { path: "diary", component: BackDiary},
            { path: "note", component: BackNote},
            { path: "circle", component: BackCircle},
            { path: "love", component: BackLove},
            { path: "travel", component: BackTravel},
            { path: "message", component: BackMessage},
            { path: "concern", component: BackConcern},
            { path: "apply", component: BackApply},
            { path: "music", component: BackMusic},
            { path: "bgImage", component: BackBgImage},
        ]
    }
]

const routes = [
    ...commonRoutes,
    ...frontRoutes,
    ...backRoutes,
    {
        path: '/:pathMatch(.*)*', // 通配符匹配所有未定义路由
        redirect: '/404',
        name: 'NotFound'
    }
]
const router  = createRouter({
    history: createWebHistory(),
    routes,
    scrollBehavior(to, from, savedPosition) {
        return savedPosition ? savedPosition : { top: 0, behavior: 'smooth' }
    }
});

// 增强版全局前置守卫
router.beforeEach((to, from, next) => {
    // 正确读取存储在user对象中的token
    const userStr = localStorage.getItem('user');
    let hasValidToken = false;

    try {
        if (userStr) {
            const user = JSON.parse(userStr);
            // 检查token是否存在且不为空
            hasValidToken = !!user.token;
        }
    } catch (e) {
        localStorage.removeItem('user'); // 清除损坏的用户信息
    }

    // console.log('路由守卫检查:', {
    //     to: to.path,
    //     requiresAuth: to.meta.requiresAuth,
    //     hasValidToken: hasValidToken
    // });

    if (to.meta.requiresAuth) {
        if (hasValidToken) {
            next(); // 已登录，放行
        } else {
            next({ path: '/login', query: { redirect: to.fullPath } });
        }
    } else {
        next();
    }
});
// 添加路由错误捕获
router.onError((error) => {
    // 处理因路由配置错误导致的跳转失败
    if (error.message.includes('Failed to resolve component')) {
        router.push('/404');
    }
});
export default router;
