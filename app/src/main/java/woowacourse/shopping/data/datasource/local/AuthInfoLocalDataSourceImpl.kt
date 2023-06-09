package woowacourse.shopping.data.datasource.local

import android.content.Context
import android.content.SharedPreferences

class AuthInfoLocalDataSourceImpl private constructor(context: Context) : AuthInfoLocalDataSource {

    private val sharedPreference: SharedPreferences =
        context.getSharedPreferences(AUTH_INFO, Context.MODE_PRIVATE)

    init {
        setAuthInfo()
    }

    override fun getAuthInfo(): String? {
        return sharedPreference.getString(USER_ACCESS_TOKEN, "")
    }

    override fun setAuthInfo() {
        sharedPreference.edit().putString(USER_ACCESS_TOKEN, "basic bG9waToxMjM0").apply()
    }

    companion object {
        private const val AUTH_INFO = "AUTH_INFO"
        private const val USER_ACCESS_TOKEN = "USER_ACCESS_TOKEN"

        private val authInfoDataSourceImpl: AuthInfoLocalDataSourceImpl? = null
        fun getInstance(context: Context): AuthInfoLocalDataSourceImpl {
            return authInfoDataSourceImpl ?: synchronized(this) {
                AuthInfoLocalDataSourceImpl(context)
            }
        }
    }
}
