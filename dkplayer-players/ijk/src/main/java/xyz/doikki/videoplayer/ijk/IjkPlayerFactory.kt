package xyz.doikki.videoplayer.ijk

import android.content.Context
import xyz.doikki.videoplayer.player.PlayerFactory

class IjkPlayerFactory : PlayerFactory {
    override fun create(context: Context): IjkPlayer {
        return IjkPlayer(context)
    }

    companion object {
        @JvmStatic
        fun create(): IjkPlayerFactory {
            return IjkPlayerFactory()
        }
    }
}