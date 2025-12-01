#import "ReactNativeBrownfieldModule.h"

#if __has_include("ReactBrownfield/ReactBrownfield-Swift.h")
#import "ReactBrownfield/ReactBrownfield-Swift.h"
#else
#import "ReactBrownfield-Swift.h"
#endif

@implementation ReactNativeBrownfieldModule
{
  bool hasListeners;
}

- (instancetype)init {
  if (self = [super init]) {
    hasListeners = NO;
  }
  return self;
}

RCT_EXPORT_MODULE(ReactNativeBrownfield);

+ (BOOL)requiresMainQueueSetup {
  return YES;
}

- (NSArray<NSString *> *)supportedEvents {
  return @[@"onGSMDeviceStatusChanged"];
}

RCT_EXPORT_METHOD(addListener:(NSString *)eventName) {
  [super addListener:eventName];
}

RCT_EXPORT_METHOD(removeListeners:(double)count) {
  [super removeListeners:count];
}

- (void)startObserving {
  hasListeners = YES;
  [[NSNotificationCenter defaultCenter] addObserver:self
                                           selector:@selector(handleGSMStatusChanged:)
                                               name:@"GSMDeviceStatusChanged"
                                             object:nil];
}

- (void)stopObserving {
  hasListeners = NO;
  [[NSNotificationCenter defaultCenter] removeObserver:self
                                                  name:@"GSMDeviceStatusChanged"
                                                object:nil];
}

- (void)handleGSMStatusChanged:(NSNotification *)notification {
  if (hasListeners) {
    NSDictionary *userInfo = notification.userInfo;
    BOOL isConnected = [[userInfo objectForKey:@"connected"] boolValue];
    
    [self sendEventWithName:@"onGSMDeviceStatusChanged"
                       body:@{@"connected": @(isConnected)}];
  }
}

// Existing brownfield methods
RCT_EXPORT_METHOD(setPopGestureRecognizerEnabled:(BOOL)enabled) {
  [ReactNativeBrownfieldModuleImpl setPopGestureRecognizerEnabled:enabled];
}

RCT_EXPORT_METHOD(popToNative:(BOOL)animated) {
  [ReactNativeBrownfieldModuleImpl popToNativeWithAnimated:animated];
}

RCT_EXPORT_METHOD(onCTAPressed:(NSString *)action) {
  [ReactNativeBrownfieldModuleImpl onCTAPressedWithAction:action];
}

RCT_EXPORT_METHOD(sendDebugLog:(NSString *)level 
                  message:(NSString *)message 
                  context:(NSString *)context 
                  timestamp:(double)timestamp) {
  [ReactNativeBrownfieldModuleImpl sendDebugLogWithLevel:level 
                                                  message:message 
                                                  context:context 
                                                timestamp:timestamp];
}

RCT_EXPORT_METHOD(sendAnalyticsEvent:(NSString *)eventName 
                  eventProperties:(NSString *)eventProperties) {
  [ReactNativeBrownfieldModuleImpl sendAnalyticsEventWithEventName:eventName 
                                                   eventProperties:eventProperties];
}

- (void)setHardwareBackButtonEnabled:(BOOL)enabled {
  // Android only
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:(const facebook::react::ObjCTurboModule::InitParams &)params {
  return std::make_shared<facebook::react::NativeReactNativeBrownfieldModuleSpecJSI>(params);
}

- (void)dealloc {
  [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
