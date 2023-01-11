package tororo1066.playersurveillanceplugin.func

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.event.player.PlayerJoinEvent
import tororo1066.playersurveillanceplugin.PlayerSurveillancePlugin
import tororo1066.playersurveillanceplugin.data.PlayerTpData
import tororo1066.tororopluginapi.utils.toPlayer

class PlayerTp(private val data: PlayerTpData): AbstractFunc() {

    private var lock = false

    override fun run() {
        if (PlayerSurveillancePlugin.cameraPlayer == null)return
        PlayerSurveillancePlugin.isRunning = true

        sEvent.biRegister(PlayerJoinEvent::class.java){ e, unit ->
            if (e.player.uniqueId != PlayerSurveillancePlugin.cameraPlayer)return@biRegister
            lock = false
        }

        while (true){
            var p = PlayerSurveillancePlugin.cameraPlayer?.toPlayer()
            if (p == null){
                lock = true
                while (lock){
                    if (isInterrupted){
                        sEvent.unregisterAll()
                        return
                    }
                    sleep(1)
                }
                p = PlayerSurveillancePlugin.cameraPlayer?.toPlayer()!!
            }

            val players = Bukkit.getOnlinePlayers().filter { !data.exclusionPlayers.contains(it.uniqueId) && PlayerSurveillancePlugin.cameraPlayer != it.uniqueId }
            val singlePlayer = players.randomOrNull()
            if (singlePlayer == null){
                sleep(data.delay)
                continue
            }

            Bukkit.getScheduler().runTask(PlayerSurveillancePlugin.plugin, Runnable {
                p.teleport(singlePlayer.location.setDirection(singlePlayer.location.direction.normalize().multiply(-data.distance)))
            })

            sleep(data.delay)
        }
    }
}