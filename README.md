# DKVideoPlayer
A video player for Android.

[![JCenter](https://api.bintray.com/packages/dueeeke/maven/dkplayer-java/images/download.svg)](https://bintray.com/dueeeke/maven/dkplayer-java/_latestVersion)
[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16)

#### [Demo](https://fir.im/1r3u)

### 特性
#### 功能支持情况
| 功能  | MediaPlayer  | IjkPlayer  | ExoPlayer  |
| :------------ | :------------ | :------------ | :------------ |
|  调整显示比例 | 支持  | 支持  |  支持 |
|  滑动调节播放进度、声音、亮度 | 支持  |  支持 | 支持  |
|  双击播放、暂停 | 支持  |  支持 | 支持  |
|  重力感应自动进入/退出全屏以及手动进入/退出全屏 | 支持  |  支持 | 支持  |
|  倍速播放 | 不支持  |  支持 | 支持  |
|  视频截图（使用SurfaceView时都不支持，默认用的是TextureView） | 支持  |  支持 | 支持  |
|  列表小窗全局悬浮播放 | 支持  |  支持 | 支持  |
|  连续播放一个列表的视频 | 支持  |  支持 | 支持  |
|  广告播放 | 支持  |  支持 | 支持  |
|  边播边缓存，使用了[ AndroidVideoCache ](https://github.com/danikula/AndroidVideoCache)实现 | 支持  |  支持 | 支持  |
|  弹幕，使用[ DanmakuFlameMaster ](https://github.com/Bilibili/DanmakuFlameMaster)实现 | 支持  |  支持 | 支持  |
|  多路播放器同时播放 | 支持  |  支持 | 支持  |
|  没有任何控制UI的纯播放 | 支持  |  支持 | 支持  |
|  Android 8.0画中画 | 支持  |  支持 | 支持  |
|  无缝衔接播放 | 支持  |  支持 | 支持  |
|  抖音 | 支持  |  支持 | 支持  |

#### 协议/格式支持情况（只列举常用格式/协议）
| 协议/格式  | MediaPlayer  | IjkPlayer  | ExoPlayer  |
| :------------ | :------------ | :------------ | :------------ |
|  https | 支持  | 支持  |  支持 |
|  rtsp | 不支持  |  支持 | 不支持  |
|  rtmp | 不支持  |  支持 | 支持  |
|  ffconcat | 不支持  |  支持 | 不支持  |
|  file（本地视频） | 支持  |  支持 | 支持  |
|  android.resource（raw） | 支持  | 支持 | 支持  |
|  assets中的视频 | 支持  |  支持 | 支持  |
|  mp4 | 支持  |  支持 | 支持  |
|  m3u8 | 支持  |  支持 | 支持  |
|  flv | 支持  |  支持 | 可播放，无法seek进度  |

### 使用
##### [Wiki](https://github.com/dueeeke/dkplayer/wiki)

### 截图
|API演示|列表播放|扩展功能|画中画
|:---:|:---:|:---:|:---:|
![](https://github.com/dueeeke/dkplayer/blob/master/art/1.png)|![](https://github.com/dueeeke/dkplayer/blob/master/art/2.png)|![](https://github.com/dueeeke/dkplayer/blob/master/art/3.png)|![](https://github.com/dueeeke/dkplayer/blob/master/art/4.png)
   
### 联系我
在github上提交[issue](https://github.com/dueeeke/dkplayer/issues)或者邮箱：xinyunjian1995@gmail.com

### License
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