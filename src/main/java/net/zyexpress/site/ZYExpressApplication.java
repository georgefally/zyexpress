package net.zyexpress.site;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.zyexpress.site.dao.UserDAO;
import net.zyexpress.site.dao.UserIdCardDAO;
import net.zyexpress.site.resources.UserIdCardResource;
import net.zyexpress.site.resources.UserResource;
import org.skife.jdbi.v2.DBI;

public class ZYExpressApplication extends Application<ZYExpressConfiguration> {
    public static void main(String[] args) throws Exception {
        new ZYExpressApplication().run(args);
    }

    @Override
    public String getName() {
        return "ZYExpressApplication";
    }

    @Override
    public void initialize(Bootstrap<ZYExpressConfiguration> bootstrap) {
        bootstrap.addBundle(new MultiPartBundle());
        bootstrap.addBundle(new AssetsBundle("/assets/", "/"));
    }

    @Override
    public void run(ZYExpressConfiguration configuration, Environment environment) throws Exception {

        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "hsqldb");

        final UserDAO userDAO = jdbi.onDemand(UserDAO.class);
        userDAO.createSomethingTable();

        final UserResource userResource = new UserResource(userDAO);
        environment.jersey().register(userResource);

        final UserIdCardDAO userIdCardDAO = jdbi.onDemand(UserIdCardDAO.class);
        userIdCardDAO.createUserIdCardTable();

        final UserIdCardResource userIdcardResource = new UserIdCardResource(userIdCardDAO, configuration.getUploadDir());
        environment.jersey().register(userIdcardResource);
    }
}
