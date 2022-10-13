package xyz.doikki.videoplayer

/*
* 1、MediaPlayer + TextureView 在湖南的某一款盒子（手上就这一款盒子）上运行，Application开启了硬件加速，但是还是有声音无画面
* 试了一下两个资料提及的办法均无效：
*
* TextureView有声音没画面&onSurfaceTextureAvailable没调用：https://blog.csdn.net/qugengting/article/details/105271008
* 即：在博主（反正在我的小米手机android 10系统上）的手机上没有回调onSurfaceTextureAvailable，但是回调了update，因此利用了update的逻辑。
*
* Textureview+Mediplay 播放视频时无画面有声音 https://blog.csdn.net/feifan12311/article/details/119332973
* 尝试结果：我通过反射参照文档所说进行设置，但是发现通过反射方法设置的属性无法生效（设置后重新读取得到的结果还是之前的结果），多半需要系统的配置设置才行。
*
*
* */