package net.zyexpress.site.api;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PackageItemMapper implements ResultSetMapper<Package.PackageItem> {
    public Package.PackageItem map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        return new Package.PackageItem(rs.getString("name"), rs.getString("brand"), rs.getString("specification"), rs.getInt("quantity"));
    }
}
