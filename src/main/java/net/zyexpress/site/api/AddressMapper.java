package net.zyexpress.site.api;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddressMapper implements ResultSetMapper<Address> {
    public Address map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        return new Address(rs.getString("accountname"),rs.getString("receivername"),rs.getString("address"),rs.getString("phonenumber"),rs.getString("postcode"),rs.getBoolean("isdefault"));
    }
}
