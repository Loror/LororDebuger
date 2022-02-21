# Android辅助开发Library-LororUtil

[![License](https://img.shields.io/badge/License%20-Apache%202-337ab7.svg)](https://www.apache.org/licenses/LICENSE-2.0)

## Studio中引入项目

```
dependencies {
    implementation 'com.github.Loror:LororDebuger:1.2.0'
}

allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

* 配置代码
* Application中
```
    private fun initDebugger() {
        externalCacheDir?.let {
            DebugConfig.setSaveDir(it.absolutePath + File.separator + "log" + File.separator)
            FileLogger.setSaveDir(it.absolutePath + File.separator + "log" + File.separator)
            FileLogger.clear()
            CrashHandler.getInstance().init(this)
        }
        DebugConfig.setPort(DebugConfig.Get.getPort())
        DebugConfig.setAllowRemote(true)
        DebugConfig.setDevice(Build.DEVICE)
        DebugConfig.setSdk(Build.VERSION.SDK_INT)
        DebugConfig.setVersion(BuildConfig.VERSION_NAME)
        DebugConfig.setOnCmdListener(object : OnCmdListener {
            override fun onCmd(handler: CmdHandler) {}
            override fun openFile(file: File) {
                RemoteLog.e("DEBUG", "rec open file " + file.name)
                if (file.name.endsWith(".apk")) {
                    val down = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    if (FileUtils.copy(file, File(down, file.name))) {
                        FileUtils.goInstall(this@App, File(down, file.name))
                    }
                }
            }
        })
        DebugConfig.setOnSelectClick {
            SharedPreferenceUtil.remove(SharedPreferenceUtil.AUTHORIZATION)
            SharedPreferenceUtil.remove(SharedPreferenceUtil.LOG_FINISH)
            StoreInfoUtil.clear()
            stopService(Intent(this, DebugService::class.java))
            ActivityUtil.finishAll()
            GlobalScope.launch {
                delay(1000)
                Process.killProcess(Process.myPid())
            }
        }
        DebugConfig.setSelect(Constant.ENVIRONMENT.keyList().toArray(arrayOf<String>()))
        val sensor = SensorManagerUtil(this)
        sensor.setOnShakeListener {
            DebugService.showIcon(this)
            sensor.stop()
        }
        sensor.start()
    }
```

License
-------

    Copyright 2021 Loror

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
