//
// Created by JooLiu on 2016/1/27.
//
#include "stdlib.h"
#include "limits.h"
#include "com_xltech_client_service_H264Decoder.h"

jint Java_com_xltech_client_service_H264Decoder_CreateDecoder(JNIEnv *env, jclass cls) {
    long rv;
    SDecodingParam decParam;
    H264Decoder *decoder = NULL;
    decoder	= (H264Decoder *)malloc(sizeof(H264Decoder));
    memset(decoder, 0, sizeof(H264Decoder));
    rv = WelsCreateDecoder(&decoder->dec);
    if (rv != 0) {
        return 0;
    }

    memset(&decParam, 0, sizeof(SDecodingParam));
    decParam.eOutputColorFormat = videoFormatI420;
    decParam.uiTargetDqLayer = UCHAR_MAX;
    decParam.eEcActiveIdc = ERROR_CON_SLICE_COPY;
    decParam.sVideoProperty.eVideoBsType = VIDEO_BITSTREAM_DEFAULT;

    rv = (*decoder->dec)->Initialize(decoder->dec, &decParam);
    if (rv != 0) {
        return 0;
    }

    return (jint)decoder;
}

void Java_com_xltech_client_service_H264Decoder_DestroyDecoder(JNIEnv *env, jclass cls, jint parm) {
    H264Decoder *decoder = (H264Decoder *)parm;
    if (decoder->dec != NULL) {
        (*decoder->dec)->Uninitialize(decoder->dec);
        WelsDestroyDecoder(decoder->dec);
    }
    free(decoder);
}

jint Java_com_xltech_client_service_H264Decoder_GetWidth(JNIEnv *env, jclass cls, jint parm) {
    H264Decoder *decoder = (H264Decoder *)parm;
    if (decoder->bufInfo.iBufferStatus == 1) {
        return decoder->bufInfo.UsrData.sSystemBuffer.iWidth;
    }
    return 0;
}

jint Java_com_xltech_client_service_H264Decoder_GetHeight(JNIEnv *env, jclass cls, jint parm) {
    H264Decoder *decoder = (H264Decoder *)parm;
    if (decoder->bufInfo.iBufferStatus == 1) {
        return decoder->bufInfo.UsrData.sSystemBuffer.iHeight;
    }
    return 0;
}

jboolean Java_com_xltech_client_service_H264Decoder_DecodeOneFrame(JNIEnv *env, jclass cls, jint parm, jbyteArray in, jint inLength) {
    DECODING_STATE rv;
    H264Decoder *decoder = (H264Decoder *)parm;
    unsigned char *arrIn = NULL;
	int width = 0, height = 0;
	int i = 0, len = 0;
	
    arrIn = (*env)->GetByteArrayElements(env, in, NULL);
    if (arrIn == NULL) {
        return false;
    }
    memset(decoder->data, 0, sizeof(decoder->data));
    memset(&decoder->bufInfo, 0, sizeof(SBufferInfo));
    rv = (*decoder->dec)->DecodeFrame2(decoder->dec, arrIn, inLength, decoder->data, &(decoder->bufInfo));
	if (arrIn != NULL) {
		(*env)->ReleaseByteArrayElements(env, in, arrIn, 0);
	}

    return (decoder->bufInfo.iBufferStatus == 1);
}

jboolean Java_com_xltech_client_service_H264Decoder_GetYUVData(JNIEnv *env, jclass cls, jint parm, jbyteArray y, jbyteArray u, jbyteArray v) {
	H264Decoder *decoder = (H264Decoder *)parm;
	unsigned char *arrY = NULL;
	unsigned char *arrU = NULL;
	unsigned char *arrV = NULL;
	int i = 0, width = 0, height = 0;
    unsigned char *src_data = NULL;
	unsigned char *dst_data = NULL;
	if (decoder->bufInfo.iBufferStatus == 1) {
		width = decoder->bufInfo.UsrData.sSystemBuffer.iWidth;
		height = decoder->bufInfo.UsrData.sSystemBuffer.iHeight;
		
		arrY = (*env)->GetByteArrayElements(env, y, NULL);
		if (arrY == NULL) {
			goto GET_YUV_ERROR;
		}
		arrU = (*env)->GetByteArrayElements(env, u, NULL);
		if (arrU == NULL) {
			goto GET_YUV_ERROR;
		}
		arrV = (*env)->GetByteArrayElements(env, v, NULL);
		if (arrV == NULL) {
			goto GET_YUV_ERROR;
		}

        src_data = decoder->data[0];
		dst_data = arrY;
        for (i = 0; i < height; i++) {
            memcpy(dst_data, src_data, width);
            src_data += decoder->bufInfo.UsrData.sSystemBuffer.iStride[0];
            dst_data += width;
        }

        width = width >> 1;
        height = height >> 1;
        src_data = decoder->data[1];
		dst_data = arrU;
        for (i = 0; i < height; i++) {
            memcpy(dst_data, src_data, width);
            src_data += decoder->bufInfo.UsrData.sSystemBuffer.iStride[1];
            dst_data += width;
        }
        src_data = decoder->data[2];
		dst_data = arrV;
        for (i = 0; i < height; i++) {
            memcpy(dst_data, src_data, width);
            src_data += decoder->bufInfo.UsrData.sSystemBuffer.iStride[2];
            dst_data += width;
        }
	}

GET_YUV_ERROR:
    if (arrY != NULL) {
        (*env)->ReleaseByteArrayElements(env, y, arrY, 0);
    }
    if (arrU != NULL) {
        (*env)->ReleaseByteArrayElements(env, u, arrU, 0);
    }
    if (arrV != NULL) {
        (*env)->ReleaseByteArrayElements(env, v, arrV, 0);
    }

	return (decoder->bufInfo.iBufferStatus == 1);
}