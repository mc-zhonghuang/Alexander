package com.alan.clients.module.impl.player.antivoid;

import com.alan.clients.module.impl.movement.Flight;
import com.alan.clients.module.impl.movement.LongJump;
import com.alan.clients.module.impl.other.Test;
import com.alan.clients.module.impl.player.AntiVoid;
import com.alan.clients.module.impl.player.Scaffold;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.other.TeleportEvent;
import com.alan.clients.newevent.impl.other.WorldChangeEvent;
import com.alan.clients.newevent.impl.packet.PacketSendEvent;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.util.player.PlayerUtil;
import com.alan.clients.value.Mode;
import com.alan.clients.value.impl.NumberValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.Vec3;

import java.util.concurrent.ConcurrentLinkedQueue;

public class WatchdogAntiVoid extends Mode<AntiVoid> {

    private final NumberValue distance = new NumberValue("Distance", this, 5, 0, 10, 1);

    private final ConcurrentLinkedQueue<C03PacketPlayer> packets = new ConcurrentLinkedQueue<>();
    private Vec3 position;

    private Scaffold scaffold;
    private LongJump longJump;
    private Flight fly;
    private Test test;

    public WatchdogAntiVoid(String name, AntiVoid parent) {
        super(name, parent);
    }


    @EventLink()
    public final Listener<PacketSendEvent> onPacketSend = event -> {

        if (scaffold == null) {
            scaffold = getModule(Scaffold.class);
        }

        if (fly== null) {
            fly = getModule(Flight.class);
        }

        if (longJump == null) {
            longJump = getModule(LongJump.class);
        }

        if (test == null) {
            test = getModule(Test.class);
        }

        if (scaffold.isEnabled() || longJump.isEnabled() || test.isEnabled() || fly.isEnabled()) {
            return;
        }

        final Packet<?> p = event.getPacket();

        if (p instanceof C03PacketPlayer) {
            final C03PacketPlayer wrapper = (C03PacketPlayer) p;

            if (!PlayerUtil.isBlockUnder()) {
                packets.add(wrapper);
                event.setCancelled(true);

                if (position != null && mc.thePlayer.fallDistance > distance.getValue().floatValue()) {
                    PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(position.xCoord, position.yCoord + 0.1, position.zCoord, false));
                }
            } else {
                if (PlayerUtil.isBlockUnder(0.1) || mc.thePlayer.onGround) {
                    position = new Vec3(wrapper.x, wrapper.y, wrapper.z);
                }

                if (!packets.isEmpty()) {
                    packets.forEach(PacketUtil::sendNoEvent);
                    packets.clear();
                }
            }
        }
    };


    @EventLink()
    public final Listener<TeleportEvent> onTeleport = event -> {
        if (packets.size() > 1) {
            packets.clear();
        }
    };

    @EventLink()
    public final Listener<WorldChangeEvent> onWorldChange = event -> {
        packets.clear();
    };
}