package xyz.doikki.dkplayer.activity

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import xyz.doikki.dkplayer.R
import xyz.doikki.dkplayer.fragment.main.ApiFragment
import xyz.doikki.dkplayer.fragment.main.ExtensionFragment
import xyz.doikki.dkplayer.fragment.main.ListFragment
import xyz.doikki.dkplayer.fragment.main.PipFragment
import xyz.doikki.dkplayer.util.ALLMManager
import xyz.doikki.dkplayer.util.PIPManager
import xyz.doikki.dkplayer.util.Tag
import xyz.doikki.dkplayer.util.Utils
import xyz.doikki.dkplayer.util.cache.ProxyVideoCacheManager
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory
import xyz.doikki.videoplayer.ijk.IjkPlayerFactory
import xyz.doikki.videoplayer.player.AndroidMediaPlayerFactory
import xyz.doikki.videoplayer.player.PlayerFactory
import xyz.doikki.videoplayer.player.VideoView
import xyz.doikki.videoplayer.player.VideoViewManager
import java.io.*

class MainActivity : BaseActivity<VideoView>(), NavigationBarView.OnItemSelectedListener {

    private val mFragments: MutableList<Fragment> = ArrayList()
    override fun getLayoutResId(): Int {
        return R.layout.activity_main
    }

    override fun enableBack(): Boolean {
        return false
    }

    override fun initView() {
        super.initView()
        copyAssetsFile()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 10000)
        }
        //检测当前是用的哪个播放器
        when (Utils.getCurrentPlayerFactory()) {
            is ExoMediaPlayerFactory -> {
                setTitle(resources.getString(R.string.app_name) + " (ExoPlayer)")
            }
            is IjkPlayerFactory -> {
                setTitle(resources.getString(R.string.app_name) + " (IjkPlayer)")
            }
            is AndroidMediaPlayerFactory -> {
                setTitle(resources.getString(R.string.app_name) + " (MediaPlayer)")
            }
            else -> {
                setTitle(resources.getString(R.string.app_name) + " (unknown)")
            }
        }
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.setOnItemSelectedListener(this)
        mFragments.add(ApiFragment())
        mFragments.add(ListFragment())
        mFragments.add(ExtensionFragment())
        mFragments.add(PipFragment())
        supportFragmentManager.beginTransaction()
            .add(R.id.layout_content, mFragments[0])
            .commitAllowingStateLoss()
        mCurrentIndex = 0
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        when (itemId) {
            R.id.close_float_window -> {
                PIPManager.getInstance().stopFloatWindow()
                PIPManager.getInstance().reset()
            }
            R.id.clear_cache -> if (ProxyVideoCacheManager.clearAllCache(this)) {
                Toast.makeText(this, "清除缓存成功", Toast.LENGTH_SHORT).show()
            }
            R.id.personal -> PersonalActivity.startPersonalActivity(this);
            R.id.cpu_info -> CpuInfoActivity.start(this)
            R.id.allm_on -> ALLMManager.openALLM()
            R.id.allm_off -> ALLMManager.closeALLM()
        }
        if (itemId == R.id.ijk || itemId == R.id.exo || itemId == R.id.media) {
            //切换播放核心，不推荐这么做，我这么写只是为了方便测试
            val config = VideoViewManager.getConfig()
            try {
                val mPlayerFactoryField = config.javaClass.getDeclaredField("mPlayerFactory")
                mPlayerFactoryField.isAccessible = true
                var playerFactory: PlayerFactory<*>? = null
                when (itemId) {
                    R.id.ijk -> {
                        playerFactory = IjkPlayerFactory.create()
                        setTitle(resources.getString(R.string.app_name) + " (IjkPlayer)")
                    }
                    R.id.exo -> {
                        playerFactory = ExoMediaPlayerFactory.create()
                        setTitle(resources.getString(R.string.app_name) + " (ExoPlayer)")
                    }
                    R.id.media -> {
                        playerFactory = AndroidMediaPlayerFactory.create()
                        setTitle(resources.getString(R.string.app_name) + " (MediaPlayer)")
                    }
                }
                mPlayerFactoryField[config] = playerFactory
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val index: Int
        val itemId = menuItem.itemId
        index = when (itemId) {
            R.id.tab_api -> 0
            R.id.tab_list -> 1
            R.id.tab_extension -> 2
            R.id.tab_pip -> 3
            else -> 0
        }
        if (mCurrentIndex != index) {
            //切换tab，释放正在播放的播放器
            if (mCurrentIndex == 1) {
                videoViewManager.releaseByTag(Tag.LIST)
                videoViewManager.releaseByTag(Tag.SEAMLESS, false) //注意不能移除
            }
            val transaction = supportFragmentManager.beginTransaction()
            val fragment = mFragments[index]
            val curFragment = mFragments[mCurrentIndex]
            if (fragment.isAdded) {
                transaction.hide(curFragment).show(fragment)
            } else {
                transaction.add(R.id.layout_content, fragment).hide(curFragment)
            }
            transaction.commitAllowingStateLoss()
            mCurrentIndex = index
        }
        return true
    }

    override fun onBackPressed() {
        if (videoViewManager.onBackPress(Tag.LIST)) return
        if (videoViewManager.onBackPress(Tag.SEAMLESS)) return
        super.onBackPressed()
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
    }

    companion object {
        @JvmField
        var mCurrentIndex = 0
    }

    private fun copyAssetsFile(): Boolean {
        val outFile = File(externalCacheDir, "test.mp4")
        if (outFile.parentFile?.exists() != true) {
            outFile.parentFile!!.mkdirs()
        }

        var inputStream: InputStream? = null
        var out: OutputStream? = null

        try {
            inputStream = assets.open("test.mp4")
            out = FileOutputStream(outFile)

            val buffer = ByteArray(1024)
            var read: Int = inputStream.read(buffer)
            while (read != -1) {
                out.write(buffer, 0, read)
                read = inputStream.read(buffer)
            }
        } catch (e: IOException) {
            return false
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                }
            }
            if (out != null) {
                try {
                    out.flush()
                    out.close()
                } catch (e: IOException) {
                }
            }
        }
        return true
    }
}