package com.crm.docs.common.response;


import static com.crm.docs.common.response.ResponseStatus.*;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공통 응답 모델")
public class CommonResponse {

	@Schema(description = "상태 코드 - 서버에서 정의한 코드")
	private String code;
	@Schema(description = "상태 값 - SUCCESS, FAIL, ERROR)")
	private String status;
	@Schema(description = "상태 메시지 - 서버에서 정의한 메시지")
	private String message;


	public static CommonResponse createCommonResponse(CustomStatus status){
		CommonResponse commonResponse = new CommonResponse();
		commonResponse.setCode(status.getCode());
		commonResponse.setStatus(status.getStatus().toString());
		commonResponse.setMessage(status.getMessage());
		return commonResponse;
	}

	public static CommonResponse createErrorResponse(Exception e){
		CommonResponse commonResponse = new CommonResponse();
		commonResponse.setCode("444");
		commonResponse.setStatus(FAIL.toString());

		return commonResponse;
	}
}

