package net.zyexpress.site.resources;

import io.dropwizard.testing.junit.ResourceTestRule;
import net.zyexpress.site.api.User;
import net.zyexpress.site.dao.UserDAO;
import org.junit.BeforeClass;
import org.junit.Rule;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserResourceTest {
    private static final UserDAO USER_DAO = mock(UserDAO.class);

    @Rule
    public static final ResourceTestRule resources = ResourceTestRule.builder().addResource(new UserResource(USER_DAO)).build();

    @BeforeClass
    public void setup() {
        when(USER_DAO.findByUserName("user")).thenReturn(new User("user", "password", false, false));
    }
}

