package me.deecaad.weaponmechanicsplus.weapon.modifiers.util;

import me.deecaad.core.file.*;
import me.deecaad.core.utils.EnumUtil;
import me.deecaad.core.utils.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

public class IntegerModifier implements Serializer<IntegerModifier> {

    /**
     * Used as a default value, doesn't modify the value at all. Can be used
     * to skip <code>null</code> checks in code.
     */
    public static final IntegerModifier NONE = new IntegerModifier(Operation.ADD, 0);

    private Operation operation;
    private int amount; // TODO let people use double as multiplier

    /**
     * Default constructor for serializer.
     */
    public IntegerModifier() {
    }

    public IntegerModifier(Operation operation, int amount) {
        this.operation = operation;
        this.amount = amount;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int apply(int num) {
        return operation.evaluate(num, amount);
    }

    @NotNull
    @Override
    public IntegerModifier serialize(SerializeData data) throws SerializerException {
        String line = data.config.getString(data.key);
        String[] split = StringUtil.split(line);

        if (split.length != 2)
            throw data.exception(null, "Invalid input: '" + line + "'",
                    "Expected 2 arguments, but got " + split.length,
                    "Valid Format: <ADD/MULTIPLY> <Integer>");

        int amount;
        Operation operation;

        // Attempt to parse the values from the string.
        try {
            operation = EnumUtil.getIfPresent(Operation.class, split[0]).orElseThrow();
            amount = Integer.parseInt(split[1]);
        } catch (NoSuchElementException ex) {
            throw new SerializerEnumException(data.serializer, Operation.class, split[0], false, data.of().getLocation());
        } catch (NumberFormatException ex) {
            throw new SerializerTypeException(data.serializer, Integer.class, String.class, split[1], data.of().getLocation());
        }

        return new IntegerModifier(operation, amount);
    }
}
