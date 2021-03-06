/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/
package org.apache.james.jmap.model.mailbox;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.james.jmap.DefaultMailboxes;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

public class Role {

    public static final String USER_DEFINED_ROLE_PREFIX = "x-";
    
    public static final Role INBOX = new Role("inbox", DefaultMailboxes.INBOX);
    public static final Role DRAFTS = new Role("drafts", DefaultMailboxes.DRAFTS);
    public static final Role OUTBOX = new Role("outbox", DefaultMailboxes.OUTBOX);
    public static final Role SENT = new Role("sent", DefaultMailboxes.SENT);
    public static final Role TRASH = new Role("trash", DefaultMailboxes.TRASH);
    public static final Role ARCHIVE = new Role("archive");
    public static final Role SPAM = new Role("spam");
    public static final Role TEMPLATES = new Role("templates");
    
    private static final Map<String, Role> ROLES = 
            ImmutableList.<Role>of(INBOX, ARCHIVE, DRAFTS, OUTBOX, SENT, TRASH, SPAM, TEMPLATES)
                .stream()
                .collect(Collectors.toMap((Role x) -> x.name.toLowerCase(Locale.ENGLISH), Function.identity()));
    
    private final String name;
    private final String defaultMailbox;

    @VisibleForTesting Role(String name, String defaultMailbox) {
        this.name = name;
        this.defaultMailbox = defaultMailbox;
    }

    @VisibleForTesting Role(String name) {
        this.name = name;
        this.defaultMailbox = null;
    }

    public static Optional<Role> from(String name) {
        Optional<Role> predefinedRole = Optional.ofNullable(ROLES.get(name.toLowerCase(Locale.ENGLISH)));
        if (predefinedRole.isPresent()) {
            return predefinedRole;
        } else {
            return tryBuildCustomRole(name);
        }
    }

    private static Optional<Role> tryBuildCustomRole(String name) {
        if (name.startsWith(USER_DEFINED_ROLE_PREFIX)) {
            return Optional.of(new Role(name));
        }
        return Optional.empty();
    }

    public boolean isSystemRole() {
        return ROLES.containsKey(name.toLowerCase(Locale.ENGLISH));
    }

    @JsonValue
    public String serialize() {
        return name;
    }

    public String getDefaultMailbox() {
        return defaultMailbox;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, defaultMailbox);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Role) {
            Role that = (Role) object;
            return Objects.equal(this.name, that.name)
                && Objects.equal(this.defaultMailbox, that.defaultMailbox);
        }
        return false;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("name", name)
            .add("defaultMailbox", defaultMailbox)
            .toString();
    }
}
