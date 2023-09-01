package com.emmsale;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import com.emmsale.activity.application.ActivityService;
import com.emmsale.block.application.BlockCommandService;
import com.emmsale.block.application.BlockQueryService;
import com.emmsale.comment.application.CommentCommandService;
import com.emmsale.comment.application.CommentQueryService;
import com.emmsale.event.application.EventService;
import com.emmsale.event.application.RecruitmentPostCommandService;
import com.emmsale.event.application.RecruitmentPostQueryService;
import com.emmsale.login.application.LoginService;
import com.emmsale.member.application.InterestTagService;
import com.emmsale.member.application.MemberActivityService;
import com.emmsale.member.application.MemberQueryService;
import com.emmsale.member.application.MemberUpdateService;
import com.emmsale.message_room.application.MessageCommandService;
import com.emmsale.notification.application.FcmTokenRegisterService;
import com.emmsale.notification.application.RequestNotificationCommandService;
import com.emmsale.notification.application.RequestNotificationQueryService;
import com.emmsale.notification.application.UpdateNotificationCommandService;
import com.emmsale.notification.application.UpdateNotificationQueryService;
import com.emmsale.report.application.ReportCommandService;
import com.emmsale.report.application.ReportQueryService;
import com.emmsale.resolver.MemberArgumentResolver;
import com.emmsale.scrap.application.ScrapCommandService;
import com.emmsale.scrap.application.ScrapQueryService;
import com.emmsale.tag.application.TagQueryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
abstract class MockMvcTestHelper {

  @Autowired
  protected MockMvc mockMvc;
  @Autowired
  protected ObjectMapper objectMapper;
  @MockBean
  private MemberArgumentResolver memberArgumentResolver;
  @MockBean
  protected TagQueryService tagQueryService;
  @MockBean
  protected ScrapQueryService scrapQueryService;
  @MockBean
  protected ScrapCommandService scrapCommandService;
  @MockBean
  protected ReportCommandService reportCommandService;
  @MockBean
  protected ReportQueryService reportQueryService;
  @MockBean
  protected UpdateNotificationQueryService updateNotificationQueryService;
  @MockBean
  protected UpdateNotificationCommandService updateNotificationCommandService;
  @MockBean
  protected RequestNotificationCommandService requestNotificationCommandService;
  @MockBean
  protected RequestNotificationQueryService requestNotificationQueryService;
  @MockBean
  protected FcmTokenRegisterService fcmTokenRegisterService;
  @MockBean
  protected MemberActivityService memberActivityService;
  @MockBean
  protected MemberUpdateService memberUpdateService;
  @MockBean
  protected MemberQueryService memberQueryService;
  @MockBean
  protected InterestTagService interestTagService;
  @MockBean
  protected LoginService loginService;
  @MockBean
  protected EventService eventService;
  @MockBean
  protected CommentCommandService commentCommandService;
  @MockBean
  protected CommentQueryService commentQueryService;
  @MockBean
  protected BlockCommandService blockCommandService;
  @MockBean
  protected BlockQueryService blockQueryService;
  @MockBean
  protected ActivityService activityService;
  @MockBean
  protected RecruitmentPostQueryService postQueryService;
  @MockBean
  protected RecruitmentPostCommandService postCommandService;
  @MockBean
  protected MessageCommandService messageCommandService;

  @BeforeEach
  void setUp(final WebApplicationContext applicationContext,
      final RestDocumentationContextProvider provider) {
    mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
        .apply(documentationConfiguration(provider).operationPreprocessors()
            .withRequestDefaults(prettyPrint())
            .withResponseDefaults(prettyPrint()))
        .build();
  }
}
