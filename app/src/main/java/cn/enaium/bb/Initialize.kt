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
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage


/**
 * @author Enaium
 */
class Initialize : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        XposedBridge.log("PACKAGE:${lpparam.packageName}")

        var init = false

        if (arrayListOf(
                "com.bilibili.azurlane", "com.miHoYo.ys.bilibili", "com.knight.union.bili"
            ).contains(lpparam.packageName)
        ) {
            init = true
        }

        if (!init) {
            if (lpparam.packageName.contains("bilibili")) {
                init = true
            }
        }

        if (!init) {
            return
        }


        val cGSCPubCommon = lpparam.classLoader.loadClass("com.gsc.pub.GSCPubCommon")

        XposedHelpers.findAndHookMethod(cGSCPubCommon,
            "startHeart",
            Activity::class.java,
            object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam?): Any? {
                    XposedBridge.log("取消心跳上报")
                    return null
                }
            })
    }
}