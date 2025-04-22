package com.crm.docs.dto.res.github.tag.compare;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GithubCompareRes {

	private String url;
	private String htmlUrl;
	private String diffUrl;
	private List<GithubCompareFileDto> files;


}