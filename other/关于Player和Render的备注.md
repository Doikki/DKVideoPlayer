# Player

目前提供基于系统的MediaPlayer、Ijk、Exo的三种实现。

# Render

主要提供基于SurfaceView和TextureView的实现

# 使用建议：建议使用Ijk作为播放器，在5.0以前使用SurfaceView，之后使用TextureView

# 一些疑难杂症

## 1. MediaPlayer + SurfaceView全屏和普通屏幕之间切换之后，每次都会退回到前几秒进行播放（比如当前播放到10s的位置，此时点击全屏按钮，全屏展示之后发现是从8-9s这个位置开始重新播放）

运行设备：vivo Xplay5A Android 5.1.1 解决办法：采用TextureView没有出现该现象。推荐采用Ijk播放器。

## 2. 旋转、镜像旋转等功能只有TextureView才支持（GLSurface之类的也不行哈）

## 3. 在某款手机上Exo+SurfaceView全屏和普通屏幕之间切换出现播放错误

运行设备：vivo Xplay5A Android 5.1.1 解决办法：Exo+TextureView在该设备上测试没有播放出错。推荐采用Ijk之类的软解方式。

## 4. MediaPlayer + TextureView 在一款湖南的盒子（4.4系统）上使用进行播放，有声音无画面

备注：MediaPlayer + SurfaceView在该设备上能够播放 解决办法：在5.0以前的系统默认使用SurfaceView，5.0及以后的系统使用TextureView
详细说明：MediaPlayer + TextureView 在湖南的某一款盒子（手上就这一款盒子）上运行，Application开启了硬件加速，但是还是有声音无画面

试了以下两个资料提及的办法均无效：
TextureView有声音没画面&onSurfaceTextureAvailable没调用：https://blog.csdn.net/qugengting/article/details/105271008
即：在博主（反正在我的小米手机android 10系统上）的手机上没有回调onSurfaceTextureAvailable，但是回调了update，因此利用了update的逻辑。

Textureview+Mediplay 播放视频时无画面有声音 https://blog.csdn.net/feifan12311/article/details/119332973
尝试结果：我通过反射参照文档所说进行设置，但是发现通过反射方法设置的属性无法生效（设置后重新读取得到的结果还是之前的结果），多半需要系统的配置设置才行。

## 5. IJK 重新播放（调用reset在重新设置数据源）的情况下，先于reset之前设置的surface会无效

解决办法：整个DKVideoView的reply逻辑应该是先reset->set option -> set render -> setDatasource
