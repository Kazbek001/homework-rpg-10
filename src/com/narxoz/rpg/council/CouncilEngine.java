package com.narxoz.rpg.council;

import com.narxoz.rpg.combatant.Hero;
import com.narxoz.rpg.guild.Captain;
import com.narxoz.rpg.guild.GuildHall;
import com.narxoz.rpg.guild.GuildMediator;
import com.narxoz.rpg.guild.Quartermaster;
import com.narxoz.rpg.quest.Quest;
import com.narxoz.rpg.quest.QuestIterator;
import com.narxoz.rpg.quest.QuestLog;
import com.narxoz.rpg.quest.QuestPriority;

import java.util.List;

/**
 * Orchestrates a planning session that uses both Iterator and Mediator.
 *
 * Phase 1 — PriorityQuestIterator (>= HIGH): the Captain issues "orders"
 *           for each high-priority quest. Quartermaster, Scout, and Healer
 *           all subscribe to "orders" and react. URGENT quests trigger an
 *           additional "danger" dispatch.
 *
 * Phase 2 — ReverseQuestIterator: the Quartermaster posts a "reward" note
 *           per quest in reverse arrival order so the Captain can confirm.
 *
 * Phase 3 — RewardSortedQuestIterator + Loremaster (Part 4 open/closed
 *           proof): for each quest in reward order, the Loremaster files a
 *           "lore" entry. URGENT quests also trigger a "curse" report.
 *
 * Note: the engine accepts the colleagues as parameters, but it does NOT
 * touch their internals — it only invokes their outbound convenience methods,
 * each of which routes through the mediator. Cross-colleague reactions
 * happen only inside their {@code receive(...)} methods.
 */
public class CouncilEngine {

    public CouncilRunResult runCouncil(List<Hero> party,
                                       QuestLog questLog,
                                       GuildMediator hall,
                                       Captain captain,
                                       Quartermaster quartermaster) {
        if (party == null || party.isEmpty() || questLog == null || hall == null) {
            System.out.println(">> Council cannot convene — missing party, quest log, or hall.");
            return new CouncilRunResult(0, 0, 0);
        }

        int questsTraversed = 0;

        // ---------------- Phase 1: priority iterator + orders ----------------
        System.out.println("\n>>> Phase 1: High-priority sweep (PriorityQuestIterator)");
        System.out.println("    Captain walks quests with priority >= HIGH and issues orders.\n");

        QuestIterator highPriority = questLog.priorityAtLeast(QuestPriority.HIGH);
        while (highPriority.hasNext()) {
            Quest quest = highPriority.next();
            questsTraversed++;
            System.out.println("  Quest #" + questsTraversed + ": " + quest);

            // Captain issues the order — Quartermaster, Scout, Healer subscribe to "orders".
            if (captain != null) {
                captain.issueOrder("orders", "Prepare assault on '" + quest.getTitle() + "'");
            } else {
                hall.dispatch("orders", null, "Prepare assault on '" + quest.getTitle() + "'");
            }

            // URGENT quests also raise a danger flag.
            if (quest.getPriority() == QuestPriority.URGENT || quest.isUrgent()) {
                if (captain != null) {
                    captain.issueOrder("danger", "URGENT: '" + quest.getTitle() + "' is time-critical");
                } else {
                    hall.dispatch("danger", null, "URGENT: '" + quest.getTitle() + "' is time-critical");
                }
            }
            System.out.println();
        }

        // ---------------- Phase 2: reverse iterator + reward log -------------
        System.out.println(">>> Phase 2: Debrief sweep (ReverseQuestIterator)");
        System.out.println("    Quartermaster logs rewards newest-first; Captain confirms.\n");

        QuestIterator reverse = questLog.reverse();
        int debriefIndex = 0;
        while (reverse.hasNext()) {
            Quest quest = reverse.next();
            debriefIndex++;
            questsTraversed++;
            System.out.println("  Debrief #" + debriefIndex + ": " + quest.getTitle()
                    + " [reward=" + quest.getRewardGold() + "g]");

            String note = "Reward booked for '" + quest.getTitle()
                    + "': " + quest.getRewardGold() + "g";
            if (quartermaster != null) {
                quartermaster.requestSupplies("reward", note);
            } else {
                hall.dispatch("reward", null, note);
            }
        }

        // ---------------- Phase 3: open/closed proof -------------------------
        System.out.println("\n>>> Phase 3: Open/Closed proof");
        System.out.println("    (a) RewardSortedQuestIterator added without modifying QuestLog internals.");
        System.out.println("    (b) Loremaster added without modifying any existing colleague.\n");

        QuestIterator byReward = questLog.rewardSorted();
        int rewardIndex = 0;
        while (byReward.hasNext()) {
            Quest quest = byReward.next();
            rewardIndex++;
            questsTraversed++;
            System.out.println("  Reward-rank #" + rewardIndex + ": "
                    + quest.getTitle() + " (" + quest.getRewardGold() + "g, "
                    + quest.getPriority() + ")");

            // Lore archival is initiated by the field party (engine acts as
            // the field party here), routed through the hall to the Loremaster.
            // We use null as 'from' so the Loremaster (the only "lore" subscriber)
            // is not skipped by the echo-prevention rule.
            String loreMsg = "Field notes about '" + quest.getTitle() + "'";
            hall.dispatch("lore", null, loreMsg);

            if (quest.getPriority() == QuestPriority.URGENT) {
                String curseMsg = "Possible curse traces in '" + quest.getTitle() + "'";
                hall.dispatch("curse", null, curseMsg);
            }
            System.out.println();
        }

        // ---------------- Tally ---------------------------------------------
        int messagesRouted = 0;
        int memberDeliveries = 0;
        if (hall instanceof GuildHall guildHall) {
            messagesRouted = guildHall.getMessagesRouted();
            memberDeliveries = guildHall.getMemberDeliveries();
        }
        return new CouncilRunResult(questsTraversed, messagesRouted, memberDeliveries);
    }
}
