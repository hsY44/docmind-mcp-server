package com.docmind.mcp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.PageRequest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.docmind.mcp.domain.Document;

// 실제 Postgres 방언에서 ILIKE 기반 커스텀 쿼리가 의도대로 동작하는지 확인 —
// 다른 테스트는 전부 mock/순수 유닛이라 이 클래스가 유일하게 실제 DB를 쓴다.
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DocumentRepositoryIntegrationTest {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

	@Autowired
	DocumentRepository repository;

	@Test
	void findsByTitleIgnoringCase() {
		repository.save(new Document("Spring Boot Basics", "content", "spring"));

		var results = repository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
				"SPRING boot", "SPRING boot", PageRequest.of(0, 10));

		assertThat(results).hasSize(1);
	}

	@Test
	void findsByTagAsSubstringNotExactMatch() {
		repository.save(new Document("doc", "content", "javascript"));

		// ponytail 주석대로 "java"가 "javascript"에도 매치되어야 함 (완전 일치 아님)
		var page = repository.findByTagsContainingIgnoreCase("java", PageRequest.of(0, 10));

		assertThat(page.getContent()).hasSize(1);
	}
}
