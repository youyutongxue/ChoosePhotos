# ChoosePhotos
相册（可以进行单张/多张图片选择）
   在项目中经常会和图片打交道，比如发表图片评论、上传个人头像等，都会先让用户进行图片选择，然后执行上传任务。图片选择可以直接进入系统自带的相册，也可以自己定义，这里我们使用自己定义的相册，使用到multi-image-selector这个模块。下面是使用方法：
   
1.将模块multi-image-selector导入到自己的项目中；

2.图片加载框架我们使用Glide，所以在build.gradle中添加：compile 'com.github.bumptech.glide:glide:3.7.0'

3.一般选择好了图片都可以点击查看原图和图片细节，所以还得在build.gradle中添加：compile 'com.github.chrisbanes.photoview:library:1.2.4'

4.调用相册方法：

```
MultiImageSelector selector = MultiImageSelector.create(MainActivity.this);

selector.showCamera(true);//是否显示照相机

selector.count(9);//设置选择图片的上限

selector.multi();//设置可以多选，默认是多选

//selector.single();//设置只能进行单选

selector.origin(mSelectPath);

selector.start(MainActivity.this, REQUEST_IMAGE);
```

![](https://github.com/youyutongxue/ChoosePhotos/blob/master/app/src/main/java/com/virgil/choosephotos/art/1.jpeg)

获取到结果：
