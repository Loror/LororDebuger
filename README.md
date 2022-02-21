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
            BLog.setSaveDir(it.absolutePath + File.separator + "crash" + File.separator)
            FileLogger.setSaveDir(it.absolutePath + File.separator + "log" + File.separator)
            FileLogger.clear()
            CrashHandler.getInstance().init(this)
        }
        if (Constant.isOther()) {
            ViewService.setOnSelectClick {
                stopService(Intent(this, ViewService::class.java))
                ActivityUtil.finishAll()
                GlobalScope.launch {
                    delay(1000)
                    Process.killProcess(Process.myPid())
                }
            }
            ViewService.setSelect(Constant.ENVIRONMENT.keyList().toArray(arrayOf<String>()))
            val sensor = SensorManagerUtil(this)
            sensor.setOnShakeListener {
                startService(
                    Intent(
                        this,
                        ViewService::class.java
                    )
                )
                sensor.stop()
            }
            sensor.start()
        } else {
            ViewService.setSelect(arrayOf(BuildConfig.HOST))
        }
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
