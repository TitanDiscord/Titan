package me.anutley.titan.util;

import me.anutley.titan.database.objects.GuildSettings;
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


    public static boolean isAdmin(Member member) {
        return RoleUtil.hasRole(member, new GuildSettings(member.getGuild().getId()).getAdminRoleId()) || member.isOwner();
    }

    public static boolean isMod(Member member) {
        return RoleUtil.hasRole(member, new GuildSettings(member.getGuild().getId()).getModRoleId());
    }

    public static boolean isStaff(Member member) {
        return isAdmin(member) || isMod(member);
    }

    public static boolean isTagManager(Member member) {
        return RoleUtil.hasRole(member, new GuildSettings(member.getGuild().getId()).getTagManagementRoleId()) || isMod(member) || isAdmin(member);
    }


}
