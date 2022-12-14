package xyz.doikki.videoplayer.player

import android.content.Context

/**
 * 创建[SystemPlayer]的工厂类，不推荐，系统的MediaPlayer兼容性较差，建议使用IjkPlayer或者ExoPlayer
 * todo 本身可以采用lambda实现，但是不利于调试时通过classname获取Player的名字
 */
class SystemPlayerFactory : PlayerFactory {

    override fun create(context: Context): SystemPlayer {
        return SystemPlayer(context)
    }

}