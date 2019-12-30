package com.danielworld.curl_interceptor_sample

import android.os.Build
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Created by Daniel Park on 2019-12-27
 */

// For Robolectrics 4.3.x, Android SDK 29 requires Java 9 (have Java 8). so set sdk = 28 immediately
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.P]
)
// Why use Robolectric ? : Because it contains many mocks of Android class which running on local JVM. (No need Android emulator or Device.// So. Use RobolectricTestRunner.class instead of MockitoJUnitRunner.class. Robolectric handle Android API.
@RunWith(RobolectricTestRunner::class)
class CurlLoggingInterceptorTest {

    val curlLoggingInterceptor = CurlLoggingInterceptor(true)

    @Before
    fun setUp() { // @namgyu.park (2019-12-27) : initialize mock, before executing each test
        MockitoAnnotations.initMocks(this)
    }

    @Test
    @Throws(JSONException::class)
    fun testPostUserActivatedEvent() {
        val body = JSONObject()
        body.put("uri", "custom://")
        body.put("event", "user_activated")
        body.put("mid", "")

        val requestBody = RequestBody.create(null, body.toString())

        val request = Request.Builder()
            .url("https://custom.test.com/event")
            .addHeader("Accept-Encoding", "gzip")
            .addHeader("User-Agent", "okhttp/3.10.0")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()


        val expected =
            "curl 'https://custom.test.com/event' -X POST -H 'Accept-Encoding: gzip' -H 'User-Agent: okhttp/3.10.0' -H 'Content-Type: application/json' -d '${body}' --compressed"

        Assert.assertEquals(expected, curlLoggingInterceptor.cURLGenerator(request))
    }

    @Test
    fun testGetUserList() {

        val request = Request.Builder()
            .url("https://custom.test.com/getUserList?id=diwl112i3")
            .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0 Safari/605.1.15")
            .addHeader("Content-Type", "application/json")
            .get()
            .build()

        val expected =
            "curl 'https://custom.test.com/getUserList?id=diwl112i3' -X GET -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0 Safari/605.1.15' -H 'Content-Type: application/json'"

        Assert.assertEquals(expected, curlLoggingInterceptor.cURLGenerator(request))
    }

    @Test
    @Throws(JSONException::class)
    fun testPutMyInfo() {
        val body = JSONObject()
        body.put("isSubscribed", true)

        val requestBody = RequestBody.create(null, body.toString())

        val request = Request.Builder()
            .url("https://custom.test.com/editMyInfo")
            .addHeader("Accept-Encoding", "gzip")
            .addHeader("User-Agent", "okhttp/3.10.0")
            .put(requestBody)
            .build()

        // @namgyu.park (2019-12-27) : CurlInterceptor insert Content-Type automatically ASOP.
        val expected =
            "curl 'https://custom.test.com/editMyInfo' -X PUT -H 'Accept-Encoding: gzip' -H 'User-Agent: okhttp/3.10.0' -H 'Content-Type: application/json' -d '${body}' --compressed"

        Assert.assertEquals(expected, curlLoggingInterceptor.cURLGenerator(request))
    }
}