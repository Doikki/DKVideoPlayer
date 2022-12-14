package xyz.doikki.videoplayer.exo

import android.content.Context
import xyz.doikki.videoplayer.player.PlayerFactory

class ExoMediaPlayerFactory : PlayerFactory {
    override fun create(context: Context): ExoMediaPlayer {
        return ExoMediaPlayer(context)
    }

    companion object {
        @JvmStatic
        fun create(): ExoMediaPlayerFactory {
            return ExoMediaPlayerFactory()
        }
    }
}