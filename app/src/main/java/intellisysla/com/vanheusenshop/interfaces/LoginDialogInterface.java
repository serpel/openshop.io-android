package intellisysla.com.vanheusenshop.interfaces;

import intellisysla.com.vanheusenshop.entities.User.User;

/**
 * Interface declaring methods for login dialog.
 */
public interface LoginDialogInterface {

    void successfulLoginOrRegistration(User user);

}
