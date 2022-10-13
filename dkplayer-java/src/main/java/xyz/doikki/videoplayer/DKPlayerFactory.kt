package xyz.doikki.videoplayer

import android.content.Context
import xyz.doikki.videoplayer.sys.SysDKPlayer

/**
 * 此接口使用方法：
 * 1.继承[DKPlayer]扩展自己的播放器。
 * 2.继承此接口并实现[create]，返回步骤1中的播放器。
 * 3a.通过[DKManager.playerFactory] 设置步骤2的实例 :该方式全局生效
 * 3b.通过[DKVideoView.setPlayerFactory] 设置步骤2的实例：该方式只对特定的VideoView生效
 *
 * 步骤1和2 可参照[xyz.doikki.videoplayer.sys.SysDKPlayer]和[xyz.doikki.videoplayer.sys.SysDKPlayerFactory]的实现。
 */
fun interface DKPlayerFactory<P : DKPlayer> {

    /**
     * @param context 注意内存泄露：内部尽可能使用context.getApplicationContext();
     * 绝大部分情况下，player的创建通过ApplicationContext创建不会有问题
     * @return
     */
    fun create(context: Context): P

    companion object {

        /**
         * 创建[SysDKPlayer]的工厂类，不推荐，系统的MediaPlayer兼容性较差，建议使用IjkPlayer或者ExoPlayer
         */
        @Deprecated("兼容性较差：比如某些盒子上不能配合texture使用")
        @JvmStatic
        fun systemMediaPlayerFactory(): DKPlayerFactory<SysDKPlayer> {
            return DKPlayerFactory {
                SysDKPlayer(it)
            }
        }
    }

}