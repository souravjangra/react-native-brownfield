package com.callstack.reactnativebrownfield

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule

/**
 * Callback interface for CTA button press events from React Native
 */
interface CTACallback {
    fun onCTAPressed(action: String)
}

/**
 * Callback interface for debug log messages from React Native
 */
interface DebugLogCallback {
    fun onDebugLog(level: String, message: String, context: String?, timestamp: Double)
}

class ReactNativeBrownfieldModule(reactContext: ReactApplicationContext) :
    NativeReactNativeBrownfieldModuleSpec(reactContext) {
    companion object {
        var shouldPopToNative: Boolean = false
        private var moduleInstance: ReactNativeBrownfieldModule? = null
        
        /**
         * Callback listener for CTA events from React Native
         */
        @JvmStatic
        var ctaCallback: CTACallback? = null

        /**
         * Callback listener for debug log messages from React Native
         */
        @JvmStatic
        var debugLogCallback: DebugLogCallback? = null

        /**
         * Send GSM device status update to React Native
         * @param isConnected Boolean indicating if GSM device is connected
         */
        @JvmStatic
        fun notifyGSMStatusChanged(isConnected: Boolean) {
            moduleInstance?.sendGSMStatusEvent(isConnected)
        }
    }

    init {
        moduleInstance = this
    }

    @ReactMethod
    override fun addListener(eventName: String) {
        // Required for RCTEventEmitter compatibility
    }

    @ReactMethod
    override fun removeListeners(count: Double) {
        // Required for RCTEventEmitter compatibility
    }

    private fun sendGSMStatusEvent(isConnected: Boolean) {
        reactApplicationContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            ?.emit("onGSMDeviceStatusChanged", createGSMStatusMap(isConnected))
    }

    private fun createGSMStatusMap(isConnected: Boolean): WritableMap {
        return Arguments.createMap().apply {
            putBoolean("connected", isConnected)
        }
    }

    @ReactMethod
    override fun popToNative(animated: Boolean) {
        shouldPopToNative = true
        onBackPressed()
    }

    @ReactMethod
    override fun setPopGestureRecognizerEnabled(enabled: Boolean) {
        shouldPopToNative = enabled
    }

    @ReactMethod
    override fun setHardwareBackButtonEnabled(enabled: Boolean) {
        shouldPopToNative = enabled
    }

    @ReactMethod
    override fun onCTAPressed(action: String) {
        reactApplicationContext.currentActivity?.runOnUiThread {
            ctaCallback?.onCTAPressed(action)
        }
    }

    @ReactMethod
    override fun sendDebugLog(level: String, message: String, context: String?, timestamp: Double) {
        reactApplicationContext.currentActivity?.runOnUiThread {
            debugLogCallback?.onDebugLog(level, message, context, timestamp)
        }
    }

    private fun onBackPressed() {
        reactApplicationContext.currentActivity?.runOnUiThread {
            reactApplicationContext.currentActivity?.onBackPressed()
        }
    }

    override fun getName(): String {
        return "ReactNativeBrownfield"
    }
}
