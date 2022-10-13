@file:JvmName("UtilsKt")

package xyz.doikki.videoplayer

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
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

inline fun <reified K> Map<*, *>.loopKeyWhen(block: (K) -> Unit) {
    for ((key) in this) {
        if (key is K) {
            block(key)
        }
    }
}

inline fun <reified V> Map<*, *>.loopValueWhen(block: (V) -> Unit) {
    for ((_, value) in this) {
        if (value is V) {
            block(value)
        }
    }
}

inline fun <K> MutableMap<K, *>.removeAllByKey(block: (K) -> Boolean) {
    val it: MutableIterator<Map.Entry<K, *>> = this.iterator()
    while (it.hasNext()) {
        val (key, _) = it.next()
        if (block(key)) {
            it.remove()
        }
    }
}

inline fun <V> MutableMap<*, V>.removeAllByValue(filter: (V) -> Boolean) {
    val it: MutableIterator<Map.Entry<*, V>> = this.iterator()
    while (it.hasNext()) {
        val (_, value) = it.next()
        if (filter(value)) {
            it.remove()
        }
    }
}


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
fun Context.getActivityContext(): Activity? {
    if (this is Activity) {
        return this
    } else if (this is ContextWrapper) {
        return this.baseContext.getActivityContext()
    }
    return null
}

inline val Context.layoutInflater: LayoutInflater get() = LayoutInflater.from(this)
inline val View.layoutInflater: LayoutInflater get() = context.layoutInflater

inline fun Context.toast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

inline fun Context.toast(@StringRes messageId: Int, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, messageId, length).show()
}

inline fun View.toast(message: String, length: Int = Toast.LENGTH_SHORT) {
    context.toast(message, length)
}

inline fun View.toast(@StringRes messageId: Int, length: Int = Toast.LENGTH_SHORT) {
    context.toast(messageId, length)
}

/**
 * 是否是第一次按下按键
 */
val KeyEvent.isUniqueDown: Boolean get() = action == KeyEvent.ACTION_DOWN && repeatCount == 0

const val INVALIDATE_SEEK_POSITION = -1

inline fun Int?.avoidZeroDividend(): Int = if (this == null || this == 0) 1 else this
inline fun Float?.avoidZeroDividend(): Float = if (this == null || this == 0f) 1f else this
inline fun Long?.avoidZeroDividend(): Long = if (this == null || this == 0L) 1L else this
inline fun Double?.avoidZeroDividend(): Double = if (this == null || this == 0.0) 1.0 else this