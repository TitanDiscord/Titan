package me.anutley.titan.util;

import me.anutley.titan.Config;
import me.anutley.titan.Titan;
import me.anutley.titan.commands.Command;
import me.anutley.titan.database.objects.RolePermissions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PermissionUtil {

    public static boolean hasPermission(String permNeeded, Member member) {

        if (member.isOwner()
                || member.hasPermission(Permission.ADMINISTRATOR)
                || member.getId().equals(Config.getInstance().get("BOT_OWNER"))) return true;

        String[] splitPermissions = permNeeded.split("\\.");
        ArrayList<String> usersPermissions = new ArrayList<>();

        List<Role> roles = new ArrayList<>(member.getRoles());
        roles.add(member.getGuild().getPublicRole());

        for (Role role : roles)
            if (new RolePermissions(member.getGuild().getId(), role.getId()).getPermissions() != null)
                usersPermissions.addAll(new RolePermissions(member.getGuild().getId(), role.getId()).getPermissions());

        for (String permission : usersPermissions) {
            String perm = permNeeded;

            if (permission.equals("*") || permission.equals(permNeeded)) return true;

            for (String splitPerm : splitPermissions) {
                perm = perm.contains(".") ?
                        perm.substring(0, perm.lastIndexOf("."))
                        : perm;

                if ((perm).equals(permission)) return true;
            }
        }
        return false;
    }

    public static boolean isValidPermission(String permission) {

        ArrayList<String> permissions = new ArrayList<>();

        if (permission.equals("*")) return true;

        for (Class<?> commandClass : Titan.getRegisteredCommands()) {
            for (Method method : commandClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Command.class)) {
                    String perm = method.getAnnotation(Command.class).permission();

                    permissions.add(method.getAnnotation(Command.class).permission());
                    for (String splitPerm : method.getAnnotation(Command.class).permission().split("\\.")) {
                        perm = perm.contains(".") ?
                                perm.substring(0, perm.lastIndexOf("."))
                                : perm;
                        permissions.add(perm);
                    }
                }
            }
        }

        for (String perm : permissions) {
            if (perm.equals(permission)) return true;
        }
        return false;
    }
}

