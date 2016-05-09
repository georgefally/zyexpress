package net.zyexpress.site;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.zyexpress.site.auth.AuthPrincipal;
import net.zyexpress.site.auth.TokenBasedAuthenticator;
import net.zyexpress.site.auth.TokenBasedAuthorizer;
import net.zyexpress.site.dao.PackageDAO;
import net.zyexpress.site.dao.UserDAO;
import net.zyexpress.site.dao.UserIdCardDAO;
import net.zyexpress.site.resources.AdminResource;
import net.zyexpress.site.resources.PackageResource;
import net.zyexpress.site.resources.UserIdCardResource;
import net.zyexpress.site.resources.UserResource;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
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
        bootstrap.addBundle(new AssetsBundle("/assets/", "/", "index.html"));
    }

    @Override
    public void run(ZYExpressConfiguration configuration, Environment environment) throws Exception {

        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "hsqldb");

        addUserResource(environment, jdbi);

        addUserIdCardResource(configuration, environment, jdbi);

        addAdminResource(configuration, environment, jdbi);

        addPackageResource(configuration, environment, jdbi);

        addAuthentication(configuration, environment);
    }

    private void addAuthentication(ZYExpressConfiguration configuration, Environment environment) {
        TokenBasedAuthenticator authenticator = new TokenBasedAuthenticator();

        TokenBasedAuthorizer authorizer = new TokenBasedAuthorizer();

        environment.jersey().register(new AuthDynamicFeature(
                new BasicCredentialAuthFilter.Builder<AuthPrincipal>()
                        .setAuthenticator(authenticator)
                        .setAuthorizer(authorizer)
                        .buildAuthFilter()));

        environment.jersey().register(RolesAllowedDynamicFeature.class);
        //If you want to use @Auth to inject a custom Principal type into your resource
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(AuthPrincipal.class));
    }

    private void addUserIdCardResource(ZYExpressConfiguration configuration, Environment environment, DBI jdbi) {
        final UserIdCardDAO userIdCardDAO = jdbi.onDemand(UserIdCardDAO.class);
        userIdCardDAO.createUserIdCardTable();
        //userIdCardDAO.insert("身份证号", "姓名"); // for test
        //userIdCardDAO.insert("", ""); // for test
        //userIdCardDAO.deleteByUserName("姓名");
        //userIdCardDAO.deleteByUserName("");

        final UserIdCardResource userIdcardResource = new UserIdCardResource(userIdCardDAO, configuration.getUploadDir());
        environment.jersey().register(userIdcardResource);
    }

    private void addAdminResource(ZYExpressConfiguration configuration, Environment environment, DBI jdbi) {
        final UserDAO userDAO = jdbi.onDemand(UserDAO.class);
        userDAO.createUserTable();
        final AdminResource adminResource = new AdminResource(userDAO, configuration.getUploadDir());
        environment.jersey().register(adminResource);
    }

    private void addUserResource(Environment environment, DBI jdbi) {
        final UserDAO userDAO = jdbi.onDemand(UserDAO.class);
        userDAO.createUserTable();

        final UserResource userResource = new UserResource(userDAO);
        environment.jersey().register(userResource);
    }

    private void addPackageResource(ZYExpressConfiguration configuration, Environment environment, DBI jdbi) {
        final PackageDAO packageDAO = jdbi.onDemand(PackageDAO.class);
        packageDAO.createPackageTable();
        packageDAO.createPackageItemTable();

        final PackageResource packageResource = new PackageResource(packageDAO);
        environment.jersey().register(packageResource);
    }
}
