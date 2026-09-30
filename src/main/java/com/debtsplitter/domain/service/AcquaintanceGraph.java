package com.debtsplitter.domain.service;

import com.debtsplitter.domain.model.entities.Group;
import com.debtsplitter.domain.model.valueObjects.UserId;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Граф знакомств: двое знакомы, если состоят хотя бы в одной общей группе.
 * Перевод допустим только между знакомыми, поэтому этот граф задаёт,
 * какие переводы вообще существуют.
 */
public final class AcquaintanceGraph {

    private final Map<UserId, Set<UserId>> adjacency;

    private AcquaintanceGraph(Map<UserId, Set<UserId>> adjacency) {
        this.adjacency = adjacency;
    }

    /** Каждая группа — клика: все её участники знакомы между собой. */
    public static AcquaintanceGraph ofGroups(Iterable<? extends List<UserId>> groups) {
        Objects.requireNonNull(groups, "groups");

        Map<UserId, Set<UserId>> adjacency = new HashMap<>();
        for (List<UserId> members : groups) {
            for (UserId member : members) {
                adjacency.computeIfAbsent(member, user -> new LinkedHashSet<>());
            }
            for (int i = 0; i < members.size(); i++) {
                for (int j = i + 1; j < members.size(); j++) {
                    UserId first = members.get(i);
                    UserId second = members.get(j);
                    if (first.equals(second)) {
                        continue;
                    }
                    adjacency.get(first).add(second);
                    adjacency.get(second).add(first);
                }
            }
        }
        return new AcquaintanceGraph(adjacency);
    }

    public boolean knows(UserId first, UserId second) {
        return adjacency.getOrDefault(first, Set.of()).contains(second);
    }

    public Set<UserId> neighboursOf(UserId user) {
        return Set.copyOf(adjacency.getOrDefault(user, Set.of()));
    }

    public boolean contains(UserId user) {
        return adjacency.containsKey(user);
    }

    /** Все, кто состоит хотя бы в одной группе. */
    public Set<UserId> users() {
        return Set.copyOf(adjacency.keySet());
    }
}
