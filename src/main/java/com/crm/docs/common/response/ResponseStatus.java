package com.crm.docs.common.response;

/**
 * SUCCESS : 응답 성공
 * FAIL : 예외
 * ERROR : 커스텀 예외를 제외한, 서버내 에러
 */
public enum ResponseStatus {

	SUCCESS, FAIL, ERROR
}
