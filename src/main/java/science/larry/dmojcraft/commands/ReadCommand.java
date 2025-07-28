package science.larry.dmojcraft.commands;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jsoup.HttpStatusException;

import science.larry.dmojcraft.dmoj.ProblemFetcher;
import science.larry.dmojcraft.util.RateLimiter;

public class ReadCommand implements CommandExecutor {
    RateLimiter limiter = new RateLimiter(1, 10000);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            return false;
        }

        Player player = ((Player) sender);

        // args[0] is the problem code

        try {
            ItemStack book = ProblemFetcher.readProblem(args[0]);
            player.getInventory().addItem(book);
        } catch (HttpStatusException e) {
            if (e.getStatusCode() == 404) {
                sender.sendMessage(ChatColor.RED + "Invalid problem code.");
            } else {
                sender.sendMessage(ChatColor.RED + "Failed to fetch: " + e.getMessage());
                e.printStackTrace();
            }
            return true;
        } catch (IOException e) {
            sender.sendMessage(ChatColor.RED + "Something died, like really badly. Join the Slack and yell at Rodrigo for being bad.");
            e.printStackTrace();
            return true;
        }

        return true;
    }
}
