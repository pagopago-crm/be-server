package com.crm.docs.dto.res.github;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GithubContentDto {

	private String type;
	private String encoding;
	private int size;
	private String name;
	private String path;
	private String content;
	private String sha;
	private String url;
	private String downloadUrl;

	//base64값에 줄바꿈문자가 포함될수도 있기 때문에 이를 제거.
	public void removeContentSpace(){
		this.content = this.content.replaceAll("\\s+", "");
	}

	@Override
	public String toString() {
		return "GithubContentDto{" +
			"content='" + content + '\'' +
			", type='" + type + '\'' +
			", encoding='" + encoding + '\'' +
			", size=" + size +
			", name='" + name + '\'' +
			", path='" + path + '\'' +
			", sha='" + sha + '\'' +
			", url='" + url + '\'' +
			", downloadUrl='" + downloadUrl + '\'' +
			'}';
	}
}
