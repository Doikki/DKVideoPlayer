# DKPlayer
A video player based on [IjkPlayer](https://github.com/Bilibili/ijkplayer).

[![](https://www.jitpack.io/v/dueeeke/dkplayer.svg)](https://www.jitpack.io/#dueeeke/dkplayer)
[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16)

#### [Wiki](https://github.com/dueeeke/dkplayer/wiki)
#### [Demo](https://fir.im/1r3u)

## 特性
* **支持直播和点播。**
* **支持调整显示比例:默认、原始大小、16:9、4:3、铺满屏幕、居中裁剪。**
* **支持滑动调节播放进度、声音、亮度；双击播放、暂停；保存播放进度。**
* **支持边播边缓存，使用了[AndroidVideoCache](https://github.com/danikula/AndroidVideoCache)。**
* **支持弹幕，使用了[DanmakuFlameMaster](https://github.com/Bilibili/DanmakuFlameMaster)。**
* **支持Https，rtsp，concat协议。**
* **支持播放本地视频以及raw和assets视频。**
* **支持重力感应自动进入/退出全屏以及手动进入/退出全屏，全屏状态下可锁定。**
* **完美实现列表播放（RecyclerView和ListView），列表自动播放。**
* **支持列表小窗全局悬浮播放，Android 8.0画中画功能。**
* **支持连续播放一个列表的视频。**
* **支持广告播放。**
* **支持清晰度切换。**
* **支持扩展自定义播放内核，MediaPlayer、ExoPlayer等。**
* **支持完全自定义控制层。**
* **支持多路播放器同时播放，没有任何控制UI的纯播放**
* **无缝衔接播放demo。**
* **抖音demo。**

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
    implementation 'com.github.dueeeke.dkplayer:dkplayer-java:2.5.6'
    implementation 'com.github.dueeeke.dkplayer:dkplayer-armv7a:2.5.6'

    # 可选，用于兼容一些其他的CPU架构
    implementation 'com.github.dueeeke.dkplayer:dkplayer-armv5:2.5.6'
    implementation 'com.github.dueeeke.dkplayer:dkplayer-arm64:2.5.6'
    implementation 'com.github.dueeeke.dkplayer:dkplayer-x86:2.5.6'
    implementation 'com.github.dueeeke.dkplayer:dkplayer-x86_64:2.5.6'

    # 可选，里面包含StandardVideoController的实现
    implementation 'com.github.dueeeke.dkplayer:dkplayer-ui:2.5.6'
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
StandardVideoController controller = new StandardVideoController(this);
controller.setTitle("网易公开课-如何掌控你的自由时间"); //设置视频标题
ijkVideoView.setVideoController(controller); //设置控制器，如需定制可继承BaseVideoController
ijkVideoView.start(); //开始播放，不调用则不自动播放
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
|API演示|列表播放|扩展功能|画中画
|:---:|:---:|:---:|:---:|
![](https://github.com/dueeeke/dkplayer/blob/master/art/1.png)|![](https://github.com/dueeeke/dkplayer/blob/master/art/2.png)|![](https://github.com/dueeeke/dkplayer/blob/master/art/3.png)|![](https://github.com/dueeeke/dkplayer/blob/master/art/4.png)

## 混淆
	-keep class tv.danmaku.ijk.** { *; }
    -dontwarn tv.danmaku.ijk.**
    -keep class com.dueeeke.videoplayer.** { *; }
    -dontwarn com.dueeeke.videoplayer.**
    
## 联系我
在github上提交[issue](https://github.com/dueeeke/dkplayer/issues)或者邮箱：xinyunjian1995@gmail.com

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