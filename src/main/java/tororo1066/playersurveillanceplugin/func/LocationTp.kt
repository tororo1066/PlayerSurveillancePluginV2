package tororo1066.playersurveillanceplugin.func

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scheduler.BukkitRunnable
import tororo1066.playersurveillanceplugin.PlayerSurveillancePlugin
import tororo1066.playersurveillanceplugin.data.LocationTpData
import tororo1066.tororopluginapi.sEvent.SEvent
import tororo1066.tororopluginapi.utils.toPlayer

class LocationTp(private val data: LocationTpData): AbstractFunc() {

    private var lock = false

    override fun run() {
        if (PlayerSurveillancePlugin.cameraPlayer == null)return
        PlayerSurveillancePlugin.isRunning = true

        sEvent.register(PlayerJoinEvent::class.java){ e ->
            if (e.player.uniqueId != PlayerSurveillancePlugin.cameraPlayer)return@register
            lock = false
        }

        while (true){
            data.locations.forEach {
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

                Bukkit.getScheduler().runTask(PlayerSurveillancePlugin.plugin, Runnable {
                    p.teleport(it)
                })
                sleep(data.delay)
            }
        }
    }
}