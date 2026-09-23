package com.debtsplitter.domain.model.entities;

import com.debtsplitter.domain.model.valueObjects.ExpenseId;
import com.debtsplitter.domain.model.valueObjects.GroupId;
import com.debtsplitter.domain.model.valueObjects.UserId;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class Group {
    final private GroupId id;

    private String name;
    private final List<UserId> participants;

    public List<UserId> getParticipants() {
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

    public Group(String name, List<UserId> participants) {

        var tempParticipant = List.copyOf(participants);

        this.id = new GroupId(UUID.randomUUID().toString());

        setName(name);

        if (tempParticipant.isEmpty()) {
            throw new IllegalArgumentException("participants cannot be null");
        }
        if (tempParticipant.size() < 2) {
            throw new IllegalArgumentException("participants must have at least 2 participants");
        }
        this.participants = tempParticipant;
    }

    public GroupId getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof GroupId)) return false;
        return (this.id != null
                && Integer.parseInt(id.id()) == Integer.parseInt(((GroupId) obj).id()));
    }
}
