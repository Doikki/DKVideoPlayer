package xyz.doikki.videoplayer.sys

import android.content.Context
import xyz.doikki.videoplayer.DKPlayerFactory

/**
 * 创建[SysDKPlayer]的工厂类，不推荐，系统的MediaPlayer兼容性较差，建议使用IjkPlayer或者ExoPlayer
 * todo 本身可以采用lambda实现，但是不利于调试时通过classname获取Player的名字
 */
class SysDKPlayerFactory : DKPlayerFactory {

    override fun create(context: Context): SysDKPlayer {
        return SysDKPlayer(context)
    }

}