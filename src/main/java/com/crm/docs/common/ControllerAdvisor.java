package com.crm.docs.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.crm.docs.common.response.CommonResponse;
import com.crm.docs.common.response.CustomStatus;
import com.crm.docs.common.response.ResponseService;
import com.crm.docs.common.response.exception.CustomException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
// @RestControllerAdvice
public class ControllerAdvisor {

	private final ResponseService responseService;

	/**
	 * 커스텀 예외들
	 */
	@ExceptionHandler(CustomException.class)
	public CommonResponse exceptionHandler(CustomException e) {

		CustomStatus status = e.getCustomExceptionStatus();

		log.warn("[ CustomException - {}] : {}",
			LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")),
			status.getMessage()
		);

		return responseService.getExceptionResponse(status);
	}

	/**
	 * 커스텀 예외로 잡히지 않은 에러들
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //서버 내부 에러일때만 보냄
	public CommonResponse exceptionHandler(Exception e, HttpServletRequest request) {


		e.printStackTrace();

		String errorFormat = String.format("[ Exception - %s] : %s",
			LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")),
			e.getMessage());

		log.error(errorFormat);


		//에러의 경우에는 따로 메시지를 응답으로 주지 않음.
		return responseService.getErrorResponse(e);
	}
}