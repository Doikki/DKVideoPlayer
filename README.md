# MagicPlayer
基于ijkPlayer的视频播放器

## Get Start

### gradle
Step 1.Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        compile 'com.github.DevlinChiu:MagicPlayer:1.0.4'
	}
### ProGuard

	-keep class tv.danmaku.ijk.** { *; }
    -dontwarn tv.danmaku.ijk.**
    -keep class com.devlin_n.magic_player.** { *; }
    -dontwarn com.devlin_n.magic_player.**
