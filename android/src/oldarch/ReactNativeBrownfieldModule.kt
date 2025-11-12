package com.callstack.reactnativebrownfield

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule

class ReactNativeBrownfieldModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {
  companion object {
    var shouldPopToNative: Boolean = false
    private var moduleInstance: ReactNativeBrownfieldModule? = null

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

  private fun onBackPressed() {
    reactApplicationContext.currentActivity?.runOnUiThread {
      reactApplicationContext.currentActivity?.onBackPressed()
    }
  }

  override fun getName(): String {
    return "ReactNativeBrownfield"
  }
}
