package com.shuaigef.springbootinit.model.entity;

import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * 扩展 Security 默认的user信息
 *
 * @author <a href="https://github.com/shuaigef">shuaigef</a>
 */
@ToString
@Setter
@Getter
public class SessionUser extends User {

    private static final long serialVersionUID = -1L;

    private Long userId;

    private Long roleId;

    private String nickname;

    private String userAvatar;

    private String userProfile;

    private Integer gender;

    public SessionUser(String username, String password,
            Collection<? extends GrantedAuthority> authorities,
            Long userId, Long roleId, String nickname, String userAvatar,
            String userProfile, Integer gender) {
        super(username, password, authorities);
        this.userId = userId;
        this.roleId = roleId;
        this.nickname = nickname;
        this.userAvatar = userAvatar;
        this.userProfile = userProfile;
        this.gender = gender;
    }

    /**
     * Returns {@code true} if the supplied object is a {@code SecurityUser} instance with the same
     * {@code username} value.
     * <p>
     * In other words, the objects are equal if they have the same username, representing the same
     * principal.
     */
    @Override
    public boolean equals(Object rhs) {
        return rhs instanceof SessionUser && userId.equals(((SessionUser) rhs).userId);
    }

    /**
     * Returns the hashcode of the {@code username}.
     */
    @Override
    public int hashCode() {
        return userId.hashCode();
    }

}
