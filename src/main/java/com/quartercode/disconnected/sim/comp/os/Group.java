/*
 * This file is part of Disconnected.
 * Copyright (c) 2013 QuarterCode <http://www.quartercode.com/>
 *
 * Disconnected is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Disconnected is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Disconnected. If not, see <http://www.gnu.org/licenses/>.
 */

package com.quartercode.disconnected.sim.comp.os;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import com.quartercode.disconnected.util.InfoString;

@XmlAccessorType (XmlAccessType.FIELD)
public class Group implements Comparable<User>, InfoString {

    /**
     * This enum declares different right overrides.
     * Right overrides override different types of permissions (e.g. for creating a root-user).
     */
    public static enum RightOverride {

        /**
         * The root user is allowed to do everything.
         * Every right check should pass root users directly.
         */
        ROOT;
    }

    @XmlIDREF
    @XmlAttribute
    private OperatingSystem host;
    private String          name;
    private RightOverride   rightOverride;

    /**
     * Creates a new empty group object.
     * This is only recommended for direct field access (e.g. for serialization).
     */
    protected Group() {

    }

    /**
     * Creates a new group and sets the host system the object is used for and his name.
     * 
     * @param host The host operating system the group is used for.
     * @param name The name the group has.
     */
    public Group(OperatingSystem host, String name) {

        this.host = host;
        this.name = name;
    }

    /**
     * Creates a new group and sets the host system the object is used for and his name and a {@link RightOverride}..
     * 
     * @param host The host operating system the group is used for.
     * @param name The name the group has.
     * @param rightOverride A type of overriding any type of permissions.
     */
    public Group(OperatingSystem host, String name, RightOverride rightOverride) {

        this(host, name);

        this.rightOverride = rightOverride;
    }

    /**
     * Returns the host operating system the group is used for.
     * 
     * @return The host operating system the group is used for.
     */
    public OperatingSystem getHost() {

        return host;
    }

    /**
     * Returns the name of the group.
     * The name is used for recognizing a group on the os-level.
     * 
     * @return The name the group has.
     */
    public String getName() {

        return name;
    }

    /**
     * Returns the type of overriding any type of permissions for this user.
     * This returns null if there's no right override defines.
     * 
     * @return The type of overriding any type of permissions for this user.
     */
    public RightOverride getRightOverride() {

        return rightOverride;
    }

    /**
     * Resolves a list of all users which are members of the group.
     * This iterates through all users on the system and doesn't cache the list.
     * 
     * @return A list of all groups the user is in.
     */
    public List<User> getUsers() {

        List<User> users = new ArrayList<User>();
        for (User user : host.getUserManager().getUsers()) {
            if (user.getGroups().contains(this)) {
                users.add(user);
            }
        }
        return users;
    }

    /**
     * Returns the unique serialization id for the group.
     * The id is a combination of the host computer's id and the group's name.
     * It should only be used by a serialization algorithm.
     * 
     * @return The unique serialization id for the group.
     */
    @XmlAttribute
    @XmlID
    protected String getId() {

        return host.getHost().getId() + "-" + name;
    }

    @Override
    public int compareTo(User o) {

        return name.compareTo(o.getName());
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Group other = (Group) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toInfoString() {

        return name + ", " + (rightOverride == null ? "" : "right override " + rightOverride.name() + ", ") + getUsers().size() + " members";
    }

    @Override
    public String toString() {

        return getClass().getName() + " [" + toInfoString() + "]";
    }

}
