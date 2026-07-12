package com.example.dashdine.data.auth

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

data class User(
    val name: String,
    val phone: String,
    val password: String
)

@Singleton
class AuthManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("bite_auth", Context.MODE_PRIVATE)

    /** 注册新用户 */
    fun register(name: String, phone: String, password: String): Result<User> {
        val users = getAllUsers()
        if (users.any { it.phone == phone }) {
            return Result.failure(Exception("该手机号已注册"))
        }
        val newUser = User(name, phone, password)
        users.add(newUser)
        saveUsers(users)
        login(phone, password)
        return Result.success(newUser)
    }

    /** 登录 */
    fun login(phone: String, password: String): Result<User> {
        val users = getAllUsers()
        val user = users.firstOrNull { it.phone == phone && it.password == password }
            ?: return Result.failure(Exception("手机号或密码错误"))
        prefs.edit().putString(KEY_CURRENT_USER, phone).apply()
        return Result.success(user)
    }

    /** 退出登录 */
    fun logout() {
        prefs.edit().remove(KEY_CURRENT_USER).apply()
    }

    /** 是否已登录 */
    fun isLoggedIn(): Boolean = prefs.getString(KEY_CURRENT_USER, null) != null

    /** 获取当前用户 */
    fun getCurrentUser(): User? {
        val phone = prefs.getString(KEY_CURRENT_USER, null) ?: return null
        return getAllUsers().firstOrNull { it.phone == phone }
    }

    private fun getAllUsers(): MutableList<User> {
        val json = prefs.getString(KEY_USERS, "[]") ?: "[]"
        val arr = JSONArray(json)
        val list = mutableListOf<User>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            list.add(User(
                name = obj.getString("name"),
                phone = obj.getString("phone"),
                password = obj.getString("password")
            ))
        }
        return list
    }

    private fun saveUsers(users: List<User>) {
        val arr = JSONArray()
        users.forEach {
            arr.put(JSONObject().apply {
                put("name", it.name)
                put("phone", it.phone)
                put("password", it.password)
            })
        }
        prefs.edit().putString(KEY_USERS, arr.toString()).apply()
    }

    companion object {
        private const val KEY_USERS = "users"
        private const val KEY_CURRENT_USER = "current_user"
    }
}
