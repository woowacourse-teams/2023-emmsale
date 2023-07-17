package com.emmsale.restdocs.product;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.headers.RequestHeadersSnippet;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.RequestParametersSnippet;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(ProductController.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
public class ProductDocumentTest {

  private static final String PRODUCT_PATH = "/test/restdocs/products";

  private static final ResponseFieldsSnippet RESPONSE_FIELDS = responseFields(
      fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("상품 상세 아이디"),
      fieldWithPath("[].name").type(JsonFieldType.STRING).description("상품명"),
      fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("상품 이미지 url"),
      fieldWithPath("[].discountRate").type(JsonFieldType.NUMBER).description("할인율"),
      fieldWithPath("[].currentPrice").type(JsonFieldType.NUMBER).description("최근가"),
      fieldWithPath("[].originalPrice").type(JsonFieldType.NUMBER).description("원가"),
      fieldWithPath("[].purchaseTemperature").type(JsonFieldType.NUMBER).description("구매 온도"),
      fieldWithPath("[].notified").type(JsonFieldType.BOOLEAN).description("알림 여부")
  );

  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  void setUp(final WebApplicationContext applicationContext,
      final RestDocumentationContextProvider provider) {
    mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
        .apply(documentationConfiguration(provider).operationPreprocessors()
            .withRequestDefaults(prettyPrint())
            .withResponseDefaults(prettyPrint()))
        .build();
  }

  @Test
  @DisplayName("최근 가격 하락 상품 목록 조회")
  void topPriceDropProductsTest() throws Exception {
    final RequestParametersSnippet requestParameters = requestParameters(
        parameterWithName("page").description("상품 목록 페이지 번호"),
        parameterWithName("size").description("보여줄 상품 개수")
    );

    final RequestHeadersSnippet requestHeaders = requestHeaders(
        headerWithName(HttpHeaders.AUTHORIZATION).description("Basic 로그인 토큰")
    );

    mockMvc.perform(get(PRODUCT_PATH + "/top-price-drop")
            .param("page", "0")
            .param("size", "4")
            .header(HttpHeaders.AUTHORIZATION, getBasicToken())
        )
        .andExpect(status().isOk())
        .andDo(
            document("top-price-drop-products", requestHeaders, requestParameters,
                RESPONSE_FIELDS)
        );
  }

  @Test
  @DisplayName("검색 상품 목록 조회")
  void searchProductsTest() throws Exception {
    final RequestParametersSnippet requestParameters = requestParameters(
        parameterWithName("page").description("상품 목록 페이지 번호"),
        parameterWithName("size").description("보여줄 상품 개수"),
        parameterWithName("keyword").description("검색어")
    );

    final RequestHeadersSnippet requestHeaders = requestHeaders(
        headerWithName(HttpHeaders.AUTHORIZATION).description("Basic 로그인 토큰")
    );

    mockMvc.perform(get(PRODUCT_PATH + "/search")
            .param("page", "0")
            .param("size", "2")
            .param("keyword", "keyword")
            .header(HttpHeaders.AUTHORIZATION, getBasicToken())
        )
        .andExpect(status().isOk())
        .andDo(
            document("search-products", requestHeaders, requestParameters, RESPONSE_FIELDS)
        );
  }

  private String getBasicToken() {
    return "Basic " + Base64.getEncoder().encodeToString("username:password".getBytes());
  }

  @Nested
  class WishTest {

    private final RequestParametersSnippet REQUEST_PARAMETERS = requestParameters(
        parameterWithName("page").description("상품 목록 페이지 번호"),
        parameterWithName("size").description("보여줄 상품 개수"),
        parameterWithName("notificationFilter").description("알림 설정된 상품만 보여주기 필터링 옵션")
    );

    @Test
    @DisplayName("찜 상품 목록 조회")
    void wishProductsTest() throws Exception {

      final RequestHeadersSnippet requestHeaders = requestHeaders(
          headerWithName(HttpHeaders.AUTHORIZATION).description("Basic 로그인 토큰")
      );

      mockMvc.perform(get(PRODUCT_PATH + "/wish?notificationFilter=false")
              .param("page", "0")
              .param("size", "2")
              .param("notificationFilter", String.valueOf(false))
              .header(HttpHeaders.AUTHORIZATION, getBasicToken())
          )
          .andExpect(status().isOk())
          .andDo(
              document("wish-products", requestHeaders, REQUEST_PARAMETERS, RESPONSE_FIELDS)
          );
    }

    @Test
    @DisplayName("찜 상품 목록 중 알림 설정된 상품만 조회")
    void wishWithNotifiedProductsTest() throws Exception {

      final RequestHeadersSnippet requestHeaders = requestHeaders(
          headerWithName(HttpHeaders.AUTHORIZATION).description("Basic 로그인 토큰")
      );

      mockMvc.perform(get(PRODUCT_PATH + "/wish?notificationFilter=true")
              .param("page", "0")
              .param("size", "2")
              .param("notificationFilter", String.valueOf(true))
              .header(HttpHeaders.AUTHORIZATION, getBasicToken())
          )
          .andExpect(status().isOk())
          .andDo(
              document("wish-with-notified-products", requestHeaders, REQUEST_PARAMETERS,
                  RESPONSE_FIELDS));
    }

    @Test
    @DisplayName("찜 상품 목록 조회시 AUTHORIZATION 헤더가 비어있을 경우 400 BAD_REQUEST를 반환한다.")
    void wishWithNoAuthorizationProductsTest() throws Exception {

      final RequestHeadersSnippet requestHeaders = requestHeaders(
          headerWithName(HttpHeaders.AUTHORIZATION).optional().description("Basic 로그인 토큰")
      );

      mockMvc.perform(get(PRODUCT_PATH + "/wish?notificationFilter=true")
              .param("page", "0")
              .param("size", "2")
              .param("notificationFilter", String.valueOf(false))
          )
          .andExpect(status().isBadRequest())
          .andDo(
              document("wish-with-no-authentication-products", requestHeaders, REQUEST_PARAMETERS)
          );
    }

  }
}
