package net.zyexpress.site.api;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PackageMapper implements ResultSetMapper<Package> {
    public Package map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        return new Package(rs.getInt("id"), rs.getString("accountName"), rs.getDouble("weight"));
    }
}