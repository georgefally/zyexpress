package net.zyexpress.site.dao;

import net.zyexpress.site.api.UserIdCard;
import net.zyexpress.site.api.UserIdCardMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.List;

public interface UserIdCardDAO {

    @SqlUpdate("create table if not exists userIdCard (idNumber varchar(128) unique not null, userName varchar(32) unique not null, fileNameFirst varchar(1024) not null, fileNameSecond varchar(1024) not null)")
    void createUserIdCardTable();

    @SqlQuery("select * from userIdCard")
    @Mapper(UserIdCardMapper.class)
    List<UserIdCard> getAll();

    @SqlQuery("select * from userIdCard where userName like :userName")
    @Mapper(UserIdCardMapper.class)
    List<UserIdCard> findByUserName(@Bind("userName") String userName);

    @SqlUpdate("insert into userIdCard (idNumber, userName, fileNameFirst, fileNameSecond) values (:idNumber, :userName, :fileNameFirst, :fileNameSecond)")
    int insert(@Bind("idNumber") String idNumber, @Bind("userName") String userName, @Bind("fileNameFirst") String fileNameFirst, @Bind("fileNameSecond") String fileNameSecond);
}
