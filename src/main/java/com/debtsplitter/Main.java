package com.debtsplitter;

import com.debtsplitter.adapters.out.memory.InMemoryExpenseRepository;
import com.debtsplitter.application.usecase.RegisterExpenseCommand;
import com.debtsplitter.application.usecase.RegisterExpenseService;
import com.debtsplitter.application.usecase.RegisterExpenseUseCase;
import com.debtsplitter.application.usecase.SplitStrategyRegistry;
import com.debtsplitter.domain.model.entities.Group;
import com.debtsplitter.domain.model.valueObjects.Money;
import com.debtsplitter.domain.model.valueObjects.UserId;
import com.debtsplitter.domain.port.ExpenseRepository;
import com.debtsplitter.domain.service.BalanceCalculator;
import com.debtsplitter.domain.service.DebtSimplifier;
import com.debtsplitter.domain.split.*;
import org.w3c.dom.ranges.Range;

import java.time.Clock;
import java.util.*;
import java.util.function.Function;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.println("Hello and welcome!");

        var user1 = new UserId(UUID.randomUUID().toString());
        var user2 = new UserId(UUID.randomUUID().toString());
        var user3 = new UserId(UUID.randomUUID().toString());

        var user4 = new UserId(UUID.randomUUID().toString());
        var user5 = new UserId(UUID.randomUUID().toString());
        var user6 = new UserId(UUID.randomUUID().toString());

        List<SplitStrategy>  ssList = new ArrayList<>();

        var weight = new HashMap<UserId, Integer>();
        weight.put(user1, 1);
        weight.put(user2, 2);
        weight.put(user3, 3);
        var groupShares = new Group("mazafakery", List.of(user1, user2, user3));

        SplitStrategy sharedSplit = new SharesSplit(weight);
        ssList.add(sharedSplit);

        var amounts = new HashMap<UserId, Money>();
        amounts.put(user4, Money.of("15.6"));
        amounts.put(user5, Money.of("12.11"));
        amounts.put(user6, Money.of("12.19"));
        var groupAmounts = new Group("mazafakeryDva", List.of(user4, user5, user6));
        var exactAmount = Money.of("39.9");


        SplitStrategy exactAmountSplit = new ExactAmountSplit(amounts);
        ssList.add(exactAmountSplit);

        var groupEQ = new Group("mazafakeryTriDorvalis", List.of(user3, user4, user5));
        ssList.add(new EqualSplit());

        var repo = new InMemoryExpenseRepository();
        SplitStrategyRegistry splitStrategyRegistry = new SplitStrategyRegistry(ssList);
        var clock = Clock.systemUTC();

        RegisterExpenseUseCase registerService = new RegisterExpenseService(repo, splitStrategyRegistry, clock);


        Function<Group, UserId> getRandUserFromGroup = (group) ->
                group.getParticipants().get(new Random().nextInt(0, group.getParticipants().size()));

        var commaGrSh = new RegisterExpenseCommand(groupShares.getId(), groupShares.getName(), getRandUserFromGroup.apply(groupShares),
                Money.of("300"), null, groupShares.getParticipants(), SplitType.SHARES, null);

        var commaGrEx = new RegisterExpenseCommand(groupAmounts.getId(), groupAmounts.getName(), getRandUserFromGroup.apply(groupAmounts),
                exactAmount, "", groupAmounts.getParticipants(), SplitType.EXACT, clock.instant().minusSeconds(6000));

        var commaGrEq = new RegisterExpenseCommand(groupEQ.getId(), groupEQ.getName(), getRandUserFromGroup.apply(groupEQ),
                Money.of("252"), "ssss", groupEQ.getParticipants(), SplitType.EQUAL, clock.instant());


        registerService.register(commaGrSh);
        registerService.register(commaGrEx);
        registerService.register(commaGrEq);

        var balances = BalanceCalculator.calculate(repo.getStorage().values());
        System.out.println(balances);
        System.out.printf("Is it 00 in sum: %b",
                Money.zero().equals(balances.values().stream().reduce(Money.zero(), Money::add, Money::add)));

        var tranfs = DebtSimplifier.simplify(repo.getStorage().values());
        tranfs.forEach(System.out::println);
    }
}
