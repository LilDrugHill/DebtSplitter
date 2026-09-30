package com.debtsplitter.application.usecase;

import com.debtsplitter.domain.model.valueObjects.GroupId;
import com.debtsplitter.domain.model.valueObjects.Money;
import com.debtsplitter.domain.model.valueObjects.UserId;
import com.debtsplitter.domain.split.SplitType;

import java.time.Instant;
import java.util.List;

public record RegisterExpenseCommand(GroupId groupId, String groupName, UserId paidBy, Money amount,
                                     String description, List<UserId> participants, SplitType splitType,
                                     Instant spentAt) { }
