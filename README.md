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
* **支持调整显示比例:默认、原始大小、16:9、4:3、铺满屏幕。**
* **暂停时前后台切换不黑屏**
* **支持IJKPlayer和MediaPlayer切换。**
* **支持Https协议。**
* **支持rtsp，concat协议。**
* **支持连续播放一个列表的视频。**
* **支持悬浮窗播放。**
* **支持广告播放。**
* **支持弹幕，使用了[DanmakuFlameMaster](https://github.com/Bilibili/DanmakuFlameMaster)。**
* **抖音demo。**

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
    # required, enough for most devices.
    implementation 'com.github.dueeeke.dkplayer:dkplayer-java:2.2'
    implementation 'com.github.dueeeke.dkplayer:dkplayer-armv7a:2.2'

    # Other ABIs: optional
    implementation 'com.github.dueeeke.dkplayer:dkplayer-armv5:2.2'
    implementation 'com.github.dueeeke.dkplayer:dkplayer-arm64:2.2'
    implementation 'com.github.dueeeke.dkplayer:dkplayer-x86:2.2'
    implementation 'com.github.dueeeke.dkplayer:dkplayer-x86_64:2.2'
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
        .usingAndroidMediaPlayer()//启动AndroidMediaPlayer，不调用此方法默认使用IjkPlayer
        .usingSurfaceView() //启用SurfaceView显示视频，不调用默认使用TextureView
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
<img src="https://github.com/dueeeke/dkplayer/blob/master/art/1.jpg" width="240px" height="426px"/>
<img src="https://github.com/dueeeke/dkplayer/blob/master/art/2.jpg" width="240px" height="426px"/>
<img src="https://github.com/dueeeke/dkplayer/blob/master/art/3.jpg" width="240px" height="426px"/>
<img src="https://github.com/dueeeke/dkplayer/blob/master/art/5.jpg" width="240px" height="426px"/>
<img src="https://github.com/dueeeke/dkplayer/blob/master/art/6.jpg" width="240px" height="426px"/>
<img src="https://github.com/dueeeke/dkplayer/blob/master/art/7.jpg" width="240px" height="426px"/>
<img src="https://github.com/dueeeke/dkplayer/blob/master/art/8.jpg" width="240px" height="426px"/>
<img src="https://github.com/dueeeke/dkplayer/blob/master/art/9.jpg" width="240px" height="426px"/>
<img src="https://github.com/dueeeke/dkplayer/blob/master/art/10.jpg" width="240px" height="426px"/>
<img src="https://github.com/dueeeke/dkplayer/blob/master/art/4.jpg" width="426px" height="240px"/>
</div>

## 混淆
	-keep class tv.danmaku.ijk.** { *; }
    -dontwarn tv.danmaku.ijk.**
    -keep class com.dueeeke.videoplayer.** { *; }
    -dontwarn com.dueeeke.videoplayer.**
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