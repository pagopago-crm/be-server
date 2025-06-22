package com.crm.docs.core.vectorSearch;

import java.util.List;

import org.springframework.ai.document.Document;

/**
 * Langchain에서 생성하는 스키마와 spring ai스키마가 달라서 커스텀하게 만들어야 함.
 */

public interface VectorSearchService {

	List<Document> searchSimilarDocuments(String query, String collectionName, int topK, double threshold);

	/**
	 * 유사도 점수 측정 - 테스트 및 성능 검증용
	 */
	List<Document> searchSimilarDocumentsScore(String query, String collectionName, int topK, double threshold);
}
