/**
 * Copyright (c) 2022 Enaium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package cn.enaium.bb

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import android.view.ContextThemeWrapper
import android.widget.Toast
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream


/**
 * @author Enaium
 */
class Initialize : IXposedHookLoadPackage {

    private var init = false

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        if (init) {
            return
        }

        val cGSCPubCommon: Class<*>?

        try {
            cGSCPubCommon = lpparam.classLoader.loadClass("com.gsc.pub.GSCPubCommon")
        } catch (_: ClassNotFoundException) {
            return
        }

        val uidPath = "/storage/emulated/0/bilibili-uid.txt"

        val uid: String? = if (File(uidPath).exists()) {
            val fileInputStream = FileInputStream(uidPath)
            val string = String(fileInputStream.readBytes())
            if (string == "") {
                null
            } else {
                string
            }
        } else {
            null
        }

        var context: Context? = null

        XposedHelpers.findAndHookMethod(
            ContextThemeWrapper::class.java,
            "attachBaseContext",
            Context::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    context = param.args[0] as Context
                    super.beforeHookedMethod(param)
                }
            })

        XposedHelpers.findAndHookMethod(cGSCPubCommon,
            "startHeart",
            Activity::class.java,
            object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam?): Any? {
                    XposedBridge.log("BiligameBypass:取消心跳上报")
                    if (context != null) {
                        Toast.makeText(context, "BiligameBypass:取消心跳上报", Toast.LENGTH_LONG).show()
                    }
                    return null
                }
            })

        XposedHelpers.findAndHookMethod(
            Application::class.java,
            "attach",
            Context::class.java,
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    val classLoader: ClassLoader = (param.args[0] as Context).classLoader
                    val aResponseBody = classLoader.loadClass("okhttp3.ResponseBody")
                    XposedHelpers.findAndHookMethod(aResponseBody, "string", object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            if (param.result != null) {
                                val toString = param.result.toString()
                                if (toString.startsWith("{") && toString.endsWith("}")) {
                                    val jsonObject = JSONObject(toString)

                                    //判断为登录
                                    if (jsonObject.has("code") && jsonObject.has("auth_name") && uid != null) {
                                        XposedBridge.log("BiligameBypass:登录")

                                        val user = JSONObject()
                                        user.put("auth_name", "NULL")
                                        user.put("realname_verified", 1)
                                        user.put("remind_status", 0)
                                        user.put("code", 0)
                                        user.put("access_key", "NULL")
                                        user.put("expires", "NULL")
                                        user.put("uid", uid)
                                        user.put("face", "NULL")
                                        user.put("s_face", "NULL")
                                        user.put("uname", "NULL")
                                        user.put("server_message", "NULL")
                                        param.result = user.toString()
                                    }
                                }
                            }
                            super.afterHookedMethod(param)
                        }
                    })
                }
            })
        init = true
    }
}