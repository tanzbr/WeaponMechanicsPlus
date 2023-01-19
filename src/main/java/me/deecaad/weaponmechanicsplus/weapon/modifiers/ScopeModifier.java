package me.deecaad.weaponmechanicsplus.weapon.modifiers;

import me.deecaad.core.file.SerializeData;
import me.deecaad.core.file.Serializer;
import me.deecaad.core.file.SerializerException;
import me.deecaad.core.utils.EnumUtil;
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.DoubleModifier;
import me.deecaad.weaponmechanicsplus.weapon.modifiers.util.Operation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScopeModifier implements Serializer<ScopeModifier> {

    private DoubleModifier zoomAmount;
    private Optional<Boolean> isNightVision;  // optional for 3 states, isEmpty -> no mod, true -> add night vision, false -> remove night vision
    private List<DoubleModifier> zoomStacking;

    public ScopeModifier() {
    }

    public ScopeModifier(DoubleModifier zoomAmount, Boolean isNightVision, List<DoubleModifier> zoomStacking) {
        this.zoomAmount = zoomAmount == null ? DoubleModifier.NONE : zoomAmount;
        this.isNightVision = Optional.ofNullable(isNightVision);
        this.zoomStacking = zoomStacking;
    }

    public DoubleModifier getZoomAmount() {
        return zoomAmount;
    }

    public Optional<Boolean> getIsNightVision() {
        return isNightVision;
    }

    public List<DoubleModifier> getZoomStacking() {
        return zoomStacking;
    }

    @NotNull
    @Override
    public ScopeModifier serialize(SerializeData data) throws SerializerException {
        DoubleModifier zoomAmount = data.of("Zoom_Amount").serialize(DoubleModifier.class);
        Boolean isNightVision = data.has("Night_Vision") ? data.of("Night_Vision").assertExists().getBool() : null;

        List<String[]> splits = data.ofList("Zoom_Stacking")
                .addArgument(Operation.class, true)
                .addArgument(double.class, true)
                .assertList().get();

        List<DoubleModifier> zoomStacking = new ArrayList<>();
        for (String[] split : splits) {
            zoomStacking.add(new DoubleModifier(EnumUtil.getIfPresent(Operation.class, split[0]).orElseThrow(), Double.parseDouble(split[1])));
        }

        return new ScopeModifier(zoomAmount, isNightVision, zoomStacking);
    }
}
