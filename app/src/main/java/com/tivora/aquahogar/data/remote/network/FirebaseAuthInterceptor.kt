package com.tivora.aquahogar.data.remote.network

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthInterceptor @Inject constructor(
    private val auth: FirebaseAuth
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val user = auth.currentUser
        val original = chain.request()
        if (user == null) {
            return chain.proceed(original)
        }
        val token = runCatching {
            Tasks.await(user.getIdToken(false), 15, TimeUnit.SECONDS).token
        }.getOrNull()
        if (token.isNullOrBlank()) {
            return chain.proceed(original)
        }
        val authorized = original.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(authorized)
    }
}
