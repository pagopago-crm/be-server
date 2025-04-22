package com.crm.docs.dto.res.github.tag;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GithubTagRes {

	private String name;//태그이름
	private GithubCommitDto commit;
	private String zipballUrl;
	private String tarballUrl;
	private String nodeId;
}
