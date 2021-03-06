LOCAL_PATH := $(call my-dir)

###########################
#
# openh264wraper shared library
#
###########################

include $(CLEAR_VARS)

LOCAL_MODULE := openh264wraper
LOCAL_ARM_MODE=arm
LOCAL_C_INCLUDES := $(LOCAL_PATH)/include \
					$(LOCAL_PATH)/Android/OpenH264/include

LOCAL_EXPORT_C_INCLUDES := $(LOCAL_C_INCLUDES)

LOCAL_SRC_FILES := \
	$(subst $(LOCAL_PATH)/,, \
	$(wildcard $(LOCAL_PATH)/src/*.c))

LOCAL_CFLAGS += -DGL_GLEXT_PROTOTYPES
LOCAL_LDFLAGS  := -L $(LOCAL_PATH)/Android/SDL2/lib -L $(LOCAL_PATH)/Android/OpenH264/lib
LOCAL_LDLIBS := -ldl -llog -lz -lopenh264

include $(BUILD_SHARED_LIBRARY)

LOCAL_SHARED_LIBRARIES = openh264wraper 
