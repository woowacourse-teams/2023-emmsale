package com.emmsale.helper;

import com.emmsale.login.utils.GithubClient;
import com.emmsale.notification.application.FirebaseCloudMessageClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@SpringBootTest
@Sql(value = "/data-test.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
public abstract class ServiceIntegrationTestHelper {

  @MockBean
  protected GithubClient githubClient;
  @MockBean
  protected FirebaseCloudMessageClient firebaseCloudMessageClient;
}
