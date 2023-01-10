package tororo1066.playersurveillanceplugin.func

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.event.player.PlayerJoinEvent
import tororo1066.playersurveillanceplugin.PlayerSurveillancePlugin
import tororo1066.playersurveillanceplugin.data.PlayerTpData
import tororo1066.tororopluginapi.utils.toPlayer

class PlayerTp(val data: PlayerTpData): AbstractFunc() {

    var lock = false

    override fun run() {
        if (PlayerSurveillancePlugin.cameraPlayer == null)return
        PlayerSurveillancePlugin.isRunning = true
        while (true){
            var p = PlayerSurveillancePlugin.cameraPlayer?.toPlayer()
            if (p == null){
                lock = true
                sEvent.biRegister(PlayerJoinEvent::class.java){ e, unit ->
                    if (e.player.uniqueId != PlayerSurveillancePlugin.cameraPlayer)return@biRegister
                    p = e.player
                    unit.unregister()
                    lock = false
                }
                while (lock){
                    if (isInterrupted)return//確証なし
                    sleep(1)
                }
            }

            val players = Bukkit.getOnlinePlayers().filter { !data.exclusionPlayers.contains(it.uniqueId) && PlayerSurveillancePlugin.cameraPlayer != it.uniqueId }
            val singlePlayer = players.randomOrNull()
            if (singlePlayer == null){
                sleep(data.delay)
                continue
            }

            Bukkit.getScheduler().runTask(PlayerSurveillancePlugin.plugin, Runnable {
                p!!.teleport(singlePlayer.location.multiply(-data.distance))
            })

            sleep(data.delay)
        }
    }
}