package com.debtsplitter;

import com.debtsplitter.domain.entities.Expense;
import com.debtsplitter.domain.entities.Group;
import com.debtsplitter.domain.entities.User;
import com.debtsplitter.domain.exceptions.InvalidAmoundFormatException;
import com.debtsplitter.domain.exceptions.NonPositiveAmountException;
import com.debtsplitter.domain.exceptions.SelfDebtException;
import com.debtsplitter.domain.valueObjects.Money;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.println("Hello and welcome!");



        var user1 = new User("Z", "asd@sad.ds");
        var user2 = new User("X", "asd@sd.sd");
        var user3 = new User("C", "asd@as.sdd");

        List<User> users = new ArrayList<User>();
        users.add(user2);
        users.add(user3);

        var group = new Group("test", users);

        var amound = Money.of("430");
        var badAmound = Money.of("-430");

        var expense1 = new Expense(user1, amound, group);
        System.out.println(expense1.getParticipantsDept().toString());

        try {
            var users1 = List.of(user1, user2, user3);
            var badGroup2 = new Group("test2", users1);
        } catch (SelfDebtException ex) {
            ex.printStackTrace();
        }

        try {
            var expense2 = new Expense(user2, badAmound, group);
        } catch (NonPositiveAmountException ex) {
            ex.printStackTrace();
        }

        try {
            var users3 = new ArrayList<User>();
            var badGroup = new Group("test", users3);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }

        try {
            var badMoney = Money.of("-samka");
        } catch (InvalidAmoundFormatException e) {
            e.printStackTrace();
        }



    }
}
