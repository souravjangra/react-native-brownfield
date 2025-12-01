import { Platform } from 'react-native';
import ReactNativeBrownfieldModule from './NativeReactNativeBrownfieldModule';

const ReactNativeBrownfield = {
  popToNative: (animated?: boolean): void => {
    if (Platform.OS === 'ios') {
      ReactNativeBrownfieldModule.popToNative(!!animated);
    } else if (Platform.OS === 'android') {
      ReactNativeBrownfieldModule.popToNative(false);
    } else {
      console.warn('Not implemented: popToNative');
    }
  },

  setNativeBackGestureAndButtonEnabled: (enabled: boolean): void => {
    if (Platform.OS === 'ios') {
      ReactNativeBrownfieldModule.setPopGestureRecognizerEnabled(enabled);
    } else if (Platform.OS === 'android') {
      ReactNativeBrownfieldModule.setHardwareBackButtonEnabled(enabled);
    } else {
      console.warn('Not implemented: setNativeGesturesAndButtonsEnabled');
    }
  },

  addListener: (eventName: string): void => {
    ReactNativeBrownfieldModule.addListener(eventName);
  },

  removeListeners: (count: number): void => {
    ReactNativeBrownfieldModule.removeListeners(count);
  },

  onCTAPressed: (action: string): void => {
    ReactNativeBrownfieldModule.onCTAPressed(action);
  },

  sendDebugLog: (level: string, message: string, context: string | null, timestamp: number): void => {
    ReactNativeBrownfieldModule.sendDebugLog(level, message, context, timestamp);
  },

  analyticsEventCallback: (eventName: string, eventProperties: Record<string, any>): void => {
    ReactNativeBrownfieldModule.sendAnalyticsEvent(eventName, JSON.stringify(eventProperties));
  },
};

export default ReactNativeBrownfield;
