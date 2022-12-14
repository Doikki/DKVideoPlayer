package xyz.doikki.videoplayer.player

import android.content.Context

/**
 * 创建[SystemPlayer]的工厂类，不推荐，系统的MediaPlayer兼容性较差，建议使用IjkPlayer或者ExoPlayer
 */
class SystemPlayerFactory : PlayerFactory {

    override fun create(context: Context): SystemPlayer {
        return SystemPlayer(context)
    }

}