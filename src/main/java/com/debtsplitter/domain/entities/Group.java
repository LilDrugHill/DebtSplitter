package com.debtsplitter.domain.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Group {
    final private Integer id;

    private String name;
    private final List<User> participants;

    public List<User> getParticipants() {
        return List.copyOf(participants);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        this.name = name;
    }

    public Group(String name, List<User> participants) {

        var tempParticipant = List.copyOf(participants);

        this.id = new Random().nextInt();
        setName(name);

        if (tempParticipant.isEmpty()) {
            throw new IllegalArgumentException("participants cannot be null");
        }
        if (participants.isEmpty()) {
            throw new IllegalArgumentException("participants must have at least 2 participants");
        }
        this.participants = tempParticipant;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public int hashCode() {
        if (id == null) return 24;
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Group)) return false;
        return (this.id != null && this.id.equals(((Group) obj).getId()));
    }
}
