package com.crm.docs.core.vectorSearch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LangChainVectorSearchService implements VectorSearchService {
	private final JdbcTemplate jdbcTemplate;
	private final EmbeddingModel embeddingModel;

	public List<Document> searchSimilarDocuments(String query, String collectionName, int topK, double threshold) {
		// 1. 쿼리를 임베딩으로 변환
		float[] embed = embeddingModel.embed(query);
		String embeddingArray = Arrays.toString(embed); // [0.1, 0.2, ...]

		// 2. SQL 쿼리 실행
		String sql = """
            SELECT e.document, e.cmetadata,
                   1 - (e.embedding <=> ?::vector) as similarity
            FROM langchain_pg_embedding e
            JOIN langchain_pg_collection c ON e.collection_id = c.uuid
            WHERE c.name = ?
              AND 1 - (e.embedding <=> ?::vector) > ?
            ORDER BY e.embedding <=> ?::vector
            LIMIT ?
            """;

		return jdbcTemplate.query(
			sql,
			(rs, rowNum) -> {
				Map<String, Object> metadata = new HashMap<>();
				// cmetadata JSON 파싱 (필요시)
				return new Document(rs.getString("document"), metadata);
			},
			embeddingArray, collectionName, embeddingArray, threshold, embeddingArray, topK
		);
	}

	@Override
	public List<Document> searchSimilarDocumentsScore(String query, String collectionName, int topK, double threshold) {
		// 1. 쿼리를 임베딩으로 변환
		float[] embed = embeddingModel.embed(query);
		String embeddingArray = Arrays.toString(embed); // [0.1, 0.2, ...]

		// 2. SQL 쿼리 실행
		String sql = """
            SELECT e.document, e.cmetadata,
                   1 - (e.embedding <=> ?::vector) as similarity
            FROM langchain_pg_embedding e
            JOIN langchain_pg_collection c ON e.collection_id = c.uuid
            WHERE c.name = ?
              AND 1 - (e.embedding <=> ?::vector) > ?
            ORDER BY e.embedding <=> ?::vector
            LIMIT ?
            """;

		return jdbcTemplate.query(
			sql,
			(rs, rowNum) -> {
				Map<String, Object> metadata = new HashMap<>();
				// cmetadata JSON 파싱 (필요시)
				return Document.builder()
					.text(rs.getString("document"))
					.metadata("similarity", rs.getDouble("similarity"))
					.build();
			},
			embeddingArray, collectionName, embeddingArray, threshold, embeddingArray, topK
		);
	}
}
