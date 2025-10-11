package com.crm.docs.common.response;


import org.springframework.stereotype.Service;

import com.crm.docs.common.response.success.CustomSuccessStatus;

@Service
public class ResponseService {

	/**
	 * 요청 성공 응답 - 업데이트 등 응답 데이터 없는 케이스
	 */
	public CommonResponse getSuccessResponse(){
		return CommonResponse.createCommonResponse(CustomSuccessStatus.RESPONSE_NO_CONTENT);
	}

	/**
	 * 요청 성공 응답 - 조회 등 응답 데이터가 있는 케이스
	 */
	public <T> DataResponse<T> getDataResponse(T data){
		return DataResponse.createDataResponse(data, CustomSuccessStatus.RESPONSE_SUCCESS);
	}

	/**
	 * 요청 실패 응답 - 예외케이스(error가 아니라 비즈니스상 정상 흐름이지만, 데이터가 없는 등 실패케이스)
	 */
	public CommonResponse getExceptionResponse(CustomStatus status){
		return CommonResponse.createCommonResponse(status);
	}

	/**
	 * 예외로 잡히지 않은 error 케이스
	 */
	public CommonResponse getErrorResponse(Exception e){
		return CommonResponse.createErrorResponse(e);
	}
}
