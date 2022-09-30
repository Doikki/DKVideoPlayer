@file:JvmName("UtilsKt")
package xyz.doikki.videoplayer


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
        e
    }
}

