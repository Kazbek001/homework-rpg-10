package com.narxoz.rpg;

import com.narxoz.rpg.combatant.Hero;
import com.narxoz.rpg.council.CouncilEngine;
import com.narxoz.rpg.council.CouncilRunResult;
import com.narxoz.rpg.guild.Captain;
import com.narxoz.rpg.guild.GuildHall;
import com.narxoz.rpg.guild.Healer;
import com.narxoz.rpg.guild.Loremaster;
import com.narxoz.rpg.guild.Quartermaster;
import com.narxoz.rpg.guild.Scout;
import com.narxoz.rpg.quest.Quest;
import com.narxoz.rpg.quest.QuestIterator;
import com.narxoz.rpg.quest.QuestLog;
import com.narxoz.rpg.quest.QuestPriority;

import java.util.List;

/**
 * Entry point for Homework 10 — Iterator + Mediator demo.
 *
 * Demo plan:
 *   1. Build 2 heroes with different stats.
 *   2. Build a QuestLog with 6 mixed-priority quests.
 *   3. Register 5 colleagues on the GuildHall mediator
 *      (Quartermaster, Scout, Healer, Captain, and the Part-4 Loremaster).
 *   4. Pre-engine warmup: directly demonstrate two iterator styles in Main.
 *   5. Hand everything to CouncilEngine.runCouncil(...) which exercises
 *      three iterators (priority, reverse, reward-sorted) plus mediator
 *      dispatch for orders, danger, reward, lore and curse topics.
 *   6. Print final CouncilRunResult.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Homework 10 Demo: Iterator + Mediator ===");

        // 1. Heroes
        Hero arden = new Hero("Arden the Bold", 100, 30, 18, 10, 75);
        Hero lyra  = new Hero("Lyra Stormcaller", 80, 60, 14, 7, 40);

        System.out.println("\n-- Party --");
        System.out.println("  " + arden);
        System.out.println("  " + lyra);

        // 2. Quest log with mixed priorities and rewards
        QuestLog log = new QuestLog();
        log.add(new Quest("Goblin Camp Cleanup",       QuestPriority.LOW,    50,  false));
        log.add(new Quest("Caravan Escort to Stoneford", QuestPriority.NORMAL, 120, false));
        log.add(new Quest("Hunt the River Wyrm",        QuestPriority.HIGH,   400, false));
        log.add(new Quest("Cursed Ruins Investigation", QuestPriority.HIGH,   250, false));
        log.add(new Quest("Dragon Roost Assault",       QuestPriority.URGENT, 900, true));
        log.add(new Quest("Smuggler Ring Bust",         QuestPriority.NORMAL, 80,  false));

        System.out.println("\n-- Quest Log (" + log.size() + " quests) -- (arrival order)");
        QuestIterator preview = log.ordered();
        while (preview.hasNext()) {
            Quest q = preview.next();
            System.out.println("  - " + q);
        }

        // 3. Register colleagues on the mediator
        System.out.println("\n-- Registering Guild Members --");
        GuildHall hall = new GuildHall();
        Captain        captain        = new Captain("Sir Roderic", hall);
        Quartermaster  quartermaster  = new Quartermaster("Bren Hollow", hall);
        Scout          scout          = new Scout("Kira Whisperwind", hall);
        Healer         healer         = new Healer("Sister Maela", hall);
        Loremaster     loremaster     = new Loremaster("Old Theron", hall); // Part 4 — open/closed

        // 4. Pre-engine warmup: Main itself uses two iterators.
        System.out.println("\n-- Warmup iterator demo (Main) --");
        System.out.println("  >> ordered():");
        printAll(log.ordered(), "    ");

        System.out.println("\n  >> reverse():");
        printAll(log.reverse(), "    ");

        // 5. Now let the engine run the full council, which uses three more
        //    iterator passes and sends mediator messages on multiple topics.
        CouncilEngine engine = new CouncilEngine();
        CouncilRunResult result = engine.runCouncil(
                List.of(arden, lyra),
                log,
                hall,
                captain,
                quartermaster
        );

        // Loremaster also gets a chance to share its own lore — proves outbound
        // mediator usage from the new colleague (Part 4).
        System.out.println("\n>>> Loremaster outbound demo (Part 4):");
        loremaster.shareLore("history", "Cross-checking guild archives for Dragon Roost lore.");

        // Per-colleague work counters — proves messages were actually received
        System.out.println("\n-- Colleague work tally --");
        System.out.println("  Captain orders issued:        " + captain.getOrdersIssued());
        System.out.println("  Quartermaster packs prepared: " + quartermaster.getPacksPrepared());
        System.out.println("  Scout routes scouted:         " + scout.getRoutesScouted());
        System.out.println("  Healer aid plans:             " + healer.getAidPlansPrepared());
        System.out.println("  Loremaster lore entries:      " + loremaster.getLoreEntriesRecorded());
        System.out.println("  Loremaster curses analyzed:   " + loremaster.getCursesAnalyzed());

        // 6. Final result
        System.out.println("\n=== " + result + " ===");
    }

    private static void printAll(QuestIterator it, String prefix) {
        while (it.hasNext()) {
            Quest q = it.next();
            System.out.println(prefix + q.getTitle()
                    + "  [" + q.getPriority() + ", " + q.getRewardGold() + "g]");
        }
    }
}
