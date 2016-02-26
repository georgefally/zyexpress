package net.zyexpress.site.api;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserIdCardMapper implements ResultSetMapper<UserIdCard> {
    public UserIdCard map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        return new UserIdCard(rs.getString("userName"), rs.getString("idNumber"));
    }
}
