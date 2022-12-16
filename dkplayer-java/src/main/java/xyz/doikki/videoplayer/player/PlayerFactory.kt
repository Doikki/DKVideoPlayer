package xyz.doikki.videoplayer.player

import android.content.Context
import xyz.doikki.videoplayer.GlobalConfig
import xyz.doikki.videoplayer.VideoView

/**
 * 此接口使用方法：
 * 1.继承[IPlayer]扩展自己的播放器。
 * 2.继承此接口并实现[create]，返回步骤1中的播放器。
 * 3a.通过[GlobalConfig.playerFactory] 设置步骤2的实例 :该方式全局生效
 * 3b.通过[VideoView.playerFactory] 设置步骤2的实例：该方式只对特定的VideoView生效
 *
 * 步骤1和2 可参照[SystemPlayer]和[SystemPlayerFactory]的实现。
 */
fun interface PlayerFactory {

    /**
     * @param context 注意内存泄露：内部尽可能使用context.getApplicationContext();
     * 绝大部分情况下，player的创建通过ApplicationContext创建不会有问题
     */
    fun create(context: Context): IPlayer

}