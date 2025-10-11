package com.crm.docs.common.response.exception;



import com.crm.docs.common.response.CustomStatus;
import com.crm.docs.common.response.ResponseStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/* api 쪽에서는 스프링 시큐리티의 필터 부분을 처리하기 위한 예외만 있으면 됨. */

@Getter
@RequiredArgsConstructor
public enum CustomExceptionStatus implements CustomStatus {


	NOT_FOUND_USER(ResponseStatus.FAIL, "4004","해당하는 유저가 없습니다."),
	NOT_FOUND_REPOSITORY(ResponseStatus.FAIL, "4005","레포지토리 정보를 확인해주세요."),

	;
	private final ResponseStatus status;
	private final String code;
	private final String message;

}
