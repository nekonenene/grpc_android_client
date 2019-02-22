.PHONY: regenerate_debug_key
regenerate_debug_key:
	rm debug.keystore
	keytool -v -genkey -keystore debug.keystore -storepass android -alias androiddebugkey -keypass android -dname "CN=Android Debug,O=Android,C=US" -keyalg RSA -validity 36500
