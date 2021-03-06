package org.orienteer.users.service;

import com.google.inject.ImplementedBy;
import org.apache.wicket.markup.html.WebPage;
import org.orienteer.users.resource.RegistrationResource;
import org.orienteer.users.resource.RestorePasswordResource;
import org.orienteer.users.model.OrienteerUser;

/**
 * Service for restore user password and register users
 */
@ImplementedBy(OrienteerUsersService.class)
public interface IOrienteerUsersService {

    /**
     * Create scheduler event for remove {@link OrienteerUser#PROP_RESTORE_ID} and send mail with restore link
     * created by {@link RestorePasswordResource} to user email.
     * @param user user to restore password for
     */
    public void restoreUserPassword(OrienteerUser user);

    /**
     * Remove scheduler event for restore user password
     * @param user user
     */
    public void clearRestoring(OrienteerUser user);

    /**
     * Send mail to user with link to {@link RegistrationResource}
     * @param user user
     */
    public void notifyUserAboutRegistration(OrienteerUser user);

    /**
     * Creates new user.
     * @return new user
     */
    public OrienteerUser createUser();

    /**
     * @return page which uses in {@link RestorePasswordResource}
     */
    public Class<? extends WebPage> getRestorePasswordPage();

    /**
     * @return page which uses in {@link RegistrationResource}
     */
    public Class<? extends WebPage> getRegistrationPage();

    Class<? extends WebPage> getLoginPage();
}
