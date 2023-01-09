package tororo1066.playersurveillanceplugin.func

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scheduler.BukkitRunnable
import tororo1066.playersurveillanceplugin.PlayerSurveillancePlugin
import tororo1066.playersurveillanceplugin.data.LocationTpData
import tororo1066.tororopluginapi.sEvent.SEvent
import tororo1066.tororopluginapi.utils.toPlayer

class LocationTp(val data: LocationTpData): AbstractFunc() {

    var lock = false

    override fun run() {
        if (PlayerSurveillancePlugin.cameraPlayer == null)return
        PlayerSurveillancePlugin.isRunning = true
        while (true){
            data.locations.forEach {
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
                        sleep(1)
                    }
                }

                p!!.gameMode = GameMode.SPECTATOR

                Bukkit.getScheduler().runTask(PlayerSurveillancePlugin.plugin, Runnable {
                    p!!.teleport(it)
                })
                sleep(data.delay)
            }
        }
    }
}