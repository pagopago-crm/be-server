package com.crm.docs.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "응답시 반환데이터 추가 모델")
public class DataResponse<T> extends CommonResponse{

	@Schema(description = "반환될 데이터 정보.")
	T data;

	public static <T> DataResponse<T> createDataResponse(T data, CustomStatus status){
		DataResponse<T> dataResponse = new DataResponse<>();
		dataResponse.setData(data);
		dataResponse.setCode(status.getCode());
		dataResponse.setStatus(status.getStatus().toString());

		return dataResponse;
	}


}
