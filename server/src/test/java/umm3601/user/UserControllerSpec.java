package umm3601.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import io.javalin.core.validation.Validator;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import umm3601.Server;

/**
 * Tests the logic of the UserController
 *
 * @throws IOException
 */
public class UserControllerSpec {

  private Context ctx = mock(Context.class);

  private UserController userController;
  private static Database db;

  @BeforeEach
  public void setUp() throws IOException {
    ctx.clearCookieStore();

    db = new Database(Server.USER_DATA_FILE);
    userController = new UserController(db);
  }

  @Test
  public void GET_to_request_all_users() throws IOException {
    // Call the method on the mock controller
    userController.getUsers(ctx);

    // Confirm that `json` was called with all the users.
    ArgumentCaptor<User[]> argument = ArgumentCaptor.forClass(User[].class);
    verify(ctx).json(argument.capture());
    assertEquals(db.size(), argument.getValue().length);
  }

  @Test
  public void GET_to_request_age_25_users() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("age", Arrays.asList(new String[] { "25" }));

    when(ctx.queryParamMap()).thenReturn(queryParams);
    userController.getUsers(ctx);

    // Confirm that all the users passed to `json` have age 25.
    ArgumentCaptor<User[]> argument = ArgumentCaptor.forClass(User[].class);
    verify(ctx).json(argument.capture());
    for (User user : argument.getValue()) {
      assertEquals(25, user.age);
    }
  }

  /**
   * Test that if the user sends a request with an illegal value in
   * the age field (i.e., something that can't be parsed to a number)
   * we get a reasonable error code back.
   */
  @Test
  public void GET_to_request_users_with_illegal_age() {
    // We'll set the requested "age" to be a string ("abc")
    // that can't be parsed to a number.
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("age", Arrays.asList(new String[] { "abc" }));

    when(ctx.queryParamMap()).thenReturn(queryParams);
    // This should now throw a `BadRequestResponse` exception because
    // our request has an age that can't be parsed to a number.
    Assertions.assertThrows(BadRequestResponse.class, () -> {
      userController.getUsers(ctx);
    });
  }

  @Test
  public void GET_to_request_company_OHMNET_users() throws IOException {

    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("company", Arrays.asList(new String[] { "OHMNET" }));

    when(ctx.queryParamMap()).thenReturn(queryParams);
    userController.getUsers(ctx);

    // Confirm that all the users passed to `json` work for OHMNET.
    ArgumentCaptor<User[]> argument = ArgumentCaptor.forClass(User[].class);
    verify(ctx).json(argument.capture());
    for (User user : argument.getValue()) {
      assertEquals("OHMNET", user.company);
    }
  }

  @Test
  public void GET_to_request_company_OHMNET_age_25_users() throws IOException {

    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("company", Arrays.asList(new String[] { "OHMNET" }));

    queryParams.put("age", Arrays.asList(new String[] { "25" }));

    when(ctx.queryParamMap()).thenReturn(queryParams);
    userController.getUsers(ctx);

    // Confirm that all the users passed to `json` work for OHMNET
    // and have age 25.
    ArgumentCaptor<User[]> argument = ArgumentCaptor.forClass(User[].class);
    verify(ctx).json(argument.capture());
    for (User user : argument.getValue()) {
      assertEquals(25, user.age);
      assertEquals("OHMNET", user.company);
    }
  }

  @Test
  public void GET_to_request_user_with_existent_id() throws IOException {
    when(ctx.pathParam("id", String.class)).thenReturn(new Validator<String>("588935f5c668650dc77df581", "", "id"));
    userController.getUser(ctx);
    verify(ctx).status(201);
  }

  @Test
  public void GET_to_request_user_with_nonexistent_id() throws IOException {
    when(ctx.pathParam("id", String.class)).thenReturn(new Validator<String>("nonexistent", "", "id"));
    Assertions.assertThrows(NotFoundResponse.class, () -> {
      userController.getUser(ctx);
    });
  }
}
