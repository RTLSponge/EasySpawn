package au.id.rleach.easyspawn;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.manipulator.mutable.entity.JoinData;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

import javax.annotation.Nullable;

@Plugin( id = au.id.rleach.easyspawn.Plugin.ID,
        name = au.id.rleach.easyspawn.Plugin.NAME,
        version = au.id.rleach.easyspawn.Plugin.VERSION,
        description = au.id.rleach.easyspawn.Plugin.DESCRIPTION
)
public class EasySpawn {

    @Listener
    public void onFirstJoin(ClientConnectionEvent.Login event){
        Optional<JoinData> joinData = event.getTargetUser().get(JoinData.class);
        if(joinData.isPresent()){
            //if player has played before.
            if(joinData.get().firstPlayed().equals(joinData.get().lastPlayed())){
                event.setToTransform(event.getToTransform().setLocation(setSpawn(event.getToTransform(), null)));
            }
        }
    }

    @Listener
    public void onSpawn(RespawnPlayerEvent event){
        if(!event.isBedSpawn()) {
            Location<World> location = setSpawn(event.getToTransform(), event.getTargetEntity());
            event.setToTransform(event.getToTransform().setLocation(location));
        }
    }

    public Location<World> setSpawn(Transform<World> toTransform, @Nullable Player player){
        Location<World> location = toTransform.getLocation();
        Location<World> spawn = location.getExtent().getSpawnLocation();
        location = location.setPosition(spawn.getPosition());
        location = location.add(.5, 0, .5);
        Optional<Location<World>> safe = Sponge.getGame().getTeleportHelper().getSafeLocation(location);
        if(safe.isPresent()) {
            if(!location.equals(safe.get())){
                if(player!=null) {
                    player.sendMessage(ChatTypes.ACTION_BAR, Text.of(TextColors.YELLOW, "Unsafe respawn, moving to safe location."));
                }
            }
            location = safe.get();
        }
        return location;
    }
}
