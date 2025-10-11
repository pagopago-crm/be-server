package com.crm.docs.common.response.success;


import com.crm.docs.common.response.CustomStatus;
import com.crm.docs.common.response.ResponseStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomSuccessStatus implements CustomStatus {

	RESPONSE_SUCCESS(ResponseStatus.SUCCESS, "2000","요청에 성공했습니다."),
	RESPONSE_NO_CONTENT(ResponseStatus.SUCCESS, "2004","조회된 데이터가 없습니다."),

	;

	private final ResponseStatus status;
	private final String code;
	private final String message;
}
