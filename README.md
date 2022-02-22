# BiligameBypass

此程序为Xposed模块,配合任意Xposed衍生版本使用

在Xposed里选择游戏即可

## 功能
* 到时间不被踢出
* 登录限制(仅支持单机游戏,并且每次都要登录)有的单机游戏有存档上传功能,需要在`/storage/emulated/0`下创建一个`bilibili-uid.txt`文件填入你的B站uid,并且开启目标游戏的文件储存权限,这个功能和第一个功能有冲突,文件内容留空就可以了,但储存权限也要开

更多功能正在开发中

## 支持的游戏
使用哩哔哩游戏SDK的游戏

## 协议

MIT协议

## 参考

[哔哩哔哩游戏SDK集成指南](http://open.biligame.com/wiki/)
