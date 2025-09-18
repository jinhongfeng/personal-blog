const { defineConfig } = require('@vue/cli-service')
const {resolve} = require("node:path");
module.exports = defineConfig({
    chainWebpack: config => {
        // 配置 .lrc 文件解析规则
        config.module
            .rule('lrc') // 规则名称
            .test(/\.lrc$/) // 匹配所有 .lrc 后缀文件
            .use('raw-loader') // 使用 raw-loader
            .loader('raw-loader') // 指定 loader
            .options({
                esModule: false, // 关键：关闭 ES 模块转换，确保返回纯字符串
                alias: {
                    '@': resolve(__dirname, 'src') // 将 @ 指向 src 目录
                }
            })
            .end();
    },
    transpileDependencies: true,

    lintOnSave: false,
    // 基本路径，打包后静态资源引用的基础路径，部署到服务器非根目录时需配置
    // publicPath: process.env.NODE_ENV === 'production' ? '/' : '/', // 根目录部署
    publicPath: process.env.NODE_ENV === 'production' ? '/' : '/',
    // 输出目录，打包后生成文件的存放文件夹，默认是 dist
    outputDir: 'dist',
    // 静态资源目录名，打包后静态资源（js、css、img 等）会放在该目录，默认 assets
    assetsDir: 'assets', // 图片输出目录
    // 是否开启生产环境的 source map，默认 false，关闭可减小包体积、提高性能
    productionSourceMap: false,

    css: {
        loaderOptions: {
            // 确保图片路径为相对路径
            css: {

            }
        }
    },
    // 开发环境代理配置（生产环境不生效）
    devServer: {
        proxy: {
            // 代理API请求
            '/api': {
                target: 'http://localhost:9090', // 后端服务地址
                changeOrigin: true,
                pathRewrite: {
                    '^/api': ''
                }
            },
        }
    }
})
