package com.narxoz.rpg.quest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Part 4 — Iterator open/closed proof.
 *
 * Traverses quests sorted by reward gold (highest reward first). Note that
 * we add this iterator class WITHOUT touching:
 *   - QuestLog (no public getQuests, no new aggregate method needed beyond
 *     what the package already offers)
 *   - QuestIterator interface
 *   - Any of the existing iterator classes
 *
 * Because this class lives inside the {@code quest} package, it can use the
 * package-private {@link QuestLog#snapshot()} method just like the other
 * iterators. No internal list is leaked.
 */
public class RewardSortedQuestIterator implements QuestIterator {

    private final List<Quest> snapshot;
    private int cursor;

    public RewardSortedQuestIterator(QuestLog questLog) {
        // Take a snapshot, then sort by reward descending.
        List<Quest> sorted = new ArrayList<>(questLog.snapshot());
        sorted.sort(Comparator.comparingInt(Quest::getRewardGold).reversed());
        this.snapshot = sorted;
        this.cursor = 0;
    }

    @Override
    public boolean hasNext() {
        return cursor < snapshot.size();
    }

    @Override
    public Quest next() {
        if (!hasNext()) {
            throw new java.util.NoSuchElementException("No more quests in RewardSortedQuestIterator");
        }
        return snapshot.get(cursor++);
    }
}
