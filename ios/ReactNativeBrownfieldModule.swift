internal import React

@objcMembers
public class ReactNativeBrownfieldModuleImpl: NSObject {
  static public func setPopGestureRecognizerEnabled(_ enabled: Bool) {
    let userInfo = ["enabled": enabled]
    DispatchQueue.main.async {
      NotificationCenter.default.post(name: Notification.Name.togglePopGestureRecognizer, object: nil, userInfo: userInfo)
    }
  }

  static public func popToNative(animated: Bool) {
    let userInfo = ["animated": animated]
    DispatchQueue.main.async {
      NotificationCenter.default.post(name: Notification.Name.popToNative, object: nil, userInfo: userInfo)
    }
  }
  
  static public func notifyGSMStatusChanged(_ isConnected: Bool) {
    let userInfo = ["connected": isConnected]
    DispatchQueue.main.async {
      NotificationCenter.default.post(
        name: NSNotification.Name.gsmDeviceStatusChanged,
        object: nil,
        userInfo: userInfo
      )
    }
  }

  static public func onCTAPressed(action: String) {
    let userInfo = ["action": action]
    DispatchQueue.main.async {
      NotificationCenter.default.post(
        name: NSNotification.Name.ctaPressed,
        object: nil,
        userInfo: userInfo
      )
    }
  }

  static public func sendDebugLog(level: String, message: String, context: String?, timestamp: Double) {
    var userInfo: [String: Any] = [
      "level": level,
      "message": message,
      "timestamp": timestamp
    ]
    if let context = context {
      userInfo["context"] = context
    }
    DispatchQueue.main.async {
      NotificationCenter.default.post(
        name: NSNotification.Name.debugLogReceived,
        object: nil,
        userInfo: userInfo
      )
    }
  }
}
