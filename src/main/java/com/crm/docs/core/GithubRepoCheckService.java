package com.crm.docs.core;

import org.springframework.stereotype.Service;

import com.crm.docs.common.response.exception.CustomException;
import com.crm.docs.common.response.exception.CustomExceptionStatus;
import com.crm.docs.infra.GithubClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 깃허브 레포 검증을 위한 클래스.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GithubRepoCheckService {

	private final GithubClient githubClient;

	public void repoExists(String owner, String repo) {

		Boolean block = githubClient.checkRepo(owner, repo).block();

		if(Boolean.FALSE.equals(block)){
			throw new CustomException(CustomExceptionStatus.NOT_FOUND_REPOSITORY);
		}
	}
}
