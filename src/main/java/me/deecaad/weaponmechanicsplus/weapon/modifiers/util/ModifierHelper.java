package me.deecaad.weaponmechanicsplus.weapon.modifiers.util;

/**
 * Modifier helper is an interface implemented by the modifier classes. This
 * interface contains helper methods useful for serialization.
 */
public interface ModifierHelper {

    /**
     * Returns a non-null equivalent of the nullable parameter. Works by
     * returning a "dummy" modifier (that makes no changes to the number) if
     * the parameter is <code>null</code>.
     *
     * @param modifier The nullable double modifier.
     * @return The non-null double modifier.
     */
    default DoubleModifier orDefault(DoubleModifier modifier) {
        return modifier == null ? DoubleModifier.NONE : modifier;
    }

    /**
     * Returns a non-null equivalent of the nullable parameter. Works by
     * returning a "dummy" modifier (that makes no changes to the number) if
     * the parameter is <code>null</code>.
     *
     * @param modifier The nullable integer modifier.
     * @return The non-null integer modifier.
     */
    default IntegerModifier orDefault(IntegerModifier modifier) {
        return modifier == null ? IntegerModifier.NONE : modifier;
    }
}
