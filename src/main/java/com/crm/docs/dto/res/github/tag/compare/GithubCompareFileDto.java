package com.crm.docs.dto.res.github.tag.compare;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GithubCompareFileDto {

	private String sha;
	private String filename;
	private String status;
	private String additions;
	private String deletions;
	private String changes;
	private String blobUrl;
	private String rawUrl;
	private String contentsUrl;
	private String patch;

	@Override
	public String toString() {
		return "GithubCompareFileDto{" +
			"additions='" + additions + '\'' +
			", sha='" + sha + '\'' +
			", filename='" + filename + '\'' +
			", status='" + status + '\'' +
			", deletions='" + deletions + '\'' +
			", changes='" + changes + '\'' +
			", blobUrl='" + blobUrl + '\'' +
			", rowUrl='" + rawUrl + '\'' +
			", contentsUrl='" + contentsUrl + '\'' +
			", patch='" + patch + '\'' +
			'}';
	}
}
