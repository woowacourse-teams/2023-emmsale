package com.emmsale;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import com.emmsale.activity.application.ActivityQueryService;
import com.emmsale.admin.activity.application.ActivityCommandService;
import com.emmsale.admin.event.application.EventCommandService;
import com.emmsale.admin.report.application.ReportQueryService;
import com.emmsale.admin.tag.application.TagCommandService;
import com.emmsale.block.application.BlockCommandService;
import com.emmsale.block.application.BlockQueryService;
import com.emmsale.comment.application.CommentCommandService;
import com.emmsale.comment.application.CommentQueryService;
import com.emmsale.event.application.EventQueryService;
import com.emmsale.event.application.RecruitmentPostCommandService;
import com.emmsale.event.application.RecruitmentPostQueryService;
import com.emmsale.feed.application.FeedCommandService;
import com.emmsale.feed.application.FeedQueryService;
import com.emmsale.login.application.LoginService;
import com.emmsale.member.application.InterestTagService;
import com.emmsale.member.application.MemberActivityCommandService;
import com.emmsale.member.application.MemberActivityQueryService;
import com.emmsale.member.application.MemberCommandService;
import com.emmsale.member.application.MemberQueryService;
import com.emmsale.message_room.application.MessageCommandService;
import com.emmsale.message_room.application.RoomQueryService;
import com.emmsale.notification.application.FcmTokenRegisterService;
import com.emmsale.notification.application.NotificationCommandService;
import com.emmsale.notification.application.NotificationQueryService;
import com.emmsale.report.application.ReportCommandService;
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
  protected TagQueryService tagQueryService;
  @MockBean
  protected TagCommandService tagCommandService;
  @MockBean
  protected ScrapQueryService scrapQueryService;
  @MockBean
  protected ScrapCommandService scrapCommandService;
  @MockBean
  protected ReportCommandService reportCommandService;
  @MockBean
  protected ReportQueryService reportQueryService;
  @MockBean
  protected FcmTokenRegisterService fcmTokenRegisterService;
  @MockBean
  protected MemberActivityQueryService memberActivityQueryService;
  @MockBean
  protected MemberActivityCommandService memberActivityCommandService;
  @MockBean
  protected MemberCommandService memberCommandService;
  @MockBean
  protected MemberQueryService memberQueryService;
  @MockBean
  protected InterestTagService interestTagService;
  @MockBean
  protected LoginService loginService;
  @MockBean
  protected EventQueryService eventQueryService;
  @MockBean
  protected EventCommandService eventCommandService;
  @MockBean
  protected CommentCommandService commentCommandService;
  @MockBean
  protected CommentQueryService commentQueryService;
  @MockBean
  protected BlockCommandService blockCommandService;
  @MockBean
  protected BlockQueryService blockQueryService;
  @MockBean
  protected ActivityQueryService activityQueryService;
  @MockBean
  protected ActivityCommandService activityCommandService;
  @MockBean
  protected RecruitmentPostQueryService postQueryService;
  @MockBean
  protected RecruitmentPostCommandService postCommandService;
  @MockBean
  protected MessageCommandService messageCommandService;
  @MockBean
  protected RoomQueryService roomQueryService;
  @MockBean
  protected FeedCommandService feedCommandService;
  @MockBean
  protected FeedQueryService feedQueryService;
  @MockBean
  private MemberArgumentResolver memberArgumentResolver;
  @MockBean
  protected NotificationQueryService notificationQueryService;
  @MockBean
  protected NotificationCommandService notificationCommandService;

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
