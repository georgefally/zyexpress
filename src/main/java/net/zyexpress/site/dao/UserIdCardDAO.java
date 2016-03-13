package net.zyexpress.site.dao;

import net.zyexpress.site.api.UserIdCard;
import net.zyexpress.site.api.UserIdCardMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

import java.util.List;

@UseStringTemplate3StatementLocator
public interface UserIdCardDAO {

    @SqlUpdate("create table if not exists userIdCardNew (idNumber varchar(128) unique not null, userName varchar(32) not null)")
    void createUserIdCardTable();

    @SqlQuery("select * from userIdCardNew order by idNumber")
    @Mapper(UserIdCardMapper.class)
    List<UserIdCard> getAll();

    @SqlQuery("select * from userIdCardNew where userName in (<userNameList>) order by idNumber")
    @Mapper(UserIdCardMapper.class)
    List<UserIdCard> findByUserName(@BindIn("userNameList") List<String> userNameStr);

    @SqlQuery("select * from userIdCardNew where idNumber in (<userIds>) order by idNumber")
    @Mapper(UserIdCardMapper.class)
    List<UserIdCard> findByUserIds(@BindIn("userIds") List<String> userIds);

    @SqlQuery("select * from userIdCardNew where idNumber = :userId")
    @Mapper(UserIdCardMapper.class)
    List<UserIdCard> findByUserId(@Bind("userId") String userId);

    @SqlUpdate("delete from userIdCardNew where idNumber= :idNumber")
    int deleteByUserId(@Bind("idNumber") String idNumber);

    @SqlUpdate("insert into userIdCardNew (idNumber, userName) values (:idNumber, :userName)")
    int insert(@Bind("idNumber") String idNumber, @Bind("userName") String userName);
}
