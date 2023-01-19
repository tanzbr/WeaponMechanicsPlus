package me.deecaad.weaponmechanicsplus.weapon.modifiers.util;

import me.deecaad.core.file.*;
import me.deecaad.core.utils.EnumUtil;
import me.deecaad.core.utils.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

public class DoubleModifier implements Serializer<DoubleModifier> {

    /**
     * Used as a default value, doesn't modify the value at all. Can be used
     * to skip <code>null</code> checks in code.
     */
    public static final DoubleModifier NONE = new DoubleModifier(Operation.ADD, 0.0);

    private Operation operation;
    private double amount;

    /**
     * Default constructor for serializer
     */
    public DoubleModifier() {
    }

    public DoubleModifier(Operation operation, double amount) {
        this.operation = operation;
        this.amount = amount;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double apply(double num) {
        return operation.evaluate(num, amount);
    }

    @NotNull
    @Override
    public DoubleModifier serialize(SerializeData data) throws SerializerException {
        String line = data.of().assertExists().assertType(String.class).get();
        String[] split = StringUtil.split(line);

        if (split.length != 2)
            throw data.exception(null, "Invalid input: '" + line + "'",
                    "Expected 2 arguments, but got " + split.length,
                    "Valid Format: <ADD/MULTIPLY> <Decimal>");

        double amount;
        Operation operation;

        // Attempt to parse the values from the string.
        try {
            operation = EnumUtil.getIfPresent(Operation.class, split[0]).orElseThrow();
            amount = Double.parseDouble(split[1]);
        } catch (NoSuchElementException ex) {
            throw new SerializerEnumException(data.serializer, Operation.class, split[0], false, data.of().getLocation());
        } catch (NumberFormatException ex) {
            throw new SerializerTypeException(data.serializer, Double.class, String.class, split[1], data.of().getLocation());
        }

        return new DoubleModifier(operation, amount);
    }
}
