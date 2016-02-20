package net.zyexpress.site.dao;

import net.zyexpress.site.api.User;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.List;

public interface UserDAO {
    @SqlUpdate("create table if not exists user (name varchar(32) primary key, password varchar(128))")
    void createSomethingTable();

    @SqlQuery("select * from User")
    List<User> getAll();

    @SqlQuery("select * from User where name = :name")
    User findByName(@Bind("name") String name);

    @SqlUpdate("delete from User where name = :name")
    int deleteByName(@Bind("name") String name);

    @SqlUpdate("update into User set password = :password where name = :name")
    int updatePassword(@BindBean User person);

    @SqlUpdate("insert into User (name, password) values (:name, :password)")
    int insert(@BindBean User user);
}
