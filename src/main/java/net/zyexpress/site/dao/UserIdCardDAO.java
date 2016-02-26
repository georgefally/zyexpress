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

    @SqlQuery("select * from userIdCardNew")
    @Mapper(UserIdCardMapper.class)
    List<UserIdCard> getAll();

    @SqlQuery("select * from userIdCardNew where userName in (<userNameList>)")
    @Mapper(UserIdCardMapper.class)
    List<UserIdCard> findByUserName(@BindIn("userNameList") List<String> userNameStr);

    @SqlUpdate("insert into userIdCardNew (idNumber, userName) values (:idNumber, :userName)")
    int insert(@Bind("idNumber") String idNumber, @Bind("userName") String userName);
}
