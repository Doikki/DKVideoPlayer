@file:JvmName("UtilsKt")

package xyz.doikki.videoplayer

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.ViewGroup
import xyz.doikki.videoplayer.util.L


inline fun Boolean?.orDefault(def: Boolean = false): Boolean {
    return this ?: def
}

inline fun Int?.orDefault(def: Int = 0) = this ?: def
inline fun Float?.orDefault(def: Float = 0f) = this ?: def
inline fun Long?.orDefault(def: Long = 0) = this ?: def
inline fun Double?.orDefault(def: Double = 0.0) = this ?: def

inline fun <T> T?.orDefault(default: T): T = this ?: default

inline fun <T> T?.orDefault(initializer: () -> T): T = this ?: initializer()


/**
 * 等同于[trySilent]，只是本方法没有对结果进行装箱处理（即没有产生[Result]中间对象）
 */
inline fun <T> T.tryIgnore(action: T.() -> Unit): Throwable? {
    return try {
        action(this)
        null
    } catch (e: Throwable) {
        L.w("error on ${Thread.currentThread().stackTrace[2].methodName} method invoke.but throwable is ignored.")
        e.printStackTrace()
        e
    }
}


/**
 * 从Parent中移除自己
 */
internal inline fun View.removeFromParent() {
    (parent as? ViewGroup)?.removeView(this)
}

internal inline val Activity.decorView: ViewGroup? get() = window.decorView as? ViewGroup

internal inline val Activity.contentView: ViewGroup? get() = findViewById(android.R.id.content)

/**
 * 从Context中获取Activity上下文
 */
fun Context.getActivityContext():Activity?{
    if (this is Activity) {
        return this
    } else if (this is ContextWrapper) {
        return this.baseContext.getActivityContext()
    }
    return null
}
