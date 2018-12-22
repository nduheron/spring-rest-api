package fr.nduheron.poc.springrestapi.user.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class ChangePasswordDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(min = 2, max = 20)
    private String newPassword;

    @NotNull
    @Size(min = 2, max = 20)
    private String oldPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}
