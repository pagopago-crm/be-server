package com.crm.docs.dto.req.testcase;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestScenarioExeReq {

	private String OSType; //안드로이드, IOS 유무(AOS, IOS)
	private String deviceType; //기기별 타입.
	private String osVersion; // os버전
	private String scenario;
}
