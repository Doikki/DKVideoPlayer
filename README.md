# MagicPlayer
基于ijkPlayer的视频播放器

##Get Start

###gradle
Step 1.Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        compile 'com.github.DevlinChiu:MagicPlayer:v1.0'
	}


###maven
Step 1. Add the JitPack repository to your build file

	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
Step 2. Add the dependency

	<dependency>
	    <groupId>com.github.DevlinChiu</groupId>
	    <artifactId>MagicPlayer</artifactId>
	    <version>v1.0</version>
	</dependency>