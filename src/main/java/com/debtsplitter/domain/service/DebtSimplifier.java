package com.debtsplitter.domain.service;

import com.debtsplitter.domain.model.entities.Expense;
import com.debtsplitter.domain.model.entities.Group;
import com.debtsplitter.domain.model.exceptions.UnsettleableDebtsException;
import com.debtsplitter.domain.model.valueObjects.Money;
import com.debtsplitter.domain.model.valueObjects.Share;
import com.debtsplitter.domain.model.valueObjects.Transfer;
import com.debtsplitter.domain.model.valueObjects.UserId;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сводит балансы к минимальному числу переводов при двух ограничениях:
 * перевод возможен только между знакомыми (теми, кто состоит в общей группе)
 * и только напрямую от должника к кредитору — без посредников и перекачки.
 *
 * <p>Задача сводится к потоку в двудольной сети: источник — должники,
 * стоки — кредиторы, рёбра — знакомства. Максимальный поток отвечает на вопрос
 * «можно ли рассчитаться вообще», а не только «получилось ли у жадного алгоритма»:
 * жадность здесь застревает на вполне разрешимых раскладах.
 *
 * <p>После поиска потока лишние рёбра убираются гашением циклов — это не меняет
 * ни одного баланса, но каждое гашение убирает минимум один перевод. На выходе
 * переводов не больше, чем участников с ненулевым балансом минус один,
 * в пределах каждой связной части.
 */
public final class DebtSimplifier {

    /**
     * Траты группы на входе — список переводов на выходе.
     *
     * @param expenses траты, из которых выводятся балансы участников
     */
    public static List<Transfer> simplify(Collection<Expense> expenses) {
        Objects.requireNonNull(expenses, "expenses");
        var groups = expenses.stream().map((expense -> {
            List<UserId> group = expense.getShares().stream().map(Share::userId).collect(Collectors.toCollection(ArrayList::new));
            group.add(expense.getPaidBy());
            return group;
        })).collect(Collectors.toCollection(ArrayList::new));
        AcquaintanceGraph graph = AcquaintanceGraph.ofGroups(groups);

        Map<UserId, Long> ledger = checkedLedger(netBalances(expenses));
        List<UserId> debtors = select(ledger, cents -> cents < 0);
        List<UserId> creditors = select(ledger, cents -> cents > 0);

        if (debtors.isEmpty() && creditors.isEmpty()) {
            return List.of();
        }

        long[][] flow = maxFlow(debtors, creditors, ledger, graph);
        checkFullySettled(flow, debtors, creditors, ledger);
        cancelCycles(flow, debtors.size(), creditors.size());

        return toTransfers(flow, debtors, creditors);
    }

    // --- подготовка -----------------------------------------------------------

    /**
     * Баланс участника выводится из трат: заплатил минус своя доля.
     * Плюс — ему должны, минус — должен он. Считаем в копейках,
     * потому что внутри алгоритма нужна целочисленная арифметика.
     */
    private static Map<UserId, Long> netBalances(Collection<Expense> expenses) {
        var ledger = new LinkedHashMap<UserId, Long>();
        for (Expense expense : expenses) {
            Objects.requireNonNull(expense, "expense");

            long total = cents(expense.getAmount());
            long distributed = 0;
            for (Share share : expense.getShares()) {
                long amount = cents(share.amount());
                ledger.merge(share.userId(), -amount, Long::sum);
                distributed += amount;
            }
            if (distributed != total) {
                throw new IllegalArgumentException(
                        "Shares do not sum to expense amount: " + expense.getId());
            }
            ledger.merge(expense.getPaidBy(), total, Long::sum);
        }
        return ledger;
    }

    private static Map<UserId, Long> checkedLedger(Map<UserId, Long> balances) {
        long total = 0;
        for (Long cents : balances.values()) {
            total += Objects.requireNonNull(cents, "balance");
        }
        if (total != 0) {
            throw new UnsettleableDebtsException(
                    Set.copyOf(balances.keySet()), money(total),
                    "sum of all balances must be zero");
        }
        var ledger = new LinkedHashMap<>(balances);
        ledger.values().removeIf(cents -> cents == 0);
        return ledger;
    }

    private static List<UserId> select(Map<UserId, Long> ledger, java.util.function.LongPredicate filter) {
        return ledger.entrySet().stream()
                .filter(entry -> filter.test(entry.getValue()))
                .map(Map.Entry::getKey)
                .sorted(Comparator.comparing(UserId::id))   // порядок фиксирован — результат воспроизводим
                .toList();
    }

    // --- поток ----------------------------------------------------------------

    /**
     * Максимальный поток из должников в кредиторов по рёбрам-знакомствам.
     * Сеть крошечная (участники группы), поэтому берём простой поиск
     * дополняющих путей в ширину — Эдмондс–Карп.
     *
     * @return матрица {@code flow[должник][кредитор]} в копейках
     */
    private static long[][] maxFlow(List<UserId> debtors, List<UserId> creditors,
                                    Map<UserId, Long> ledger, AcquaintanceGraph graph) {

        int d = debtors.size();
        int c = creditors.size();
        int source = d + c;
        int sink = d + c + 1;
        int size = d + c + 2;

        // capacity[i][j] — остаточная пропускная способность
        long[][] capacity = new long[size][size];
        for (int i = 0; i < d; i++) {
            capacity[source][i] = -ledger.get(debtors.get(i));      // долг, положительное число
        }
        for (int j = 0; j < c; j++) {
            capacity[d + j][sink] = ledger.get(creditors.get(j));
        }
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < c; j++) {
                if (graph.knows(debtors.get(i), creditors.get(j))) {
                    capacity[i][d + j] = Long.MAX_VALUE / 4;        // знакомы — ограничения нет
                }
            }
        }

        long[][] residual = new long[size][size];
        for (int i = 0; i < size; i++) {
            residual[i] = capacity[i].clone();
        }

        int[] previous = new int[size];
        while (true) {
            java.util.Arrays.fill(previous, -1);
            previous[source] = source;
            var queue = new ArrayDeque<Integer>();
            queue.add(source);

            while (!queue.isEmpty() && previous[sink] == -1) {
                int current = queue.poll();
                for (int next = 0; next < size; next++) {
                    if (previous[next] == -1 && residual[current][next] > 0) {
                        previous[next] = current;
                        queue.add(next);
                    }
                }
            }
            if (previous[sink] == -1) {
                break;                                              // дополняющих путей больше нет
            }

            long bottleneck = Long.MAX_VALUE;
            for (int node = sink; node != source; node = previous[node]) {
                bottleneck = Math.min(bottleneck, residual[previous[node]][node]);
            }
            for (int node = sink; node != source; node = previous[node]) {
                residual[previous[node]][node] -= bottleneck;
                residual[node][previous[node]] += bottleneck;
            }
        }

        long[][] flow = new long[d][c];
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < c; j++) {
                flow[i][j] = capacity[i][d + j] - residual[i][d + j];
            }
        }
        return flow;
    }

    /** Поток меньше суммы долгов — значит, кому-то некому платить напрямую. */
    private static void checkFullySettled(long[][] flow, List<UserId> debtors,
                                          List<UserId> creditors, Map<UserId, Long> ledger) {
        var stuck = new LinkedHashSet<UserId>();
        long residual = 0;

        for (int i = 0; i < debtors.size(); i++) {
            long paid = 0;
            for (int j = 0; j < creditors.size(); j++) {
                paid += flow[i][j];
            }
            long left = -ledger.get(debtors.get(i)) - paid;
            if (left > 0) {
                stuck.add(debtors.get(i));
                residual += left;
            }
        }
        for (int j = 0; j < creditors.size(); j++) {
            long received = 0;
            for (int i = 0; i < debtors.size(); i++) {
                received += flow[i][j];
            }
            if (ledger.get(creditors.get(j)) - received > 0) {
                stuck.add(creditors.get(j));
            }
        }

        if (!stuck.isEmpty()) {
            throw new UnsettleableDebtsException(stuck, money(residual),
                    "no direct transfer between acquainted users covers these balances");
        }
    }

    // --- сокращение числа переводов -------------------------------------------

    /**
     * Гасит циклы в двудольном графе переводов: сумма у каждого участника
     * сохраняется, но минимум одно ребро на каждом проходе обнуляется.
     * Пока цикл есть, переводов больше необходимого.
     */
    private static void cancelCycles(long[][] flow, int d, int c) {
        while (true) {
            List<int[]> cycle = findCycle(flow, d, c);
            if (cycle == null) {
                return;
            }
            // рёбра цикла идут по очереди «прибавить / отнять»: баланс каждого узла не меняется
            long delta = Long.MAX_VALUE;
            for (int step = 1; step < cycle.size(); step += 2) {
                delta = Math.min(delta, flow[cycle.get(step)[0]][cycle.get(step)[1]]);
            }
            for (int step = 0; step < cycle.size(); step++) {
                int[] edge = cycle.get(step);
                if (step % 2 == 0) {
                    flow[edge[0]][edge[1]] += delta;
                } else {
                    flow[edge[0]][edge[1]] -= delta;
                }
            }
        }
    }

    /**
     * Ищет цикл в графе ненулевых переводов. Вершины — должники {@code 0..d-1}
     * и кредиторы {@code d..d+c-1}; возвращается список рёбер цикла
     * в виде пар {@code {должник, кредитор}}, чередующихся «+» и «−».
     */
    private static List<int[]> findCycle(long[][] flow, int d, int c) {
        int size = d + c;
        var adjacency = new ArrayList<List<Integer>>(size);
        for (int node = 0; node < size; node++) {
            adjacency.add(new ArrayList<>());
        }
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < c; j++) {
                if (flow[i][j] > 0) {
                    adjacency.get(i).add(d + j);
                    adjacency.get(d + j).add(i);
                }
            }
        }

        int[] parent = new int[size];
        boolean[] visited = new boolean[size];
        java.util.Arrays.fill(parent, -1);

        for (int start = 0; start < size; start++) {
            if (visited[start]) {
                continue;
            }
            var stack = new ArrayDeque<Integer>();
            stack.push(start);
            visited[start] = true;

            while (!stack.isEmpty()) {
                int current = stack.pop();
                for (int next : adjacency.get(current)) {
                    if (next == parent[current]) {
                        continue;
                    }
                    if (visited[next]) {
                        return buildCycle(parent, current, next, d);
                    }
                    visited[next] = true;
                    parent[next] = current;
                    stack.push(next);
                }
            }
        }
        return null;
    }

    /** Разворачивает найденную хорду в список рёбер цикла. */
    private static List<int[]> buildCycle(int[] parent, int from, int to, int d) {
        var pathFrom = new ArrayList<Integer>();
        for (int node = from; node != -1; node = parent[node]) {
            pathFrom.add(node);
        }
        var pathTo = new ArrayList<Integer>();
        for (int node = to; node != -1; node = parent[node]) {
            pathTo.add(node);
        }

        // общий предок — конец обоих путей; обрезаем хвосты до него
        var inFrom = new LinkedHashSet<>(pathFrom);
        int meeting = pathTo.stream().filter(inFrom::contains).findFirst().orElseThrow();

        var nodes = new ArrayList<Integer>();
        for (Integer node : pathFrom) {
            nodes.add(node);
            if (node.equals(meeting)) {
                break;
            }
        }
        var tail = new ArrayList<Integer>();
        for (Integer node : pathTo) {
            if (node.equals(meeting)) {
                break;
            }
            tail.add(node);
        }
        for (int index = tail.size() - 1; index >= 0; index--) {
            nodes.add(tail.get(index));
        }

        var edges = new ArrayList<int[]>(nodes.size());
        for (int index = 0; index < nodes.size(); index++) {
            int a = nodes.get(index);
            int b = nodes.get((index + 1) % nodes.size());
            int debtor = a < d ? a : b;
            int creditor = a < d ? b : a;
            edges.add(new int[]{debtor, creditor - d});
        }
        return edges;
    }

    // --- вывод ----------------------------------------------------------------

    private static List<Transfer> toTransfers(long[][] flow, List<UserId> debtors, List<UserId> creditors) {
        var transfers = new ArrayList<Transfer>();
        for (int i = 0; i < debtors.size(); i++) {
            for (int j = 0; j < creditors.size(); j++) {
                if (flow[i][j] > 0) {
                    transfers.add(new Transfer(debtors.get(i), creditors.get(j), money(flow[i][j])));
                }
            }
        }
        return List.copyOf(transfers);
    }

    private static long cents(Money money) {
        return money.getAmount().movePointRight(2).longValueExact();
    }

    private static Money money(long cents) {
        return Money.of(BigDecimal.valueOf(cents).movePointLeft(2).toPlainString());
    }
}
