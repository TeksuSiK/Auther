package pl.teksusik.auther.configuration;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MiniMessageTransformer extends BidirectionalTransformer<Component, String> {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public GenericsPair<Component, String> getPair() {
        return genericsPair(Component.class, String.class);
    }

    @Override
    public String leftToRight(Component data, SerdesContext serdesContext) {
        return this.miniMessage.serialize(data);
    }

    @Override
    public Component rightToLeft(String data, SerdesContext serdesContext) {
        if (MessageConfiguration.containsLegacyColors(data)) {
            return LegacyComponentSerializer.legacy('&').deserialize(data).asComponent();
        }

        return this.miniMessage.deserialize(data);
    }
}