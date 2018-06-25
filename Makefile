# see:
#  https://onshift.atlassian.net/wiki/spaces/OUTBACK/pages/57546362/Git-crypt+android-native+repo

ubuntu-install-android-sdk:
	wget https://dl.google.com/android/repository/sdk-tools-linux-3859397.zip
	unzip -o sdk-tools-linux-3859397.zip -d /tmp/workspace/android-sdk-tools/
	export ANDROID_HOME=/tmp/workspace/android-sdk-tools
	mkdir -p /home/jenkins_agent/.android/
	touch /home/jenkins_agent/.android/repositories.cfg
	yes | /tmp/workspace/android-sdk-tools/tools/bin/sdkmanager --licenses
	/tmp/workspace/android-sdk-tools/tools/bin/sdkmanager "build-tools;26.0.0"

generate-apk:
	echo "sdk.dir=/tmp/workspace/android-sdk-tools" > ./local.properties
	# cp $(BUILD_TYPE)-gradle.properties gradle.properties
	./gradlew :app:assembleRelease


clean-up:
	rm -rf /tmp/workspace/android-sdk-tools/

clean-apks:
	rm -rf app/build/outputs/apk/*

move-apks:
	mkdir -p app/build/outputs/apk/
	echo "apks: $$(find . -name *.apk | head -1)"
	mv $$(find . -name *.apk | head -1) app/build/outputs/apk/

build-project:
	export GRADLE_USER_HOME=$(pwd)
	make ubuntu-install-android-sdk
	# mv /tmp/workspace/android-native/$(BUILD_TYPE)-google-services.json /tmp/workspace/android-native/google-services.json
	# mv /tmp/workspace/android-native/$(BUILD_TYPE)-gradle.properties /tmp/workspace/android-native/gradle.properties
	make clean-apks
	make generate-apk
	make clean-up
	make move-apks
