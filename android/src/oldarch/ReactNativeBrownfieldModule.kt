package com.callstack.reactnativebrownfield

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
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

/**
 * Callback interface for analytics events from React Native
 */
interface AnalyticsEventCallback {
  fun onAnalyticsEvent(eventName: String, eventProperties: Map<String, Any>)
}

class ReactNativeBrownfieldModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {
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
     * Callback listener for analytics events from React Native
     */
    @JvmStatic
    var analyticsEventCallback: AnalyticsEventCallback? = null

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
  fun addListener(eventName: String) {
    // Required for RCTEventEmitter compatibility
  }

  @ReactMethod
  fun removeListeners(count: Int) {
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
  fun popToNative(animated: Boolean) {
    shouldPopToNative = true
    onBackPressed()
  }

  @ReactMethod
  fun setPopGestureRecognizerEnabled(enabled: Boolean) {
    shouldPopToNative = enabled
  }

  @ReactMethod
  fun setHardwareBackButtonEnabled(isFirstRoute: Boolean) {
    shouldPopToNative = isFirstRoute
  }

  @ReactMethod
  fun onCTAPressed(action: String) {
    reactApplicationContext.currentActivity?.runOnUiThread {
      ctaCallback?.onCTAPressed(action)
    }
  }

  @ReactMethod
  fun sendDebugLog(level: String, message: String, context: String?, timestamp: Double) {
    reactApplicationContext.currentActivity?.runOnUiThread {
      debugLogCallback?.onDebugLog(level, message, context, timestamp)
    }
  }

  @ReactMethod
  fun sendAnalyticsEvent(eventName: String, eventProperties: String) {
    reactApplicationContext.currentActivity?.runOnUiThread {
      try {
        // Parse JSON string to Map
        val gson = com.google.gson.Gson()
        val mapType = object : com.google.gson.reflect.TypeToken<Map<String, Any>>() {}.type
        val propertiesMap: Map<String, Any> = gson.fromJson(eventProperties, mapType)
        analyticsEventCallback?.onAnalyticsEvent(eventName, propertiesMap)
      } catch (e: Exception) {
        // Fallback to empty map if JSON parsing fails
        analyticsEventCallback?.onAnalyticsEvent(eventName, emptyMap())
      }
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
