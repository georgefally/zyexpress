package net.zyexpress.site.api;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lumengyu on 2016/5/13.
 */
public class IdCardMapper implements ResultSetMapper<IdCard> {
    public IdCard map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        return new IdCard(rs.getInt("id"),rs.getString("accountname"),rs.getString("idcardname"),
                rs.getString("idcardnumber"),rs.getBoolean("isapproved"));
    }
}