import Vue from 'vue'
import Router from 'vue-router'
Vue.use(Router)

/* Layout */
import Layout from '@/layout'
import Main from '@/App'

/* Router Modules */

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar
    noCache: true                if set true, the page will no be cached(default is false)
    affix: true                  if set true, the tag will affix in the tags-view
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/404'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/ReqAnalysis',
    meta: { title: '年度数据分析', icon: 'chart', affix: true },
    children: [
      {
        path: 'ReqAnalysis',
        component: () => import('@/views/analysis/ReqAnalysis'),
        name: 'ReqAnalysis',
        meta: { title: '毕业要求', icon: 'chart', affix: true }
      },
      {
        path: 'TargetsAnalysis',
        component: () => import('@/views/analysis/TargetsAnalysis'),
        name: 'TargetsAnalysis',
        meta: { title: '指标点分析', icon: 'chart', affix: true }
      },
      {
        path: 'CourseAnalysis',
        component: () => import('@/views/analysis/CourseAnalysis'),
        name: 'CourseAnalysis',
        meta: { title: '课程目标', icon: 'chart', affix: true }
      }
    ]
  },
  {
    path: '/GradRequirementEdit',
    component: Layout,
    redirect: '/GradRequirementEdit',
    children: [
      {
        path: 'GradRequirementEdit',
        component: () => import('@/views/gradRequirement/GradRequirementEdit'),
        name: 'GradRequirementEdit',
        meta: { title: '毕业要求', icon: 'education', affix: true }
      }
    ]
  },
  {
    path: '/Target',
    component: Layout,
    redirect: '/Target',
    children: [
      {
        path: 'Target',
        component: () => import('@/views/target/Target'),
        name: 'Target',
        meta: { title: '指标点', icon: 'star', affix: true }
      }
    ]
  },
  {
    path: '/Course',
    component: Layout,
    redirect: '/Course',
    meta: { title: '课程编辑', icon: 'edit', affix: true },
    children: [
      {
        path: 'Course',
        component: () => import('@/views/course/Course'),
        name: 'Course',
        meta: { title: '课程目标', icon: 'edit', affix: true }
      },
      {
        path: 'CheckLinks',
        component: () => import('@/views/course/CheckLinks'),
        name: 'CheckLinks',
        meta: { title: '考核环节', icon: 'edit', affix: true }
      }
    ]
  },
  {
    path: '/Score',
    component: Layout,
    redirect: '/Score',
    meta: { title: '成绩管理', icon: 'list', affix: true },
    children: [
      {
        path: 'CheckLinkScore',
        component: () => import('@/views/score/CheckLinkScore'),
        name: 'CheckLinkScore',
        meta: { title: '编辑成绩', icon: 'list', affix: true }
      }
    ]
  },
  {
    path: '/Questionnaire',
    component: Main,
    redirect: '/Questionnaire',
    meta: { title: '调查问卷', icon: 'list', affix: true },
    children: [
      {
        path: 'CourseTaskQuestionnaire',
        component: () => import('@/views/questionnaire/CourseTaskQuestionnaire'),
        name: 'CourseTaskQuestionnaire',
        meta: { title: '调查问卷', icon: 'list', affix: true }
      }
    ]
  },
  {
    path: '/User',
    component: Layout,
    redirect: '/User',
    meta: { title: '用户权限', icon: 'peoples', affix: true },
    children: [
      {
        path: 'User',
        component: () => import('@/views/user/index'),
        name: 'User',
        meta: { title: '账户管理', icon: 'peoples', affix: true }
      },
      {
        path: 'Role',
        component: () => import('@/views/user/Role'),
        name: 'Role',
        meta: { title: '角色管理', icon: 'peoples', affix: true }
      },
      {
        path: 'Teacher',
        component: () => import('@/views/user/Teacher'),
        name: 'Teacher',
        meta: { title: '教师管理', icon: 'peoples', affix: true }
      }
    ]
  }
]

/**
 * asyncRoutes
 * the routes that need to be dynamically loaded based on user roles
 */
export const asyncRoutes = [
  // {
  //   path: '/user',
  //   component: Layout,
  //   redirect: '/user/index',
  //   children: [
  //     {
  //       path: 'index',
  //       component: () => import('@/views/user/index'),
  //       name: 'User',
  //       meta: { title: '用户管理', icon: 'user', noCache: true, roles: ['adminExclusive'] }
  //     }
  //   ]
  // },
  // 404 page must be placed at the end !!!
  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
