package marumasa.tps_log.TPS;

import marumasa.tps_log.Config;
import marumasa.tps_log.http.request;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Date;

public class count extends BukkitRunnable {
    private Date date = null;
    private final Config config;

    public count(Config data) {
        config = data;
    }

    @Override
    public void run() {
        if (date == null) {
            date = new Date();
        } else {
            final double start = date.getTime();
            final double end = new Date().getTime();

            double result = 300 * 1000 / (end - start);

            String text = config.LoadText;

            if (result > config.Value_VeryLow) {
                text += config.Text_VeryLow;
                result = config.MaxTPS;
            } else if (result > config.Value_Low) {
                text += config.Text_Low;
            } else if (result > config.Value_Medium) {
                text += config.Text_Medium;
            } else if (result > config.Value_High) {
                text += config.Text_High;
            } else {
                text += config.Text_VeryHigh;
            }

            if (result <= config.WarnTPS) {
                final String TPS = new DecimalFormat("###.00").format(result);
                final String MaxTPS = new DecimalFormat("###.00").format(config.MaxTPS);


                if (config.playerList) {
                    Collection<? extends Player> Players = Bukkit.getServer().getOnlinePlayers();
                    StringBuilder playerList = new StringBuilder("   ").append(config.playerListText).append("[  ");
                    if (config.UUID) {
                        for (Player player : Players) {
                            playerList.append(player.getDisplayName()).append(" : ").append(player.getUniqueId()).append("  ");
                        }
                    } else {
                        for (Player player : Players) {
                            playerList.append(player.getDisplayName()).append("  ");
                        }
                    }
                    playerList.append("]");
                    text += playerList;
                }
                final String TextTPS = TPS + " / " + MaxTPS + " " + text;

                if (config.Discord) {
                    final String JSON = "{\"content\":\"" + TextTPS + "\"}";
                    try {
                        new request(config.URL).post(JSON);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                if (config.Log) {
                    Bukkit.getLogger().warning(TextTPS);
                }
            }
            this.cancel();
        }
    }
}