#当前包名&当前百度sdk key  %不要修改
cur_package="com.test.mapmove"
cur_key="0DGBHWk5U2ymQfBTwNEwdr2vNtm2CqlF"

#百度开发者平台申请key 需要的信息  开发版SHA1和发布版SHA1相同即可
发布版SHA1="80:64:BE:C8:65:45:63:BA:D9:0F:0D:81:60:16:61:4C:21:57:78:C2"

#替换包名&百度sdk key <***>随意修改  %需要上百度sdk开发者平台先申请key
replace_package="com.test.mapmove"
replace_key="0DGBHWk5U2ymQfBTwNEwdr2vNtm2CqlF"


sed -i '' 's/'${cur_package}'/'${replace_package}'/g' config.gradle
sed -i '' 's/'${cur_key}'/'${replace_key}'/g' config.gradle
./gradlew app:assembleRelease
sed -i '' 's/'${replace_package}'/'${cur_package}'/g' config.gradle
sed -i '' 's/'${replace_key}'/'${cur_key}'/g' config.gradle
open app/build/outputs/apk/release/
