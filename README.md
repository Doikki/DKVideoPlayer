# DKPlayer
A video player based on [IjkPlayer](https://github.com/Bilibili/ijkplayer).

[![](https://www.jitpack.io/v/dueeeke/dkplayer.svg)](https://www.jitpack.io/#dueeeke/dkplayer)
[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16)


## 特性
* **支持滑动调节进度，声音、亮度。**
* **支持双击播放、暂停。**
* **支持重力感应自动进入/退出全屏。**
* **支持边播边缓存，使用了[AndroidVideoCache](https://github.com/danikula/AndroidVideoCache)。**
* **完美实现列表播放（RecyclerView和ListView）。**
* **支持列表自动播放。**
* **支持列表小窗悬浮播放。**
* **支持封面。**
* **支持锁定/解锁全屏。**
* **支持调整显示比例:默认、原始大小、16:9、4:3、铺满屏幕、居中裁剪。**
* **支持IjkPlayer、MediaPlayer和ExoPlayer切换。**
* **支持Https，rtsp，concat协议。**
* **支持连续播放一个列表的视频。**
* **支持广告播放。**
* **支持清晰度切换。**
* **支持保存播放进度。**
* **支持弹幕，使用了[DanmakuFlameMaster](https://github.com/Bilibili/DanmakuFlameMaster)。**
* **抖音demo。**
* **Android O PiP demo。**

[demo下载](https://fir.im/1r3u)
## 使用

1.添加类库
```
gradle

allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    # 必选，可兼容市面上绝大部分设备
    implementation 'com.github.dueeeke.dkplayer:dkplayer-java:2.5.0'
    implementation 'com.github.dueeeke.dkplayer:dkplayer-armv7a:2.5.0'

    # 可选，用于兼容一些其他的CPU架构
    implementation 'com.github.dueeeke.dkplayer:dkplayer-armv5:2.5.0'
    implementation 'com.github.dueeeke.dkplayer:dkplayer-arm64:2.5.0'
    implementation 'com.github.dueeeke.dkplayer:dkplayer-x86:2.5.0'
    implementation 'com.github.dueeeke.dkplayer:dkplayer-x86_64:2.5.0'

    # 可选，里面包含StandardVideoController的实现
    implementation 'com.github.dueeeke.dkplayer:dkplayer-ui:2.5.0'
}
```
或者将library下载并导入项目中使用

2.添加布局
```xml
<com.dueeeke.videoplayer.player.IjkVideoView
        android:id="@+id/player"
        android:layout_width="match_parent"
        android:layout_height="300dp" />
```

3.设置视频地址、标题、Controller等
```java
ijkVideoView.setUrl(URL_VOD); //设置视频地址
ijkVideoView.setTitle("网易公开课-如何掌控你的自由时间"); //设置视频标题
StandardVideoController controller = new StandardVideoController(this);
ijkVideoView.setVideoController(controller); //设置控制器，如需定制可继承BaseVideoController
ijkVideoView.start(); //开始播放，不调用则不自动播放

//高级设置（可选，须在start()之前调用方可生效）
PlayerConfig playerConfig = new PlayerConfig.Builder()
        .enableCache() //启用边播边缓存功能
        .autoRotate() //启用重力感应自动进入/退出全屏功能
        .enableMediaCodec()//启动硬解码，启用后可能导致视频黑屏，音画不同步
        .usingSurfaceView() //启用SurfaceView显示视频，不调用默认使用TextureView
        .savingProgress() //保存播放进度
        .disableAudioFocus() //关闭AudioFocusChange监听
        .build();
ijkVideoView.setPlayerConfig(playerConfig);
```

4.在`Activity`中
```java
@Override
    protected void onPause() {
        super.onPause();
        ijkVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ijkVideoView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ijkVideoView.release();
    }
    

    @Override
    public void onBackPressed() {
        if (!ijkVideoView.onBackPressed()) {
            super.onBackPressed();
        }
    }
```

5.在`AndroidManifest.xml`中
```
<activity
    android:name=".PlayerActivity"
    android:configChanges="orientation|screenSize|keyboardHidden"
    android:screenOrientation="portrait" /> <!-- or android:screenOrientation="landscape"-->
```

其他API的用法参照demo

## 截图
<div>
<img src="https://github.com/dueeeke/dkplayer/blob/master/art/1.png" width="240px" height="426px"/>
<img src="https://github.com/dueeeke/dkplayer/blob/master/art/2.png" width="240px" height="426px"/>
<img src="https://github.com/dueeeke/dkplayer/blob/master/art/3.png" width="240px" height="426px"/>
<img src="https://github.com/dueeeke/dkplayer/blob/master/art/4.png" width="240px" height="426px"/>
<img src="https://github.com/dueeeke/dkplayer/blob/master/art/5.png" width="240px" height="426px"/>
<img src="https://github.com/dueeeke/dkplayer/blob/master/art/6.png" width="240px" height="426px"/>
<img src="https://github.com/dueeeke/dkplayer/blob/master/art/7.png" width="240px" height="426px"/>
<img src="https://github.com/dueeeke/dkplayer/blob/master/art/8.png" width="240px" height="426px"/>
<img src="https://github.com/dueeeke/dkplayer/blob/master/art/9.png" width="240px" height="426px"/>
<img src="https://github.com/dueeeke/dkplayer/blob/master/art/10.png" width="426px" height="240px"/>
</div>

## 混淆
	-keep class tv.danmaku.ijk.** { *; }
    -dontwarn tv.danmaku.ijk.**
    -keep class com.dueeeke.videoplayer.** { *; }
    -dontwarn com.dueeeke.videoplayer.**
    
## 写在最后
```
 首先我想谈谈我开发这个项目的初衷，我开发这个项目之前看了很多别人写的播放器的源码，很多都存在一个致命的问题，就是 
 把播放和控制全写在一起了，这就会导致播放器扩展性极差，增加一个功能要对原有的代码进行破坏性的改造，于是乎我萌生了
 一个自己写一个高扩展性的播放器的想法。我从Android系统的VideoView得到启示，把播放和控制分离，控制层以动态的方式
 绑定到播放器上,并对播放层，控制层以及播放核心都进行了抽象，大大提高了播放器的扩展性。接下来我要重点说说这个扩展性：

 Demo中提供StandardVideoController供大家参考，如果它不符合你也要求你可以继承它并重写其中的方法来实现你自己的需
 求，如果你觉得我这个Controller完全不符合你的要求，你大可以继承BaseVideoController这个类进行完全自定义。

 如果对IjkVideoView的功能你还需要进一步扩展或者不符合你的需求，你也可以继承它，重写里面的相应方法，实现自己的业务
 需求。Demo中的弹幕、广告等功能就是通过这种方法实现的。

 如果你不想使用IjkPlayer来播放视频，你还可以继承AbstractPlayer这个抽象类来扩展自己的播放器，一行代码切换系统
 MediaPlayer和ExoPlayer就是这么实现的。

 ps:喜欢的小伙伴可以在右上角star一下，如果大家有什么问题最好到GitHub上提交issue来讨论，当然你也可以发邮件到我的
    邮箱：xinyunjian1995@gmail.com

 pps:有什么问题最好先看demo中是否有相应的解决方案，因为demo真的非常详细。有时间也可以去研究一下我写的源代码，因为
     我写的真的很简单。
```
## License
```
Copyright (c) 2017 dueeeke

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```