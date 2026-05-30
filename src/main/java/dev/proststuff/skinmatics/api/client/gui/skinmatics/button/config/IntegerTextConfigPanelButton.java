package dev.proststuff.skinmatics.api.client.gui.skinmatics.button.config;

import dev.proststuff.utilitary.api.config.field.value.math.IntegerConfigField;
import dev.proststuff.utilitary.api.utility.SimpleIdentifier;

public class IntegerTextConfigPanelButton extends TextConfigPanelButton<Integer, IntegerConfigField> {
    public IntegerTextConfigPanelButton(SimpleIdentifier identifier) {
        super(identifier);

        textbox.setVerification((c) -> Character.isDigit(c) || Character.toString(c).equals("-"));
        textbox.setModification(((text, textBeforeModification, _, _) -> {
            if (text.isEmpty() || text.equals("-")) {
                return text;
            }

            try {
                long value = Long.parseLong(text);

                if (value > Integer.MAX_VALUE) {
                    return Integer.toString(Integer.MAX_VALUE);
                }

                if (value < Integer.MIN_VALUE) {
                    return Integer.toString(Integer.MIN_VALUE);
                }

                return text;
            } catch (NumberFormatException e) {
                return textBeforeModification;
            }
        }));
    }

    @Override
    public Integer parse(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            assert field != null;
            return field.get();
        }
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (isFocused() && in(x, y)) {
            Integer parsed = parse(textbox.getContent());
            parsed += Math.toIntExact(Math.round(scrollY));
            assert field != null;
            field.set(parsed);

            textbox.setContent(field.getAsString());
            return true;
        }

        return super.mouseScrolled(x, y, scrollX, scrollY);
    }
}
