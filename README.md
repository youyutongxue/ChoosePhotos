# ChoosePhotos
# 自定义相册（可以进行单张/多张图片选择）

### 在项目中经常会和图片打交道，比如发表图片评论、上传个人头像等，都会先让用户进行图片选择，然后执行上传任务。图片选择可以直接进入系统自带的相册，也可以自己定义，这里我们使用自己定义的相册，使用到multi-image-selector这个模块。下面是使用方法：
   
#### 1.将模块multi-image-selector导入到自己的项目中；

#### 2.图片加载框架我们使用Glide，所以在build.gradle中添加：compile 'com.github.bumptech.glide:glide:3.7.0'

#### 3.一般选择好了图片都可以点击查看原图和图片细节，所以还得在build.gradle中添加：compile 'com.github.chrisbanes.photoview:library:1.2.4'

#### 4.调用相册方法：

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

#### 获取到结果：

![](https://github.com/youyutongxue/ChoosePhotos/blob/master/app/src/main/java/com/virgil/choosephotos/art/2.png)

#### 5.执行上传：在项目中我们不可能直接把获取到的图片上传，因为有可能图片太大，一张图片就几MB，直接上传太浪费流量和耗时了，所以在上传之前我们要对图片进行压缩处理：

![](https://github.com/youyutongxue/ChoosePhotos/blob/master/app/src/main/java/com/virgil/choosephotos/art/3.png)

### 在项目中，选择图片以后还会遇到各种问题：

#### 1.有的手机用相机拍照后，上传的图片是旋转了90度的，如三星、魅族等，所以我们也在代码中进行了处理：
![](https://github.com/youyutongxue/ChoosePhotos/blob/master/app/src/main/java/com/virgil/choosephotos/art/4.png)

#### 图片处理的方法都在ImageProcessingUtils这个类中。

#### 2.有的手机从相册选择了照片后，获取到的地址为null，如小米，解决方法：

```
/**
 * 解决小米手机上获取图片路径为null的情况
 *
 * @param intent
 * @return
 */
public static Uri geturi(android.content.Intent intent, Activity activity) {
    Uri uri = intent.getData();
    String type = intent.getType();
    if (uri != null) {
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = activity.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=")
                        .append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.ImageColumns._ID},
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    // set _id value
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    // do nothing
                } else {
                    Uri uri_temp = Uri
                            .parse("content://media/external/images/media/"
                                    + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
    }
    return uri;
}
```

### 当然，在项目中还会遇到很多问题，这里只是列举了自己在项目中所遇到的问题，仅供参考。

