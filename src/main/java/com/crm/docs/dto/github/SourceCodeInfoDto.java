package com.crm.docs.dto.github;

import com.crm.docs.dto.res.github.GithubContentDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 깃허브에서 받은 소스 코드 정보.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SourceCodeInfoDto {

	private String fileName;
	private String encoding;
	private String content;

	public static SourceCodeInfoDto of(GithubContentDto githubContentDto){

		return SourceCodeInfoDto.builder()
			.fileName(githubContentDto.getName())
			.encoding(githubContentDto.getEncoding())
			.content(githubContentDto.getContent())
			.build();
	}
}
