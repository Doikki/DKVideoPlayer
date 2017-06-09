# YinYangPlayer
A video player based on IjkPlayer.

## Get Started

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
	        compile 'com.github.DevlinChiu:YinYangPlayer:1.1'
	}
### ProGuard

	-keep class tv.danmaku.ijk.** { *; }
    -dontwarn tv.danmaku.ijk.**
    -keep class com.devlin_n.yin_yang_player.** { *; }
    -dontwarn com.devlin_n.yin_yang_player.**
