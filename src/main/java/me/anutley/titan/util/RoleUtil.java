package me.anutley.titan.util;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;


public class RoleUtil {
    public static boolean hasRole(Member member, Role roleToCheck) {
        for (Role role : member.getRoles()) {
            if (role == roleToCheck) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasRole(Member member, String roleId) {
        for (Role role : member.getRoles()) {
            if (role.getId().equals(roleId)) {
                return true;
            }
        }
        return false;
    }

    public static Role highestRole(Member member) {
        return member.getRoles().size() != 0 ? member.getRoles().get(0) : member.getGuild().getPublicRole();

    }
}
