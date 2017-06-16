# YinYangPlayer
A video player based on IjkPlayer.

[![](https://www.jitpack.io/v/DevlinChiu/YinYangPlayer.svg)](https://www.jitpack.io/#DevlinChiu/YinYangPlayer)


## 特性
* **支持滑动调节进度，声音、亮度。**
* **支持双击播放、暂停。**
* **支持重力感应自动进入/退出全屏。**
* **支持边播边缓存，使用了[AndroidVideoCache](https://github.com/danikula/AndroidVideoCache)。**
* **完美实现列表播放（RecyclerView和ListView）。**
* **支持封面。**
* **支持锁定/解锁全屏。**
* **支持调整显示比例:默认、原始大小、16:9、4:3、铺满屏幕。**
* **暂停时前后台切换不黑屏**
* **支持IJKPlayer和MediaPlayer切换。**
* **支持Https协议。**
* **支持连续播放一个列表的视频。**
* **支持悬浮窗播放。**
* **支持广告播放。**
* **支持弹幕**

## 使用

1.添加类库
```
gradle
Step 1.Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        compile 'com.github.DevlinChiu:YinYangPlayer:1.2'
	}
```

2.添加布局
```xml
<com.devlin_n.yinyangplayer.player.YinYangPlayer
        android:id="@+id/player"
        android:layout_width="match_parent"
        android:layout_height="300dp" />
```

3.设置视频地址、标题、Controller等
```java
yinYangPlayer = (YinYangPlayer) findViewById(R.id.player);
StandardVideoController controller = new StandardVideoController(this);
yinYangPlayer
        .autoRotate() //启用重力感应自动进入/推出全屏功能
        .enableCache() //启用边播边缓存功能
        .useSurfaceView() //启用SurfaceView显示视频，不调用默认使用TextureView
        .useAndroidMediaPlayer() //启动AndroidMediaPlayer，不调用此方法默认使用IjkPlayer
        .setUrl(URL_VOD) //设置视频地址
        .setTitle("网易公开课-如何掌控你的自由时间") //设置视频标题
        .setVideoController(controller) //设置控制器，如需定制可继承BaseVideoController
        .start(); //开始播放，不调用则不自动播放
```

4.在`Activity`中
```java
@Override
    protected void onPause() {
        super.onPause();
        yinYangPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        yinYangPlayer.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        yinYangPlayer.release();
    }


    @Override
    public void onBackPressed() {
        if (!yinYangPlayer.onBackPressed()) {
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
<img src="https://github.com/DevlinChiu/YinYangPlayer/blob/master/ScreenShot/1.jpg" width="240px" height="426px"/>
<img src="https://github.com/DevlinChiu/YinYangPlayer/blob/master/ScreenShot/2.jpg" width="240px" height="426px"/>
<img src="https://github.com/DevlinChiu/YinYangPlayer/blob/master/ScreenShot/3.jpg" width="240px" height="426px"/>
<img src="https://github.com/DevlinChiu/YinYangPlayer/blob/master/ScreenShot/5.jpg" width="240px" height="426px"/>
<img src="https://github.com/DevlinChiu/YinYangPlayer/blob/master/ScreenShot/6.jpg" width="240px" height="426px"/>
<img src="https://github.com/DevlinChiu/YinYangPlayer/blob/master/ScreenShot/7.jpg" width="240px" height="426px"/>
<img src="https://github.com/DevlinChiu/YinYangPlayer/blob/master/ScreenShot/8.jpg" width="240px" height="426px"/>
<img src="https://github.com/DevlinChiu/YinYangPlayer/blob/master/ScreenShot/9.jpg" width="240px" height="426px"/>
<img src="https://github.com/DevlinChiu/YinYangPlayer/blob/master/ScreenShot/10.jpg" width="240px" height="426px"/>
<img src="https://github.com/DevlinChiu/YinYangPlayer/blob/master/ScreenShot/4.jpg" width="426px" height="240px"/>
</div>

## 混淆
	-keep class tv.danmaku.ijk.** { *; }
    -dontwarn tv.danmaku.ijk.**
    -keep class com.devlin_n.yinyangplayer.** { *; }
    -dontwarn com.devlin_n.yinyangplayer.**
