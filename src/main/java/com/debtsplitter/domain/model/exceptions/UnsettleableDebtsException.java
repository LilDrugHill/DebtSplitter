package com.debtsplitter.domain.model.exceptions;

import com.debtsplitter.domain.model.valueObjects.Money;
import com.debtsplitter.domain.model.valueObjects.UserId;

import java.util.Set;

/**
 * Долги нельзя свести прямыми переводами между знакомыми: у кого-то из участников
 * не осталось знакомого с противоположным балансом, а платить незнакомцу нельзя.
 */
public class UnsettleableDebtsException extends DomainException {

    private final Set<UserId> users;
    private final Money residual;

    public UnsettleableDebtsException(Set<UserId> users, Money residual, String reason) {
        super(formatMessage(users, residual, reason));
        this.users = Set.copyOf(users);
        this.residual = residual;
    }

    private static String formatMessage(Set<UserId> users, Money residual, String reason) {
        return "Debts cannot be settled: " + reason
                + System.lineSeparator()
                + "users=" + users
                + System.lineSeparator()
                + "residual=" + residual;
    }

    /** Участники, чьи балансы остались непогашенными. */
    public Set<UserId> getUsers() {
        return users;
    }

    /** Сколько денег не удалось развести по знакомым. */
    public Money getResidual() {
        return residual;
    }
}
