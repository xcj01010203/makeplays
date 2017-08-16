// 加 md5
fis.match('/static/{js,css,images}/*.{js,css,png}', {
    //release: '../$0',
	//url:'../$0',
    useHash: true
	//domain:'/analysis'
});

//忽略指纹
//fis.match('lib/*', {
 //   useHash: false
//});

// 启用 fis-spriter-csssprites 插件
//fis.match('::package', {
  //  spriter: fis.plugin('csssprites')
//})

fis.match('/static/js/*.js', {
    // fis-optimizer-uglify-js 插件进行压缩，已内置
    optimizer: fis.plugin('uglify-js')
});


fis.match('/static/css/*.css', {
    // fis-optimizer-clean-css 插件进行压缩，已内置
    optimizer: fis.plugin('clean-css')
});

//对 CSS 进行图片合并
fis.match('/static/css/*.css', {
    // 给匹配到的文件分配属性 `useSprite`
    useSprite: true
});

//
//fis.match('*.png', {
//    // fis-optimizer-png-compressor 插件进行压缩，已内置
//    optimizer: fis.plugin('png-compressor')
//});

// 开发环境
fis.media('debug').match('*.{js,css,png,jpg}', {
    useHash: false,
    useSprite: false,
    optimizer: null
})