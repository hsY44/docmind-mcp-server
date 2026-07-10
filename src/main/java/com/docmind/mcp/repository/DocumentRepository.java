package com.docmind.mcp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.docmind.mcp.domain.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {

	// 제목+내용에 대해 대소문자 구분 없이 키워드 검색 (ContainingIgnoreCase로 LIKE/ILIKE 처리).
	// Pageable로 limit 적용, count 쿼리는 실행 안 함.
	List<Document> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
			String title, String content, Pageable pageable);

	// ponytail: 콤마로 구분된 tags 컬럼에 대한 부분 문자열 매치, 완전한 태그 일치가 아님
	// (태그 "java"가 "javascript"에도 매치됨). 문제 되면 FIND_IN_SET이나 태그 조인 테이블로 업그레이드.
	Page<Document> findByTagsContainingIgnoreCase(String tag, Pageable pageable);
}
