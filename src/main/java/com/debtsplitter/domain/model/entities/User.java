package com.debtsplitter.domain.model.entities;

import com.debtsplitter.domain.model.valueObjects.GroupId;
import com.debtsplitter.domain.model.valueObjects.UserId;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;

public class User {
    final private UserId id;
    private String name;
    private String email;

    public String getName() {
        return name;
    }

    public UserId getId() {
        return id;
    }

    public void setName(String name) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }
        else throw new IllegalArgumentException("Name cannot be null");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {

        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null empty");
        }

        Predicate<String> isEmail = (s) -> {
            int dogIndex = s.indexOf("@");
            return dogIndex > -1 && s.indexOf(".") > dogIndex;
        };

        String lowerCaseEmail = email.toLowerCase();

        if (isEmail.test(lowerCaseEmail)) {
            this.email = lowerCaseEmail;
        }
        else throw new IllegalArgumentException("Bad email. Received: %s".formatted(lowerCaseEmail));
    }

    // TODO: We be edited in future
    public User(String name, String email) {
        this.id = new UserId(UUID.randomUUID().toString());
        setName(name);
        setEmail(email);
    }

    @Override
    public int hashCode() {
        if (id == null) return 31;
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof UserId)) return false;
        return (this.id != null && Integer.parseInt(id.id()) == Integer.parseInt(((UserId) obj).id()));
    }
}
