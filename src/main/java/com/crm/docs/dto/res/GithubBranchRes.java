package com.crm.docs.dto.res;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GithubBranchRes {

	private String name;
	private String type;
	private int size;
	private String content; // base64인코딩된 결과값.
	private String sha;
	private String gitUrl;
	private String downloadUrl;


	//base64값에 줄바꿈문자가 포함될수도 있기 때문에 이를 제거.
	public void removeContentSpace(){
		this.content = this.content.replaceAll("\\s+", "");
	}

	//파일 명에서 확장아 추출.
	public String getFileExtension(){
		if(this.name ==null || this.name.isEmpty()){
			return null;
		}

		//마지막으로 나온 (.)점 위치 구하기
		int lastIndex = this.name.lastIndexOf('.');
		//-1(없거나), 길이 - 1(맨마지막에 점이 찍혔다면) 패스.
		if(lastIndex == -1 || lastIndex == this.name.length() - 1){
			return null;
		}

		//마지막 점부터 끝까지 - 소문자로 변경해서 반환.
		return this.name.substring(lastIndex + 1).toLowerCase();
	}
}
