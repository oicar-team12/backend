package hr.algebra.shiftschedulingapp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hr.algebra.shiftschedulingapp.IntegrationTest;
import hr.algebra.shiftschedulingapp.model.dto.GroupDto;
import hr.algebra.shiftschedulingapp.util.Credentials;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/sql/user.sql", executionPhase = BEFORE_TEST_METHOD)
class UserControllerTest extends IntegrationTest {

  private static final String USER_GROUPS_PATH = "/user/groups";

  @Test
  void getGroups_validData_ok() throws Exception {
    Credentials credentials = login();

    MvcResult result = mockMvc.perform(get(USER_GROUPS_PATH)
        .header(AUTHORIZATION, BEARER_PREFIX + credentials.getAccessToken()))
      .andExpect(status().isOk())
      .andReturn();

    List<GroupDto> items = fromJsonString(result.getResponse().getContentAsString(), new TypeReference<>() {});
    assertEquals(2, items.size());
    assertEquals("First Group", items.getFirst().getName());
    assertEquals("Second Group", items.getLast().getName());
  }
}
