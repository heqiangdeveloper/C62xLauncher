

adb install -r -t -d build\outputs\apk\release\3Y1_Settings.apk


adb shell am start -W com.chinatsp.settings/.MainActivity
TIMEOUT /T 5
exit